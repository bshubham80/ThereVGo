package com.android.therevgo.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by shubham on 22/4/17.
 */

public class StateModel {

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
        @SerializedName("ST_CODE")
        public String ST_CODE;
        @SerializedName("ST_NAME")
        public String ST_NAME;
        @SerializedName("Country_Code")
        public String Country_Code;
    }
}
