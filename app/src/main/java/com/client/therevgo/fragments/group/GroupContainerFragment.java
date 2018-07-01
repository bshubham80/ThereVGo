package com.client.therevgo.fragments.group;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.android.therevgo.R;
import com.client.therevgo.activities.MainActivity;
import com.client.therevgo.adapters.GroupAdapter;
import com.client.therevgo.base.BaseFragment;
import com.client.therevgo.database.AppDb;
import com.client.therevgo.database.GroupBean;
import com.client.therevgo.database.GroupTable;
import com.client.therevgo.utility.Utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;


/**
 * A simple {@link Fragment} subclass.
 */
public class GroupContainerFragment extends BaseFragment
        implements GroupAdapter.OnGroupEditListener {

    public static final String TAG = GroupContainerFragment.class.getName();
    private View view;
    private Context context;
    private FloatingActionButton mAddGroup;
    private ListView mGroupList;

    //Database Instance
    private AppDb appDb;
    private GroupTable groupTable;
    private GroupAdapter adapter;

    private MainActivity activity;

    private ArrayList<GroupBean> mGroupArrayList = new ArrayList<>();
    ;

    public GroupContainerFragment() {
        // Required empty public constructor
    }

    @Override
    protected String getTitle() {
        return "Group";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_group, container, false);

        //getting reference of parent activity
        context = getActivity();

        activity = (MainActivity) context;

        //find all view views and initialize them
        findViewByIds(view);

        //initialize instances
        initializeInstances(context);

        return view;
    }

    private void findViewByIds(final View view) {
        mGroupList = (ListView) view.findViewById(R.id.group_list);
        mAddGroup = (FloatingActionButton) view.findViewById(R.id.add_group_btn);
    }

    private void initializeInstances(final Context context) {
        appDb = AppDb.getInstance(context);
        groupTable = (GroupTable) appDb.getTableObject(GroupTable.TABLE_NAME);

        //setDate in list
        getGroupList();

        //add grouplist in adapter
        adapter = new GroupAdapter(context, R.layout.group_list_item, mGroupArrayList);
        adapter.setEditListener(this);

        //set adapter to listview
        mGroupList.setAdapter(adapter);

        //setListeners
        setListenerToViews();
    }

    private void setListenerToViews() {

        mAddGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createGroupDialog();
            }
        });

        mGroupList.setLongClickable(true);
        mGroupList.setEmptyView(view.findViewById(R.id.empty_view));

        mGroupList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                GroupBean bean = mGroupArrayList.get(position);
                deleteGroupDialog(bean.getName(), bean.getId());
                return true;
            }
        });

        mGroupList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final GroupBean bean = mGroupArrayList.get(position);

                activity.attachFragment(GroupsContactFragment.newInstance(bean.getId(), bean.getName())
                        , GroupsContactFragment.TAG);

                //Toast.makeText(context, ""+mGroupArrayList.get(position).getId(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createGroupDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);

        final RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(10, 10, 10, 10);


        final EditText edittext = new EditText(context);
        edittext.setLayoutParams(params);
        edittext.setPadding(10, 10, 10, 10);

        alert.setTitle("Enter Your Group Name");
        alert.setView(edittext);

        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                Utils.getInstance().hideKeyboard(context);

                //What ever you want to do with the value
                String groupName = edittext.getText().toString();
                int resGroup = groupTable.createGroup(groupName, context);

                //get new data from database in list
                mGroupArrayList.add(new GroupBean(resGroup, groupName));

                //refresh listview adapter
                adapter.notifyDataSetChanged();

                dialog.dismiss();
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

    private void deleteGroupDialog(String groupName, final int groupID) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setMessage("Do you want to delete '" + groupName + "' group");

        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int resGroup = groupTable.deleteGroup("" + groupID);

                //get new data from database in list
                Iterator<GroupBean> it = mGroupArrayList.iterator();
                while (it.hasNext()) {
                    GroupBean bean = it.next();
                    if (bean.getId() == groupID) {
                        it.remove();
                    }
                }

                //refresh listview adapter
                adapter.notifyDataSetChanged();

                dialog.dismiss();
            }
        });

        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                int resGroup = groupTable.deleteGroup("" + groupID);
//                //get new data from database in list
//                getGroupList();
//                //refresh listview adapter
//                adapter.notifyDataSetInvalidated();
                dialog.dismiss();
            }
        });

        final AlertDialog dialog = alert.create();
        dialog.show();
    }

    private void updateGroupDialog(final int pos, final GroupBean bean) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);

        final RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(10, 10, 10, 10);


        final EditText edittext = new EditText(context);
        edittext.setLayoutParams(params);
        edittext.setText(bean.getName());
        edittext.setSelection(bean.getName().length());
        edittext.setPadding(10, 10, 10, 10);

        alert.setTitle("Update Your Group Name");
        alert.setView(edittext);

        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                Utils.getInstance().hideKeyboard(context);

                //What ever you want to do with the value
                String groupName = edittext.getText().toString();
                int resGroup = groupTable.updateGroup(new GroupBean(bean.getId(), groupName));

                if (resGroup > 0) {
                    mGroupArrayList.remove(bean);
                    //get new data from database in list
                    mGroupArrayList.add(pos, new GroupBean(resGroup, groupName));

                    //refresh listview adapter
                    adapter.notifyDataSetChanged();
                }

                dialog.dismiss();
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

    public void getGroupList() {
        //get all group created by user
        mGroupArrayList = new ArrayList<>();
        final Vector<GroupBean> mGroupBean = groupTable.getAllData(context);
        mGroupArrayList.addAll(mGroupBean);
    }

    @Override
    public void OnEdit(int position) {
        updateGroupDialog(position, mGroupArrayList.get(position));
    }
}