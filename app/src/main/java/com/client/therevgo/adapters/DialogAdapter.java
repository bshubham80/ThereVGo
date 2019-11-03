package com.client.therevgo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.therevgo.R;
import com.client.therevgo.dto.DialogBean;

import java.util.ArrayList;

/**
 * Created by shubham on 13/9/16.
 */
public class DialogAdapter extends ArrayAdapter<DialogBean> {

    private Context context;
    private int resource;
    private ArrayList<DialogBean> list;
    private LayoutInflater inflater;

    public DialogAdapter(Context context, int resource, ArrayList<DialogBean> list) {
        super(context, resource, list);
        this.context = context;
        this.resource = resource;
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        ViewHolder viewHolder = null;

        if (convertView == null) {
            view = inflater.inflate(resource, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView) view.findViewById(R.id.title);
            viewHolder.icon = (ImageView) view.findViewById(R.id.icon);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.name.setText(list.get(position).getTitle());
        //viewHolder.icon.setImageDrawable(context.getResources().getDrawable(list.get(position).getIconResourceID()));

        return view;
    }

    static class ViewHolder {
        TextView name;
        ImageView icon;
    }
}
