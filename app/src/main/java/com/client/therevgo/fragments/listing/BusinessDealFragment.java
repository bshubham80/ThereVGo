package com.client.therevgo.fragments.listing;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.therevgo.R;
import com.client.therevgo.activities.ContainerActivity;
import com.client.therevgo.adapters.BusinessDealAdapter;
import com.client.therevgo.dto.BusinessDealDTO;
import com.client.therevgo.dto.ListStatusDTO;
import com.client.therevgo.networks.HttpConnection;
import com.client.therevgo.networks.ResponseListener;
import com.client.therevgo.utility.PrefManager;
import com.client.therevgo.utility.Utils;
import com.google.gson.Gson;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class BusinessDealFragment extends Fragment implements ResponseListener {

    public static final String TAG = BusinessDealFragment.class.getName();
    private EditText et_deal_in;

    private ListView list_deal;

    public int con_id/*, info_id*/, mSelectedIndex = -1 ;
    private String user_id ;

    private ProgressDialog dialog;

    private Handler handler = new Handler();

    private BusinessDealAdapter adapter;

    private boolean fetchingData, insertingData, updatingData;

    private String mUpdateText ;
    private ContainerActivity activity;

    private Utils utils = Utils.getInstance();

    private Context context;
    Button btn_submit, btn_live;
    public BusinessDealFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_business_deal, container, false);

        context = getActivity();

        activity = (ContainerActivity) context;
        activity.setTitle("Update Keyword");

        user_id = (String) PrefManager.getInstance(context).getDataFromPreference(PrefManager.Key.USER_ID, PrefManager.Type.TYPE_STRING);


        et_deal_in = (EditText)  view.findViewById(R.id.et_deal_item);

        btn_live = (Button) view.findViewById(R.id.btn_live_deals);
        btn_submit = (Button) view.findViewById(R.id.btn_save_deals);

        if (BusinessContactInfoFragment.LIVE_STATUS.equals("Y")) {
            btn_live.setText("Unlive Your Listing");
        } else {
            btn_live.setText("Live Your Listing");
        }
        btn_submit.setOnClickListener(new MyClickListener());
        btn_live.setOnClickListener(new MyClickListener());

        list_deal = (ListView) view.findViewById(R.id.list_deal);
        list_deal.setEmptyView(view.findViewById(R.id.contact_list_empty));
        list_deal.setDividerHeight(0);

        dialog = new ProgressDialog(context);
        dialog.setMessage("Please Wait");
        dialog.setCancelable(false);

        Bundle bundle = getArguments();
        if (bundle != null){
            con_id = bundle.getInt(BusinessContactInfoFragment.CONTACT_ID);
            fetchData();
        }


        return view;
    }

    private void fetchData(){
        String Url = "http://tapi.therevgo.in/api/BusinessDealsListing/GetBusDealsSel?" +
                "userid="+user_id+
                "&con_id="+con_id;

        fetchingData = true ;
        dialog.show();
        HttpConnection.RequestGet(Url, this);
    }

    @Override
    public void onResponse(int StatusCode, JSONObject jsonObject) {
          if (insertingData) {
              Gson gson = new Gson();
              final BusinessDealDTO resObj = gson.fromJson(jsonObject.toString(), BusinessDealDTO.class);

              insertingData = false ;
              handler.post(new Runnable() {
                  @Override
                  public void run() {
                      if (resObj.error == null) {
                          dialog.dismiss();
                          Toast.makeText(context, resObj.message, Toast.LENGTH_SHORT).show();
                          //Utils.showToast(context, resObj.message);
                          et_deal_in.setText("");
                          
                          if ( resObj.Data.size() > 6){
                              btn_submit.setText("Only 6 keywords allowed for a business");
                              btn_submit.setEnabled(false);
                          }
                          fetchData();
                      } else {
                          onError(resObj.error);
                      }
                  }
              });
          }

        if (fetchingData) {
            Gson gson = new Gson();
            final BusinessDealDTO resObj = gson.fromJson(jsonObject.toString(), BusinessDealDTO.class);

            fetchingData = false ;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (resObj.error == null) {
                        dialog.dismiss();
                        if (resObj.Data != null && resObj.Data.size() > 0) {
                            adapter = new BusinessDealAdapter(context, R.layout.business_list_layout, resObj.Data, BusinessDealFragment.this);
                            list_deal.setAdapter(adapter);
                            if ( resObj.Data.size() > 6){
                                btn_submit.setText("Only 6 keywords allowed for a business");
                                btn_submit.setEnabled(false);
                            }
                        } else {
                            Toast.makeText(context, "No Record Found", Toast.LENGTH_SHORT).show();
                            //Utils.showToast(context,"No Record Found");
                        }
                    } else {
                        onError(resObj.error);
                    }
                }
            });
        }

        if (updatingData) {
            Gson gson = new Gson();
            final BusinessDealDTO resObj = gson.fromJson(jsonObject.toString(), BusinessDealDTO.class);

            updatingData = false ;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (resObj.error == null) {
                        dialog.dismiss();
                        Toast.makeText(context, resObj.message, Toast.LENGTH_SHORT).show();
                        //Utils.showToast(context, resObj.message);
                        BusinessDealDTO.Data data = (BusinessDealDTO.Data) list_deal.getAdapter().getItem(mSelectedIndex);
                        data.deals_name  = mUpdateText;
                        adapter.notifyDataSetChanged();
                        activity.finish();
                    } else {
                        onError(resObj.error);
                    }
                }
            });
        }
    }

    @Override
    public void onError(final String msg) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                //Utils.showToast(activity, msg);
                //emptyView();
            }
        });
    }

    private class MyClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.btn_save_deals:
                    if(validateForm()) {
                        String reg = "[a-zA-Z ]+";
                        if (utils.validateString(et_deal_in.getText().toString(), reg)) {
                            insertingData = true;
                            insertValues();
                        } else {
                            utils.displayAlert(context, "Error", "Keyword should contain alphanumeric value only");
                        }
                    }
                break;

                case R.id.btn_live_deals:
                    String msg, btnText ;
                    if (BusinessContactInfoFragment.LIVE_STATUS.equals("Y")) {
                        msg = "You List will remove.";
                        btnText = "UNLIVE";
                    } else {
                        msg = "We will live your list with in 48 hours";
                        btnText = "LIVE";
                    }
                    new AlertDialog.Builder(context)
                    .setCancelable(false)
                    .setTitle("Information")
                    .setMessage(msg)
                    .setPositiveButton(btnText, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            updateStatus();
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
                break;
            }
        }
    }

    private void updateStatus() {
        String url = "http://tapi.therevgo.in/api/BusinessListing/BUSCONTLIVE?";

        url+="userid=" + PrefManager.getInstance(context).getDataFromPreference(PrefManager.Key.USER_ID,
                PrefManager.Type.TYPE_STRING);

        url+="&con_id=" + con_id;

        if (BusinessContactInfoFragment.LIVE_STATUS.equals("N")) {
            url += "&live_status=Y";
        } else {
            url += "&live_status=N";
        }

        HttpConnection.RequestGet(url, new ResponseListener() {
            @Override
            public void onResponse(int statusCode, final JSONObject jsonObject) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ListStatusDTO profileModel =
                                new Gson().fromJson(jsonObject.toString(), ListStatusDTO.class);
                        if (profileModel.Data != null && profileModel.Data.size() > 0) {
                            Toast.makeText(activity, "Thanks for list your business. We will live your list with in 48 hours ", Toast.LENGTH_SHORT).show();
                        } else {
                            if (profileModel.error != null) {
                                onError(profileModel.error);
                            } else {
                                onError("Unable to connect server");
                            }
                        }
                    }
                });
            }

            @Override
            public void onError(final String msg) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private boolean validateForm() {
        return utils.validateView(activity, et_deal_in, "Enter your keyword");
    }

    private void insertValues(){
        dialog.show();
        String URL = "http://tapi.therevgo.in/api/BusinessDealsListing/BUSDealsINSl";
        HttpConnection.RequestPost(URL, getRequestParam(), this);
    }
    private ArrayList<NameValuePair> getRequestParam() {
        ArrayList<NameValuePair> map = new ArrayList<>();
        map.add(new BasicNameValuePair("con_id", ""+con_id));
        map.add(new BasicNameValuePair("userid", user_id));
        map.add(new BasicNameValuePair("deals_name", et_deal_in.getText().toString()));

        return map;
    }

    public void UpdateValuesDialog(final int id, final int position) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);

        final RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(10,10,10,10);


        final EditText edittext = new EditText(context);

        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(20);
        edittext.setFilters(FilterArray);

        edittext.setLayoutParams(params);
        edittext.setPadding(10,10,10,10);

        alert.setTitle("Update Your Keyword");
        alert.setView(edittext);

        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                utils.hideKeyboard(context);

                //What ever you want to do with the value
                String dealsName = edittext.getText().toString();

                String reg = "[a-zA-Z ]+";
                if (!utils.validateString(dealsName, reg)) {
                    edittext.setError("Keyword should contain alphanumeric value only");
                    return ;
                } else {

                    mUpdateText = dealsName;
                    mSelectedIndex = position;
                    dialog.dismiss();
                    String url =  "http://tapi.therevgo.in/api/BussListingDealsUpd/BUSDealsUPD"+
                            "?userid="+user_id+
                            "&deal_name="+dealsName+
                            "&con_id="+con_id+
                            "&id="+id;
                    url = url.replace(" ","+");
                    updateValues(url);
                }
            }
        });

        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // what ever you want to do with No option.
                dialog.dismiss();
            }
        });

        alert.show();
    }

    private void updateValues(String url) {
        dialog.show();
        updatingData = true ;
        HttpConnection.RequestPost(url, null, this);
    }

    /*private View.OnClickListener mListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final int pos = (int) v.getTag();
            TextView textView = (TextView) v;
            String text = textView.getText().toString();

            final Activity activity = (Activity) context;

            String url = "http://tapi.therevgo.in/api/BusinessListing/BUSCONTLIVE?";

            url+="userid=" + prefManager.getDataFromPreference(PrefManager.Key.USER_ID,
                    PrefManager.Type.TYPE_STRING);

            url+="&con_id=" + dataList.get(pos).con_id;

            if (text.equals(LIVE)) {
                url += "&live_status=Y";
            } else if (text.equals(UNLIVE)) {
                url += "&live_status=N";
            }

            HttpConnection.RequestGet(url, new ResponseListener() {
                @Override
                public void onResponse(int statusCode, final JSONObject jsonObject) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ListStatusDTO profileModel =
                                    new Gson().fromJson(jsonObject.toString(), ListStatusDTO.class);
                            if (profileModel.Data != null && profileModel.Data.size() > 0) {
                                dataList.get(pos).c_live_status = profileModel.Data.get(pos).live_status;
                                dataList.get(pos).b_bussiness_date = "jhgfds";
                                notifyDataSetChanged();
                            } else {
                                onError("No Result Found");
                            }
                        }
                    });
                }

                @Override
                public void onError(final String msg) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
    };*/


}
