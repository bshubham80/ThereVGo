package com.client.therevgo.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by shubham on 29/1/17.
 */

public class BusinessInfoModel {

    @SerializedName("status")
    public Boolean status;
    @SerializedName("message")
    public String message;
    @SerializedName("error")
    public String error;
    @SerializedName("Data")
    public List<Data> Data;

    public static class Data {
        @SerializedName("id")
        public Integer id;
        @SerializedName("userid")
        public Integer userid;
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
        @SerializedName("state_id")
        public String state_id;
        @SerializedName("state")
        public String state;
        @SerializedName("country_id")
        public String country_id;
        @SerializedName("country")
        public String country;
        @SerializedName("creation_date")
        public String creation_date;
        @SerializedName("created_by")
        public Integer created_by;
        @SerializedName("modification_date")
        public String modification_date;
        @SerializedName("modified_by")
        public Integer modified_by;
        @SerializedName("status")
        public String status;
        @SerializedName("categoryid")
        public Integer categoryid;
        @SerializedName("categoryName")
        public String categoryName;
        @SerializedName("subcategoryid")
        public Integer subcategoryid;
        @SerializedName("sub_category_name")
        public String sub_category_name;
        @SerializedName("option_id")
        public String option_id;
        @SerializedName("optionName")
        public String optionName;
    }
}
