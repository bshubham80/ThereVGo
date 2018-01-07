package com.client.therevgo.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.therevgo.R;

/**
 * Created by shubham on 6/4/17.
 */

public class ServiceAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater inflater ;

    // Keep all Images in array
    private Integer[] mThumbIds = {
            R.drawable.business, R.drawable.event,
            R.drawable.new_product, R.drawable.offer
    };
    // Keep all Images in array
    private String[] mNameIds = {
            "Business Listing", "Event",
            "New Product", "Offer"
    };
    // Keep all Images in array
    private String[] mLiveIds = {
            "", "Comming Soon",
            "Comming Soon", "Comming Soon"
    };
    // Keep all Images in array
    private String mColorIds = "#FF9CE9BB";

    // Constructor
    public ServiceAdapter(Context c){
        mContext = c;
        inflater = LayoutInflater.from(mContext);
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
            convertView = inflater.inflate(R.layout.layout_service_adapter_row, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.layout = (LinearLayout) convertView.findViewById(R.id.ll_service_container);
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.img_service_icon);
            viewHolder.name = (TextView) convertView.findViewById(R.id.txt_service_name);
            viewHolder.live = (TextView) convertView.findViewById(R.id.txt_service_live);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.layout.setBackgroundColor(Color.parseColor(mColorIds));
        viewHolder.icon.setImageResource(mThumbIds[position]);
        viewHolder.name.setText(mNameIds[position]);
        viewHolder.live.setText(mLiveIds[position]);

        return convertView;
    }

    private static class ViewHolder {
        LinearLayout layout;
        ImageView icon;
        TextView name, live ;
    }
}
