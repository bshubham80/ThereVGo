package com.android.therevgo.fragments.listing;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.therevgo.R;
import com.android.therevgo.activities.ContainerActivity;
import com.android.therevgo.adapters.BusinessAdapter;
import com.android.therevgo.dto.BusinessProfileModel;
import com.android.therevgo.networks.HttpConnection;
import com.android.therevgo.networks.ResponseListener;
import com.android.therevgo.utility.PrefManager;
import com.google.gson.Gson;

import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BusinessListViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BusinessListViewFragment extends Fragment implements ResponseListener,
                AdapterView.OnItemClickListener {

    public static final String TAG = BusinessListViewFragment.class.getSimpleName();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ListView mBusinessList;
    private ProgressBar mProgressBar;
    private FloatingActionButton actionButton ;
    private Handler handler = new Handler();
    private Context context;

    private PrefManager prefManager;
    private BusinessProfileModel profileModel;
    private BusinessAdapter adapter;
    private String user_id;

    public BusinessListViewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of BusinessListViewFragment.
     */
    public static BusinessListViewFragment newInstance(String param1, String param2) {
        BusinessListViewFragment fragment = new BusinessListViewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_business_list_view, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = view.getContext();
        prefManager = PrefManager.getInstance(context);

        actionButton = (FloatingActionButton) view.findViewById(R.id.add_new_list);
        mBusinessList = (ListView) view.findViewById(R.id.business_profile_list);
        mBusinessList.setOnItemClickListener(this);
        mProgressBar = (ProgressBar) view.findViewById(R.id.business_progressBar);

        user_id = (String) prefManager.getDataFromPreference(PrefManager.Key.USER_ID,
                                                                PrefManager.Type.TYPE_STRING);

        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putBoolean(ContainerActivity.ARG_FORCE_FINISH, true);
                bundle.putString(ContainerActivity.ARG_TITLE, "Contact Info");
                bundle.putInt(ContainerActivity.ARG_FRAGMENT, ContainerActivity.LISTING_FORM);

                Intent intent = new Intent(context, ContainerActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        startNetwork();
    }

    public void startNetwork() {
        mProgressBar.setVisibility(View.VISIBLE);
        /*mBusinessList.setVisibility(View.GONE);*/
        String url = "http://tapi.therevgo.in/api/BusinessDetails/?userid="+user_id;

        HttpConnection.RequestGet(url, this);
    }

    @Override
    public void onResponse(int statusCode, final JSONObject jsonObject) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                profileModel = new Gson().fromJson(jsonObject.toString(), BusinessProfileModel.class);
                if (profileModel.Data != null && profileModel.Data.size() > 0) {
                    mProgressBar.setVisibility(View.GONE);
                    mBusinessList.setVisibility(View.VISIBLE);
                    adapter = new BusinessAdapter(context, R.layout.business_list_row, profileModel.Data);
                    mBusinessList.setAdapter(adapter);
                } else {
                    onError("No Result Found");
                }
            }
        });
    }

    @Override
    public void onError(final String msg) {
        handler.post(new Runnable() {
            @Override
            public void run() {
            mProgressBar.setVisibility(View.GONE);
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        BusinessProfileModel.ListModel model = profileModel.Data.get(position);

        Bundle bundle = new Bundle();
        bundle.putBoolean(ContainerActivity.ARG_FORCE_FINISH, true);
        bundle.putString(ContainerActivity.ARG_TITLE, "Contact Info");
        bundle.putInt(ContainerActivity.ARG_FRAGMENT, ContainerActivity.LISTING_FORM);
        bundle.putSerializable("data", model);

        Intent intent = new Intent(context, ContainerActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
