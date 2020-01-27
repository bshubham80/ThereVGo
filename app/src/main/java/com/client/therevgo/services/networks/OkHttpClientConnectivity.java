package com.client.therevgo.services.networks;

import android.util.Log;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by PC10 on 12/22/2015.
 */
public class OkHttpClientConnectivity {

    private static final String TAG = "OkHttpClient";

    public static JSONObject doGetJSONOBJECT(HashMap<String, String> param, String url) {
        JSONObject result = null;
        String response;
        Set keys = param.keySet();

        int count = 0;
        for (Iterator i = keys.iterator(); i.hasNext(); ) {
            count++;
            String key = (String) i.next();
            String value = (String) param.get(key);
            if (count == param.size()) {
                Log.e("Key", key + "");
                Log.e("Value", value + "");
                url += key + "=" + URLEncoder.encode(value);

            } else {
                Log.e("Key", key + "");
                Log.e("Value", value + "");
                url += key + "=" + URLEncoder.encode(value) + "&";
            }

        }
        Log.e("OkHttpClient URL", url);
        OkHttpClient client = new OkHttpClient();

        Request request;
        try {
            request = new Request.Builder()
                    .url(url)
                    .build();

        } catch (IllegalArgumentException e) {
            JSONObject jsonObject = new JSONObject();

            return jsonObject;
        }


        Response responseClient = null;
        try {


            responseClient = client.newCall(request).execute();
            response = responseClient.body().string();
            result = new JSONObject(response);
            Log.e("OkHttpClient Response", response + "");
        } catch (Exception e) {
            JSONObject jsonObject = new JSONObject();

            return jsonObject;
        }

        return result;

    }


    public static JSONArray doGetJSONARRAY(HashMap<String, String> param, String url) {
        JSONArray result = null;
        String response;
        Set keys = param.keySet();

        int count = 0;
        for (Iterator i = keys.iterator(); i.hasNext(); ) {
            count++;
            String key = (String) i.next();
            String value = (String) param.get(key);
            if (count == param.size()) {
                Log.e("Key", key + "");
                Log.e("Value", value + "");
                url += key + "=" + URLEncoder.encode(value);

            } else {
                Log.e("Key", key + "");
                Log.e("Value", value + "");

                url += key + "=" + URLEncoder.encode(value) + "&";
            }

        }
        Log.e("URL", url);
        OkHttpClient client = new OkHttpClient();

        Request request;
        try {
            request = new Request.Builder()
                    .url(url)
                    .build();

        } catch (IllegalArgumentException e) {
            JSONArray array = new JSONArray();
            JSONObject jsonObject = new JSONObject();
            array.put(jsonObject);
            Log.e("URL", "1");

            return array;
        }


        Response responseClient = null;
        try {
            Log.e("URL", "2");


            responseClient = client.newCall(request).execute();
            //<string xmlns="http://tempuri.org/">


            response = responseClient.body().string();
            Log.e("URL", responseClient.body().string());
            Log.e("response", response + "==============");
            //Log.e("JSON ARRAY", new JSONArray(response).length() + "");
            result = new JSONArray(response);
            Log.e("URL", result.toString());
        } catch (Exception e) {
            JSONArray array = new JSONArray();
            JSONObject jsonObject = new JSONObject();
            array.put(jsonObject);
            Log.e("URL", e.getMessage());
            Log.e("URL", String.valueOf(result));

            return array;
        }

        return result;

    }


    public static JSONObject doPostJSONOBJECT(HashMap<String, String> data, String url) {


        try {
            RequestBody requestBody;
            MultipartBuilder mBuilder = new MultipartBuilder().type(MultipartBuilder.FORM);

            if (data != null) {
                for (String key : data.keySet()) {
                    String value = data.get(key);
                    Log.e("OkHttpClient Key Values", key + "=" + value);
                    mBuilder.addFormDataPart(key, value);
                }
            } else {
                mBuilder.addFormDataPart("temp", "temp");
            }

            requestBody = mBuilder.build();
            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();

            OkHttpClient client = new OkHttpClient();
            Response response = client.newCall(request).execute();
            String responseBody = response.body().string();
            url = "";
            Log.e("OkHttpClient Response ", responseBody);
            return new JSONObject(responseBody);

        } catch (UnknownHostException | UnsupportedEncodingException | NullPointerException | JSONException | UnsupportedOperationException e) {

            JSONObject jsonObject = new JSONObject();

            try {
                jsonObject.put("status", "false");
                jsonObject.put("message", e.getLocalizedMessage());
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
            Log.e("", "Error: " + e.getLocalizedMessage());
        } catch (Exception e) {
            e.printStackTrace();
            JSONObject jsonObject = new JSONObject();

            try {
                jsonObject.put("status", "false");
                jsonObject.put("message", e.getLocalizedMessage());
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
            Log.e("", "Other Error: " + e.getLocalizedMessage());
        }
        return null;
    }


    public static JSONObject doPostRequestWithFile(HashMap<String, String> data,
                                                   String url,
                                                   ArrayList<String> imageList,
                                                   String fileParamName) {


        try {
            final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/*");

            RequestBody requestBody;
            MultipartBuilder mBuilder = new MultipartBuilder().type(MultipartBuilder.FORM);

            for (String key : data.keySet()) {
                String value = data.get(key);
                Log.e("OkHttpClientKey Values", key + "-----------------" + value);

                mBuilder.addFormDataPart(key, value);

            }
            for (int i = 0; i < imageList.size(); i++) {
                File f = new File(imageList.get(i));

                Log.e("OkHttpClient" + fileParamName, f.getName() + "===========");
                if (f.exists()) {
                    Log.e("File Exits", "===================");
                    mBuilder.addFormDataPart(fileParamName, f.getName(), RequestBody.create(MEDIA_TYPE_PNG, f));
                }
            }
            requestBody = mBuilder.build();


            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();

            OkHttpClient client = new OkHttpClient();
            Response response = client.newCall(request).execute();


            String result = response.body().string();

            Log.e("OkHttpClient Response", result + "");
            return new JSONObject(result);

        } catch (Exception e) {
            Log.e(TAG, "Error: " + e.getLocalizedMessage());
            JSONObject jsonObject = new JSONObject();

            try {
                jsonObject.put("status", "false");
                jsonObject.put("message", e.getLocalizedMessage());
                return jsonObject;

            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }
        return null;
    }


    // GET ALL  SUBJECTS


}
