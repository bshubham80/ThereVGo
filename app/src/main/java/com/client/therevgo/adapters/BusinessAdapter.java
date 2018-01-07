package com.client.therevgo.adapters;


import android.content.Context;
import android.graphics.Color;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.therevgo.R;
import com.client.therevgo.dto.BusinessProfileModel;
import com.client.therevgo.library.ImageLoader;
import com.client.therevgo.utility.PrefManager;
import com.client.therevgo.utility.Utils;
import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by shubham on 9/4/17.
 */

public class BusinessAdapter extends ArrayAdapter<BusinessProfileModel.ListModel> {

    private static final String LIVE = "Live List";
    private static final String UNLIVE = "Unlive List";

    private List<BusinessProfileModel.ListModel> dataList ;
    private Context context;
    private LayoutInflater inflater ;
    private int resourceId ;
    private ImageLoader imageLoader ;
    private PrefManager prefManager;


    public BusinessAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<BusinessProfileModel.ListModel> objects) {
        super(context, resource, objects);
        this.context = context;
        inflater = LayoutInflater.from(context);
        dataList = objects ;
        resourceId = resource ;
        imageLoader = new ImageLoader(context);
        prefManager = PrefManager.getInstance(context);
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View view;
        ViewHolder viewHolder;

        if(convertView == null){
            view = inflater.inflate(resourceId,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.mBusinessName = (TextView) view.findViewById(R.id.business_name) ;
            viewHolder.mBusinessAddress = (TextView) view.findViewById(R.id.business_address) ;
            viewHolder.mBusinessLive = (TextView) view.findViewById(R.id.live_status) ;
            viewHolder.mBusinessCreateOn = (TextView) view.findViewById(R.id.created_date) ;

            viewHolder.mBusinessLogo = (ImageView) view.findViewById(R.id.business_icon) ;

            view.setTag(viewHolder);
        }else{
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        final BusinessProfileModel.ListModel data = dataList.get(position);
        // set business created date
        viewHolder.mBusinessCreateOn.setText(data.b_bussiness_date);

        if (!data.b_bussiness_name.equals("null")) {
            viewHolder.mBusinessName.setText(data.b_bussiness_name);
        } else {
            viewHolder.mBusinessName.setText(data.c_name);
        }

        if(!data.b_area.equals("null")) {
            viewHolder.mBusinessAddress.setText(data.b_area);
        } else {
            viewHolder.mBusinessAddress.setText(data.c_email_id);
        }

        if (data.c_live_status.equals("N")) {
            viewHolder.mBusinessLive.setText(LIVE);
            viewHolder.mBusinessLive.setTextColor(Color.parseColor("#0C9342"));
        } else {
            viewHolder.mBusinessLive.setText(UNLIVE);
            viewHolder.mBusinessLive.setTextColor(Color.parseColor("#FFF13326"));
        }

        if (!data.image_name.equals("null")) {
            //loadLogo(Utils.IMAGE_URL_PREFIX + data.image_name, viewHolder.mBusinessLogo);
            viewHolder.mBusinessLogo.setTag(Utils.IMAGE_URL_PREFIX + data.image_name.replace(" ", "%20"));
            imageLoader.displayImage(Utils.IMAGE_URL_PREFIX + data.image_name, viewHolder.mBusinessLogo);
        } else {
            viewHolder.mBusinessLogo.setImageResource(R.drawable.ic_panorama);
        }

        return view;
    }

    private static class ViewHolder{
        TextView mBusinessName,mBusinessAddress, mBusinessLive, mBusinessCreateOn;
        ImageView mBusinessLogo;
    }

    private void loadLogo(String imageURL, ImageView logo){
        Glide.with(getContext())
            .load(imageURL)
            .placeholder(R.drawable.ic_panorama)
            .into(logo);
    }
}
