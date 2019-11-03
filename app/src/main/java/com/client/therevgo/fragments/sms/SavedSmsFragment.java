package com.client.therevgo.fragments.sms;


import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.therevgo.R;
import com.client.therevgo.adapters.SavedSmsAdapter;
import com.client.therevgo.base.BaseFragment;
import com.client.therevgo.constants.Config;
import com.client.therevgo.dto.SavedSmsModel;
import com.client.therevgo.library.SaveSmsLib;
import com.client.therevgo.networks.HttpConnection;
import com.client.therevgo.networks.ResponseListener;
import com.client.therevgo.utility.PrefManager;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * A simple {@link Fragment} subclass.
 */
public class SavedSmsFragment extends BaseFragment {

    public static final String TAG = SavedSmsFragment.class.getName();

    private String user_id ;

    private View view;
    private Context context;

    private ListView mGroupList;

    private TextView emptyTextView ;
    private SavedSmsAdapter adapter;

    private Handler handler = new Handler();

    private ArrayList<SavedSmsModel> mSMSList = new ArrayList<>();

    EditText edit_sms;
    Button btn_save;

    public SavedSmsFragment() {
        // Required empty public constructor
    }

    @Override
    protected String getTitle() {
        return "Edit SMS";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_my_sm, container, false);

        context = getActivity();

        user_id = (String) PrefManager.getInstance(context).getDataFromPreference(PrefManager.Key.USER_ID, PrefManager.Type.TYPE_STRING);

        //find all view views and initialize them
        findViewByIds(view);

        //initialize instances
        initializeInstances(context);

        return view;
    }

    private void findViewByIds(final View view) {
        mGroupList = (ListView) view.findViewById(R.id.sms_list);
        emptyTextView = (TextView) view.findViewById(R.id.empty_view);
        mGroupList.setEmptyView(emptyTextView);

        edit_sms = (EditText) view.findViewById(R.id.et_sms_message);
        btn_save = (Button) view.findViewById(R.id.btn_send_msg);
    }

    private void initializeInstances(final Context context) {

        String url = Config.DOMAIN + "api/MSGSAVE/GetSND_SMS?userid=" + user_id;
        HttpConnection.RequestGet(url, new ResponseListener() {
            @Override
            public void onResponse(int Statuscode, final JSONObject jsonObject) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (!jsonObject.isNull("status") && jsonObject.getBoolean("status")) {
                                JSONArray array = jsonObject.getJSONArray("Data");
                                if (array.length() > 0) {
                                    for (int i = 0; i <= array.length() - 1; i++) {
                                        JSONObject object = array.getJSONObject(i);
                                        SavedSmsModel model = new SavedSmsModel(object.getInt("id"), object.getString("msg"));
                                        mSMSList.add(model);

                                        //add grouplist in adapter
                                        adapter = new SavedSmsAdapter(context, R.layout.simple_text_view, mSMSList);

                                        //set adapter to listview
                                        mGroupList.setAdapter(adapter);
                                    }
                                } else {
                                    emptyTextView.setText("NO SMS FOUND");
                                }
                            } else {
                                emptyTextView.setText("NO SMS FOUND");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            emptyTextView.setText("NO SMS FOUND");
                            onError("ErrorMessage");
                        }
                    }
                });
            }

            @Override
            public void onError(final String msg) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        emptyTextView.setText("NO SMS FOUND");
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        //setListeners
        setListenerToViews();
    }

    private void setListenerToViews() {
        mGroupList.setLongClickable(true);

        mGroupList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                SavedSmsModel bean = mSMSList.get(position);
                deleteGroupDialog(bean.getId());

                return true;
            }
        });

        mGroupList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final SavedSmsModel bean = mSMSList.get(position);

                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                clipboard.setText(bean.getMsg());
                Toast.makeText(context, "Text Copied", Toast.LENGTH_SHORT).show();
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edit_sms.getText().toString().isEmpty()) {
                    Toast.makeText(context, "Please Enter Message", Toast.LENGTH_SHORT).show();
                    return;
                }

                String msg = edit_sms.getText().toString();
                mSMSList.add(0,new SavedSmsModel(-1,msg));

                //refresh listview adapter
                if (adapter == null) {
                    adapter = new SavedSmsAdapter(context, R.layout.simple_text_view, mSMSList);
                    //set adapter to listview
                    mGroupList.setAdapter(adapter);
                } else {
                    adapter.notifyDataSetChanged();
                }

                new SaveSmsLib(context, user_id, msg);
                edit_sms.setText(null);
            }
        });
    }

    private void deleteGroupDialog(final int msgID) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setMessage("Do you want to delete message");
        alert.setPositiveButton("Yes", null);
        alert.setNegativeButton("No", null);

        final AlertDialog dialog = alert.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                deleteMSg(msgID);

                Iterator<SavedSmsModel> it = mSMSList.iterator();
                while (it.hasNext()) {
                    SavedSmsModel bean = it.next();
                    if (bean.getId() == msgID) {
                        it.remove();
                    }
                }
                //refresh listview adapter
                adapter.notifyDataSetChanged();

                dialog.dismiss();
            }
        });

        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void deleteMSg(int msg_id) {
        String url = Config.DOMAIN + "api/MsgSave/SMSDEL?id="+msg_id+"&userid="+user_id;
        HttpConnection.RequestPost(url, new ArrayList<NameValuePair>(), new ResponseListener() {
            @Override
            public void onResponse(int Statuscode, JSONObject jsonObject)  {
                Log.i("Remove",jsonObject.toString());
            }

            @Override
            public void onError(String msg) {

            }
        });
    }


}
