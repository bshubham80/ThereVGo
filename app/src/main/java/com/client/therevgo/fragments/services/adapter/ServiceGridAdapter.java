package com.client.therevgo.fragments.services.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.therevgo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shubham on 16/1/18.
 */
public class ServiceGridAdapter extends BaseAdapter {
    public static final int STATE_SELECTED = 1;
    public static final int STATE_UNSELECTED = 2;

    private Context mContext;
    private LayoutInflater inflater ;

    // Keep all Images in array
    private Integer[] mThumbIds = {
            R.drawable.advertisements, R.drawable.home_services,
            R.drawable.interior_work, R.drawable.jobs,
            R.drawable.properties, R.drawable.tiffin_services,
            R.drawable.tour___travel, R.drawable.securities_services
    };
    // Keep all text in array
    private String[] mNameIds = {
            "ADVERTISEMENTS", "HOME SERVICES",
            "INTERIOR WORKS", "JOBS",
            "PROPERTY", "TIFFIN SERVICES",
            "TOUR & TRAVEL", "SECURITIES & SERVICES"
    };
    // Keep all status in array
    private String[] mLiveIds = {
            "", "Comming Soon",
            "Comming Soon", "Comming Soon"
    };

    private String mColorIds = "#FF9CE9BB";


    private boolean[] stateSelected = new boolean[mThumbIds.length];

    // Constructor
    public ServiceGridAdapter(Context c){
        mContext = c;
        inflater = LayoutInflater.from(mContext);

        for (int i = 0; i <= mThumbIds.length-1 ; i++) {
            stateSelected[i] = false;
        }
    }

    @Override
    public int getCount() {
        return mThumbIds.length;
    }

    @Override
    public Object getItem(int position) {
        return mThumbIds[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder ;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.layout_service_grid_adapter_row, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.layout = (CardView) convertView.findViewById(R.id.layut_background);
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.grid_icon);
            viewHolder.name = (TextView) convertView.findViewById(R.id.grid_name);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (stateSelected[position]) {
            viewHolder.layout.setBackgroundColor(Color.parseColor("#FFC1F5BE"));
        } else {
            viewHolder.layout.setBackgroundColor(Color.WHITE);
        }

        // viewHolder.layout.setBackgroundColor(Color.parseColor(mColorIds));
        viewHolder.icon.setImageResource(mThumbIds[position]);
        viewHolder.name.setText(mNameIds[position]);
        // viewHolder.live.setText(mLiveIds[position]);

        return convertView;
    }

    public boolean[] getStates() {
        return stateSelected;
    }

    public void resetState(){
        stateSelected = new boolean[mThumbIds.length];
    }

    public String getSelectedServices() {
        StringBuilder ser = new StringBuilder();
        boolean first = true;
        for (int i = 0; i <= stateSelected.length-1 ; i++) {
            if (stateSelected[i]) {
                if (first) {
                    first = false;
                    ser.append(mNameIds[i]);
                } else {
                    ser.append(",").append(mNameIds[i]);
                }
            }
        }
        return ser.toString();
    }

    private static class ViewHolder {
        CardView layout;
        ImageView icon;
        TextView name, live ;
    }
}
