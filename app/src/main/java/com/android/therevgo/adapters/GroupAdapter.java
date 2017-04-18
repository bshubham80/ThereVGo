package com.android.therevgo.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.therevgo.R;
import com.android.therevgo.database.GroupBean;

import java.util.ArrayList;

public class GroupAdapter extends ArrayAdapter {

    private final ArrayList<GroupBean> groupList;
    private final Context context;
    private final int layout;
    private final LayoutInflater inflater;

    public GroupAdapter(Context context, int resource, ArrayList<GroupBean> groupList) {
        super(context, resource, groupList);
        this.context = context;
        this.layout = resource;
        this.groupList = groupList;
        inflater = (LayoutInflater) context.getSystemService
                                    (Context.LAYOUT_INFLATER_SERVICE);
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return groupList.get(position);
    }

    @Override
    public int getCount() {
        return groupList.size();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(layout, parent, false);
            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.list_display_text);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        GroupBean bean = groupList.get(position);
        holder.title.setText(bean.getName());

        return convertView;
    }

    private static class ViewHolder {
        TextView title;
    }
}
