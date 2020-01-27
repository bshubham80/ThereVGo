package com.client.therevgo.services.utility;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.client.therevgo.services.fragments.sms.SendSmsFragment;
import com.client.therevgo.services.networks.HttpConnection;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Created by shubham on 6/4/17.
 */

public class Utils {

    public static String BALANCE_SMS = "0";

    public static final String IMAGE_URL_PREFIX = "http://www.therevgo.in/product_img/";

    private static final Utils ourInstance = new Utils();

    public static Utils getInstance() {
        return ourInstance;
    }

    private Utils() {
    }

    /**
     * The {@link ArrayList} that will hold the array of number
     * that will use when send sms api called.
     */
    public ArrayList<String> phoneList = new ArrayList<>();

    /**
     * Method check the internet Connectivity Available on device
     * @param context for getting system internet connectivity service
     * @return true if available otherwise false
     */
    public boolean isInternetAvailable(Context context){
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    /**
     * To hide the system keyboard from the screen.
     * @param context to get system input method service
     */
    public void hideKeyboard(Context context) {
        InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        // check if no view has focus:
        View v = ((Activity) context).getCurrentFocus();
        if (v == null)
            return;

        inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    /**
     * Method trim the white spaces from starting and ending from string
     * @param string that contain whitespace
     * @return removed whitespaces String
     */
    public String removeWhitSpace(String string){
        if(string.contains(" ")){
            return string.trim() ;
        }
        return string;
    }

    /**
     * Generic method to display a pop up over the screen
     * @param context that will used to create views.
     * @param title that will hold the dialog title
     * @param errorMessage message that will display in pop-up
     */
    public void displayAlert(Context context, String title, String errorMessage) {
        if(context != null) {
            AlertDialog.Builder dlgAlert = new AlertDialog.Builder(context);
            TextView myMsg = new TextView(context);
            //by shubham 20-apr-2016
            errorMessage = errorMessage.replaceAll("[|?*<\\\":>+\\\\[\\\\]/'\\[\\]]", " ");
            errorMessage = errorMessage.substring(0, 1).toUpperCase() + errorMessage.substring(1);
            myMsg.setText(errorMessage);
            myMsg.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            myMsg.setPadding(10, 10, 10, 10);
            myMsg.setTextColor(Color.parseColor("#000000"));
            myMsg.setGravity(Gravity.CENTER_HORIZONTAL);

            dlgAlert.setView(myMsg);
            if (!title.trim().isEmpty()) {
                dlgAlert.setTitle(title);
            }
            dlgAlert.setCancelable(true);
            dlgAlert.setPositiveButton("OK", null);
            dlgAlert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            AlertDialog dialog = dlgAlert.create();
            dialog.show();
        }
    }

    //Strings Operations
    public boolean validateEmail(Activity activity, EditText editText, String ErrorMsg) {
        String email = editText.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            editText.setError(ErrorMsg);
            requestFocus(activity ,editText);
            return false;
        }

        return true;
    }

    //Strings Operations
    public boolean validateMobile(Activity activity, EditText editText, String ErrorMsg) {
        String email = editText.getText().toString().trim();

        if (email.isEmpty() || !isValidMobile(email)) {
            editText.setError(ErrorMsg);
            requestFocus(activity ,editText);
            return false;
        }

        return true;
    }

    
    public boolean validateString(String str, String expr) {
        return Pattern.compile(expr).matcher(str).matches();
    }

    public boolean validateView(Activity activity, EditText editText, String msg) {
        if (editText.getText().toString().trim().isEmpty()) {
            editText.setError(msg);
            requestFocus(activity ,editText);
            return false;
        }
        return true;
    }

    private boolean isValidMobile(String email) {
        return !TextUtils.isEmpty(email) && Patterns.PHONE.matcher(email).matches() && Pattern.matches("[0-9]{10}", email);
    }

    private boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void requestFocus(Activity activity, View view) {
        if (view.requestFocus()) {
            activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    public static void getTotalBalanceSms(String username,String password ){
        String URL = "http://sms.therevgo.in/vendorsms/CheckBalance.aspx?user="+username+"&password="+password ;
        new GetBalanceSms().execute(URL);
    }

    //
    public void makeCall(Context context) {
        try {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:+919467722693"));
            context.startActivity(intent);
        } catch(Exception e){
            Log.i("makeCall", e.toString());
            Toast.makeText(context, "Please install dialer", Toast.LENGTH_SHORT).show();
        }
    }

    public void makeCall(Context context, @NonNull String number) {
        try {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:"+number));
            context.startActivity(intent);
        } catch(Exception e){
            Log.i("makeCall", e.toString());
            Toast.makeText(context, "Please install dialer", Toast.LENGTH_SHORT).show();
        }
    }

    public static void CopyStream(InputStream is, OutputStream os) throws IOException {
        byte[] buffer = new byte[1024]; // Adjust if you want
        int bytesRead;
        while ((bytesRead = is.read(buffer)) != -1)
        {
            os.write(buffer, 0, bytesRead);
        }
    }

    private static class GetBalanceSms extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null ;
            String responseFromServer = null ;
            try {
                Log.i("Request",params[0]);
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                /* for Get request */
                connection.setRequestMethod("GET");
                int responseCode = connection.getResponseCode();

                if(responseCode == HttpURLConnection.HTTP_OK) {
                    responseFromServer = HttpConnection.getString(new BufferedInputStream(connection.getInputStream()));
                    Log.i("Response",responseFromServer);
                }

            } catch (IOException e) {
                e.printStackTrace();
                return null ;
            }
            return responseFromServer;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s != null) {
                if(s.contains(":") && s.contains("|")) {
                    int startPos = s.indexOf("#")+1;//indexOf(":");
                    int endpos = s.length();//indexOf("|");

                    BALANCE_SMS = s.substring(startPos, endpos).replace("|"," ");

                } else {
                    BALANCE_SMS = "0";
                }
                if (SendSmsFragment.balance != null) {
                    SendSmsFragment.balance.setText("Balance SMS:" + BALANCE_SMS);
                }
            }
        }
    }
}
