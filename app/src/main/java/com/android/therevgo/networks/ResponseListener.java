package com.android.therevgo.networks;

import org.json.JSONObject;

/**
 * Interface that will hold the response from server
 */
public interface ResponseListener {

    /**
     * Called when request successfully executed and return some response
     * from the server.
     *
     * <p>This callback run on another thread from main thread</p>
     *
     * @param statusCode The statusCode got from server response
     * @param jsonObject that will hold the result come from server
     */
    void onResponse(final int statusCode, final JSONObject jsonObject) ;

    /**
     * Called when got some error from server or when some connection problem.
     *
     * <p>This callback run on another thread from main thread</p>
     *
     * @param msg that will define the proper error
     */
    void onError(final String msg);
}
