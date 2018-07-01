package com.client.therevgo.fragments.sms;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.therevgo.R;
import com.client.therevgo.base.BaseFragment;
import com.client.therevgo.utility.Utils;

public class SmsPlanFragment extends BaseFragment {

    public static final String TAG = SmsPlanFragment.class.getName();

    public SmsPlanFragment() {
        // Required empty public constructor
    }

    @Override
    protected String getTitle() {
        return "SMS Plans";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_smsplan, container, false);
        Button bnt = (Button) view.findViewById(R.id.call_btn);
        bnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.getInstance().makeCall(getActivity());
            }
        });
        return view ;
    }
}
