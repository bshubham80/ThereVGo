package com.client.therevgo.activities;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.therevgo.R;
import com.client.therevgo.constants.Config;
import com.client.therevgo.dto.LoginModel;
import com.client.therevgo.networks.HttpConnection;
import com.client.therevgo.networks.ResponseListener;
import com.client.therevgo.utility.PrefManager;
import com.client.therevgo.utility.Utils;
import com.google.gson.Gson;

import org.json.JSONObject;

import static android.widget.Toast.LENGTH_SHORT;

public class LoginActivity extends AppCompatActivity implements ResponseListener {

    private static final long SPLASH_LENGTH = 2000;

    private final String[] preferenceKey = {
            PrefManager.Key.USER_ID,
            PrefManager.Key.USER_NAME,
            PrefManager.Key.USER_EMAIL,
            PrefManager.Key.USER_MOBILE,
            PrefManager.Key.USER_MSG_ID,
            PrefManager.Key.USER_PASSWORD,
            PrefManager.Key.SMS_TYPE,
            PrefManager.Key.MESSAGE_UNIQUE_CODE,
    };

    private boolean showAnimation = true;

    private ProgressDialog dialog;
    private Handler handler = new Handler();
    private ImageView logo;
    private LinearLayout login;
    private EditText inputEmail, inputPassword;

    private Utils utils = Utils.getInstance();
    private PrefManager prefManager;

    private boolean isFirst = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // initialize the prefManager obj to get data from preference
        prefManager = PrefManager.getInstance(this);

        logo = (ImageView) findViewById(R.id.logo);
        login = (LinearLayout) findViewById(R.id.fragmentContainer);

        if (getSupportActionBar() != null)
            getSupportActionBar().hide();

        inputEmail = (EditText) findViewById(R.id.input_email);
        inputPassword = (EditText) findViewById(R.id.input_password);

        TextView forgot = (TextView) findViewById(R.id.forgot);
        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showforgotDialog();
            }
        });

        Button createAccount = (Button) findViewById(R.id.create_Account);
        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSignUp(v);
            }
        });

        Button btnSignUp = (Button) findViewById(R.id.btn_login);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm();
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus && isFirst) {
            isFirst = false;
            if (utils.isInternetAvailable(this)) {
                String user_id = (String)
                        prefManager.getDataFromPreference(PrefManager.Key.USER_ID, PrefManager.Type.TYPE_STRING);

                if (!user_id.equals(PrefManager.DEFAULT_STRING) && user_id.length() > 0) {
                    changeFragment();
                } else {
//                    handler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            if (showAnimation) {
//                                startAnimation();
//                                showAnimation = false;
//                            }
//                        }
//                    }, SPLASH_LENGTH);
                }
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("An error occurred while launching app. Please check your internet connection and try again!");
                builder.setPositiveButton("OK", null);
                builder.setCancelable(true);


                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                Dialog dialog = builder.create();
                dialog.show();
            }
        }
    }

    private void startAnimation() {
        int height = logo.getHeight();
        float Y_Cor = logo.getY();

        float layoutY = login.getY();

        ObjectAnimator translate = ObjectAnimator.ofFloat(logo, View.Y, Y_Cor, layoutY - height);
        translate.setDuration(1000);

        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(login, View.ALPHA, 0f, 1f);
        translate.setDuration(1000);

        AnimatorSet set = new AnimatorSet();
        set.play(translate).before(fadeIn);
        set.start();
    }

    //Validating form
    private void submitForm() {
        if (!utils.validateEmail(this, inputEmail, getString(R.string.err_msg_email))) {
            return;
        }

        if (!utils.validateView(this, inputPassword, getString(R.string.err_msg_password))) {
            return;
        }

        if (utils.isInternetAvailable(this))
            sendRequest();
        else
            Toast.makeText(this, getString(R.string.no_internet_available), LENGTH_SHORT).show();
    }

    private void sendRequest() {
        String URL = Config.DOMAIN + "api/SignUp?" +
                "emailid=" + utils.removeWhitSpace(inputEmail.getText().toString()) +
                "&password=" + inputPassword.getText().toString();

        dialog = new ProgressDialog(this);
        dialog.setMessage("Please Wait...");
        dialog.setCancelable(false);
        dialog.show();

        utils.hideKeyboard(this);
        HttpConnection.RequestGet(URL, LoginActivity.this);
    }

    public void showSignUp(View view) {
        Intent intent = new Intent(this, SignUpActivity.class);
//        ActivityOptions opt;
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//            opt = ActivityOptions.makeSceneTransitionAnimation(
//                    this,
//                    new Pair<View, String>(logo, getResources().getString(R.string.logo))
//            );
//            startActivity(intent, opt.toBundle());
//            finish();
//        } else {
            startActivity(intent);
            finish();
//        }
    }

    @Override
    public void onResponse(final int statusCode, final JSONObject jsonObject) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();

                Gson gson = new Gson();
                final LoginModel loginModel = gson.fromJson(jsonObject.toString(), LoginModel.class);
                if (loginModel.status) {
                    LoginModel.Data data = loginModel.Data.get(0);
                    String[] preferenceValues = {
                            data.id + "",
                            data.name,
                            data.email_id,
                            data.mobile_no,
                            data.msg_id,
                            data.password,
                            data.sms_type + "",
                            data.mesg_unq_code
                    };
                    prefManager.setDataInPreference(preferenceKey, preferenceValues);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            changeFragment();
                        }
                    });
                } else {
                    Toast.makeText(LoginActivity.this, "Invalid Email/Password", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onError(final String msg) {
        dialog.dismiss();
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(LoginActivity.this, msg, LENGTH_SHORT).show();
            }
        });
    }

    private void changeFragment() {
        Intent intent = new Intent(this, MainActivity.class);//DashBoard.class);
        startActivity(intent);
        finish();
    }
}
