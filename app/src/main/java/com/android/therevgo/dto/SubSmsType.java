package com.android.therevgo.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by shubham on 8/3/17.
 */

public class SubSmsType {

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
        @SerializedName("product_id")
        public int product_id;
        @SerializedName("sub_category_name")
        public String sub_category_name;
        @SerializedName("status")
        public String status;
        @SerializedName("option_id")
        public int option_id;
    }
}
