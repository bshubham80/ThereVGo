package com.android.therevgo.adapters;


import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.therevgo.R;
import com.android.therevgo.dto.BusinessProfileModel;
import com.android.therevgo.dto.ListStatusDTO;
import com.android.therevgo.library.ImageLoader;
import com.android.therevgo.networks.HttpConnection;
import com.android.therevgo.networks.ResponseListener;
import com.android.therevgo.utility.PrefManager;
import com.android.therevgo.utility.Utils;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by shubham on 9/4/17.
 */

public class BusinessAdapter extends ArrayAdapter<BusinessProfileModel.ListModel> {

    private static final String LIVE = "Tap To Live List";
    private static final String UNLIVE = "Tap To Unlive List";

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
        viewHolder.mBusinessLive.setTag(position);
        viewHolder.mBusinessLive.setOnClickListener(mListener);

        if (!data.image_name.equals("null")) {
            //loadLogo(Utils.IMAGE_URL_PREFIX + data.image_name, viewHolder.mBusinessLogo);
            viewHolder.mBusinessLogo.setTag(Utils.IMAGE_URL_PREFIX + data.image_name);
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

    private View.OnClickListener mListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final int pos = (int) v.getTag();
            TextView textView = (TextView) v;
            String text = textView.getText().toString();

            final Activity activity = (Activity) context;

            String url = "http://tapi.therevgo.in/api/BusinessListing/BUSCONTLIVE?";

            url+="userid=" + prefManager.getDataFromPreference(PrefManager.Key.USER_ID,
                    PrefManager.Type.TYPE_STRING);

            url+="&con_id=" + dataList.get(pos).con_id;

            if (text.equals(LIVE)) {
                url += "&live_status=Y";
            } else if (text.equals(UNLIVE)) {
                url += "&live_status=N";
            }

            HttpConnection.RequestGet(url, new ResponseListener() {
                @Override
                public void onResponse(int statusCode, final JSONObject jsonObject) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ListStatusDTO profileModel =
                                    new Gson().fromJson(jsonObject.toString(), ListStatusDTO.class);
                            if (profileModel.Data != null && profileModel.Data.size() > 0) {
                                dataList.get(pos).c_live_status = profileModel.Data.get(pos).live_status;
                                dataList.get(pos).b_bussiness_date = "jhgfds";
                                notifyDataSetChanged();
                            } else {
                                onError("No Result Found");
                            }
                        }
                    });
                }

                @Override
                public void onError(final String msg) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
    };

}
