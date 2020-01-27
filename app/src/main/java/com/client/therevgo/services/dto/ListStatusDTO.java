package com.client.therevgo.services.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by shubham on 2/5/17.
 */

public class ListStatusDTO {
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
        @SerializedName("name")
        public String name;
        @SerializedName("email_id")
        public String email_id;
        @SerializedName("mobile_no")
        public String mobile_no;
        @SerializedName("landline_no")
        public String landline_no;
        @SerializedName("fax_no")
        public String fax_no;
        @SerializedName("area")
        public String area;
        @SerializedName("city")
        public String city;
        @SerializedName("pinncode")
        public String pinncode;
        @SerializedName("state")
        public String state;
        @SerializedName("StateName")
        public String StateName;
        @SerializedName("country")
        public String country;
        @SerializedName("countryName")
        public String countryName;
        @SerializedName("creation_date")
        public String creation_date;
        @SerializedName("created_by")
        public int created_by;
        @SerializedName("modification_date")
        public String modification_date;
        @SerializedName("modified_by")
        public int modified_by;
        @SerializedName("status")
        public String status;
        @SerializedName("live_status")
        public String live_status;
        @SerializedName("priority")
        public String priority;
    }
}
