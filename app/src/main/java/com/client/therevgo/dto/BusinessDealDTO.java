package com.client.therevgo.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by shubham on 29/1/17.
 */

public class BusinessDealDTO {

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
        @SerializedName("deals_name")
        public String deals_name;
        @SerializedName("status")
        public String status;
        @SerializedName("creation_date")
        public String creation_date;
        @SerializedName("created_by")
        public int created_by;
        @SerializedName("modification_date")
        public String modification_date;
        @SerializedName("modified_by")
        public int modified_by;
        @SerializedName("con_id")
        public int con_id;
    }
}
