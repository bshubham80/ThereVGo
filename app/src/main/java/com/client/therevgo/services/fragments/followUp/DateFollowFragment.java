package com.client.therevgo.services.fragments.followUp;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.therevgo.R;
import com.client.therevgo.services.activities.MainActivity;
import com.client.therevgo.services.adapters.FollowAdapter;
import com.client.therevgo.services.base.BaseFragment;
import com.client.therevgo.services.constants.Config;
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

import static com.client.therevgo.services.fragments.followUp.CustomDateFilterFollowUpFragment.SINGLE;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link }
 * interface.
 */
public class DateFollowFragment extends Fragment implements ResponseListener {

    public static final int  TODAY = 1;
    public static final int  YESTERDAY = 2;

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String ARG_FRAGMENT_TYPE = "fragment-type";

    // TODO: Customize parameters
    private int mColumnCount = 1;
    private int mFragmentType = TODAY;

    private String fromDate, toDate, URL;

    private ListView followList ;
    private ProgressBar progressBar;

    private List<FollowModel> modelList ;
    private ArrayList<String> phoneList ;

    private FollowAdapter adapter ;
    private TextView txt ;
    private Context context;


    private boolean isSelectAll = false ;
    private MainActivity activity;
    
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public DateFollowFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static DateFollowFragment newInstance(int columnCount,int type) {
        DateFollowFragment fragment = new DateFollowFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putInt(ARG_FRAGMENT_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            mFragmentType = getArguments().getInt(ARG_FRAGMENT_TYPE);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        context = getActivity() ;
        activity = (MainActivity) context;

        followList = (ListView) view.findViewById(R.id.follow_list);
        txt = (TextView)view.findViewById(R.id.empty);

        followList.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        followList.setSelectionAfterHeaderView();

        progressBar = (ProgressBar) view.findViewById(R.id.follow_progressBar);
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy");

        Date dt = new Date();

        if(mFragmentType == TODAY){
            fromDate = toDate = format.format(dt);
        }else{
            int date  = dt.getDate() - 1;
            int month = dt.getMonth();
            int year  = dt.getYear();
            Date yesDT = new Date(year,month,date);
            fromDate = toDate = format.format(yesDT);
        }

        followList.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
               // if(position != 0) {
                    adapter.setSelectedItem(position, checked);
                    mode.setTitle(adapter.getSelectedItemSize() + " Selected");
               // }
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

        //refreshFollowList();
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
    public void refreshFollowList() {

        modelList = new ArrayList<>();
        phoneList = new ArrayList<>();

        String user_id = (String) PrefManager.getInstance(context)
                            .getDataFromPreference(PrefManager.Key.USER_ID, PrefManager.Type.TYPE_STRING);
        URL = Config.DOMAIN+"api/FollowUpBYDATE?fromdate="+fromDate+"&todate="+toDate+"&userid="+ user_id;
        HttpConnection.RequestGet(URL,DateFollowFragment.this);
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
                                progressBar.setVisibility(View.GONE);
                                followList.setVisibility(View.VISIBLE);
                                adapter = new FollowAdapter(getActivity(), R.layout.follow_row ,modelList);
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
                Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onError(String msg) {

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
