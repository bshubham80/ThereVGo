package com.client.therevgo.services.base;


import android.support.v4.app.Fragment;

import com.android.therevgo.R;
import com.client.therevgo.services.activities.MainActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public abstract class BaseFragment extends Fragment {


    public BaseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() instanceof MainActivity) {
            MainActivity activity = (MainActivity) getActivity();
            if (activity.getToolbar() != null) {
                if (getTitle() != null) {
                    activity.getSupportActionBar().setIcon(null);
                } else {
                    activity.getSupportActionBar().setIcon(R.drawable.logo);
                }
                activity.getToolbar().setTitle(getTitle());
            }
        }
    }

    protected abstract String getTitle();
}
