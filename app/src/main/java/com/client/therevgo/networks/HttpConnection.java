package com.client.therevgo.networks;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class HttpConnection {


    //private static final String REQUEST  = "Request";
    private static final String RESPONSE = "Response";

    private static final int SECOND  = 1000;
    // Set the timeout in milliseconds until a connection is established.
    // The default value is zero, that means the timeout is not used.
    private static final int timeoutConnection = 30 * SECOND;
    // Set the default socket timeout (SO_TIMEOUT)
    // in milliseconds which is the timeout for waiting for data.
    private static final int timeoutSocket = 30 * SECOND;

    public static void RequestPost(final String url, final ArrayList<NameValuePair> valuePairs,
                                   final ResponseListener responseListener) {
         Runnable runnable = new Runnable() {
             @Override
             public void run() {
                 try {
                     HttpParams httpParameters = new BasicHttpParams();
                     HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
                     HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

                     HttpClient httpClient = new DefaultHttpClient(httpParameters);

                     HttpPost post = new HttpPost(url);
                     BasicHeader basicHeader = new BasicHeader("Content-Type","application/x-www-form-urlencoded");
                     post.addHeader(basicHeader);

                     Log.d("Request", url);
                     if (valuePairs != null) {
                         post.setEntity(new UrlEncodedFormEntity(valuePairs));
                     }

                     HttpResponse res = httpClient.execute(post);

                     StatusLine sl = res.getStatusLine();

                     HttpEntity resEntity = res.getEntity();
                     InputStream in = resEntity.getContent();
                     String ResponseFromServer = getString(in);

                     JSONObject object = new JSONObject(ResponseFromServer);
                     Log.i(RESPONSE,object.toString());

                     responseListener.onResponse(sl.getStatusCode() , object);

                 } catch (JSONException | ClientProtocolException e) {
                     e.printStackTrace();
                     responseListener.onError(e.toString());
                 } catch (ConnectTimeoutException e){
                     e.printStackTrace();
                     responseListener.onError("Please Check your internet connection.");
                 } catch (IOException e) {
                     e.printStackTrace();
                     responseListener.onError(e.toString());
                 }
             }
         };
        new Thread(runnable).start();
    }

    public static void RequestGet(final String URL, final ResponseListener responseListener){
        Runnable runnable = new Runnable() {
            HttpURLConnection connection = null ;
            @Override
            public void run() {
                try {
                    Log.i("Request",URL);
                    java.net.URL url = new URL(URL);
                    String responseFromServer;
                    connection = (HttpURLConnection) url.openConnection();

                    /* for Get request */
                    connection.setConnectTimeout(timeoutConnection);
                    connection.setReadTimeout(timeoutSocket);
                    connection.setRequestMethod("GET");
                    int responseCode = connection.getResponseCode();


                    if(responseCode == HttpURLConnection.HTTP_OK) {
                        responseFromServer = getString(new BufferedInputStream(connection.getInputStream()));

                        String finalResponse = new String(responseFromServer.getBytes(), Charset.forName("UTF-8"));
                        JSONObject object = new JSONObject(finalResponse);
                        Log.i(RESPONSE,object.toString());
                        responseListener.onResponse(responseCode,object);
                    } else if (responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
                        Log.i(RESPONSE,URL+" page Not Found");
                        responseListener.onError("Unable to connect server");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    responseListener.onError(e.toString());
                } catch (SocketTimeoutException | ConnectException e) {
                    e.printStackTrace();
                    responseListener.onError("Please Check your internet connection");
                } catch (IOException e) {
                    e.printStackTrace();
                    responseListener.onError(e.toString());
                }
            }
        };
        new Thread(runnable).start();
    }

    public static String getString(InputStream in) {
        int c;
        StringBuilder r = new StringBuilder();

        try {
            while ((c = in.read()) != -1)
                r.append((char) c);

            return r.toString();
        } catch (Exception ignored) {
        }

        return null;
    }

    public static void uploadFile(final String url, final MultipartEntityBuilder mpEntity
            , final ResponseListener l) {

        Runnable runnable = new Runnable() {
            public void run() {
                try {
                    HttpParams httpParameters = new BasicHttpParams();
                    HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
                    HttpConnectionParams.setSoTimeout(httpParameters, 10000);

                    HttpClient httpClient = new DefaultHttpClient(httpParameters);
                    HttpEntity entity = mpEntity.build();

                    HttpPost httppost = new HttpPost(url);
                   // httppost.addHeader("Content-Type", "multipart/form-data; boundary=----WebKitFormBoundaryt2wg4s9P4AfTS1Ny");
                    httppost.addHeader("Content-Type","application/x-www-form-urlencoded");
                    //httppost.addHeader("Content-Type","application/x-www-form-urlencoded");
                    httppost.setEntity(entity);

                    Log.i("httppost", url + "   " +mpEntity.toString());
                    HttpResponse response = httpClient.execute(httppost);
                    StatusLine sl = response.getStatusLine();

                    HttpEntity resEntity = response.getEntity();
                    InputStream in = resEntity.getContent();
                    String serverResponse = getString(in);

                    httpClient.getConnectionManager().shutdown();

                    JSONObject object = new JSONObject(serverResponse);
                    Log.i(RESPONSE,object.toString());

                    l.onResponse(sl.getStatusCode() , object);

                } catch (IOException | JSONException e) {
                    Log.i(RESPONSE,e.toString());
                    l.onError(e.toString());
                }
            }
        };
        new Thread(runnable).start();
    }
}
