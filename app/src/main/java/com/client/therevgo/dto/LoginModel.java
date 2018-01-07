package com.client.therevgo.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Model class that will hold the data come from server and
 * store in corresponding variable.
 */

public class LoginModel {

    @SerializedName("status")
    public boolean status;
    @SerializedName("message")
    public String message;
    @SerializedName("error")
    public String error;
    @SerializedName("Data")
    public List<Data> Data;

    public static class Data {
        @SerializedName("id")
        public int id;
        @SerializedName("name")
        public String name;
        @SerializedName("email_id")
        public String email_id;
        @SerializedName("mobile_no")
        public String mobile_no;
        @SerializedName("password")
        public String password;
        @SerializedName("status")
        public String status;
        @SerializedName("creation_date")
        public String creation_date;
        @SerializedName("user_type")
        public String user_type;
        @SerializedName("msg_id")
        public String msg_id;
        @SerializedName("sms_type")
        public int sms_type;
    }
}
