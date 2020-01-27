package com.client.therevgo.services.fragments.group;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.therevgo.R;
import com.client.therevgo.services.activities.ContactActivity;
import com.client.therevgo.services.adapters.ContactAdapter;
import com.client.therevgo.services.base.BaseFragment;
import com.client.therevgo.services.customviews.RecyclerViewWithEmptyView;
import com.client.therevgo.services.database.AppDb;
import com.client.therevgo.services.database.ContactBean;
import com.client.therevgo.services.database.ContactTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;


/**
 * A simple {@link Fragment} subclass.
 */
public class GroupsContactFragment extends BaseFragment
        implements ContactActivity.ContactListener, ContactAdapter.OnItemLongClickListener {

    public static final String TAG = GroupsContactFragment.class.getName();

    public static final String GROUP_ID = "GROUP_ID";
    public static final String GROUP_NAME = "GROUP_NAME";
    /**
     * Array of ContactBean items
     */
    public static final List<ContactBean> ITEMS = new ArrayList<ContactBean>();
    private static final int REQUEST_CODE = 1;
    public static GroupsContactFragment instance;
    //TODO Dynamically Variables
    private String mGroupName = "None";
    private int mGroupID;
    private View view;
    private Context context;
    private TextView txtGroupName;
    private Button mAddContact;
    private RecyclerViewWithEmptyView recyclerView;
    //Database Instance
    private AppDb appDb;
    private ContactTable contactTable;
    private ContactAdapter adapter;

    public GroupsContactFragment() {
        // Required empty public constructor
    }

    @Override
    protected String getTitle() {
        return "Add Participants";
    }

    public static Fragment newInstance(int id, String name) {
        GroupsContactFragment fragment = new GroupsContactFragment();
        Bundle args = new Bundle();
        args.putInt(GROUP_ID, id);
        args.putString(GROUP_NAME, name);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mGroupID = getArguments().getInt(GROUP_ID);
            mGroupName = getArguments().getString(GROUP_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_groups_contact, container, false);

        instance = this;

        recyclerView = (RecyclerViewWithEmptyView) view.findViewById(R.id.group_contacts_list);
        txtGroupName = (TextView) view.findViewById(R.id.txt_group_name);
        mAddContact = (Button) view.findViewById(R.id.btn_add_contact);
        mAddContact.setOnClickListener(new MyListener());

        //intialize value in view
        initializeValues();

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        appDb = AppDb.getInstance(context);
    }

    private void initializeValues() {
        Resources resources = context.getResources();
        String nameString = resources.getString(R.string.group_name, mGroupName);

        txtGroupName.setText(nameString);

        contactTable = (ContactTable) appDb.getTableObject(ContactTable.TABLE_NAME);
        Vector<ContactBean> beanList = contactTable.getAllDataByID(String.valueOf(mGroupID));

        for (ContactBean bean : beanList) {
            ITEMS.add(bean);
        }

        recyclerView.addItemDecoration(new DividerItemDecoration(context));
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setEmptyView(view.findViewById(R.id.contact_list_empty));
        recyclerView.setLongClickable(true);
        adapter = new ContactAdapter(ITEMS, this);
        recyclerView.setAdapter(adapter);

    }

    public void getContact() {
        Intent intent = new Intent(getActivity(), ContactActivity.class);
        intent.putExtra(ContactActivity.INSTANCE, ContactActivity.GROUPCONTACT);
        startActivity(intent);
    }

    @Override
    public void onContactListFound(ContactActivity activity, HashMap<String, String> map) {
        activity.finish();

        Log.i("Size", map.size() + "");

        for (String key : map.keySet()) {
            String name = map.get(key);

            ContactBean bean = new ContactBean(0, name, key);
            int id = contactTable.createContact(bean, mGroupID);
            bean.setId(id);
            ITEMS.add(bean);
        }

        if (map.size() > 0) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        ITEMS.clear();
    }

    @Override
    public void onItemLongClick(int pos, View view) {
        ContactBean bean = ITEMS.get(pos);
        deleteContactDialog(bean.getName(), bean.getId());
    }

    private void deleteContactDialog(String contactName, final int contactID) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setMessage("Do you want to delete '" + contactName + "' contact");
        alert.setPositiveButton("Yes", null);
        alert.setNegativeButton("No", null);

        final AlertDialog dialog = alert.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int resGroup = contactTable.deleteContact(String.valueOf(contactID));

                //get new data from database in list
                /*Iterator<ContactBean> it = ITEMS.iterator();
                while (it.hasNext()) {
                    ContactBean bean = it.next();
                    if (bean.getId() == contactID) {
                        it.remove();
                    }
                }*/

                ITEMS.clear();
                Vector<ContactBean> beanList = contactTable.getAllDataByID(String.valueOf(mGroupID));

                ITEMS.addAll(beanList);

                //refresh listView adapter
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

    /**
     * TODO Custom Divider for RecyclerView
     */
    private class DividerItemDecoration extends RecyclerView.ItemDecoration {

        private final int[] ATTRS = new int[]{android.R.attr.listDivider};

        private Drawable divider;

        /**
         * Default divider will be used
         */
        public DividerItemDecoration(Context context) {
            final TypedArray styledAttributes = context.obtainStyledAttributes(ATTRS);
            divider = styledAttributes.getDrawable(0);
            styledAttributes.recycle();
        }

        /**
         * Custom divider will be used
         */
        public DividerItemDecoration(Context context, int resId) {
            divider = ContextCompat.getDrawable(context, resId);
        }

        @Override
        public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
            int left = parent.getPaddingLeft();
            int right = parent.getWidth() - parent.getPaddingRight();

            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = parent.getChildAt(i);

                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                int top = child.getBottom() + params.bottomMargin;
                int bottom = top + divider.getIntrinsicHeight();

                divider.setBounds(left, top, right, bottom);
                divider.draw(c);
            }
        }
    }

    private class MyListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.btn_add_contact:

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (context.checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CODE);
                        } else {
                            getContact();
                        }
                    } else {
                        getContact();
                    }

                    break;
            }
        }
    }
}
