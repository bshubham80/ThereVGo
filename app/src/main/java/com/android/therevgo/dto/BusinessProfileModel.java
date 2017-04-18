package com.android.therevgo.dto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by shubham on 9/4/17.
 */

public class BusinessProfileModel {
    
    @SerializedName("status")
    public Boolean status;
    @SerializedName("message")
    public String     message;
    @SerializedName("error")
    public String     error;
    @SerializedName("Data")
    public List<ListModel> Data;
    
    public static class ListModel implements Serializable{
        @SerializedName("userid")
        public Integer    userid;
        @SerializedName("con_id")
        public Integer    con_id;
        @SerializedName("c_name")
        public String c_name;
        @SerializedName("c_email_id")
        public String c_email_id;
        @SerializedName("c_mobile_no")
        public String c_mobile_no;
        @SerializedName("c_landline_no")
        public String c_landline_no;
        @SerializedName("c_fax_no")
        public String c_fax_no;
        @SerializedName("c_area")
        public String c_area;
        @SerializedName("c_city")
        public String c_city;
        @SerializedName("c_pinncode")
        public String c_pinncode;
        @SerializedName("c_state")
        public String c_state;
        @SerializedName("c_state_name")
        public String c_state_name;
        @SerializedName("c_country")
        public String c_country;
        @SerializedName("c_country_name")
        public String c_country_name;
        @SerializedName("c_status")
        public String c_status;
        @SerializedName("c_live_status")
        public String c_live_status;
        @SerializedName("b_comp_name")
        public String b_comp_name;
        @SerializedName("b_company_type")
        public String b_company_type;
        @SerializedName("b_bussiness_name")
        public String b_bussiness_name;
        @SerializedName("b_bussiness_date")
        public String b_bussiness_date;
        @SerializedName("b_bussiness_type")
        public String b_bussiness_type;
        @SerializedName("b_website")
        public String b_website;
        @SerializedName("b_area")
        public String b_area;
        @SerializedName("b_city")
        public String b_city;
        @SerializedName("b_pincode")
        public String b_pincode;
        @SerializedName("b_state")
        public String b_state;
        @SerializedName("b_state_name")
        public String b_state_name;
        @SerializedName("b_country")
        public String b_country;
        @SerializedName("b_country_name")
        public String b_country_name;
        @SerializedName("b_categoryid")
        public String b_categoryid;
        @SerializedName("b_categoryName")
        public String b_categoryName;
        @SerializedName("b_subcategoryid")
        public String b_subcategoryid;
        @SerializedName("b_subcategoryName")
        public String b_subcategoryName;
        @SerializedName("b_option_id")
        public String b_option_id;
        @SerializedName("b_optionName")
        public String b_optionName;
        @SerializedName("image_name")
        public String image_name;
        @SerializedName("key1")
        public String key1;
        @SerializedName("key2")
        public String key2;
        @SerializedName("key3")
        public String key3;
        @SerializedName("key4")
        public String key4;
        @SerializedName("key5")
        public String key5;
        @SerializedName("key6")
        public String key6;
    }
}
