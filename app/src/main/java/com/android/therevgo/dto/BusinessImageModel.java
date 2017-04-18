package com.android.therevgo.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by shubham on 22/2/17.
 */
public class BusinessImageModel {

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
        @SerializedName("userid")
        public int userid;
        @SerializedName("image_name")
        public String image_name;
        @SerializedName("status")
        public String status;
        @SerializedName("creation_date")
        public String creation_date;
        @SerializedName("modified_by")
        public int modified_by;
        @SerializedName("created_by")
        public int created_by;
        @SerializedName("modification_date")
        public String modification_date;
        @SerializedName("con_id")
        public int con_id;
    }
}
