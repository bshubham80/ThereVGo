package com.client.therevgo.dto;

/**
 * Created by shubham on 13/9/16.
 */
public class DialogBean {
    String title;
    int iconResourceID;

    public DialogBean(String title, int iconResourceID) {
        this.title = title;
        this.iconResourceID = iconResourceID;
    }

    public String getTitle() {
        return title;
    }

    public int getIconResourceID() {
        return iconResourceID;
    }
}
