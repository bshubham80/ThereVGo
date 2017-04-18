package com.android.therevgo.database;

/**
 * Created by shubham on 7/11/16.
 */

public class GroupBean {
    private int id ;
    private String name ;

    public GroupBean(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
