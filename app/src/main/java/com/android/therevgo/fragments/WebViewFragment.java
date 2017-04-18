package com.android.therevgo.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.android.therevgo.R;

public class WebViewFragment extends Fragment {

    private static final String ARG_URL = "url";
    public static final String TAG = WebViewFragment.class.getName();
    View rootView;
    WebView staticPageWebView;

    public static WebViewFragment newInstance(String url) {

        Bundle args = new Bundle();
        args.putString(ARG_URL,url);
        WebViewFragment fragment = new WebViewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_webview, container, false);
        staticPageWebView = (WebView) rootView.findViewById(R.id.webViewStaticPage);

        String pageUrl = getArguments().getString(ARG_URL);
        staticPageWebView.setHorizontalScrollBarEnabled(false);
        staticPageWebView.getSettings().setJavaScriptEnabled(true);
        staticPageWebView.getSettings().setPluginState(WebSettings.PluginState.ON);

        staticPageWebView.loadUrl(pageUrl);

        staticPageWebView.setWebViewClient(new WebViewController());

        final ProgressBar Pbar = (ProgressBar) rootView.findViewById(R.id.pbStaticPage);
        staticPageWebView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                if (progress < 100 && Pbar.getVisibility() == ProgressBar.GONE) {
                    Pbar.setVisibility(ProgressBar.VISIBLE);
                }
                Pbar.setProgress(progress);
                if (progress == 100) {
                    Pbar.setVisibility(ProgressBar.GONE);
                }
            }
        });

        return rootView;
    }

    public class WebViewController extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub
            return false;// true won't let you load the url
        }

    }
}
