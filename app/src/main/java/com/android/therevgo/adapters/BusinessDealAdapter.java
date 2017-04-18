package com.android.therevgo.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.therevgo.R;
import com.android.therevgo.dto.BusinessDealDTO;
import com.android.therevgo.fragments.listing.BusinessDealFragment;

import java.util.List;

/**
 * Created by shubham on 29/1/17.
 */

public class BusinessDealAdapter extends ArrayAdapter<BusinessDealDTO.Data> {

    private List<BusinessDealDTO.Data> dataList ;
    private Context context;
    private LayoutInflater inflater ;
    private int resourceId ;

    BusinessDealFragment deal;

    public BusinessDealAdapter(Context context, int resource, List<BusinessDealDTO.Data> objects, Fragment fragment) {
        super(context, resource, objects);
        this.context = context;
        inflater = LayoutInflater.from(context);
        dataList = objects ;
        resourceId = resource ;
        deal = (BusinessDealFragment) fragment;
    }


    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = null;
        ViewHolder viewHolder = null;

        if(convertView == null){
            view = inflater.inflate(resourceId,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView) view.findViewById(R.id.follow_name) ;
            viewHolder.email = (TextView) view.findViewById(R.id.follow_email) ;
            viewHolder.image = (ImageView) view.findViewById(R.id.img_edit) ;

            view.setTag(viewHolder);
        }else{
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        final BusinessDealDTO.Data data = dataList.get(position);

        viewHolder.name.setText(data.deals_name);
        viewHolder.email.setVisibility(View.GONE);

        viewHolder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deal.UpdateValuesDialog(data.id, position);
            }
        });

        return view;
    }
    static class ViewHolder{
        TextView name,email;
        ImageView image;
    }
}
