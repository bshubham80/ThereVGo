package com.android.therevgo.activities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.android.therevgo.R;
import com.android.therevgo.dto.BusinessProfileModel;
import com.android.therevgo.fragments.WebViewFragment;
import com.android.therevgo.fragments.listing.BusinessContactInfoFragment;
import com.android.therevgo.fragments.listing.BusinessListViewFragment;
import com.android.therevgo.utility.PrefManager;

public class ContainerActivity extends AppCompatActivity {

    // the fragment initialization parameters,
    public static final String ARG_FRAGMENT = "fragment";
    public static final String ARG_TITLE = "title";
    public static final String ARG_URL = "url";
    public static final String ARG_FORCE_FINISH = "force_finish";

    public static final int LISTING_CONTAINER = 1;
    public static final int LISTING_LISTVIEW = 2;
    public static final int LISTING_FORM = 3;
    public static final int WEBVIEW = 4;

    // used to hold the url if current fragment is using webView
    private String mWebUrl ;

    // used if you have just finish the activity
    private boolean forceFinish = false ;

    private ActionBar actionBar ;

    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        PrefManager prefManager = PrefManager.getInstance(this);
        actionBar = getSupportActionBar();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            forceFinish = bundle.getBoolean(ARG_FORCE_FINISH, false);
            mWebUrl = bundle.getString(ARG_URL, null);

            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                if (bundle.getString(ARG_TITLE, null) == null) {
                    actionBar.setIcon(R.drawable.logo);
                    actionBar.setDisplayShowTitleEnabled(false);
                } else {
                    setTitle(bundle.getString(ARG_TITLE));
                }
            }

            switch (bundle.getInt(ARG_FRAGMENT)) {
                case LISTING_LISTVIEW:
                    attachFragment(BusinessListViewFragment.newInstance("",""),
                                                            BusinessListViewFragment.TAG);
                break;

                case LISTING_FORM:
                    BusinessProfileModel.ListModel model = (BusinessProfileModel.ListModel)
                                                                bundle.getSerializable("data");
                    attachFragment(BusinessContactInfoFragment.newInstance(model),
                                                                BusinessContactInfoFragment.TAG);
                break;

                case WEBVIEW:
                    String url = bundle.getString(ARG_URL, null);
                    attachFragment(WebViewFragment.newInstance(url),
                            WebViewFragment.TAG);
                break;
            }

        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!forceFinish) {
            int count = fragmentManager.getBackStackEntryCount();
            if (count > 1) {
                fragmentManager.popBackStack();
            } else {
                finish();
            }
        } else {
            finish();
        }
    }

    public void attachFragment(Fragment fragment, String backStackEntry){
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.activity_fragmentContainer, fragment);
        transaction.addToBackStack(backStackEntry);
        transaction.commit();
    }
    
    public void setTitle(String title) {
        actionBar.setTitle(title);
    }
}
