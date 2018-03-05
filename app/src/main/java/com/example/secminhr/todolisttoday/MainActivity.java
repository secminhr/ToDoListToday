package com.example.secminhr.todolisttoday;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    ArrayList<Todo> todoArrayList = new ArrayList<>();
    Toolbar toolbar;
    RecyclerView recyclerView;
    TodoAdapter adapter;
    FloatingActionButton fab;
    final String key = "ArrObj";
    final String preferenceName = "TODO";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SharedPreferences preferences = getSharedPreferences(preferenceName, MODE_PRIVATE);
        String s = preferences.getString(key, "");
        Type type = new TypeToken<ArrayList<Todo>>(){}.getType();
        Object arr = new Gson().fromJson(s, type);
        if (arr != null) {
            todoArrayList.addAll((ArrayList<Todo>)new Gson().fromJson(s, type));
        }
        recyclerView = findViewById(R.id.todoList);
        recyclerView.setHasFixedSize(true);
        adapter = new TodoAdapter(todoArrayList);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(R.string.add_new);
                final View dialog = LayoutInflater.from(MainActivity.this).inflate(R.layout.new_todo_dialog, null);
                builder.setView(dialog);
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        TextInputLayout inputLayout = dialog.findViewById(R.id.titleInputLayout);
                        TimePicker picker = dialog.findViewById(R.id.timePicker2);
                        Calendar calendar = Calendar.getInstance();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            calendar.set(Calendar.MINUTE, picker.getMinute());
                            calendar.set(Calendar.HOUR_OF_DAY, picker.getHour());
                        } else {
                            calendar.set(Calendar.MINUTE, picker.getCurrentMinute());
                            calendar.set(Calendar.HOUR_OF_DAY, picker.getCurrentHour());
                        }
                        todoArrayList.add(new Todo(MainActivity.this, inputLayout.getEditText().getText().toString(), calendar.getTime()));
                        adapter.notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.show();
            }
        });

        ItemTouchHelper.Callback callback = new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
                return makeMovementFlags(0, swipeFlags);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                todoArrayList.remove(position);
                adapter.notifyItemRemoved(position);
            }
        };

        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(recyclerView);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        SharedPreferences preferences = getSharedPreferences(preferenceName, MODE_PRIVATE);
        String s = preferences.getString(key, "");
        Type type = new TypeToken<ArrayList<Todo>>(){}.getType();
        todoArrayList.clear();
        todoArrayList.addAll((ArrayList<Todo>)new Gson().fromJson(s, type));
        synchronized (adapter) {
            adapter.notify();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Gson gson = new Gson();
        String json = gson.toJson(todoArrayList);
        SharedPreferences preferences = getSharedPreferences(preferenceName, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, json);
        editor.apply();
    }
}
