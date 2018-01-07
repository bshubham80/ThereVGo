package com.client.therevgo.fragments.inquiry;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.therevgo.R;
import com.client.therevgo.activities.MainActivity;
import com.client.therevgo.adapters.InquiryAdapter;
import com.client.therevgo.constants.Config;
import com.client.therevgo.dto.InquiryModel;
import com.client.therevgo.fragments.sms.SmsContainerFragment;
import com.client.therevgo.networks.HttpConnection;
import com.client.therevgo.networks.ResponseListener;
import com.client.therevgo.utility.PrefManager;
import com.client.therevgo.utility.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.client.therevgo.fragments.followUp.CustomDateFilterFollowUpFragment.SINGLE;

/**
 * A simple {@link Fragment} subclass.
 */
public class CustomerInquiry extends Fragment implements ResponseListener, DatePickerDialog.OnDateSetListener {

    public static final String TAG = "CustomerInquiry";

    public static final String HEADER_TEXT = "%s Inquiry found from %s to %s";


    private Context context;
    private TextView fromDate, toDate, client, activeTextView;
    private Button search_btn;


    private ListView inquiryList;

    private ProgressDialog dialog;

    private int ClickRefernece, CLIENT = 1, SEARCH = 2;
    private List<Integer> MessageID;
    private ArrayList<String> MessageText;

    private Handler handler = new Handler();
    private ArrayAdapter adapter;
    private int con_id;

    private ProgressBar progressBar;
    private TextView header ;
    private TextView txt ;


    private List<InquiryModel> modelList;
    private ArrayList<String> phoneList;
    private InquiryAdapter inquiryAdapter;
    private boolean isSelectAll = false;


