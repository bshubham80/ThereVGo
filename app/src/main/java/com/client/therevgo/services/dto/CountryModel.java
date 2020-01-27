package com.client.therevgo.services.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by shubham on 22/4/17.
 */

public class CountryModel {

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
        @SerializedName("CountryCode")
        public String CountryCode;
        @SerializedName("CountryISD")
        public String CountryISD;
        @SerializedName("CountryName")
        public String CountryName;
    }
}
