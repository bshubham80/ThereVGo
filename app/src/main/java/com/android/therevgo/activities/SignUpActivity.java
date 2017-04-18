package com.android.therevgo.activities;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.therevgo.R;
import com.android.therevgo.constants.Config;
import com.android.therevgo.dto.LoginModel;
import com.android.therevgo.networks.HttpConnection;
import com.android.therevgo.networks.ResponseListener;
import com.android.therevgo.utility.PrefManager;
import com.android.therevgo.utility.Utils;
import com.google.gson.Gson;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SignUpActivity extends AppCompatActivity implements ResponseListener {

    private final String[] preferenceKey = {
            PrefManager.Key.USER_ID,
            PrefManager.Key.USER_NAME,
            PrefManager.Key.USER_EMAIL,
            PrefManager.Key.USER_MOBILE,
            PrefManager.Key.USER_MSG_ID,
            PrefManager.Key.USER_PASSWORD,
            PrefManager.Key.SMS_TYPE,
    };

    private Handler handler = new Handler();

    private EditText inputName, inputContact ,inputEmail, inputPassword;
    private ImageView logo ;
    private ProgressDialog dialog ;

    private Utils utils = Utils.getInstance();
    private PrefManager prefManager ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        if(getSupportActionBar() != null)
            getSupportActionBar().hide();

        prefManager = PrefManager.getInstance(this);

        logo = (ImageView) findViewById(R.id.sign_logo);
        inputEmail = (EditText) findViewById(R.id.et_sign_email);
        inputName = (EditText) findViewById(R.id.et_sign_name);
        inputContact = (EditText) findViewById(R.id.et_sign_contact);
        inputPassword = (EditText) findViewById(R.id.et_sign_password);

        //inputEmail.setText("a@a.a");
        //inputPassword.setText("a@a.a");

        TextView sign_login_txt = (TextView) findViewById(R.id.sign_login_txt);
        sign_login_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogin(v);
            }
        });
        Button btnSignUp = (Button) findViewById(R.id.btn_sign_up);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm();
            }
        });
    }

    public void showLogin(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        ActivityOptions opt;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            opt = ActivityOptions.makeSceneTransitionAnimation(
                    this,
                    new Pair<View, String>(logo, getResources().getString(R.string.logo))
            );
            startActivity(intent, opt.toBundle());
        } else {
            startActivity(intent);
        }
        finish();
    }

    private ArrayList<NameValuePair> getRequestParam(){
        ArrayList<NameValuePair> map = new ArrayList<>();
        //map.add("id","");
        map.add(new BasicNameValuePair("name",utils.removeWhitSpace(inputName.getText().toString())));
        map.add(new BasicNameValuePair("email_id",utils.removeWhitSpace(inputEmail.getText().toString())));
        map.add(new BasicNameValuePair("mobile_no",utils.removeWhitSpace(inputContact.getText().toString())));
        map.add(new BasicNameValuePair("password",utils.removeWhitSpace(inputPassword.getText().toString())));
        map.add(new BasicNameValuePair("user_type","m"));

        return map;
    }
    /**
     * Validating form
     */
    private void submitForm() {
        if (!utils.validateEmail(this,inputEmail,getString(R.string.err_msg_email))) {
            return;
        }

        if (!utils.validateView(this,inputPassword,getString(R.string.err_msg_password)) ||
                !utils.validateView(this,inputName,getString(R.string.err_msg_name)) ||
                !utils.validateView(this,inputContact, getString(R.string.err_msg_contact))) {
            return;
        }

        if(utils.isInternetAvailable(this))
            sendRequest();
        else
            Toast.makeText(this, getString(R.string.no_internet_available), Toast.LENGTH_SHORT).show();

    }

    private void sendRequest() {
        dialog = new ProgressDialog(this);
        dialog.setMessage("Please Wait...");
        dialog.show();

        String URL =  Config.DOMAIN+"api/SignUp" ;
        HttpConnection.RequestPost(URL,getRequestParam(),SignUpActivity.this);
    }

    @Override
    public void onResponse(int statusCode, JSONObject jsonObject) {
        dialog.dismiss();
        if(statusCode == 200) {
            try {
                final String error = jsonObject.getString("error");
                if(jsonObject.isNull("error")){
                    // chagefragment();// check Thiss Status "message": "Record Inserted Successfully."
                    final String message = jsonObject.getString("message");
                    if( message.equals("Record Inserted Successfully.")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                new AfterRegister(SignUpActivity.this, inputEmail.getText().toString(),
                                        inputPassword.getText().toString());
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(SignUpActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                } else{
                    SignUpActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(SignUpActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } catch (JSONException e) {
                // e.printStackTrace();
            }
        }
    }

    @Override
    public void onError(final String msg) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
                Toast.makeText(SignUpActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private class AfterRegister implements ResponseListener {

        private String email, password;
        private Context context;
        private ProgressDialog dialog;

        AfterRegister(Context context, String email, String password) {
            this.context = context;
            this.email = email;
            this.password = password;
            login();
        }

        private void login() {
            String URL = Config.DOMAIN + "api/SignUp?" +
                    "emailid=" + email +
                    "&password=" + password;

            dialog = new ProgressDialog(context);
            dialog.setMessage("Please Wait");
            dialog.show();
            HttpConnection.RequestGet(URL, AfterRegister.this);
        }

        @Override
        public void onResponse(int statusCode, final JSONObject jsonObject) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();

                    Gson gson = new Gson();
                    final LoginModel loginModel = gson.fromJson(jsonObject.toString(), LoginModel.class);
                    if (loginModel.status) {
                        LoginModel.Data data = loginModel.Data.get(0);
                        String[] preferenceValues = {
                                data.id+"",
                                data.name,
                                data.email_id,
                                data.mobile_no,
                                data.msg_id,
                                data.password,
                                data.sms_type+""
                        };
                        prefManager.setDataInPreference(preferenceKey, preferenceValues);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                changeFragment();
                            }
                        });
                    } else {
                        Toast.makeText(SignUpActivity.this, loginModel.message, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        @Override
        public void onError(final String msg) {
            dialog.dismiss();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(SignUpActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void changeFragment() {
            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

}
