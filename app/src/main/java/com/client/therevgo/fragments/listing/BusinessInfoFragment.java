package com.client.therevgo.fragments.listing;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.therevgo.R;
import com.client.therevgo.activities.ContainerActivity;
import com.client.therevgo.dto.BusinessInfoModel;
import com.client.therevgo.dto.CountryModel;
import com.client.therevgo.dto.StateModel;
import com.client.therevgo.networks.HttpConnection;
import com.client.therevgo.networks.ResponseListener;
import com.client.therevgo.utility.PrefManager;
import com.client.therevgo.utility.Utils;
import com.google.gson.Gson;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class BusinessInfoFragment extends Fragment implements ResponseListener {

    public static final String TAG = BusinessInfoFragment.class.getName();

    private static final int OPTION = 0;
    private static final int PRODUCT = 1;
    private static final int SUB_PRODUCT = 2;
    private static final int COUNTRY = 3;
    private static final int STATE = 4;
    private final Gson gson = new Gson();

    public String country_id, state_id ;

    private static final String[]paths = {"Select Company Type", "Limited", "Pvt. Ltd.", "Firm"};

    private int ClickRefernece;
    private String optionID = null, productID = null, subProductID = null;

    //define views
    private EditText et_company_name, et_company_type, et_option,
                     et_category, et_subcategory,et_business_name,
                        et_business_date, et_website;
    private EditText et_area,et_city, et_pincode, et_state, et_country;



    private Button btn_submit, btn_update;
    private Spinner spinner_com_type ;

    public int con_id, info_id ;

    private ProgressDialog dialog;

    private boolean fetchingData, insertingData, updatingData;

    private Handler handler = new Handler();

    private String user_id  ;

    private Context context;

    private ContainerActivity containerActivity;
    private Utils utils = Utils.getInstance();
    
    private List<String> MessageID;
    private ArrayList<String> MessageText;

    public BusinessInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_business_info, container, false);
        
        context = getActivity();
        
        containerActivity = (ContainerActivity) context;
        containerActivity.setTitle("Business Info");

        user_id = (String) PrefManager.getInstance(context).getDataFromPreference(PrefManager.Key.USER_ID, PrefManager.Type.TYPE_STRING);

        et_company_name = (EditText)  view.findViewById(R.id.et_com_name);

        et_option = (EditText)  view.findViewById(R.id.et_option);
        et_category = (EditText)  view.findViewById(R.id.et_category);
        et_subcategory = (EditText)  view.findViewById(R.id.et_sub_category);

        et_option.setOnClickListener(new MyClickListener());
        et_category.setOnClickListener(new MyClickListener());
        et_subcategory.setOnClickListener(new MyClickListener());

        et_business_name = (EditText) view.findViewById(R.id.et_business_name);

        et_business_date = (EditText) view.findViewById(R.id.et_business_date);
        et_business_date.setOnClickListener(new MyClickListener());

        et_website = (EditText) view.findViewById(R.id.et_website);
        
        //area views 
        et_area = (EditText) view.findViewById(R.id.et_area);
        et_city = (EditText) view.findViewById(R.id.et_city);
        et_pincode = (EditText) view.findViewById(R.id.et_pin_code);
        et_state = (EditText) view.findViewById(R.id.et_state);
        et_country = (EditText) view.findViewById(R.id.et_country);

        spinner_com_type = (Spinner)view.findViewById(R.id.spine_com_type);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_item,paths);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_com_type.setAdapter(adapter);

        dialog = new ProgressDialog(context);
        dialog.setMessage("Please Wait");
        dialog.setCancelable(false);

        btn_submit = (Button) view.findViewById(R.id.btn_submit);
        btn_update = (Button) view.findViewById(R.id.btn_update);

        et_state.setOnClickListener(new MyClickListener());
        et_country.setOnClickListener(new MyClickListener());
        btn_submit.setOnClickListener(new MyClickListener());
        btn_update.setOnClickListener(new MyClickListener());

        Bundle bundle = getArguments();
        if (bundle != null) {
            con_id = bundle.getInt(BusinessContactInfoFragment.CONTACT_ID);

            String url = "http://tapi.therevgo.in/api/BusinessInfoListing/GetBusinfoSel?" +
                    "userid=" +user_id+
                    "&con_id="+con_id;

            fetchingData = true ;
            dialog.show();
            HttpConnection.RequestGet(url, this);
        }

        return view;
    }
    public void selectDropDown(Context ctx, String title, final TextView txt, final ArrayList<String> list) {

        final ArrayAdapter arrayAdapter = new ArrayAdapter<>(ctx, android.R.layout.simple_list_item_1, list);

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(ctx);
        builderSingle.setTitle(title);

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog1, int which) {
                txt.setText(list.get(which));
                switch (ClickRefernece) {
                    case OPTION:
                        //optionID = MessageID.get(which);
                        optionID = MessageID.get(which);
                    break;

                    case PRODUCT:
                        // productID = MessageID.get(which);
                        productID = MessageID.get(which);
                    break;

                    case SUB_PRODUCT:
                       //  subProductID = MessageID.get(which);
                        subProductID = MessageID.get(which);
                    break;

                    case COUNTRY:
                        //  subProductID = MessageID.get(which);
                        country_id = MessageID.get(which);
                    break;

                    case STATE:
                        //  subProductID = MessageID.get(which);
                        state_id = MessageID.get(which);
                    break;
                }
            }
        });
        builderSingle.show();
    }

    private class MyClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {

                case R.id.et_option:
                    MessageID = new ArrayList<>();
                    MessageText = new ArrayList<>();
                    dialog.show();

                    ClickRefernece = OPTION;

                    // productID = -1 ;
                    // subProductID = -1 ;
                    productID = null ;
                    subProductID = null ;


                    et_category.setText(null);
                    et_subcategory.setText(null);

                    String u0 = "http://tapi.therevgo.in/api/BusOption";
                    HttpConnection.RequestGet(u0, new ResponseListener() {
                        @Override
                        public void onResponse(final int Statuscode, final JSONObject jsonObject) {
                            containerActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dialog.dismiss();
                                    if (Statuscode == 200) {
                                        try {
                                            if (!jsonObject.isNull("status") && jsonObject.getBoolean("status")) {
                                                JSONArray jsonArray = jsonObject.getJSONArray("Data");
                                                if (jsonArray.length() > 0) {
                                                    for (int i = 0; i <= jsonArray.length() - 1; i++) {
                                                        JSONObject object = jsonArray.getJSONObject(i);
                                                        MessageID.add(object.getString("optionid"));
                                                        MessageText.add(object.getString("optionname"));
                                                    }
                                                    dialog.dismiss();
                                                    selectDropDown(containerActivity, "Select Option", et_option, MessageText);
                                                } else {
                                                    Toast.makeText(context, "No Data Found", Toast.LENGTH_SHORT).show();
                                                    //utils.showToast(containerActivity, "No Data Found");
                                                }
                                            } else {
                                                Toast.makeText(context, "Unable To Connect To Server", Toast.LENGTH_SHORT).show();
                                                //utils.showToast(containerActivity, "Unable To Connect To Server");
                                            }
                                        } catch (JSONException e) {
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
                case R.id.et_category:
                    if (optionID == /*-1*/null) {
                        Toast.makeText(context, "Please select option first", Toast.LENGTH_SHORT).show();
                        //utils.showToast(context, "Please select option first");
                        return;
                    }
                    MessageID = new ArrayList<>();
                    MessageText = new ArrayList<>();
                    dialog.show();

                    ClickRefernece = PRODUCT;

                    subProductID = /*-1*/ null ;

                    et_category.setText(null);
                    et_subcategory.setText(null);

                    String u1 = "http://tapi.therevgo.in/api/Product/GetCategoryByProductID?productid="+optionID;
                    HttpConnection.RequestGet(u1, new ResponseListener() {
                        @Override
                        public void onResponse(final int Statuscode, final JSONObject jsonObject) {
                            containerActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dialog.dismiss();
                                    if (Statuscode == 200) {
                                        try {
                                            if (!jsonObject.isNull("status") && jsonObject.getBoolean("status")) {
                                                JSONArray jsonArray = jsonObject.getJSONArray("Data");
                                                if (jsonArray.length() > 0) {
                                                    for (int i = 0; i <= jsonArray.length() - 1; i++) {
                                                        JSONObject object = jsonArray.getJSONObject(i);
                                                        MessageID.add(object.getString("id"));
                                                        MessageText.add(object.getString("categoryName"));
                                                    }
                                                    dialog.dismiss();
                                                    selectDropDown(containerActivity, "Select Option", et_category, MessageText);
                                                } else {
                                                    Toast.makeText(context, "No Data Found", Toast.LENGTH_SHORT).show();
                                                    //utils.showToast(containerActivity, "No Data Found");
                                                }
                                            } else {
                                                Toast.makeText(context, "Unable To Connect To Server", Toast.LENGTH_SHORT).show();
                                                //utils.showToast(containerActivity, "Unable To Connect To Server");
                                            }
                                        } catch (JSONException e) {
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

                case R.id.et_sub_category:
                    if (productID == /*-1*/ null) {
                        Toast.makeText(context, "Please select Category first", Toast.LENGTH_SHORT).show();
                        //utils.showToast(context, "Please select Category first");
                        return;
                    }
                    MessageID = new ArrayList<>();
                    MessageText = new ArrayList<>();
                    dialog.show();

                    ClickRefernece = SUB_PRODUCT;

                    String u = "http://tapi.therevgo.in/api/SubCat?id="+productID+"&option_id="+optionID;
                    HttpConnection.RequestGet(u, new ResponseListener() {
                        @Override
                        public void onResponse(final int Statuscode, final JSONObject jsonObject) {
                            containerActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dialog.dismiss();
                                    if (Statuscode == 200) {
                                        try {
                                            if (!jsonObject.isNull("status") && jsonObject.getBoolean("status")) {
                                                JSONArray jsonArray = jsonObject.getJSONArray("Data");
                                                if (jsonArray.length() > 0) {
                                                    for (int i = 0; i <= jsonArray.length() - 1; i++) {
                                                        JSONObject object = jsonArray.getJSONObject(i);
                                                        MessageID.add(object.getString("id"));
                                                        MessageText.add(object.getString("sub_category_name"));
                                                    }
                                                    selectDropDown(containerActivity, "Select Category", et_subcategory, MessageText);
                                                } else {
                                                    Toast.makeText(context, "No Data Found", Toast.LENGTH_SHORT).show();
                                                    //utils.showToast(containerActivity, "No Data Found");
                                                }
                                            } else {
                                                Toast.makeText(context, "Unable To Connect To Server", Toast.LENGTH_SHORT).show();
                                                //utils.showToast(containerActivity, "Unable To Connect To Server");
                                            }
                                        } catch (JSONException e) {
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

                case R.id.et_country:
                    MessageID = new ArrayList<>();
                    MessageText = new ArrayList<>();
                    dialog.show();

                    ClickRefernece = COUNTRY;
                    state_id = null ;
                    et_state.setText(null);

                    String u2 = "http://tapi.therevgo.in/api/Country";
                    HttpConnection.RequestGet(u2, new ResponseListener() {
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
                                                    MessageID.add(data.CountryCode);
                                                    MessageText.add(data.CountryName);
                                                }
                                                selectDropDown(containerActivity, "Select Country", et_country, MessageText);
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
                    MessageID = new ArrayList<>();
                    MessageText = new ArrayList<>();
                    dialog.show();

                    ClickRefernece = STATE;

                    String u3 = "http://tapi.therevgo.in/api/State?CountryCode="+country_id;
                    HttpConnection.RequestGet(u3, new ResponseListener() {
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
                                                    MessageID.add(String.valueOf(data.id));
                                                    MessageText.add(data.ST_NAME);
                                                }
                                                selectDropDown(containerActivity, "Select State", et_state, MessageText);
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

                case R.id.et_business_date:
                    Date date = new Date();
                    int year = 1900 + date.getYear();
                    int month = date.getMonth();
                    int day = date.getDate();
                    DatePickerDialog dialog = new DatePickerDialog(context, new MyDateListener(), year, month, day);
                    dialog.show();
                break;

                case R.id.btn_submit:

                    Button btn = (Button) v;
                    if(btn.getText().toString().equals("NEXT")) {
                        
                        changeFragment();
                    } else {
                        if(validateForm()) {
                            insertingData = true ;
                            insertValues();
                        }
                    }
                break;

                case R.id.btn_update:
                    if(validateForm()) {
                        updatingData = true ;
                        updatesValues();
                    }
                break;
            }
        }
    }

    private void changeFragment() {
        Fragment fragment = new BusinessLogoFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(BusinessContactInfoFragment.CONTACT_ID, con_id);

        fragment.setArguments(bundle);
        containerActivity.attachFragment(fragment,BusinessLogoFragment.TAG);
    }

    private class MyDateListener implements DatePickerDialog.OnDateSetListener {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            monthOfYear = monthOfYear + 1;

            if (monthOfYear <= 9) {
                et_business_date.setText("0" + monthOfYear + "/" + dayOfMonth + "/" + year);
            } else {
                et_business_date.setText(monthOfYear + "/" + dayOfMonth + "/" + year);
            }

        }
    }


    private boolean validateForm() {
        if (!utils.validateView(containerActivity, et_company_name, "Enter Company Name")) {
            return false;
        }
        if (spinner_com_type.getSelectedItemPosition() == 0) {
            utils.displayAlert(context, "", "Please Select Company Type");
            return false;
        }

        if (et_option.getText().toString().isEmpty()) {
            utils.displayAlert(context, "Warning", "Please Select Option ");
            return false;
        }

        if (et_category.getText().toString().isEmpty()) {
            utils.displayAlert(context, "Warning", "Please Select Category ");
            return false;
        }

        if (et_subcategory.getText().toString().isEmpty()) {
            utils.displayAlert(context, "Warning", "Please Select Sub Category");
            return false;
        }
        if (!utils.validateView(containerActivity, et_business_name, "Enter Business Name")) {
            return false;
        }
        if (!utils.validateView(containerActivity, et_business_date, "Enter Business Start Date")) {
            return false;
        }
        if (!utils.validateView(containerActivity, et_website, "Enter Website URL")) {
            return false;
        }

       

        if (!utils.validateView(containerActivity, et_area, "Enter Area Name")) {
            return false;
        }
        if (!utils.validateView(containerActivity, et_city, "Enter City Name")) {
            return false;
        }
        if (!utils.validateView(containerActivity, et_pincode, "Enter PinCode")) {
            return false;
        }

        if (!utils.validateView(containerActivity, et_country, "Enter Country Name")) {
            return false;
        }
        if (!utils.validateView(containerActivity, et_state, "Enter State Name")) {
            return false;
        }
        return true ;
    }

    private void insertValues(){
        dialog.show();
        String URL = "http://tapi.therevgo.in/api/BusinessInfoListing/BUSInfoINS";
        HttpConnection.RequestPost(URL, getRequestParam(), BusinessInfoFragment.this);
    }
    private void updatesValues(){
        dialog.show();

        //http://tapi.therevgo.in/api/BusinessInfoListing/BUSInfoUPD
        String URL ="http://tapi.therevgo.in/api/BusinessInfoListing/BUSInfoUPD?"+
                "userid="+          user_id+
                "&comp_name="+      et_company_name.getText().toString()+
                "&company_type="+   spinner_com_type.getSelectedItem().toString()+
                "&bussiness_name="+ et_business_name.getText().toString()+
                "&bussiness_date="+ et_business_date.getText().toString()+
                "&bussiness_type="+ spinner_com_type.getSelectedItem().toString()+
                "&website="+        et_website.getText().toString()+
                "&area="+           et_area.getText().toString()+
                "&city="+           et_city.getText().toString()+
                "&pincode="+        et_pincode.getText().toString()+
                "&state="+          state_id+
                "&country="+        country_id+
                "&con_id="+         con_id+
                "&id="+               info_id+
                "&categoryid="+productID+
                "&subcategoryid="+subProductID+
                "&option_id="+optionID;

        URL = URL.replace(" ","+");
        HttpConnection.RequestPost(URL, null, this);
    }

    private ArrayList<NameValuePair> getRequestParam() {
        ArrayList<NameValuePair> map = new ArrayList<>();
        map.add(new BasicNameValuePair("con_id", ""+con_id));

        map.add(new BasicNameValuePair("userid", user_id));

        map.add(new BasicNameValuePair("comp_name", et_company_name.getText().toString()));
        map.add(new BasicNameValuePair("company_type", spinner_com_type.getSelectedItem().toString()));

        map.add(new BasicNameValuePair("bussiness_name", et_business_name.getText().toString()));
        map.add(new BasicNameValuePair("bussiness_date", et_business_date.getText().toString()));
        map.add(new BasicNameValuePair("bussiness_type", spinner_com_type.getSelectedItem().toString()));

        map.add(new BasicNameValuePair("website", et_website.getText().toString()));

        map.add(new BasicNameValuePair("option_id", ""+optionID));
        map.add(new BasicNameValuePair("categoryid", ""+productID));
        map.add(new BasicNameValuePair("subcategoryid", ""+subProductID));

        map.add(new BasicNameValuePair("area", et_area.getText().toString()));
        map.add(new BasicNameValuePair("city", et_city.getText().toString()));
        map.add(new BasicNameValuePair("pincode", et_pincode.getText().toString()));
        map.add(new BasicNameValuePair("state", state_id));
        map.add(new BasicNameValuePair("country", country_id));

        return map;
    }

    private void setValueToViews(BusinessInfoModel.Data data) {
        info_id = data.id;

        et_company_name.setText(data.comp_name);
        if (data.company_type.equals(paths[1])) {
            spinner_com_type.setSelection(1);
        }
        else if (data.company_type.equals(paths[2])) {
            spinner_com_type.setSelection(2);
        }
        else if (data.company_type.equals(paths[3])) {
            spinner_com_type.setSelection(3);
        }

        optionID = data.option_id;
        productID = String.valueOf(data.categoryid);
        subProductID = String.valueOf(data.subcategoryid);
    
        if (data.optionName != null) {
            et_option.setText(data.optionName);
        }
    
        if (data.categoryName != null) {
            et_category.setText(data.categoryName);
        }
        if (data.sub_category_name != null) {
            et_subcategory.setText(data.sub_category_name);
        }

        et_business_name.setText(data.bussiness_name);
        et_business_date.setText(data.bussiness_date);

        et_website.setText(data.website);

        et_area.setText(data.area);
        et_city.setText(data.city);
        et_pincode.setText(data.pincode);

        state_id = data.state_id;
        country_id = data.country_id;

        et_state.setText(data.state);
        et_country.setText(data.country);
    }

    @Override
    public void onResponse(int Statuscode, JSONObject jsonObject) {
        Gson gson = new Gson();
        final BusinessInfoModel resObj = gson.fromJson(jsonObject.toString(), BusinessInfoModel.class);

        if(fetchingData) {
            fetchingData = false;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (resObj.error == null) {
                        dialog.dismiss();
                        if (resObj.Data != null && resObj.Data.size() > 0) {
                            setValueToViews(resObj.Data.get(0));
                            btn_update.setVisibility(View.VISIBLE);
                            btn_submit.setText("NEXT");
                        }
                    } else {
                        onError(resObj.error);
                    }
                }
            });
        }

        if(insertingData){
            insertingData = false;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (resObj.error == null) {
                        dialog.dismiss();
                        /*if (resObj.Data != null && resObj.Data.size() > 0) {
                            setValueToViews(resObj.Data.get(0));
                        } else {
                            Toast.makeText(context, resObj.message, Toast.LENGTH_SHORT).show();
                            emptyView();
                        }*/
                        Toast.makeText(context, resObj.message, Toast.LENGTH_SHORT).show();
                        changeFragment();
                    } else {
                        onError(resObj.error);
                    }
                }
            });
        }

        if(updatingData) {
            updatingData = false;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (resObj.error == null) {
                        dialog.dismiss();
                        if (resObj.Data != null && resObj.Data.size() > 0) {
                            setValueToViews(resObj.Data.get(0));
                            containerActivity.finish();
                        } else {
                            Toast.makeText(context, resObj.message, Toast.LENGTH_SHORT).show();
                            emptyView();
                            containerActivity.finish();
                        }
                    } else {
                        onError(resObj.error);
                    }
                }
            });
        }

    }

    @Override
    public void onError(final String msg) {
        containerActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                //emptyView();
            }
        });
    }

    private void emptyView() {
        et_company_name.setText("");
        spinner_com_type.setSelection(0);

        et_option.setText("");
        et_category.setText("");
        et_subcategory.setText("");

        et_business_name.setText("");
        et_business_date.setText("");

        et_website.setText("");
        et_area.setText("");
        et_city.setText("");
        et_pincode.setText("");
        et_state.setText("");
        et_country.setText("");
    }

}
