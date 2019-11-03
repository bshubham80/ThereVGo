package com.client.therevgo.adapters;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.therevgo.R;
import com.client.therevgo.dto.SelectUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by Trinity Tuts on 10-01-2015.
 */
public class SelectUserAdapter extends BaseAdapter {

    private List<SelectUser> _data;
    private Context _c;
    private ViewHolder v;
    ArrayList<String> tempNumber = new ArrayList<>();
    private HashMap<String, String> selectedContactMap = new HashMap<>();
    private ArrayList<SelectUser> arraylist;

    public SelectUserAdapter(List<SelectUser> selectUsers, Context context) {
        _data = selectUsers;
        _c = context;
        this.arraylist = new ArrayList<>();
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
    public View getView(final int i, View convertView, ViewGroup viewGroup) {
        View view = convertView;
        if (view == null) {
            LayoutInflater li = (LayoutInflater) _c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = li.inflate(R.layout.contact_info_another, null);
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
        v.check.setTag(i);
        v.phone.setText(data.getPhone());

        /*v.check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    setSelectedItem(pos);
                else
                    removeSelectedItem(pos);
            }
        });*/

        v.check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = (int) v.getTag();

                if(((CheckBox)v).isChecked()) {
                    setSelectedItem(pos);
                    showCountToast();
                }
                else {
                    removeSelectedItem(pos);
                    showCountToast();
                }
            }
        });
        // Set image if exists

        if (data.getThumb() != null) {
            v.imageView.setImageBitmap(data.getThumb());
        } else {
            v.imageView.setImageDrawable(_c.getResources().getDrawable(R.drawable.ic_account_circle));
        }

        view.setTag(data);
        return view;
    }

    public void showCountToast() {
        int count = getCheckedItems().size();
        String msg ;
        if (count <= 1) {
             msg = String.format(Locale.ENGLISH,"%d number selected", count);
        } else {
             msg = String.format(Locale.ENGLISH,"%d numbers selected", count);
        }
        Toast.makeText(_c, msg, Toast.LENGTH_SHORT).show();
    }
    public void setSelectedItem(int postion) {
        String number = _data.get(postion).getPhone();
        String name = _data.get(postion).getName();
        _data.get(postion).setCheckedBox(true);
        selectedContactMap.put(number, name);
        notifyDataSetChanged();
    }

    public void removeSelectedItem(int postion) {
        _data.get(postion).setCheckedBox(false);
        String number = _data.get(postion).getPhone();
        selectedContactMap.remove(number);
        notifyDataSetChanged();
    }

    public int getSelectedItemSize() {
        return getCheckedItems().size();
    }

    public void clearSelectedList() {
        selectedContactMap.clear();
    }

    public HashMap<String, String> getCheckedItems() {
        return selectedContactMap;
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
