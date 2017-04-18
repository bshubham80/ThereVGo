package com.android.therevgo.library;

import android.content.Context;
import android.util.Log;

import com.android.therevgo.constants.Config;
import com.android.therevgo.networks.HttpConnection;
import com.android.therevgo.networks.ResponseListener;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by shubham on 2/12/16.
 */

public class SaveSmsLib implements ResponseListener {

    private String message ;
    private Context context ;
    private String userID ;

    public SaveSmsLib(Context context, String user_id, String message) {
        this.message = message;
        this.context = context ;
        this.userID = user_id;

        genarateSaveRequest();
    }

    private void genarateSaveRequest(){
        String URL =  Config.DOMAIN+"api/msgsave/SMSINS" ;
        HttpConnection.RequestPost(URL,getRequestParam(),SaveSmsLib.this);
    }

    private ArrayList<NameValuePair> getRequestParam(){
        ArrayList<NameValuePair> map = new ArrayList<>();
        map.add(
                 new BasicNameValuePair("userid", userID)
               );
        map.add( new BasicNameValuePair("msg", message));

        return map;
    }

    @Override
    public void onResponse(int Statuscode, JSONObject jsonObject) {
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
