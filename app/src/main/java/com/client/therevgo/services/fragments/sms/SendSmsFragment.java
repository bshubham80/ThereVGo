package com.client.therevgo.services.fragments.sms;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.therevgo.R;
import com.client.therevgo.services.activities.ContactActivity;
import com.client.therevgo.services.base.BaseFragment;
import com.client.therevgo.services.database.AppDb;
import com.client.therevgo.services.database.ContactBean;
import com.client.therevgo.services.database.ContactTable;
import com.client.therevgo.services.database.GroupBean;
import com.client.therevgo.services.database.GroupTable;
import com.client.therevgo.services.networks.HttpConnection;
import com.client.therevgo.services.networks.ResponseListener;
import com.client.therevgo.services.networks.SaveSMS;
import com.client.therevgo.services.utility.PrefManager;
import com.client.therevgo.services.utility.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

public class SendSmsFragment extends BaseFragment
        implements ResponseListener, ContactActivity.ContactListener {

    public static final String TAG = SendSmsFragment.class.getName();

    private static final int PICK_CONTACT = 1;
    private static final int REQUEST_CODE = 2;
    
    public static final long ONE_SECOND = 1000 * 60;
    
    public static final long DELETE_MINUTE = 30 * ONE_SECOND;

    public static TextView balance;
    static EditText contactNo, message;

    public static SendSmsFragment instance ;
    private ImageView contactpicker;
    private Spinner mSelectType, mGroupList;
    private ScrollView scrollView;
    private FloatingActionButton down, up;
    private CheckBox saveCheckBox;
    private View view;
    private Button send, explorer;
    private Context context;
    private int completedRequest, totalRequest;
    private ArrayList<String> phonelist;
    private Handler handler = new Handler();
    private LinearLayout contactLayout, groupLayout;
    private ProgressDialog dialog;

    private HashMap<String, Integer> groupmap = new HashMap<>();

    private Utils utils = Utils.getInstance();

    private String messgae ;

    private AppDb appDb;
    private GroupTable groupTable;
    private ContactTable contactTable;

    private String user_id, email, password, sender_id, sms_type, msg_code;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_send_sms, container, false);

        context = getActivity();

        init(view);

        PrefManager prefManager = PrefManager.getInstance(context);

        user_id = (String) prefManager.getDataFromPreference(PrefManager.Key.USER_ID, PrefManager.Type.TYPE_STRING);
        email = (String) prefManager.getDataFromPreference(PrefManager.Key.USER_EMAIL, PrefManager.Type.TYPE_STRING);
        password = (String) prefManager.getDataFromPreference(PrefManager.Key.USER_PASSWORD, PrefManager.Type.TYPE_STRING);
        sender_id = (String) prefManager.getDataFromPreference(PrefManager.Key.USER_MSG_ID, PrefManager.Type.TYPE_STRING);
        sms_type = (String) prefManager.getDataFromPreference(PrefManager.Key.SMS_TYPE, PrefManager.Type.TYPE_STRING);
        msg_code = (String) prefManager.getDataFromPreference(PrefManager.Key.MESSAGE_UNIQUE_CODE, PrefManager.Type.TYPE_STRING);

        instance = this ;

        dialog = new ProgressDialog(context);
        dialog.setMessage("Please Wait");
        dialog.setCancelable(false);

        phonelist = utils.phoneList;
        
        removeDuplicateValues(phonelist);
        
        setNumberFromList(phonelist);
    
        Utils.getTotalBalanceSms(email, password);
        
        return view;
    }
    
    private void removeDuplicateValues(ArrayList<String> phoneList) {
        // phoneList = name of arrayList from which u want to remove duplicates
        HashSet hs = new HashSet();
        hs.addAll(phoneList);
    
        phoneList.clear();
        phoneList.addAll(hs);
    }
    
    private void setNumberFromList(ArrayList<String> phonelist) {
        boolean isFirst = true;
        contactNo.setText("");
        if (phonelist.size() > 0) {
            String numbers = "";
            for (String val : phonelist) {
                if (isFirst) {
                    isFirst = false;
                    numbers = val;
                } else {
                    numbers += "," + val;
                }
            }
            contactNo.setText(numbers);
        }

    }

    private void setNumberFromList(HashMap<String, String> phonelist) {
        boolean isFirst = true;
        contactNo.setText("");
        if (phonelist.size() > 0) {
            String numbers = "";

            for (String val : phonelist.keySet()) {
                if (isFirst) {
                    isFirst = false;
                    numbers = val;
                } else {
                    numbers += "," + val;
                }
            }
            contactNo.setText(numbers);
        }

    }

    private void init(View view) {

        scrollView = (ScrollView) view.findViewById(R.id.smsScroll);

        down = (FloatingActionButton) view.findViewById(R.id.updown);
        up = (FloatingActionButton) view.findViewById(R.id.downup);

        contactNo = (EditText) view.findViewById(R.id.et_sms_no);
        message = (EditText) view.findViewById(R.id.et_sms_message);

        saveCheckBox = (CheckBox) view.findViewById(R.id.save_check_box);

        balance = (TextView) view.findViewById(R.id.total_balance);
        String balance_text = String.format(context.getResources().getString(R.string.Total_balance), Utils.BALANCE_SMS);
        balance.setText(balance_text);

        contactpicker = (ImageView) view.findViewById(R.id.img_contact_picker);
        send = (Button) view.findViewById(R.id.btn_send_msg);

        contactLayout = (LinearLayout) view.findViewById(R.id.contact_layout);
        groupLayout = (LinearLayout) view.findViewById(R.id.group_layout);

        mSelectType = (Spinner) view.findViewById(R.id.select_type);
        mGroupList = (Spinner) view.findViewById(R.id.select_group_spinner);

        AppDb db = AppDb.getInstance(context);
        groupTable = (GroupTable) db.getTableObject(GroupTable.TABLE_NAME);
        contactTable = (ContactTable) db.getTableObject(ContactTable.TABLE_NAME);

        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("Contact");
        categories.add("Group");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        mSelectType.setAdapter(dataAdapter);


        // group Drop down elements
        List<String> group = new ArrayList<String>();
        Vector<GroupBean> groupBeen = groupTable.getAllData(context);
        for (GroupBean bean : groupBeen) {
            group.add(bean.getName());
            groupmap.put(bean.getName(), bean.getId());
        }

        // Creating adapter for spinner
        ArrayAdapter<String> groupAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, group);

        // Drop down layout style - list view with radio button
        groupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        mGroupList.setAdapter(groupAdapter);

        setClickListener();
    }

    private void setClickListener() {
        contactpicker.setOnClickListener(new MyClickListener());
        send.setOnClickListener(new MyClickListener());

        // Spinner click listener
        mSelectType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        contactLayout.setVisibility(View.VISIBLE);
                        groupLayout.setVisibility(View.GONE);
                        break;

                    case 1:
                        contactLayout.setVisibility(View.GONE);
                        groupLayout.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CODE);
            } else {
                getContact();
            }
        } else {
            getContact();
        }
    }

    public void getContact() {
        Intent intent = new Intent(context, ContactActivity.class);
        intent.putExtra(ContactActivity.INSTANCE, ContactActivity.SENDSMS);
        startActivity(intent);
    }


    @Override
    public void onResponse(final int Statuscode, final JSONObject jsonObject)  {
        handler.post(new Runnable() {
            @Override
            public void run() {
                utils.hideKeyboard(context);
                if (Statuscode == 200) {
                    dialog.dismiss();

                    try {
                        if (!jsonObject.isNull("ErrorMessage")) {
                            Toast.makeText(context, jsonObject.getString("ErrorMessage"), Toast.LENGTH_SHORT).show();

                            completedRequest++;

                            if (completedRequest == totalRequest && saveCheckBox.isChecked()) {
                                new SaveSMS(context, messgae, user_id);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(context, "ErrorMessage", Toast.LENGTH_SHORT).show();
                    }

                    message.setText(null);
                    contactNo.setText(null);
                    mSelectType.setSelection(0, true);
                    contactLayout.setVisibility(View.VISIBLE);
                    groupLayout.setVisibility(View.GONE);

                    //gett total balance sms after sending
                    Utils.getTotalBalanceSms(email, password);
                }
            }
        });
    }

    @Override
    public void onError(final String msg) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onContactListFound(ContactActivity activity, HashMap<String, String> ContactList) {
        activity.finish();
        setNumberFromList(ContactList);
    }

    private void validateForm() {

        utils.hideKeyboard(context);

        if (contactLayout.isShown()) {
            if (!utils.validateView(getActivity(), contactNo, "Please enter number")) {
                return;
            }
        } else if (groupLayout.isShown()) {
            String groupName = mGroupList.getSelectedItem().toString();
            int groupId = groupmap.get(groupName);

            Vector<ContactBean> contactBeen = contactTable.getAllDataByID("" + groupId);
            int totalContact = contactBeen.size();

            if (totalContact == 0) {
                Toast.makeText(context, "Group doesn't have contact", Toast.LENGTH_LONG).show();
                return;
            } else {
                boolean isFirst = true;
                contactNo.setText("");
                String numbers = "";
                for (ContactBean bean : contactBeen) {
                    if (isFirst) {
                        isFirst = false;
                        numbers = bean.getNumber();
                    } else {
                        numbers += "," + bean.getNumber();
                    }
                }
                contactNo.setText(numbers);
            }
        }

        if (!utils.validateView(getActivity(), message, "Enter Message ")) {
            return;
        }

        String messageText = URLEncoder.encode(message.getText().toString());

        messgae = message.getText().toString() ;

        String nolist = contactNo.getText().toString();
        String[] no_Array = null;
        String actucalno = null;

        nolist = nolist.replaceAll("[^\\d,]", "");

        if (nolist.contains(" ")) {
            nolist = utils.removeWhitSpace(nolist);
        }

        if (nolist.contains(",")) {
            no_Array = nolist.split(",");
        }

        if (no_Array != null) {

            int size = no_Array.length;
            int count = size / 100;
            int remain = size % 100;
            int start = 0;
            int end = 0;
            boolean firstFlagIndex = true;

            if (remain > 0)
                count = count + 1;

            totalRequest = count;

            // Outer Loop
            for (int i = 0; i <= count - 1; i++) {
                if (!firstFlagIndex) {
                    start = start + 1;
                }
                if (i == count - 1) {
                    end = size;
                } else {
                    end = end + 100;
                }

                boolean firstflg = true;
                // Inner Loop
                for (int j = start; j <= end - 1; j++) {
                    //value
                    String value = no_Array[j];
                    //Condition
                    if (value.length() > 10) {
                        int startpoint = value.length() - 10;
                        if (firstflg) {
                            actucalno = "91" + value.substring(startpoint, value.length());
                            firstflg = false;
                        } else {
                            actucalno += "," + "91" + value.substring(startpoint, value.length());
                        }
                    } else if (value.length() == 10) {
                        if (firstflg) {
                            actucalno = "91" + value;
                            firstflg = false;
                        } else {
                            actucalno += "," + "91" + value;
                        }
                    }
                }
                start = end;
                firstFlagIndex = false;

                Log.i("validateForm:if " + i, actucalno);


                String URL = "http://bulksms.therevgo.com/vendorsms/pushsms.aspx?" +
                        "user=" + email +
                        "&api="+ msg_code +
                        "&password=" + password +
                        "&msisdn=" + actucalno +
                        "&sid=" + sender_id +
                        "&msg=" + messageText +
                        "&fl=0";

                if (sms_type.equals("2")) {
                    URL += "&gwid=2";
                }
                dialog.show();
                HttpConnection.RequestGet(URL, SendSmsFragment.this);

            }
        } else {
            if (nolist.length() > 10) {
                int startpoint = nolist.length() - 10;
                actucalno = "91" + nolist.substring(startpoint, nolist.length());

            } else if (nolist.length() == 10) {
                actucalno = "91" + nolist;
            } else if (nolist.length() < 10) {
                Toast.makeText(context,  "Enter Valid No", Toast.LENGTH_SHORT).show();
                return;
            }

            String URL = "http://bulksms.therevgo.com/vendorsms/pushsms.aspx?" +
                    "user=" + email +
                    "&api="+ msg_code +
                    "&password=" +password +
                    "&msisdn=" + actucalno +
                    "&sid=" + sender_id +
                    "&msg=" + messageText +
                    "&fl=0";

            if (sms_type.equals("2")) {
                URL += "&gwid=2";
            }

            //set Total request to 1 if only one contact number is there
            totalRequest = 1;

            dialog.show();
            HttpConnection.RequestGet(URL, SendSmsFragment.this);

            Log.i("validateForm:else ", actucalno);
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        
        if (phonelist != null && phonelist.size() > 0) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    utils.phoneList.clear();
                }
            }, DELETE_MINUTE);
        }
    }

    @Override
    protected String getTitle() {
        return "Bulk Sms";
    }

    private class MyClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.img_contact_picker:
                    checkPermission();
                    break;

                case R.id.btn_send_msg:
                    validateForm();
                    break;
            }
        }
    }
}
