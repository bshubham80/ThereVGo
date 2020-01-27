package com.client.therevgo.services.utility;

import android.content.Context;
import android.content.SharedPreferences;

import com.client.therevgo.services.activities.IntroSlider;

import java.util.Map;

/**
 * Created by shubham on 6/4/17.
 */

public class PrefManager {

    // Shared preferences file name
    private static final String PREF_NAME = "tvg_sharedPreference";

    // shared pref mode
    private static final int PRIVATE_MODE = 0;

    // default value for preference
    public static final String DEFAULT_STRING = "";
    public static final int DEFAULT_INT = 0;
    public static final boolean DEFAULT_BOOLEAN = false;
    
    private static PrefManager ourInstance;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    public static PrefManager getInstance(final Context context) {
        if (ourInstance == null) {
            ourInstance =  new PrefManager(context);
        }
        return ourInstance;
    }

    private PrefManager(final Context ctx) {
        Context context = ctx.getApplicationContext();
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(Key.IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(Key.IS_FIRST_TIME_LAUNCH, true);
    }

    public void setDataInPreference(final String[] keys, final Object[] values){
        if (keys.length != values.length) {
            throw new IllegalAccessError("Keys and values both must have size");
        }

        for (int i = 0; i <= keys.length-1 ; i++) {
            setDataInPreference(keys[i], values[i]);
        }
    }

    public void setDataInPreference(final String key, final Object value){
        if (value instanceof String) {
            editor.putString(key, String.valueOf(value));
        }
        else if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        }
        else if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        }
        else {
            editor.putString(key, String.valueOf(value));
        }
        editor.commit();
    }

    public Object getDataFromPreference(final String key, final int type) {
        if (type == Type.TYPE_STRING) {
            return pref.getString(key, DEFAULT_STRING);
        }
        else if (type == Type.TYPE_BOOLEAN) {
            return pref.getBoolean(key, DEFAULT_BOOLEAN);
        }
        else if (type == Type.TYPE_INT) {
            return pref.getInt(key, DEFAULT_INT);
        }
        else {
            return pref.getString(key, DEFAULT_STRING);
        }
    }

    public void clearPreference(){
        Map<String,?> prefs = pref.getAll();
        for(Map.Entry<String,?> prefToReset : prefs.entrySet()){
            if (!prefToReset.getKey().equals(Key.IS_FIRST_TIME_LAUNCH)) {
                editor.remove(prefToReset.getKey()).commit();
            }
        }
    }

    public static class Key {
        /**
         * isTimeLaunchFlag for {@link IntroSlider}
         */
        public static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";

        public static final String USER_ID = "id";
        public static final String USER_NAME = "name";
        public static final String USER_EMAIL = "email_id";
        public static final String USER_PASSWORD = "password";
        public static final String USER_MOBILE = "mobile_no";
        public static final String USER_MSG_ID = "msg_id";
        public static final String SMS_TYPE = "sms_type";
        public static final String MESSAGE_UNIQUE_CODE = "msg_unique_code";

        public static final String USER_SMS_USERNAME = "sms_username";
        public static final String USER_SMS_PASSWORD = "password";
        public static final String USER_SMS_SENDER_ID = "senderID";
    }

    public static class Type {
        public static final int TYPE_STRING = 1;
        public static final int TYPE_BOOLEAN = 2;
        public static final int TYPE_INT = 3;
    }

}
