package com.example.secminhr.todolisttoday;

import android.content.Context;

import java.io.Serializable;
import java.util.Date;

public class Todo implements Serializable {

    private String title;
    private Date date;

    public Todo(Context context, String title, Date date) {
        this.title = title;
        this.date = date;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public Date getDate() {
        return date;
    }
}
