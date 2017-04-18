package com.android.therevgo.adapters;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.therevgo.R;
import com.android.therevgo.dto.SelectUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by Trinity Tuts on 10-01-2015.
 */
public class SelectUserAdapter extends BaseAdapter {

    public List<SelectUser> _data;
    private ArrayList<SelectUser> arraylist;
    Context _c;
    ViewHolder v;

    ArrayList<String> tempNumber = new ArrayList<>();
    HashMap<String,String> selectedContactMap = new HashMap<>();

    public SelectUserAdapter(List<SelectUser> selectUsers, Context context) {
        _data = selectUsers;
        _c = context;
        this.arraylist = new ArrayList<SelectUser>();
        this.arraylist.addAll(_data);
    }

    @Override
    public int getCount() {
        return _data.size();
    }

    @Override
    public Object getItem(int i) {
        return _data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        View view = convertView;
        if (view == null) {
            LayoutInflater li = (LayoutInflater) _c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = li.inflate(R.layout.contact_info, null);
        } else {
            view = convertView;
        }

        v = new ViewHolder();

        v.title = (TextView) view.findViewById(R.id.name);
        v.check = (CheckBox) view.findViewById(R.id.check);
        v.phone = (TextView) view.findViewById(R.id.no);
        v.imageView = (ImageView) view.findViewById(R.id.pic);

        final SelectUser data = (SelectUser) _data.get(i);
        v.title.setText(data.getName());
        v.check.setChecked(data.getCheckedBox());
        v.phone.setText(data.getPhone());

        // Set image if exists

            if (data.getThumb() != null) {
                v.imageView.setImageBitmap(data.getThumb());
            } else {
                v.imageView.setImageDrawable(_c.getResources().getDrawable(R.drawable.ic_account_circle));
            }

        view.setTag(data);
        return view;
    }

    public void setSelectedItem(int postion , boolean status) {
        String number = _data.get(postion).getPhone();
        String name   = _data.get(postion).getName() ;
        selectedContactMap.put(number,name);
    }
    public void removeSelectedItem(int postion , boolean status) {
        String number = _data.get(postion).getPhone();
        selectedContactMap.remove(number) ;
    }

    public int getSelectedItemSize(){
        return getCheckedItems().size() ;
    }

     public void clearSelectedList(){
         selectedContactMap.clear();
     }

    public HashMap<String,String> getCheckedItems() {
        return  selectedContactMap ;
    }
    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        _data.clear();
        if (charText.length() == 0) {
            _data.addAll(arraylist);
        } else {
            for (SelectUser wp : arraylist) {
                if (wp.getName().toLowerCase(Locale.getDefault())
                        .contains(charText)) {
                    _data.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }
    static class ViewHolder {
        ImageView imageView;
        TextView title, phone;
        CheckBox check;
    }
}
