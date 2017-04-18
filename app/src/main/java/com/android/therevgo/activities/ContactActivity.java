package com.android.therevgo.activities;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.android.therevgo.R;
import com.android.therevgo.adapters.SelectUserAdapter;
import com.android.therevgo.dto.SelectUser;
import com.android.therevgo.fragments.group.GroupsContactFragment;
import com.android.therevgo.fragments.sms.SendSmsFragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ContactActivity extends AppCompatActivity {

    public static final java.lang.String INSTANCE = "instance";
    public static final int GROUPCONTACT = 1;
    public static final int SENDSMS = 2;
    // ArrayList
    ArrayList<SelectUser> selectUsers;
    // Contact List
    ListView listView;
    // Cursor to load contacts list
    Cursor phones;
    ContactListener mListener ;

    private ArrayList<String> phoneList = new ArrayList<>();

    @SuppressLint("UseSparseArrays")
    private HashMap<Integer,Integer> hashMap = new HashMap<>();

    // Pop up
    ContentResolver resolver;
    SearchView search;
    SelectUserAdapter adapter;

    private boolean isSelectAll = false ;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        selectUsers = new ArrayList<>();
        resolver = this.getContentResolver();
        listView = (ListView) findViewById(R.id.contacts_list);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);

        search  = (SearchView) findViewById(R.id.searchView);
        if( search != null) {
            search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    if (!search.isIconified()) {
                        search.setIconified(true);
                    }
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    adapter.filter(s);
                    return false;
                }
            });
        }

        Bundle bundle = getIntent().getExtras() ;
        int instance = bundle.getInt(INSTANCE);
        if (instance == GROUPCONTACT) {
            mListener = GroupsContactFragment.instance ;
        } else {
            mListener = SendSmsFragment.instance ;
        }

        phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        LoadContact loadContact = new LoadContact();
        loadContact.execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // I do not want this...
                // Home as up button is to navigate to Home-Activity not previous acitivity
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Load data on background
    private class LoadContact extends AsyncTask<Void, Void, Void> {
        ProgressDialog dialog ;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(ContactActivity.this) ;
            dialog.setMessage("Please Wait.....");
            dialog.setCancelable(false);
            dialog.show();

        }

        @Override
        protected Void doInBackground(Void... voids) {
            // Get Contact list from Phone

            if (phones != null) {
                Log.e("count", "" + phones.getCount());
                if (phones.getCount() == 0) {
                    Toast.makeText(ContactActivity.this, "No contacts in your contact list.", Toast.LENGTH_LONG).show();
                }

                while (phones.moveToNext()) {

                    Bitmap bit_thumb = null;
                    String id = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                    String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    String image_thumb = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
                        image_thumb = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI));
                    }
                    try {
                        if (image_thumb != null) {
                            bit_thumb = MediaStore.Images.Media.getBitmap(resolver, Uri.parse(image_thumb));
                        } else {
                            Log.e("No Image Thumb", "--------------");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if(!hashMap.containsKey(Integer.parseInt(id))) {
                        SelectUser selectUser = new SelectUser();
                        selectUser.setThumb(bit_thumb);
                        selectUser.setName(name);
                        phoneNumber = phoneNumber.contains(" ") ? phoneNumber.replaceAll("\\s+","") : phoneNumber ;
                        selectUser.setPhone(phoneNumber);
                        selectUser.setEmail(id);
                        selectUser.setCheckedBox(false);
                        selectUsers.add(selectUser);
                        hashMap.put(Integer.parseInt(id), Integer.parseInt(id)) ;
                    }
                }
            } else {
                Log.e("Cursor close 1", "----------------");
            }
            //phones.close();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            adapter = new SelectUserAdapter(selectUsers, ContactActivity.this);
            listView.setAdapter(adapter);
            dialog.dismiss();

            listView.setFastScrollEnabled(true);
            addMultiChoice();
        }
    }

    private void addMultiChoice() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

                @TargetApi(Build.VERSION_CODES.HONEYCOMB)
                @Override
                public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

                        if(checked) {
                            adapter.setSelectedItem(position, true);
                            mode.setTitle(adapter.getSelectedItemSize() + " Selected");
                        } else {
                            adapter.removeSelectedItem(position, false);
                            mode.setTitle(adapter.getSelectedItemSize() + " Selected");
                        }

                }

                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    MenuInflater inflater = getMenuInflater();
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
                            HashMap<String,String> phoneMap = adapter.getCheckedItems() ;
                            mListener.onContactListFound(ContactActivity.this,phoneMap);
                        return true;

                        case R.id.select_all:
                            if(!isSelectAll) {
                                for (int i = 0; i <= adapter.getCount() - 1; i++) {
                                    adapter.setSelectedItem(i, true);
                                }
                                mode.setTitle(adapter.getSelectedItemSize() + " Selected");
                                isSelectAll = true ;
                            }else {
                                Toast.makeText(ContactActivity.this, "Already Selected", Toast.LENGTH_SHORT).show();
                            }
                        return true ;

                        default:
                        return false ;
                    }
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {
                    //mode.finish();
                    adapter.clearSelectedList();
                    isSelectAll = false ;
                    if(phoneList.size() > 0) {
                        phoneList.clear();
                    }
                }
            });
        }

    }

    public interface ContactListener {
         void onContactListFound(ContactActivity activity, HashMap<String, String> ContactList);
    }

    @Override
    protected void onStop() {
        super.onStop();
        phones.close();
    }
}