    public CustomerInquiry() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_customer_inquiry, container, false);
        context = getActivity();

        init(view);
        return view;
    }

    private void init(View view) {
        fromDate = (TextView) view.findViewById(R.id.fromDate);
        toDate = (TextView) view.findViewById(R.id.toDate);
        client = (TextView) view.findViewById(R.id.client);

        search_btn = (Button) view.findViewById(R.id.btn_search_customer);

        inquiryList = (ListView) view.findViewById(R.id.customerList);

        txt = (TextView)view.findViewById(R.id.empty);

        header = (TextView) LayoutInflater.from(context).inflate(R.layout.follow_list_header,null);
        header.setClickable(false);

        inquiryList.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        inquiryList.setSelectionAfterHeaderView();
        inquiryList.addHeaderView(header);
        inquiryList.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                Log.i(TAG, "onItemCheckedStateChanged");
                if(position != 0) {
                    inquiryAdapter.setSelectedItem(position - 1, checked);
                    mode.setTitle(inquiryAdapter.getSelectedItemSize() + " Selected");
                }
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater inflater = getActivity().getMenuInflater();
                inflater.inflate(R.menu.contact_action, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.send:
                        Utils.getInstance().phoneList = inquiryAdapter.getCheckedItems();
                        mode.finish();
                        changeFragment(SmsContainerFragment.newInstance(true), SmsContainerFragment.TAG);
                        return true;

                    case R.id.select_all:
                        if (!isSelectAll) {
                            for (int i = 0; i <= inquiryAdapter.getCount() - 1; i++) {
                                inquiryAdapter.setSelectedItem(i, true);
                            }
                            mode.setTitle(inquiryAdapter.getSelectedItemSize() + " Selected");
                            isSelectAll = true;
                        } else {
                            Toast.makeText(context, "Already Selected", Toast.LENGTH_SHORT).show();
                        }
                        return true;

                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                resetAdapter();
            }
        });

        progressBar = (ProgressBar) view.findViewById(R.id.follow_progressBar);

        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Please Wait...");
        dialog.setCancelable(false);

        intializeList();

        setListener();

        getMyClient();
    }

    private void intializeList() {
        modelList = new ArrayList<>();
        phoneList = new ArrayList<>();

        MessageID = new ArrayList<>();
        MessageText = new ArrayList<>();
    }

    public void resetAdapter() {
        if (adapter != null) {
            for (int i = 0; i <= adapter.getCount() - 1; i++) {
                inquiryAdapter.setSelectedItem(i, false);
            }
            isSelectAll = false;
            if (phoneList.size() > 0) {
                phoneList.clear();
            }
        }
    }

    private void setListener() {
        fromDate.setOnClickListener(new MyCLickListener());
        toDate.setOnClickListener(new MyCLickListener());
        client.setOnClickListener(new MyCLickListener());
        search_btn.setOnClickListener(new MyCLickListener());
    }

    public void getMyClient() {
        final String user_id = (String) PrefManager.getInstance(context)
                .getDataFromPreference(PrefManager.Key.USER_ID, PrefManager.Type.TYPE_STRING);

        String URL = Config.DOMAIN + "api/BusNameByUser?userid=" + user_id;

        HttpConnection.RequestGet(URL, this);
    }

    /**
     * @param view       the picker associated with the dialog
     * @param year       the selected year
     * @param month      the selected month (0-11 for compatibility with
     *                   {@link Calendar#MONTH})
     * @param dayOfMonth th selected day of the month (1-31, depending on
     */
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        activeTextView.setText(new StringBuilder().append(year).append("/")
                .append(month + 1).append("/").append(dayOfMonth));
    }

    private void showDatePicker() {
        Calendar c = Calendar.getInstance();
        final int year = c.get(Calendar.YEAR);
        final int month = c.get(Calendar.MONTH);
        final int date = c.get(Calendar.DATE);

        new DatePickerDialog(getContext(), this, year, month, date).show();
    }

    /**
     * Called when request successfully executed and return some response
     * from the server.
     * <p>
     * <p>This callback run on another thread from main thread</p>
     *
     * @param statusCode The statusCode got from server response
     * @param jsonObject that will hold the result come from server
     */
    @Override
    public void onResponse(final int statusCode, final JSONObject jsonObject) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (statusCode == 200 && ClickRefernece != SEARCH) {
                    try {
                        dialog.dismiss();
                        if (!jsonObject.isNull("status") && jsonObject.getBoolean("status")) {
                            JSONArray jsonArray = jsonObject.getJSONArray("Data");
                            if (jsonArray.length() > 0) {
                                for (int i = 0; i <= jsonArray.length() - 1; i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    MessageID.add(object.getInt("con_id"));
                                    MessageText.add(object.getString("comp_name"));
                                }
                            } else {
                                onError("No Data Found");
                            }
                        } else {
                            onError("Unable To Connect To Server");
                        }
                    } catch (JSONException e) {
                        onError("Unable To Connect To Server");
                    }
                } else {
                    try {
                        dialog.dismiss();
                        if (!jsonObject.isNull("status") && jsonObject.getBoolean("status")) {
                            final JSONArray jsonArray = jsonObject.getJSONArray("Data");

                            if (jsonArray.length() > 0) {
                                for (int i = 0; i <= jsonArray.length() - 1; i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);

                                    InquiryModel model = new InquiryModel();
                                    model.setId(object.getInt("id"));
                                    model.setCus_name(object.optString("cus_name"));
                                    model.setCus_email_id(object.optString("cus_email_id"));
                                    model.setCus_phne_no(object.optString("cus_phne_no"));

                                    modelList.add(model);
                                }
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        setFollowUpCount(jsonArray.length());
                                        inquiryList.setVisibility(View.VISIBLE);
                                        inquiryAdapter = new InquiryAdapter(getActivity(), R.layout.follow_row, modelList);
                                        inquiryList.setAdapter(inquiryAdapter);
                                    }
                                });
                            } else {
                                onError("No Data Found");
                            }

                        } else {
                            onError("Unable To Connect To Server");
                        }
                    } catch (JSONException e) {
                        onError("Unable To Connect To Server");
                    }
                }
            }
        });
    }

    private void setFollowUpCount(int count) {
        header.setVisibility(count == 0 ? View.GONE : View.VISIBLE);
        header.setText(String.format(HEADER_TEXT, count, fromDate.getText().toString(), toDate.getText().toString()));
        header.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
    }

    /**
     * Called when got some error from server or when some connection problem.
     * <p>
     * <p>This callback run on another thread from main thread</p>
     *
     * @param msg that will define the proper error
     */
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

    public void selectDropDown(Context ctx, String title, final TextView txt, final ArrayList<String> list) {

        final ArrayAdapter arrayAdapter = new ArrayAdapter<>(ctx, android.R.layout.simple_list_item_1, list);

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(ctx);
        builderSingle.setTitle(title);

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog1, int which) {
                txt.setText(list.get(which));
                con_id = MessageID.get(which);
            }
        });
        builderSingle.show();
    }

    private void changeFragment(Fragment fragment, String stack) {
        ((MainActivity) context).getMyFragmentManager().popBackStack();
        //Immediate(ShowFollowUpContainerFragment.TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        //activity.attachFragment(SmsContainerFragment.newInstance(true), SendSmsFragment.TAG);
        ((MainActivity) context).attachFragment(fragment, stack);
    }

    @Override
    public void onPause() {
        super.onPause();
        setHasOptionsMenu(false);
        invalidateList(SINGLE);
    }

    public void invalidateList(int choice) {
        if (inquiryList != null) {
            inquiryList.setChoiceMode(choice);
        }
    }

    private class MyCLickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.fromDate:
                    activeTextView = fromDate;
                    showDatePicker();
                    break;

                case R.id.toDate:
                    activeTextView = toDate;
                    showDatePicker();
                    break;

                case R.id.client:
                    selectDropDown(context, "Client", client, MessageText);
                    break;

                case R.id.btn_search_customer:
                    final String fromDateText = fromDate.getText().toString();
                    final String toDateText = toDate.getText().toString();
                    final String clientText = client.getText().toString();

                    if (TextUtils.isEmpty(fromDateText)) {
                        Toast.makeText(context, "Please select FROM DATE first ", Toast.LENGTH_SHORT).show();
                        return ;
                    }
                    if (TextUtils.isEmpty(toDateText)) {
                        Toast.makeText(context, "Please select TO DATE", Toast.LENGTH_SHORT).show();
                        return ;
                    }
                    if (TextUtils.isEmpty(clientText)) {
                        Toast.makeText(context, "Please select client", Toast.LENGTH_SHORT).show();
                        return ;
                    }

                    ClickRefernece = SEARCH;

                    //clear previous data
                    modelList.clear();
                    if (inquiryAdapter != null) {
                        inquiryAdapter.clear();
                        inquiryAdapter.notifyDataSetChanged();
                        setFollowUpCount(0);
                    }

                    String url = "http://tapi.therevgo.in/api/SendSMSEnquiry?con_id=" + con_id +
                            "&from_date=" + fromDate.getText().toString() + "&to_date=" + toDate.getText().toString();

                    //
                    //or http://tapi.therevgo.in/api/MessageTips?id=0&sub_cat_id=14

                    dialog.show();
                    HttpConnection.RequestGet(url, CustomerInquiry.this);
                    break;
            }
        }
    }
}
