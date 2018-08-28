package com.beyond.entity;

public class Note extends Document {

    private final String type = "note";

    @Override
    public String getType() {
        return type;
    }
}
