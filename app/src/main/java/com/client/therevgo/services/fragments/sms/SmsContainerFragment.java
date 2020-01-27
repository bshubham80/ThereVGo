package com.client.therevgo.services.fragments.sms;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.therevgo.R;
import com.client.therevgo.services.activities.MainActivity;
import com.client.therevgo.services.base.BaseFragment;
import com.client.therevgo.services.fragments.group.GroupContainerFragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class SmsContainerFragment extends BaseFragment {

    public static final String TAG = SmsContainerFragment.class.getName();
    private static final String ARG_SHOW_SEND_SMS = "arg_show_sms" ;

    private boolean showSendSms;
    private View view;
    private Context context;
    private CardView sendSMSCard, smsHintCard, manageGroupCard, mySMSCard,
            leadCard, planCard ;

    private MainActivity activity;

    public static SmsContainerFragment newInstance(boolean showSendSms) {
        Bundle args = new Bundle();
        args.putBoolean(ARG_SHOW_SEND_SMS, showSendSms);
        SmsContainerFragment fragment = new SmsContainerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
            showSendSms = getArguments().getBoolean(ARG_SHOW_SEND_SMS, false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_sms_container, container, false);

        //getting parent activity instance
        context = getActivity();

        activity = (MainActivity) context;

        //initialize Views
        findViewById();

        //set Listener to views
        setListener(new MyListener());

        if (showSendSms) {
            showSendSms = false;
            this.getArguments().clear();
            activity.attachFragment(new SendSmsFragment(), SendSmsFragment.TAG);
        }

        return view;
    }

    @Override
    protected String getTitle() {
        return "SMS Dashboard";
    }

    private void findViewById() {
        sendSMSCard = (CardView) view.findViewById(R.id.send_sms_card);
        smsHintCard = (CardView) view.findViewById(R.id.sms_hint_card);
        manageGroupCard = (CardView) view.findViewById(R.id.manage_group_card);
        mySMSCard = (CardView) view.findViewById(R.id.my_sms_card);
        leadCard = (CardView) view.findViewById(R.id.business_lead_card);
        planCard = (CardView) view.findViewById(R.id.plan_card);
    }

    private void setListener(View.OnClickListener listener) {
        sendSMSCard.setOnClickListener(listener);
        smsHintCard.setOnClickListener(listener);
        manageGroupCard.setOnClickListener(listener);
        mySMSCard.setOnClickListener(listener);
        leadCard.setOnClickListener(listener);
        planCard.setOnClickListener(listener);
    }

    private class MyListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            int id = v.getId() ;
            switch (id) {
                case R.id.send_sms_card:
                    activity.attachFragment(new SendSmsFragment(), SendSmsFragment.TAG);
                    break;

                case R.id.sms_hint_card:
                    activity.attachFragment(new SmsHintFragment(), SmsHintFragment.TAG);
                    break;

                case R.id.manage_group_card:
                    activity.attachFragment(new GroupContainerFragment(), GroupContainerFragment.TAG);
                    break;

                case R.id.my_sms_card:
                    activity.attachFragment(new SavedSmsFragment(), SavedSmsFragment.TAG);
                    break;

                case R.id.business_lead_card:
                    //Toast.makeText(context, "Delivery Report will coming soon", Toast.LENGTH_SHORT).show();
                    attachWebViewFragment("Delivery Report", "http://sms.therevgo.in");
                    break;

                case R.id.plan_card:
                    activity.attachFragment(new SmsPlanFragment(), SmsPlanFragment.TAG);
                    break;
            }
        }
    }

    /*private void attachFragment(Fragment fragment, String backStack){
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.addToBackStack(backStack);
        transaction.commit();
    }*/

    public void attachWebViewFragment(String title, String url) {

//        Bundle bundle = new Bundle();
//        bundle.putString(ContainerActivity.ARG_TITLE, title);
//        bundle.putString(ContainerActivity.ARG_URL, url);
//        bundle.putInt(ContainerActivity.ARG_FRAGMENT, ContainerActivity.WEBVIEW);

        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

}
