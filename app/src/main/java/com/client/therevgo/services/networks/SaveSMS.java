package com.client.therevgo.services.networks;

import android.content.Context;
import android.util.Log;

import com.client.therevgo.services.constants.Config;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by shubham on 2/12/16.
 */

public class SaveSMS implements ResponseListener {

    private String message ;
    private Context context ;
    private String user_id;

    public SaveSMS(Context context, String message, String user_id) {
        this.message = message;
        this.context = context ;
        this.user_id = user_id;

        genarateSaveRequest();
    }

    private void genarateSaveRequest(){
        String URL =  Config.DOMAIN+"api/msgsave/SMSINS" ;
        HttpConnection.RequestPost(URL,getRequestParam(),SaveSMS.this);
    }

    private ArrayList<NameValuePair> getRequestParam(){
        ArrayList<NameValuePair> map = new ArrayList<>();
        map.add(
                 new BasicNameValuePair("userid", user_id)
               );
        map.add( new BasicNameValuePair("msg", message));

        return map;
    }

    @Override
    public void onResponse(int Statuscode, JSONObject jsonObject)  {
        try {
            if(!jsonObject.isNull("ErrorMessage")) {
                Log.i("SaveResponse",jsonObject.getString("ErrorMessage"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(String msg) {

    }
}
