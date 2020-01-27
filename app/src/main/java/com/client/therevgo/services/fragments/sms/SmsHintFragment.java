package com.client.therevgo.services.fragments.sms;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.text.ClipboardManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.therevgo.R;
import com.client.therevgo.services.base.BaseFragment;
import com.client.therevgo.services.constants.Config;
import com.client.therevgo.services.dto.SubSmsType;
import com.client.therevgo.services.networks.HttpConnection;
import com.client.therevgo.services.networks.ResponseListener;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shubham on 27/7/16.
 */
public class SmsHintFragment extends BaseFragment implements ResponseListener {

    public static final String TAG = SmsHintFragment.class.getName();

    private static final int CATEGORY = 1;
    private static final int TYPE = 2;
    private static final int SUB_TYPE = 4;
    private static final int LIST = 3;

    private TextView category , type, subType;
    private Button search_btn ;
    private ListView msgList ;
    private ProgressDialog dialog ;
    private int ClickRefernece , typeID = -1 , sub_typeID = -1,  categoryID = -1 ;

    private List<Integer> MessageID ;
    private ArrayList<String> MessageText ;

    private Handler handler = new Handler();
    private ArrayAdapter adapter ;

    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sms_hint, container, false);

        context = getActivity();

        init(view);
        return view;
    }

    private void init(View view) {
        category = (TextView) view.findViewById(R.id.msgcategory);
        type = (TextView) view.findViewById(R.id.msgType);
        subType = (TextView) view.findViewById(R.id.msgSubType);

        search_btn = (Button) view.findViewById(R.id.btn_search_sms);

        msgList = (ListView) view.findViewById(R.id.msgList);

        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Please Wait...");
        dialog.setCancelable(false);

        intializeList();

        setListener();
    }

    private void setListener() {
        category.setOnClickListener(new MyCLickListener());
        type.setOnClickListener(new MyCLickListener());
        subType.setOnClickListener(new MyCLickListener());
        search_btn.setOnClickListener(new MyCLickListener());

        msgList.setOnItemClickListener(new MyListCLickListener());

    }

    @Override
    public void onResponse(final int Statuscode, final JSONObject jsonObject) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if(Statuscode == 200 && ClickRefernece == CATEGORY ) {
                     try {
                         dialog.dismiss();
                        if( !jsonObject.isNull("status") && jsonObject.getBoolean("status")) {
                          JSONArray jsonArray = jsonObject.getJSONArray("Data");
                           if( jsonArray.length() > 0 ){
                               for (int i = 0; i <= jsonArray.length()-1 ; i++) {
                                   JSONObject object = jsonArray.getJSONObject(i);
                                   MessageID.add(object.getInt("id"));
                                   MessageText.add(object.getString("product"));
                               }
                               selectDropDown(getActivity(),"Select Category",category,MessageText);
                           } else{
                               onError("No Data Found");
                           }
                        }
                        else {
                            onError("Unable To Connect To Server");
                        }
                     } catch (JSONException e) {
                         onError("Unable To Connect To Server");
                     }
                }

                if(Statuscode == 200 && ClickRefernece == TYPE ) {
                    try {
                        dialog.dismiss();
                        if( !jsonObject.isNull("status") && jsonObject.getBoolean("status")) {
                            JSONArray jsonArray = jsonObject.getJSONArray("Data");
                            if( jsonArray.length() > 0 ){
                                for (int i = 0; i <= jsonArray.length()-1 ; i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    MessageID.add(object.getInt("id"));
                                    MessageText.add(object.getString("categoryName"));
                                }
                                selectDropDown(getActivity(),"Select Sub Category",type,MessageText);
                            } else{
                                onError("No Data Found");
                            }
                        }
                        else {
                            onError("Unable To Connect To Server");
                        }
                    } catch (JSONException e) {
                        onError("Unable To Connect To Server");
                    }
                }

                if(Statuscode == 200 && ClickRefernece == SUB_TYPE ) {
                    dialog.dismiss();

                    Gson gson = new Gson();
                    final SubSmsType resObj = gson.fromJson(jsonObject.toString(), SubSmsType.class);

                    if (resObj.Data != null && resObj.Data.size() > 0) {
                        for (SubSmsType.Data data : resObj.Data) {
                            MessageID.add(data.id);
                            MessageText.add(data.sub_category_name);
                        }
                        selectDropDown(getActivity(),"Select Message Type",subType,MessageText);
                    } else {
                        subType.setVisibility(View.GONE);
                    }

                }

                if(Statuscode == 200 && ClickRefernece == LIST ) {
                    try {
                        dialog.dismiss();
                        if( !jsonObject.isNull("status") && jsonObject.getBoolean("status")) {
                            JSONArray jsonArray = jsonObject.getJSONArray("Data");
                            if( jsonArray.length() > 0 ){
                                for (int i = 0; i <= jsonArray.length()-1 ; i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    MessageText.add(object.getString("msg_description"));
                                }
                                showList(MessageText);
                            } else{
                                //category.setText("No Data Found");
                                onError("No Data Found");
                            }
                        }
                        else {
                            onError("Unable To Connect To Server");
                        }
                    } catch (JSONException e) {
                        onError("Unable To Connect To Server");
                    }
                }
            }
        });
    }

    private void showList(ArrayList<String> messageText) {
         adapter= new ArrayAdapter<>(context, R.layout.simple_text_view, messageText);
         msgList.setAdapter(adapter);
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

    private void intializeList(){
        MessageID = new ArrayList<>();
        MessageText = new ArrayList<>();
    }

    @Override
    protected String getTitle() {
        return "SMS Hint";
    }

    private class MyCLickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int id =  v.getId();
            switch (id){
                case R.id.msgcategory:

                    if (adapter != null) {
                       adapter.clear();
                    }

                    if (type != null ) {
                        type.setText(getResources().getString(R.string.message_type));
                        typeID = -1;
                    }

                    if (subType != null ) {
                        subType.setText(getResources().getString(R.string.message_type));
                        sub_typeID = -1;
                    }

                    subType.setVisibility(View.VISIBLE);

                    dialog.show();
                    intializeList();

                    ClickRefernece  = CATEGORY ;
                    String URL = Config.DOMAIN+"api/MessageCategory";
                    HttpConnection.RequestGet(URL,SmsHintFragment.this);
                break;

                case R.id.msgType:

                    if (adapter != null) {
                        adapter.clear();
                    }

                    if( categoryID != -1) {

                        if (subType != null ) {
                            subType.setText(getResources().getString(R.string.message_type));
                            sub_typeID = -1;
                        }

                        dialog.show();
                        intializeList();

                        subType.setVisibility(View.VISIBLE);

                        ClickRefernece = TYPE;
                        String URL_MSG_TYPE = Config.DOMAIN + "api/Product/GetCategoryByProductID?productid=" + categoryID;
                        HttpConnection.RequestGet(URL_MSG_TYPE, SmsHintFragment.this);
                    } else{
                        Toast.makeText(context, "Please Select Category", Toast.LENGTH_SHORT).show();
                    }

                break;

                case R.id.msgSubType:

                    if (adapter != null) {
                        adapter.clear();
                    }

                    if( typeID != -1) {
                        dialog.show();
                        intializeList();

                        ClickRefernece = SUB_TYPE;
                        String URL_MSG_TYPE = Config.DOMAIN + "api/SubCat?id="+typeID+"&option_id=" +categoryID ;
                        HttpConnection.RequestGet(URL_MSG_TYPE, SmsHintFragment.this);
                    } else{
                        Toast.makeText(context, "Please Select Sub Category", Toast.LENGTH_SHORT).show();
                    }

                break;

                case R.id.btn_search_sms:
                    String url;

                    if (subType.getVisibility() == View.VISIBLE) {
                        url = "http://tapi.therevgo.in/api/MessageTips?id="+typeID+"&sub_cat_id="+ sub_typeID;
                    } else {
                        url = "http://tapi.therevgo.in/api/MessageTips?id="+typeID+"&sub_cat_id=0";
                    }

                    //
                    //or http://tapi.therevgo.in/api/MessageTips?id=0&sub_cat_id=14

                    ClickRefernece = LIST;
                    intializeList();
                    dialog.show();
                    HttpConnection.RequestGet(url, SmsHintFragment.this);
                break ;
            }
        }
    }

    private class MyListCLickListener implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

           ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
           clipboard.setText(MessageText.get(position));
            Toast.makeText(context, "Text Copied", Toast.LENGTH_SHORT).show();

        }
    }

    public void selectDropDown(Context ctx, String title , final TextView txt, final ArrayList<String> list) {

       final ArrayAdapter arrayAdapter = new ArrayAdapter<>(ctx,android.R.layout.simple_list_item_1,list);

       AlertDialog.Builder builderSingle = new AlertDialog.Builder(ctx);
       builderSingle.setTitle(title);

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog1, int which) {
               txt.setText(list.get(which));
               switch (ClickRefernece) {
                   case CATEGORY:
                       categoryID = MessageID.get(which);
                   break;
                   case TYPE:
                       typeID = MessageID.get(which);
                   break;
                   case SUB_TYPE:
                       sub_typeID = MessageID.get(which);
                   break;
               }
            }
        });
        builderSingle.show();
    }

 /*   private class MyAdapter extends ArrayAdapter<String> {

        private Context context ;
        private int layout ;
        private ArrayList<String> mList;
        private LayoutInflater inflater ;

        public MyAdapter(Context context, int resource, ArrayList<String> objects) {
            super(context, resource, objects);
            this.context = context ;
            this.layout = resource ;
            this.mList = objects ;

            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view = convertView;
            if( view == null ) {
                 view = inflater.inflate(layout,null);
            }



            return super.getView(position, convertView, parent);
        }
    }

    // Copy EditCopy text to the ClipBoard
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void copyToClipBoard(String mssg)
    {

    }*/
}



