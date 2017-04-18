package com.android.therevgo.dto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by shubham on 29/1/17.
 */

public class BusinessInfoModel {

    @SerializedName("status")
    public boolean status;
    @SerializedName("message")
    public String message;
    @SerializedName("error")
    public String error;
    @SerializedName("Data")
    public List<Data> Data;

    public static class Data implements Serializable {
        @SerializedName("id")
        public int id;
        @SerializedName("userid")
        public int userid;
        @SerializedName("comp_name")
        public String comp_name;
        @SerializedName("company_type")
        public String company_type;
        @SerializedName("bussiness_name")
        public String bussiness_name;
        @SerializedName("bussiness_date")
        public String bussiness_date;
        @SerializedName("bussiness_type")
        public String bussiness_type;
        @SerializedName("website")
        public String website;
        @SerializedName("area")
        public String area;
        @SerializedName("city")
        public String city;
        @SerializedName("pincode")
        public String pincode;
        @SerializedName("state")
        public String state;
        @SerializedName("country")
        public String country;
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
        @SerializedName("categoryid")
        public String categoryid;
        @SerializedName("subcategoryid")
        public String subcategoryid;
        @SerializedName("option_id")
        public String option_id;
    }
}
