package com.client.therevgo.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.therevgo.R;
import com.client.therevgo.dto.InquiryModel;
import com.client.therevgo.utility.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shubham on 20/11/17.
 */

public class InquiryAdapter extends ArrayAdapter<InquiryModel> implements View.OnClickListener {

    public static final String TAG = "InquiryAdapter";

    private List<InquiryModel> inquiryModels;
    private Context context;
    private LayoutInflater inflater;
    private int resourceId;

    public InquiryAdapter(Context context, int resource, List<InquiryModel> objects) {
        super(context, resource, objects);
        this.context = context;
        inflater = LayoutInflater.from(context);
        inquiryModels = objects;
        resourceId = resource;
    }

    @Override
    public int getCount() {
        return inquiryModels.size();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        FollowAdapter.ViewHolder viewHolder = null;

        if (convertView == null) {
            view = inflater.inflate(resourceId, parent, false);
            viewHolder = new FollowAdapter.ViewHolder();
            viewHolder.name = (TextView) view.findViewById(R.id.follow_name);
            viewHolder.email = (TextView) view.findViewById(R.id.follow_email);
            viewHolder.contact = (TextView) view.findViewById(R.id.follow_phone);
            viewHolder.description = (TextView) view.findViewById(R.id.follow_description);
            viewHolder.date = (TextView) view.findViewById(R.id.follow_date);
            viewHolder.area = (TextView) view.findViewById(R.id.follow_area);
            viewHolder.city = (TextView) view.findViewById(R.id.follow_city);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (FollowAdapter.ViewHolder) view.getTag();
        }

        InquiryModel model = inquiryModels.get(position);

        if (model.getName() != null && !model.getName().equals("null"))
            viewHolder.name.setText(model.getName());

        if (model.getEmail() != null && !model.getEmail().equals("null"))
            viewHolder.email.setText(model.getEmail());

        if (model.getContact() != null && !model.getContact().equals("null")) {
            viewHolder.contact.setText(model.getContact());
            viewHolder.contact.setOnClickListener(this);
            viewHolder.contact.setTag(model.getContact());
        }

        viewHolder.description.setVisibility(View.GONE);

        viewHolder.date.setVisibility(View.GONE);

        viewHolder.area.setVisibility(View.GONE);

        viewHolder.city.setVisibility(View.GONE);
        return view;
    }

    public void setSelectedItem(int postion, boolean status) {
        inquiryModels.get(postion).setSelected(status);
    }

    public int getSelectedItemSize() {
        return getCheckedItems().size();
    }


    public ArrayList<String> getCheckedItems() {
        ArrayList<String> temp = new ArrayList<>();
        for (int i = 0; i <= inquiryModels.size() - 1; i++) {
            if (inquiryModels.get(i).isSelected()) {
                temp.add(inquiryModels.get(i).getContact());
            }
        }
        return temp;
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.follow_phone:
                Log.i(TAG, "onClick: ");
                String contact = (String) v.getTag();
                Utils.getInstance().makeCall(context, contact);
                break;
        }
    }

    static class ViewHolder {
        TextView name, email, contact, description, date, area, city;

    }
}
