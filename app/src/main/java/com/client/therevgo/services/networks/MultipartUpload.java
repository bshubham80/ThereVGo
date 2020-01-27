package com.client.therevgo.services.networks;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;

import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * This class handles the multipart api calls
 */

public class MultipartUpload extends Request<JSONObject> {


    private final Response.Listener<JSONObject> mListener;
    private final Map<String, File> mFilePartData;
    private final Map<String, String> mStringPart;
    private final Map<String, String> mHeaderPart;

    private MultipartEntityBuilder mEntityBuilder = MultipartEntityBuilder.create();
    private HttpEntity mHttpEntity;
    private Context mContext;
    private int index;


    public MultipartUpload(int method, Context mContext, String url, Response.Listener<JSONObject> listener,
                           Response.ErrorListener errorListener, Map<String, File> mFilePartData,
                           Map<String, String> mStringPart, Map<String, String> mHeaderPart) {
        super(method, url, errorListener);
        mListener = listener;

        this.mFilePartData = mFilePartData;
        this.mStringPart = mStringPart;
        this.mHeaderPart = mHeaderPart;
        this.mContext = mContext;

        mEntityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

        buildMultipartFileEntity();
        buildMultipartTextEntity();

        mHttpEntity = mEntityBuilder.build();

        setRetryPolicy(
            new DefaultRetryPolicy(
                (int) TimeUnit.MINUTES.toMillis(3),
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
        );
    }

    public static String getMimeType(Context context, String url) {
        Uri uri = Uri.fromFile(new File(url));
        String mimeType = null;
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            ContentResolver cr = context.getContentResolver();
            mimeType = cr.getType(uri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.toLowerCase());
        }
        return mimeType;
    }

    private void buildMultipartFileEntity() {
        for (Map.Entry<String, File> entry : mFilePartData.entrySet()) {
            try {

                String key = entry.getKey();
                File file = entry.getValue();
                String mimeType = getMimeType(mContext, file.toString());
                mEntityBuilder.addBinaryBody(key, file, ContentType.create(mimeType), file.getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void buildMultipartTextEntity() {
        for (Map.Entry<String, String> entry : mStringPart.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (key != null && value != null)
                mEntityBuilder.addTextBody(key, value);
        }
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return mHeaderPart;
    }


    @Override
    public String getBodyContentType() {
        return mHttpEntity.getContentType().getValue();
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            mHttpEntity.writeTo(bos);
        } catch (IOException e) {
            VolleyLog.e("IOException writing to ByteArrayOutputStream");
        }
        return bos.toByteArray();
    }

    @Override
        protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            return Response.success(new JSONObject(jsonString), HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException usee) {
            return Response.error(new ParseError(usee));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }catch (Exception e){
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected VolleyError parseNetworkError(VolleyError volleyError) {
        Log.e("VolleyError","most probably an error");
        return super.parseNetworkError(volleyError);
    }

    @Override
    public void deliverError(VolleyError error) {
        Log.e("deliverError","most probably an error");
        super.deliverError(error);
    }

    @Override
    protected void deliverResponse(JSONObject response) {
        mListener.onResponse(response);
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

}