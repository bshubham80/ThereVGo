package com.android.therevgo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.therevgo.R;
import com.android.therevgo.dto.FollowModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shubham on 17/7/16.
 */
public class FollowAdapter extends ArrayAdapter<FollowModel> {

    private List<FollowModel> followModelList ;
    private Context context;
    private LayoutInflater inflater ;
    private int resourceId ;

    public FollowAdapter(Context context, int resource, List<FollowModel> objects) {
        super(context, resource, objects);
        this.context = context;
        inflater = LayoutInflater.from(context);
        followModelList = objects ;
        resourceId = resource ;
    }

    @Override
    public int getCount() {
        return followModelList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        ViewHolder viewHolder = null;

        if(convertView == null){
            view = inflater.inflate(resourceId,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView) view.findViewById(R.id.follow_name) ;
            viewHolder.email = (TextView) view.findViewById(R.id.follow_email) ;
            viewHolder.contact = (TextView) view.findViewById(R.id.follow_phone) ;
            viewHolder.description = (TextView) view.findViewById(R.id.follow_description) ;
            viewHolder.date = (TextView) view.findViewById(R.id.follow_date) ;
            viewHolder.area = (TextView) view.findViewById(R.id.follow_area) ;
            viewHolder.city = (TextView) view.findViewById(R.id.follow_city) ;
            view.setTag(viewHolder);
        }else{
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        FollowModel model = followModelList.get(position);

        if(model.getName() != null && !model.getName().equals("null"))
            viewHolder.name.setText(model.getName());

        if(model.getEmail() != null && !model.getEmail().equals("null"))
            viewHolder.email.setText(model.getEmail());

        if(model.getContact() != null && !model.getContact().equals("null"))
            viewHolder.contact.setText(model.getContact());

        if(model.getDescription() != null && !model.getDescription().equals("null"))
            viewHolder.description.setText(model.getDescription());

        if(model.getFollowData() != null && !model.getFollowData().equals("null"))
            viewHolder.date.setText(model.getFollowData());

        if(model.getArea() != null && !model.getArea().equals("null"))
            viewHolder.area.setText(model.getArea());

        if(model.getCity() != null && !model.getCity().equals("null"))
            viewHolder.city.setText(model.getCity());

        return view;
    }

    public void setSelectedItem(int postion , boolean status) {
        followModelList.get(postion).setSelected(status);
    }

    public int getSelectedItemSize(){
        return getCheckedItems().size() ;
    }


    public ArrayList<String> getCheckedItems() {
        ArrayList<String> temp = new ArrayList<>();
        for (int i = 0; i <= followModelList.size()-1 ; i++) {
            if(followModelList.get(i).isSelected()) {
                temp.add(followModelList.get(i).getContact());
            }
        }
        return  temp ;
    }

    static class ViewHolder{
        TextView name,email,contact,description,date,area,city;
    }
}
