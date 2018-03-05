package com.example.secminhr.todolisttoday;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.ViewHolder> {

    ArrayList<Todo> todoArrayList = new ArrayList<>();
    private int id = 0;

    public TodoAdapter(ArrayList<Todo> list) {
        this.todoArrayList = list;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView titleView;
        public TextView dateView;
        public TextView actionView;

        public ViewHolder(View itemView) {
            super(itemView);
            titleView = itemView.findViewById(R.id.titleView);
            dateView = itemView.findViewById(R.id.dateView);
            actionView = itemView.findViewById(R.id.action_view);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.todo_item_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Todo item = todoArrayList.get(position);
        final Context context = holder.itemView.getContext();
        holder.titleView.setText(item.getTitle());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(item.getDate());
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        String timeString = format.format(calendar.getTime());
        holder.dateView.setText(holder.itemView.getContext().getString(R.string.notification_message, timeString));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final PopupMenu menu = new PopupMenu(context, holder.actionView);
                menu.inflate(R.menu.menu_main);
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.menu_delete:
                                todoArrayList.remove(holder.getAdapterPosition());
                                TodoAdapter.this.notifyDataSetChanged();
                                break;
                            case R.id.menu_edit:
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setTitle(R.string.edit_todo);
                                View v = LayoutInflater.from(context).inflate(R.layout.new_todo_dialog, null);
                                builder.setView(v);
                                final TextInputLayout inputLayout = v.findViewById(R.id.titleInputLayout);
                                final TimePicker picker = v.findViewById(R.id.timePicker2);
                                final Calendar calendar = Calendar.getInstance();
                                calendar.setTime(item.getDate());
                                inputLayout.getEditText().setText(item.getTitle());
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    picker.setHour(calendar.get(Calendar.HOUR_OF_DAY));
                                    picker.setMinute(calendar.get(Calendar.MINUTE));
                                } else {
                                    picker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
                                    picker.setCurrentMinute(calendar.get(Calendar.MINUTE));
                                }
                                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        item.setTitle(inputLayout.getEditText().getText().toString());
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                            calendar.set(Calendar.MINUTE, picker.getMinute());
                                            calendar.set(Calendar.HOUR_OF_DAY, picker.getHour());
                                        } else {
                                            calendar.set(Calendar.MINUTE, picker.getCurrentMinute());
                                            calendar.set(Calendar.HOUR_OF_DAY, picker.getCurrentHour());
                                        }
                                        item.setDate(calendar.getTime());
                                        TodoAdapter.this.notifyItemChanged(holder.getAdapterPosition());
                                    }
                                });
                                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                });
                                builder.show();
                                break;
                            default:
                                break;
                        }
                        return true;
                    }
                });
                menu.show();
            }
        });

        NotificationCompat.Builder builder;
        builder = new NotificationCompat.Builder(context, "TODO");

        PendingIntent intent = PendingIntent.getActivity(holder.itemView.getContext(), 0, new Intent(holder.itemView.getContext(), MainActivity.class), 0);

        Notification notification = builder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(context.getResources().getString(R.string.app_name))
                .setContentText(context.getResources().getString(R.string.notification_content) + " " + item.getTitle())
                .setChannelId("TODO")
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(intent)
                .build();
        Intent notificationIntent = new Intent(holder.itemView.getContext(), NotificationPusher.class);
        notificationIntent.putExtra(NotificationPusher.ID, id++);
        notificationIntent.putExtra(NotificationPusher.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        long targetTime = item.getDate().getTime();
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        manager.set(AlarmManager.RTC_WAKEUP, targetTime, pendingIntent);
    }

    @Override
    public int getItemCount() {
        return todoArrayList.size();
    }
}
