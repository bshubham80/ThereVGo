package com.client.therevgo.customviews;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TabHost;
import android.widget.TextView;

import com.android.therevgo.R;

/**
 * Created by shubham on 10/01/17.
 */
public class DateRangePickerDialog extends DialogFragment implements View.OnClickListener {
    TabHost tabs;
    Button setTimeRange;
    DatePicker startTimePicker, endTimePicker;
    OnTimeRangeSelectedListener onTimeRangeSelectedListener;
    boolean is24HourMode;
    Resources res;

    public static DateRangePickerDialog newInstance(OnTimeRangeSelectedListener callback, boolean is24HourMode) {
        DateRangePickerDialog ret = new DateRangePickerDialog();
        ret.initialize(callback, is24HourMode);
        return ret;
    }

    public void initialize(OnTimeRangeSelectedListener callback,
                           boolean is24HourMode) {
        onTimeRangeSelectedListener = callback;
        this.is24HourMode = is24HourMode;
    }

    public interface OnTimeRangeSelectedListener {
        void onTimeRangeSelected(int startYear, int startMonth, int startDay, int endYear, int endMonth, int endDay);
    }

    public void setOnTimeRangeSetListener(OnTimeRangeSelectedListener callback) {
        onTimeRangeSelectedListener = callback;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.timerange_picker_dialog, container, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        res = getActivity().getResources();
        tabs = (TabHost) root.findViewById(R.id.tabHost);
        setTimeRange = (Button) root.findViewById(R.id.bSetTimeRange);
        startTimePicker = (DatePicker) root.findViewById(R.id.startTimePicker);
        endTimePicker = (DatePicker) root.findViewById(R.id.endTimePicker);
        setTimeRange.setOnClickListener(this);
        tabs.findViewById(R.id.tabHost);
        tabs.setup();

        TabHost.TabSpec tabpage1 = tabs.newTabSpec("one");
        tabpage1.setContent(R.id.startTimeGroup);
        tabpage1.setIndicator(createView(getActivity(), "Start Date"));

        TabHost.TabSpec tabpage2 = tabs.newTabSpec("two");
        tabpage2.setContent(R.id.endTimeGroup);
        tabpage2.setIndicator(createView(getActivity(), "End Date"));

        tabs.addTab(tabpage1);
        tabs.addTab(tabpage2);

        tabs.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                if (tabId.equals("one")) {
                    setTimeRange.setText(res.getString(R.string.next));
                } else {
                    setTimeRange.setText(res.getString(R.string.ok));
                }
            }
        });

        startTimePicker.setSpinnersShown(false);
        endTimePicker.setSpinnersShown(false);

        return root;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onStart() {
        super.onStart();

        // safety check
        if (getDialog() == null)
            return;
        getDialog().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bSetTimeRange) {
            String btnText = setTimeRange.getText().toString();
            if (btnText.equals(res.getString(R.string.next))) {
                setTimeRange.setText(res.getString(R.string.ok));
                tabs.setCurrentTab(1);
            } else {
                dismiss();

                int startYear  = startTimePicker.getYear();
                int startMonth = startTimePicker.getMonth() ;
                int startDay   = startTimePicker.getDayOfMonth();

                int endYear  = endTimePicker.getYear();
                int endMonth = endTimePicker.getMonth() ;
                int endDay   = endTimePicker.getDayOfMonth();
                onTimeRangeSelectedListener.onTimeRangeSelected(startYear, startMonth, startDay, endYear, endMonth, endDay);
            }
        }
    }

    private int formatInteger(int i) {
       if (i <= 9) {
           return Integer.parseInt("0"+i);
       } else{
           return i;
       }
    }

    private View createView(final Context context, final String tabText) {
        View view = LayoutInflater.from(context).inflate(R.layout.tab_custom, null, false);
        TextView tv = (TextView) view.findViewById(R.id.tabTitleText);
        tv.setText(tabText);
        return view;
    }
}