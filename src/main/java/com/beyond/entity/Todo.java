package com.beyond.entity;

import java.util.Date;

public class Todo extends Document {

    private Date remindDate;

    @Override
    public String getType() {
        return "todo";
    }

    public Date getRemindDate() {
        return remindDate;
    }

    public void setRemindDate(Date remindDate) {
        this.remindDate = remindDate;
    }
}
