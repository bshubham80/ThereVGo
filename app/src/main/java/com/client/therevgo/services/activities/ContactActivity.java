package com.client.therevgo.services.activities;

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
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.android.therevgo.R;
import com.client.therevgo.services.adapters.SelectUserAdapter;
import com.client.therevgo.services.dto.SelectUser;
import com.client.therevgo.services.fragments.group.GroupsContactFragment;
import com.client.therevgo.services.fragments.sms.SendSmsFragment;

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
    ContactListener mListener;
    // Pop up
    ContentResolver resolver;
    SearchView search;
    SelectUserAdapter adapter;
    private ArrayList<String> phoneList = new ArrayList<>();
    @SuppressLint("UseSparseArrays")
    private HashMap<Integer, Integer> hashMap = new HashMap<>();
    private boolean isSelectAll = false;
    private ProgressDialog dialog;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        dialog = new ProgressDialog(this);
        dialog.setMessage("Please Wait.....");
        dialog.setCancelable(false);
        dialog.dismiss();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(getDrawable(R.drawable.ic_check_white_24dp));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        selectUsers = new ArrayList<>();
        resolver = this.getContentResolver();
        listView = (ListView) findViewById(R.id.contacts_list);
        //listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        //listView.setTextFilterEnabled(true);


        search = (SearchView) findViewById(R.id.searchView);
        if (search != null) {
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

        Bundle bundle = getIntent().getExtras();
        int instance = bundle.getInt(INSTANCE);
        if (instance == GROUPCONTACT) {
            mListener = GroupsContactFragment.instance;
        } else {
            mListener = SendSmsFragment.instance;
        }

        phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        LoadContact loadContact = new LoadContact();
        loadContact.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.contact_action_2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // I do not want this...
                // Home as up button is to navigate to Home-Activity not previous activity
                HashMap<String, String> phoneMap = adapter.getCheckedItems();
                mListener.onContactListFound(ContactActivity.this, phoneMap);
                Log.i("Size", "onOptionsItemSelected: "+phoneMap.size());
                super.onBackPressed();
                return true;

            case R.id.select_all:
                for (int i = 0; i <= adapter.getCount()-1 ; i++) {
                    adapter.setSelectedItem(i);
                }
                adapter.showCountToast();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        phones.close();
    }

    public interface ContactListener {
        void onContactListFound(ContactActivity activity, HashMap<String, String> ContactList);
    }

    // Load data on background
    @SuppressLint("StaticFieldLeak")
    private class LoadContact extends AsyncTask<Void, Void, ArrayList> {

        @Override
        protected ArrayList doInBackground(Void... voids) {
            // Get Contact list from Phone

            if (phones != null) {

                while (phones.moveToNext()) {

                    Bitmap bit_thumb = null;
                    String id = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                    String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    String image_thumb = null;
                    image_thumb = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI));
                    try {
                        if (image_thumb != null) {
                            bit_thumb = MediaStore.Images.Media.getBitmap(resolver, Uri.parse(image_thumb));
                        } else {
                            Log.e("No Image Thumb", "--------------");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (!hashMap.containsKey(Integer.parseInt(id))) {
                        SelectUser selectUser = new SelectUser();
                        selectUser.setThumb(bit_thumb);
                        selectUser.setName(name);
                        phoneNumber = phoneNumber.contains(" ") ? phoneNumber.replaceAll("\\s+", "") : phoneNumber;
                        selectUser.setPhone(phoneNumber);
                        selectUser.setEmail(id);
                        selectUser.setCheckedBox(false);
                        selectUsers.add(selectUser);
                        hashMap.put(Integer.parseInt(id), Integer.parseInt(id));
                    }
                }
            } else {
                Log.e("Cursor close 1", "----------------");
            }
            //phones.close();
            return selectUsers;
        }

        @Override
        protected void onPostExecute(ArrayList list) {
            super.onPostExecute(list);

            if (list.size() == 0) {
                Toast.makeText(ContactActivity.this, "No contacts in your contact list.", Toast.LENGTH_LONG).show();
            }
            adapter = new SelectUserAdapter(list, ContactActivity.this);
            listView.setAdapter(adapter);
            if (dialog != null)
                dialog.dismiss();

           // listView.setFastScrollEnabled(true);
            //addMultiChoice();
        }
    }
}