package com.client.therevgo.fragments.services;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.android.therevgo.R;
import com.client.therevgo.adapters.ServiceAdapter;
import com.client.therevgo.constants.Config;
import com.client.therevgo.fragments.followUp.CreateFollowUpFragment;
import com.client.therevgo.fragments.services.adapter.ServiceGridAdapter;
import com.client.therevgo.networks.ResponseListener;
import com.client.therevgo.utility.PrefManager;
import com.client.therevgo.utility.Utils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;

import static com.client.therevgo.networks.HttpConnection.RequestPost;

/**
 * A simple {@link Fragment} subclass.
 */
public class ServiceContainerFragment extends Fragment
        implements AdapterView.OnItemClickListener, View.OnClickListener, ResponseListener {

    public static final String TAG = "ServiceContainerFragment";

    private TextInputLayout mNameLayout;
    private TextInputLayout mEmailLayout;

    private TextInputEditText mNameText;
    private TextInputEditText mEmailText;

    private GridView mServiceView;

    private Button mSubmitButton;

    private Utils utils = Utils.getInstance();

    private ServiceGridAdapter adapter;

    private final Handler handler = new Handler();
    private ProgressDialog dialog;

    public ServiceContainerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_service_container, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        dialog = new ProgressDialog(getContext());
        dialog.setMessage("Please Wait");
        dialog.setCancelable(false);
        mNameLayout = (TextInputLayout) view.findViewById(R.id.name_layout);
        mEmailLayout = (TextInputLayout) view.findViewById(R.id.email_layout);

        mNameText = (TextInputEditText) view.findViewById(R.id.edittxt_name);
        mEmailText = (TextInputEditText) view.findViewById(R.id.edittxt_email);

        mSubmitButton = (Button) view.findViewById(R.id.button_send);
        mSubmitButton.setOnClickListener(this);

        GridView gridView = (GridView) view.findViewById(R.id.service_view);

        adapter = new ServiceGridAdapter(getContext());

        // Instance of ImageAdapter Class
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(this);
    }

    /**
     * Callback method to be invoked when an item in this AdapterView has
     * been clicked.
     * <p>
     * Implementers can call getItemAtPosition(position) if they need
     * to access the data associated with the selected item.
     *
     * @param parent   The AdapterView where the click happened.
     * @param view     The view within the AdapterView that was clicked (this
     *                 will be a view provided by the adapter)
     * @param position The position of the view in the adapter.
     * @param id       The row id of the item that was clicked.
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        adapter.getStates()[position] = !adapter.getStates()[position];

        adapter.notifyDataSetChanged();

        // Toast.makeText(getContext(), ""+position, Toast.LENGTH_SHORT).show();
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        if (!utils.validateView(getActivity(), mNameText, getString(R.string.err_msg_name))) {
            return;
        }
        if (!utils.validateView(getActivity(), mEmailText, "Enter Email")) {
            return;
        }
        String user_id = (String) PrefManager.getInstance(getContext())
                .getDataFromPreference(PrefManager.Key.USER_ID, PrefManager.Type.TYPE_STRING);

        String URL = Config.DOMAIN +
                "api/msgsave/ServiceMOBIns/?" +
                "name="+ URLEncoder.encode(mNameText.getText().toString().trim())+
                "&email_id="+URLEncoder.encode(mEmailText.getText().toString().trim())+
                "&service_name="+adapter.getSelectedServices()+"&userid="+user_id;

        dialog.show();

        RequestPost(URL, getRequestParam(), this);
    }

    private ArrayList<NameValuePair> getRequestParam() {
        return new ArrayList<>();
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
                dialog.dismiss();
                if (statusCode == 200 && jsonObject.optBoolean("status")) {
                    // {"status":true,"message":"Record Add Successfully.","error":null,"Data":null}
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            resetViews();
                        }
                    });
                    Toast.makeText(
                        getContext(),
                        "Your services request is generated.We will respond you shortly.",
                        Toast.LENGTH_SHORT
                    ).show();
                }
            }
        });
    }

    private void resetViews() {
        mNameText.setText(null);
        mEmailText.setText(null);
        adapter.resetState();
        adapter.notifyDataSetChanged();
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
                Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
