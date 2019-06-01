package com.client.therevgo.fragments.listing;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.therevgo.R;
import com.client.therevgo.activities.ContainerActivity;
import com.client.therevgo.dto.BusinessProfileModel;
import com.client.therevgo.dto.CountryModel;
import com.client.therevgo.dto.StateModel;
import com.client.therevgo.networks.HttpConnection;
import com.client.therevgo.networks.ResponseListener;
import com.client.therevgo.utility.PrefManager;
import com.client.therevgo.utility.Utils;
import com.google.gson.Gson;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class BusinessContactInfoFragment extends Fragment implements ResponseListener {

    public static final String TAG =  BusinessContactInfoFragment.class.getName();
    public static final String CONTACT_ID = "contact_id";
    private static final String ARG_DATA = "data";
    private final Gson gson = new Gson();

    public static String LIVE_STATUS ;

    private int clickRefernece;
    private static final int COUNTRY = 0;
    private static final int STATE = 1;

    public String country_id, state_id ;

    private Context context;

    private List<String> idsList;
    private ArrayList<String> textList;

    private EditText follow_name,
            follow_mobile,
            follow_email,
            follow_area,
            follow_city,
            follow_pincode,
            follow_state,
            follow_country;

    private Button btn_submit, btn_update;

    public int con_id;
    private String user_id  ;

    private Utils utils = Utils.getInstance();

    private boolean updating, inserting ;

    private ProgressDialog dialog;

    private ContainerActivity containerActivity;

    public static BusinessContactInfoFragment newInstance(BusinessProfileModel.ListModel model) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATA, model);
        BusinessContactInfoFragment fragment = new BusinessContactInfoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_contact_info, container, false);

        context = getActivity();

        containerActivity = (ContainerActivity) context;

        follow_name = (EditText) view.findViewById(R.id.et_name);
        follow_mobile = (EditText) view.findViewById(R.id.et_mobile);
        follow_email = (EditText) view.findViewById(R.id.et_email);
        follow_area = (EditText) view.findViewById(R.id.et_area);
        follow_city = (EditText) view.findViewById(R.id.et_city);
        follow_pincode = (EditText) view.findViewById(R.id.et_pincode);
        follow_state = (EditText) view.findViewById(R.id.et_state);
        follow_country = (EditText) view.findViewById(R.id.et_country);

        btn_submit = (Button) view.findViewById(R.id.btn_submit);
        btn_update = (Button) view.findViewById(R.id.btn_update);

        follow_state.setOnClickListener(new MyClickListener());
        follow_country.setOnClickListener(new MyClickListener());

        btn_submit.setOnClickListener(new MyClickListener());
        btn_update.setOnClickListener(new MyClickListener());

        dialog = new ProgressDialog(context);
        dialog.setMessage("Please Wait");
        dialog.setCancelable(false);

        Bundle bundle = getArguments();
        if (bundle != null) {
            BusinessProfileModel.ListModel data = (BusinessProfileModel.ListModel) bundle.getSerializable(ARG_DATA);
            if (data != null) {
                con_id = data.con_id;
                LIVE_STATUS = data.c_live_status;
                setValueToViews(data);
                btn_update.setVisibility(View.VISIBLE);
                btn_submit.setText("NEXT");
            }
        }

        user_id = (String) PrefManager.getInstance(context).getDataFromPreference(PrefManager.Key.USER_ID, PrefManager.Type.TYPE_STRING);
        return view ;
    }

    private class MyClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.btn_submit:
                    Button btn = (Button) v;
                    if(btn.getText().toString().equals("NEXT")) {
                       /* Fragment fragment = new BusinessInfoFragment();
                        Bundle bundle = new Bundle();
                        bundle.putInt(CONTACT_ID, con_id);

                        fragment.setArguments(bundle);

                        containerActivity.attachFragment(fragment, BusinessInfoFragment.TAG);*/
                       changeFragment();
                    } else {
                        if(validateForm()) {
                            insertValues();
                        }
                    }
                break;

                case R.id.btn_update:
                    if(validateForm()) {
                        updatesValues();
                    }
                break;

                case R.id.et_country:
                    idsList = new ArrayList<>();
                    textList = new ArrayList<>();
                    dialog.show();

                    clickRefernece = COUNTRY;
                    state_id = null ;
                    follow_state.setText(null);

                    String u0 = "http://tapi.therevgo.in/api/Country";
                    HttpConnection.RequestGet(u0, new ResponseListener() {
                        @Override
                        public void onResponse(final int statusCode, final JSONObject jsonObject) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dialog.dismiss();
                                    if (statusCode == 200) {
                                        CountryModel model = gson.fromJson(jsonObject.toString(), CountryModel.class);
                                        if (model.status != null && model.status) {
                                            if (model.Data.size() > 0) {
                                                for (CountryModel.Data data : model.Data) {
                                                    idsList.add(data.CountryCode);
                                                    textList.add(data.CountryName);
                                                }
                                                selectDropDown(containerActivity, "Select Country", follow_country, textList);
                                            } else {
                                                Toast.makeText(context, "No Data Found", Toast.LENGTH_SHORT).show();
                                            }

                                        } else {
                                            Toast.makeText(context, "Unable To Connect To Server", Toast.LENGTH_SHORT).show();
                                            //utils.showToast(containerActivity, "Unable To Connect To Server");
                                        }
                                    }
                                }
                            });
                        }

                        @Override
                        public void onError(String msg) {

                        }
                    });
                break;

                case R.id.et_state:
                    idsList = new ArrayList<>();
                    textList = new ArrayList<>();
                    dialog.show();

                    clickRefernece = STATE;

                    String u1 = "http://tapi.therevgo.in/api/State?CountryCode="+country_id;
                    HttpConnection.RequestGet(u1, new ResponseListener() {
                        @Override
                        public void onResponse(final int statusCode, final JSONObject jsonObject) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dialog.dismiss();
                                    if (statusCode == 200) {
                                        StateModel model = gson.fromJson(jsonObject.toString(), StateModel.class);
                                        if (model.status != null && model.status) {
                                            if (model.Data.size() > 0) {
                                                for (StateModel.Data data : model.Data) {
                                                    idsList.add(String.valueOf(data.id));
                                                    textList.add(data.ST_NAME);
                                                }
                                                selectDropDown(containerActivity, "Select State", follow_state, textList);
                                            } else {
                                                Toast.makeText(context, "No Data Found", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            Toast.makeText(context, "Unable To Connect To Server", Toast.LENGTH_SHORT).show();
                                            //utils.showToast(containerActivity, "Unable To Connect To Server");
                                        }
                                    }
                                }
                            });
                        }

                        @Override
                        public void onError(String msg) {

                        }
                    });
                break;
            }
        }
    }

    public void selectDropDown(Context ctx, String title, final TextView txt, final ArrayList<String> list) {

        final ArrayAdapter arrayAdapter = new ArrayAdapter<>(ctx, android.R.layout.simple_list_item_1, list);

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(ctx);
        builderSingle.setTitle(title);

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog1, int which) {
                txt.setText(list.get(which));
                switch (clickRefernece) {
                    case COUNTRY:
                        //optionID = MessageID.get(which);
                        country_id = idsList.get(which);
                    break;
                    case STATE:
                        // productID = MessageID.get(which);
                        state_id = idsList.get(which);
                    break;
                }
            }
        });
        builderSingle.show();
    }

    private boolean validateForm() {
        if (!utils.validateView(getActivity(), follow_name, getString(R.string.err_msg_name))) {
            return false;
        }
        if (!utils.validateView(getActivity(), follow_email, getString(R.string.err_msg_email))) {
            return false;
        }
        if (!utils.validateView(getActivity(), follow_mobile, "Enter correct mobile No.")) {
            return false;
        }

        String regx = "[0-9]+";
        Pattern pattern = Pattern.compile(regx);
        if (follow_mobile.getText().length() != 10) {
            follow_mobile.setError("Enter Valid Number");
            return false;
        } else if (!pattern.matcher(follow_mobile.getText().toString()).matches()) {
            follow_mobile.setError("Enter Valid Number");
            return false;
        }

        if (!utils.validateView(getActivity(), follow_area, "Enter Area Name")) {
            return false;
        }
        if (!utils.validateView(getActivity(), follow_city, "Enter City Name")) {
            return false;
        }
        if (!utils.validateView(getActivity(), follow_pincode, "Enter PinCode")) {
            return false;
        }

        if (!utils.validateView(getActivity(), follow_state, "Enter State Name")) {
            return false;
        }
        if (!utils.validateView(getActivity(), follow_country, "Enter Country Name")) {
            return false;
        }
        return true ;
    }

    private void insertValues() {
        inserting = true ;
        dialog.show();
        String URL = "http://tapi.therevgo.in/api/BusinessListing/BUSCONTINS";
        HttpConnection.RequestPost(URL, getRequestParam(), this);
    }

    private void updatesValues() {
        updating = true ;
        dialog.show();

        String URL ="http://tapi.therevgo.in/api/BusinessListing/BUSCONTUPD?"+
                "userid="+user_id+
                "&name="+ URLEncoder.encode(follow_name.getText().toString())+
                "&mobile_no="+ URLEncoder.encode(follow_mobile.getText().toString())+
                "&email_id="+ URLEncoder.encode(follow_email.getText().toString())+
                "&landline_no=na"+
                "&fax_no=na"+
                "&area="+ URLEncoder.encode(follow_area.getText().toString())+
                "&city="+ URLEncoder.encode(follow_city.getText().toString())+
                "&pinncode="+ URLEncoder.encode(follow_pincode.getText().toString())+
                "&state="+ URLEncoder.encode(follow_state.getText().toString())+
                "&country="+URLEncoder.encode(follow_country.getText().toString())+
                "&id="+ URLEncoder.encode(con_id+"");


        HttpConnection.RequestPost(URL, null, this);
    }

    private ArrayList<NameValuePair> getRequestParam() {
        ArrayList<NameValuePair> map = new ArrayList<>();
        map.add(new BasicNameValuePair("userid", user_id));
        map.add(new BasicNameValuePair("name", follow_name.getText().toString()));
        map.add(new BasicNameValuePair("mobile_no", follow_mobile.getText().toString()));
        map.add(new BasicNameValuePair("email_id", follow_email.getText().toString()));
        map.add(new BasicNameValuePair("area", follow_area.getText().toString()));
        map.add(new BasicNameValuePair("city", follow_city.getText().toString()));
        map.add(new BasicNameValuePair("pinncode", follow_pincode.getText().toString()));

        map.add(new BasicNameValuePair("landline_no", "N/A"));
        map.add(new BasicNameValuePair("fax_no", "N/A"));

        map.add(new BasicNameValuePair("state", follow_state.getText().toString()));
        map.add(new BasicNameValuePair("country", follow_country.getText().toString()));

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
                status = null;
            }
            if (status != null && status.equals("true")) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        if (inserting) {
                            inserting = false;
                            Toast.makeText(context, "Details Entered Successfully", Toast.LENGTH_SHORT).show();
                            containerActivity.finish();
                            //changeFragment();
                        }

                        if (updating) {
                            updating = false ;
                            Toast.makeText(context, "Details Entered Successfully", Toast.LENGTH_SHORT).show();
                            containerActivity.finish();
                            //emptyView();
                        }
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
                        emptyView();
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
                //emptyView();
            }
        });
    }

    private void setValueToViews(BusinessProfileModel.ListModel data) {
        follow_name.setText(data.c_name);
        follow_mobile.setText(data.c_mobile_no);
        follow_email.setText(data.c_email_id);
        follow_area.setText(data.c_area);
        follow_city.setText(data.c_city);
        follow_pincode.setText(data.c_pinncode);
        follow_state.setText(data.c_state_name);
        follow_country.setText(data.c_country_name);

        country_id = data.c_country;
        state_id = data.c_state;
    }

    private void emptyView() {
        follow_name.setText("");
        follow_mobile.setText("");
        follow_email.setText("");
        follow_area.setText("");
        follow_city.setText("");
        follow_pincode.setText("");
        follow_state.setText("");
        follow_country.setText("");
    }

    private void changeFragment() {
        Fragment fragment = new BusinessInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(CONTACT_ID, con_id);

        fragment.setArguments(bundle);

        containerActivity.attachFragment(fragment, BusinessInfoFragment.TAG);
    }
}
