package com.client.therevgo.services.fragments.listing;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.android.therevgo.R;
import com.client.therevgo.services.activities.ContainerActivity;
import com.client.therevgo.services.adapters.ServiceAdapter;
import com.client.therevgo.services.base.BaseFragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BusinessListingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BusinessListingFragment extends BaseFragment implements AdapterView.OnItemClickListener {

    public static final String TAG = BusinessListingFragment.class.getName();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Context context ;

    public BusinessListingFragment() {
        // Required empty public constructor
    }

    @Override
    protected String getTitle() {
        return "Listing";
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BusinessListingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BusinessListingFragment newInstance(String param1, String param2) {
        BusinessListingFragment fragment = new BusinessListingFragment();
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
        return inflater.inflate(R.layout.fragment_business_listing, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = view.getContext();

        GridView gridView = (GridView) view.findViewById(R.id.grid_view);

        // Instance of ImageAdapter Class
        gridView.setAdapter(new ServiceAdapter(getContext()));
        gridView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0) {
            Bundle bundle = new Bundle();
            bundle.putBoolean(ContainerActivity.ARG_FORCE_FINISH, false);
            /*bundle.putString(ContainerActivity.ARG_TITLE, "Hello");*/
            bundle.putInt(ContainerActivity.ARG_FRAGMENT, ContainerActivity.LISTING_LISTVIEW);

            Intent intent = new Intent(context, ContainerActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }
}
