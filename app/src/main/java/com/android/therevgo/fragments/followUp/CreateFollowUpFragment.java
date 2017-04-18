package com.android.therevgo.fragments.followUp;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.therevgo.R;
import com.android.therevgo.constants.Config;
import com.android.therevgo.networks.ResponseListener;
import com.android.therevgo.utility.PrefManager;
import com.android.therevgo.utility.Utils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Pattern;

import static com.android.therevgo.networks.HttpConnection.RequestPost;

public class CreateFollowUpFragment extends Fragment implements ResponseListener {

    public static final String TAG = CreateFollowUpFragment.class.getName();

    private Context context;
    private EditText follow_name,
            follow_contact,
            follow_email,
            follow_description,
            follow_date,
            follow_area,
            follow_city;

    private ProgressDialog dialog;
    private Spinner title, stype;

    private final Utils utils = Utils.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_create_follow_up, container, false);
        context = getActivity();

        follow_name = (EditText) v.findViewById(R.id.et_name);
        follow_contact = (EditText) v.findViewById(R.id.et_contact);
        follow_email = (EditText) v.findViewById(R.id.et_email);
        follow_description = (EditText) v.findViewById(R.id.et_description);
        follow_date = (EditText) v.findViewById(R.id.et_follow_date);
        follow_area = (EditText) v.findViewById(R.id.et_area);
        follow_city = (EditText) v.findViewById(R.id.et_city);
        Button submit = (Button) v.findViewById(R.id.btn_follow_submit);

        String[] rmNameList = new String[]{"Select Gender", "Mr.", "Miss/Mrs."};
        title = (Spinner) v.findViewById(R.id.person_title);

        ArrayAdapter<String> nameAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, rmNameList);
        nameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        title.setAdapter(nameAdapter);

        String[] type = new String[]{"Select User Type", "Customer", "Client", "Friend"};
        stype = (Spinner) v.findViewById(R.id.user_type);

        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, type);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stype.setAdapter(typeAdapter);

        dialog = new ProgressDialog(context);
        dialog.setMessage("Please Wait");
        dialog.setCancelable(false);

        follow_date.setOnClickListener(new MyClickListener());
        submit.setOnClickListener(new MyClickListener());

        return v;
    }

    private void showDateDialog() {
        Date date = new Date();
        int year = 1900 + date.getYear();
        int month = date.getMonth();
        int day = date.getDate();
        DatePickerDialog dialog = new DatePickerDialog(context, new MyDateListener(), year, month, day);
        dialog.show();
    }

    private void validateForm() {
        if (title.getSelectedItemPosition() == 0) {
            utils.displayAlert(context, "Warning", "Please Select Gender");
            return;
        }
        if (stype.getSelectedItemPosition() == 0) {
            utils.displayAlert(context, "Warning", "Please Select User Type");
            return;
        }
        if (!utils.validateView(getActivity(), follow_name, getString(R.string.err_msg_name))) {
            return;
        }
        if (!utils.validateView(getActivity(), follow_contact, "Enter correct mobile No.")) {
            return;
        }

        String regx = "[0-9]+";
        Pattern pattern = Pattern.compile(regx);
        if (follow_contact.getText().length() != 10) {
            follow_contact.setError("Enter Valid Number");
            return;
        } else if (!pattern.matcher(follow_contact.getText().toString()).matches()) {
            follow_contact.setError("Enter Valid Number");
            return;
        }
        
        if (!utils.validateView(getActivity(), follow_date, "Enter follow up date")) {
            return ;
        }

        dialog.show();
        String URL = Config.DOMAIN + "api/FollowUp";
        RequestPost(URL, getRequestParam(), CreateFollowUpFragment.this);
    }

    private ArrayList<NameValuePair> getRequestParam() {
        ArrayList<NameValuePair> map = new ArrayList<>();

        String user_id = (String) PrefManager.getInstance(context).
                getDataFromPreference(PrefManager.Key.USER_ID, PrefManager.Type.TYPE_STRING);

        map.add(new BasicNameValuePair("userid", user_id));
        map.add(new BasicNameValuePair("type_of_user", stype.getSelectedItem().toString()));
        map.add(new BasicNameValuePair("title", title.getSelectedItem().toString()));
        map.add(new BasicNameValuePair("name", follow_name.getText().toString()));
        map.add(new BasicNameValuePair("mobile_no", follow_contact.getText().toString()));
        map.add(new BasicNameValuePair("email_id", follow_email.getText().toString()));
        map.add(new BasicNameValuePair("description", follow_description.getText().toString()));
        map.add(new BasicNameValuePair("follow_up_date", follow_date.getText().toString()));
        map.add(new BasicNameValuePair("area", follow_area.getText().toString()));
        map.add(new BasicNameValuePair("city", follow_city.getText().toString()));

        return map;
    }

    @Override
    public void onResponse(int statusCode, final JSONObject jsonObject) {
        if (statusCode == 200) {
            String status;
            try {
                status = jsonObject.getString("status");
            } catch (JSONException e) {
                e.printStackTrace();
                status = "false";
            }
            if (status.equals("true")) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    dialog.dismiss();
                    Toast.makeText(context, "Follow Up Create Successfully", Toast.LENGTH_SHORT).show();
                    emptyView();
                    }
                });
            } else {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    dialog.dismiss();
                    try {
                        Toast.makeText(context, jsonObject.getString("error"), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    }
                });
            }
        }
    }

    @Override
    public void onError(final String msg) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                emptyView();
            }
        });
    }

    private void emptyView() {
        title.setSelection(0);
        stype.setSelection(0);
        follow_name.setText("");
        follow_contact.setText("");
        follow_email.setText("");
        follow_description.setText("");
        follow_date.setText("");
        follow_area.setText("");
        follow_city.setText("");
    }

    private class MyDateListener implements DatePickerDialog.OnDateSetListener {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            monthOfYear = monthOfYear + 1;

            if (monthOfYear <= 9) {
                follow_date.setText("0" + monthOfYear + "/" + dayOfMonth + "/" + year);
            } else {
                follow_date.setText(monthOfYear + "/" + dayOfMonth + "/" + year);
            }

        }
    }

    private class MyClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.et_follow_date:
                    showDateDialog();
                break;
                case R.id.btn_follow_submit:
                    validateForm();
                break;
            }
        }
    }
}
