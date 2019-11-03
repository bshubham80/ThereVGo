package com.client.therevgo.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.therevgo.R;
import com.client.therevgo.base.BaseFragment;
import com.client.therevgo.constants.Config;
import com.client.therevgo.dto.LoginModel;
import com.client.therevgo.fragments.followUp.ShowFollowUpContainerFragment;
import com.client.therevgo.fragments.inquiry.CustomerInquiry;
import com.client.therevgo.fragments.listing.BusinessListingFragment;
import com.client.therevgo.fragments.followUp.CreateFollowUpFragment;
import com.client.therevgo.fragments.services.ServiceContainerFragment;
import com.client.therevgo.fragments.sms.SmsContainerFragment;
import com.client.therevgo.networks.HttpConnection;
import com.client.therevgo.networks.ResponseListener;
import com.client.therevgo.utility.PrefManager;
import com.client.therevgo.utility.Utils;
import com.google.gson.Gson;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener, ResponseListener {

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

    private DrawerLayout drawer;
    private Toolbar toolbar;
    private FragmentManager fragmentManager;
    private boolean doublePress;

    private Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        PrefManager prefManager = PrefManager.getInstance(this);


        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        TextView txt_name = (TextView) headerView.findViewById(R.id.txt_header_person_name);
        TextView txt_email = (TextView) headerView.findViewById(R.id.txt_header_person_email);

        String name = (String) prefManager
                .getDataFromPreference(PrefManager.Key.USER_NAME, PrefManager.Type.TYPE_STRING);
        String email = (String) prefManager
                .getDataFromPreference(PrefManager.Key.USER_EMAIL, PrefManager.Type.TYPE_STRING);
        String password = (String) prefManager
                .getDataFromPreference(PrefManager.Key.USER_PASSWORD, PrefManager.Type.TYPE_STRING);

        txt_name.setText(name);
        txt_email.setText(email);

        // attachFragment(BusinessListingFragment.newInstance("", ""), BusinessListingFragment.TAG);
        attachFragment(new ServiceContainerFragment(), ServiceContainerFragment.TAG);


        String URL = Config.DOMAIN + "api/SignUp?" +
                "emailid=" + Utils.getInstance().removeWhitSpace(email) +
                "&password=" + password;

        HttpConnection.RequestGet(URL, MainActivity.this);
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            int count = fragmentManager.getBackStackEntryCount();
            if (count > 1) {
                /*if (fragmentManager.getBackStackEntryAt(count - 1).getName()
                        .equals(ShowFollowUpContainerFragment.TAG)){
                    ShowFollowUpContainerFragment fragment =
                            (ShowFollowUpContainerFragment) fragmentManager.findFragmentByTag(ShowFollowUpContainerFragment.TAG);

                    fragment.onPause();
                }*/
                fragmentManager.popBackStack();
            } else {
                closeApp();
            }
        }
    }

    private void closeApp() {
        if (doublePress) {
            finish();
        }
        doublePress = true;
        Toast.makeText(this, "Please press back again to exit", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doublePress = false;
            }
        }, 2000);
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if ( id == R.id.action_create) {
            return false;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        BaseFragment fragment = null;
        String backStack = null;

        if (id == R.id.nav_customer_inquiry) {
            fragment = new CustomerInquiry();
            backStack = CustomerInquiry.TAG;

        } else if (id == R.id.nav_show_follow_up) {
            fragment = new ShowFollowUpContainerFragment();
            backStack = ShowFollowUpContainerFragment.TAG;

        } else if (id == R.id.nav_create_follow_up) {
            fragment = new CreateFollowUpFragment();
            backStack = CreateFollowUpFragment.TAG;

        } else if (id == R.id.nav_send_sms) {
            fragment = new SmsContainerFragment();
            backStack = SmsContainerFragment.TAG;

        }
        else if (id == R.id.nav_services) {
            fragment = BusinessListingFragment.newInstance("", "");
            backStack = BusinessListingFragment.TAG;

        }
        else if (id == R.id.nav_call) {
            Utils.getInstance().makeCall(this);

        } else if (id == R.id.nav_aboutus) {
            attachWebViewFragment("About Us", "http://therevgo.in/maboutus.html");

        } else if (id == R.id.nav_privacy) {
            attachWebViewFragment("Privacy Policy", "http://therevgo.in/mPrivacy_Policy.html");

        } else if (id == R.id.nav_terms) {
            attachWebViewFragment("Terms of Use", "http://www.therevgo.in/mTerm_use.html");

        } else if (id == R.id.nav_logout) {
            // clear all preference
            PrefManager.getInstance(this).clearPreference();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        // close drawer
        drawer.closeDrawer(GravityCompat.START);

        // attach fragment in container
        if (fragment != null && backStack != null) {
            attachFragment(fragment, backStack);
        }
        return true;
    }

    public void attachFragment(@NonNull Fragment fragment, @NonNull String backStackEntry) {
        fragmentManager = getSupportFragmentManager();

        Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragmentContainer);
        if (currentFragment != null && currentFragment.getClass().equals(fragment.getClass()))
            return;

        FragmentTransaction transaction = fragmentManager.beginTransaction();

        transaction.replace(R.id.fragmentContainer, fragment);
//        if (fragment.getTitle() != null) {
//            setTitle(fragment.getTitle());
//            // getSupportActionBar().setTitle(fragment.getTitle());
//        }
        if (fragmentManager.getBackStackEntryCount() > 1){
            fragmentManager.popBackStack();
        }
        transaction.addToBackStack(backStackEntry);
        transaction.commit();
    }

    public void attachWebViewFragment(String title, String url) {

        Bundle bundle = new Bundle();
        bundle.putString(ContainerActivity.ARG_TITLE, title);
        bundle.putString(ContainerActivity.ARG_URL, url);
        bundle.putInt(ContainerActivity.ARG_FRAGMENT, ContainerActivity.WEBVIEW);

        Intent intent = new Intent(this, ContainerActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public FragmentManager getMyFragmentManager(){
        return fragmentManager;
    }

    @Override
    public void onResponse(int statusCode, final JSONObject jsonObject) {
        handler.post(new Runnable() {
            @Override
            public void run() {
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
                    PrefManager.getInstance(MainActivity.this).setDataInPreference(preferenceKey, preferenceValues);
                }
            }
        });
    }

    @Override
    public void onError(String msg) {

    }
}
