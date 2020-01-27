package com.client.therevgo.services.fragments.followUp;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.therevgo.R;
import com.client.therevgo.services.activities.MainActivity;
import com.client.therevgo.services.adapters.FollowAdapter;
import com.client.therevgo.services.base.BaseFragment;
import com.client.therevgo.services.constants.Config;
import com.client.therevgo.services.customviews.DateRangePickerDialog;
import com.client.therevgo.services.dto.FollowModel;
import com.client.therevgo.services.fragments.sms.SmsContainerFragment;
import com.client.therevgo.services.networks.HttpConnection;
import com.client.therevgo.services.networks.ResponseListener;
import com.client.therevgo.services.utility.PrefManager;
import com.client.therevgo.services.utility.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CustomDateFilterFollowUpFragment extends Fragment implements ResponseListener,
                                                DateRangePickerDialog.OnTimeRangeSelectedListener {

    public static final int SINGLE = AbsListView.CHOICE_MODE_SINGLE;
    public static final int MULTIPLE = AbsListView.CHOICE_MODE_MULTIPLE_MODAL;
    public static final String TAG = CustomDateFilterFollowUpFragment.class.getSimpleName()
            ;
    public static final String HEADER_TEXT = "%s Follow Up found from %s to %s";

    private final DateRangePickerDialog  dialog = DateRangePickerDialog.newInstance(
                                                        CustomDateFilterFollowUpFragment.this,false);

    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
    private String URL ;
    private ListView followList ;
    private ProgressBar progressBar;
    private List<FollowModel> modelList ;
    private FollowAdapter adapter ;

    private Context context ;
    private TextView header ;

    private ArrayList<String> phoneList ;

    private TextView txt ;
    private MainActivity activity;

    private String fromDate = dateFormatter.format(new Date()) ;
    private String toDate = fromDate ;

    private boolean isSelectAll = false ;

    public CustomDateFilterFollowUpFragment() {
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_fragmnet, container, false);

        context= getActivity();
        activity = (MainActivity) context;

        followList = (ListView) view.findViewById(R.id.follow_list);

        txt = (TextView) view.findViewById(R.id.empty);

        header = (TextView) LayoutInflater.from(context).inflate(R.layout.follow_list_header,null);
        header.setClickable(false);

        followList.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        followList.setSelectionAfterHeaderView();
        followList.addHeaderView(header);


        followList.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                if(position != 0) {
                    adapter.setSelectedItem(position - 1, checked);
                    mode.setTitle(adapter.getSelectedItemSize() + " Selected");
                }
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater inflater = getActivity().getMenuInflater();
                inflater.inflate(R.menu.contact_action,menu);
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
                switch (id){
                    case R.id.send:
                        Utils.getInstance().phoneList = adapter.getCheckedItems() ;
                        mode.finish();
                        changeFragment(SmsContainerFragment.newInstance(true), SmsContainerFragment.TAG);
                    return true;

                    case R.id.select_all:
                        if(!isSelectAll) {
                            for (int i = 0; i <= adapter.getCount() - 1; i++) {
                                adapter.setSelectedItem(i, true);
                            }
                            mode.setTitle(adapter.getSelectedItemSize() + " Selected");
                            isSelectAll = true ;
                        }else {
                            Toast.makeText(context, "Already Selected", Toast.LENGTH_SHORT).show();
                        }
                    return true;

                    default:
                        return false ;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                resetAdapter();
            }
        });

        followList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                followList.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);

                return true;
            }
        });

        progressBar = (ProgressBar) view.findViewById(R.id.follow_progressBar);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab_btn);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show(getActivity().getSupportFragmentManager(), TAG);
            }
        });

        // initiateList
        refreshFollowList();

        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_create, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_create) {
            changeFragment(new CreateFollowUpFragment(), CreateFollowUpFragment.TAG);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void resetAdapter(){
        if(adapter !=null) {
            for (int i = 0; i <= adapter.getCount() - 1; i++) {
                adapter.setSelectedItem(i, false);
            }
            isSelectAll = false;
            if (phoneList.size() > 0) {
                phoneList.clear();
            }
        }
    }

    private void refreshFollowList() {
        modelList = new ArrayList<>();
        phoneList = new ArrayList<>();

        String user_id = (String) PrefManager.getInstance(context)
                .getDataFromPreference(PrefManager.Key.USER_ID, PrefManager.Type.TYPE_STRING);

        URL = Config.DOMAIN+"api/FollowUpBYDATE?fromdate="+fromDate+"&todate="+toDate+"&userid="+user_id;
        HttpConnection.RequestGet(URL, this);
        progressBar.setVisibility(View.VISIBLE);
        txt.setVisibility(View.GONE);
    }

    @Override
    public void onResponse(int Statuscode, JSONObject jsonObject) {
        if(Statuscode == 200){
            try {
                if( !jsonObject.isNull("status") && jsonObject.getString("status").equals("true") ) {

                    final JSONArray data = jsonObject.getJSONArray("Data");

                    if( data.length() > 0 ) {
                        for (int i = 0; i <= data.length() - 1; i++) {

                            JSONObject object = data.getJSONObject(i);

                            FollowModel model = new FollowModel();
                            model.setId(object.getInt("id"));
                            model.setUserId(object.getInt("userid"));
                            model.setName(object.getString("name"));
                            model.setContact(object.getString("mobile_no"));
                            model.setEmail(object.getString("email_id"));
                            model.setDescription(object.getString("description"));
                            model.setFollowData(object.getString("follow_up_date"));
                            model.setArea(object.getString("area"));
                            model.setCity(object.getString("city"));

                            modelList.add(model);
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setFollowUpCount(data.length());
                                progressBar.setVisibility(View.GONE);
                                followList.setVisibility(View.VISIBLE);
                                adapter = new FollowAdapter(getActivity(),R.layout.follow_row ,modelList);
                                followList.setAdapter(adapter);
                            }
                        });
                    } else {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                                followList.setVisibility(View.VISIBLE);
                                txt.setVisibility(View.VISIBLE);
                                followList.setEmptyView(txt);
                            }
                        });
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onError(String msg) {

    }

    @Override
    public void onTimeRangeSelected(int startYear, int startMonth, int startDay, int endYear, int endMonth, int endDay) {
        Date startDate = new Date(startYear-1900, startMonth, startDay);
        Date endDate = new Date(endYear-1900, endMonth, endDay);

        fromDate = dateFormatter.format(startDate);
        //formatInteger(startDay) + "-" + formatInteger(startMonth+1) + "-" + startYear
        toDate = dateFormatter.format(endDate);
        //formatInteger(endDay) + "-" + formatInteger(endMonth+1) + "-" + endYear;

        //checking for valid date range
        if(startDate.after(endDate)) {
            Toast.makeText(context, context.getResources().getString(R.string.valid_date_range),
                    Toast.LENGTH_SHORT).show();
            return ;
        }

        refreshFollowList();
    }

    private void setFollowUpCount(int count) {
        header.setText(String.format(HEADER_TEXT, count, fromDate, toDate));
        header.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
    }

    private void changeFragment(BaseFragment fragment, String stack){
        //activity.getMyFragmentManager().popBackStack();
        //Immediate(ShowFollowUpContainerFragment.TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        //activity.attachFragment(SmsContainerFragment.newInstance(true), SendSmsFragment.TAG);
        activity.attachFragment(fragment, stack);
    }

    @Override
    public void onPause() {
        super.onPause();
        invalidateList(SINGLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        setHasOptionsMenu(false);
    }
    
    public void invalidateList(int choice){
        if (followList != null) {
            followList.setChoiceMode(choice);
        }
    }
}
