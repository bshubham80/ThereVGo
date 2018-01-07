package com.client.therevgo.fragments.services;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.therevgo.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ServiceContainerFragment extends Fragment {

    public static final String TAG = "ServiceContainerFragment";

    public ServiceContainerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_service_container, container, false);
    }

}
