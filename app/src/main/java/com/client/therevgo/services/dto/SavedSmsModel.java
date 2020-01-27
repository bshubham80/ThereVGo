package com.client.therevgo.services.dto;

/**
 * Created by shubham on 2/12/16.
 */

public class SavedSmsModel {
    private int id;
    private String msg ;

    public SavedSmsModel(int id, String msg) {
        this.id = id;
        this.msg = msg;
    }

    public int getId() {
        return id;
    }

    public String getMsg() {
        return msg;
    }
}
