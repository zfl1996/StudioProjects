package com.ads.abcbank.activity;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ads.abcbank.R;
import com.ads.abcbank.utils.Utils;
import com.ads.abcbank.view.BaseActivity;

public class WebViewActivity extends BaseActivity {
    private ProgressBar progressBar;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        init();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void init() {
        TextView back = (TextView) findViewById(R.id.back);
        webView = (WebView) findViewById(R.id.webview);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        Intent intent = getIntent();
        String url = intent.getStringExtra(Utils.WEBURL);
        back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                finish();
                Utils.changeIntent(WebViewActivity.this);
            }
        });
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setInitialScale(25);
        WebSettings settings = webView.getSettings();
        // 适应屏幕
        settings.setUseWideViewPort(true);
        settings.setSupportZoom(true);
        settings.setJavaScriptEnabled(true);
        int screenDensity = getResources().getDisplayMetrics().densityDpi;
        WebSettings.ZoomDensity zoomDensity = WebSettings.ZoomDensity.MEDIUM;
        switch (screenDensity) {
            case DisplayMetrics.DENSITY_LOW:
                zoomDensity = WebSettings.ZoomDensity.CLOSE;
                break;
            case DisplayMetrics.DENSITY_MEDIUM:
                zoomDensity = WebSettings.ZoomDensity.MEDIUM;
                break;
            case DisplayMetrics.DENSITY_HIGH:
                zoomDensity = WebSettings.ZoomDensity.FAR;
                break;
        }
        settings.setDefaultZoom(zoomDensity);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        settings.setLoadWithOverviewMode(true);
        settings.setBuiltInZoomControls(true);
        webView.setWebChromeClient(new WebChromeClient() {

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult jsResult) {
                final JsResult finalJsResult = jsResult;
                new AlertDialog.Builder(view.getContext()).setMessage(message)
                        .setPositiveButton(android.R.string.ok, new AlertDialog.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finalJsResult.confirm();
                            }
                        }).setCancelable(false).create().show();
                return true;
            }

        });
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView webview, String url, Bitmap favicon) {
            }

            @Override
            public void onPageFinished(WebView webview, String url) {
                progressBar.setVisibility(View.GONE);
            }

        });
        webView.loadUrl(url);
        setReInitRunnable(new Runnable() {
            @Override
            public void run() {
                recreate();
            }
        });
    }



    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack(); //goBack()表示返回WebView的上一页面
        } else {
            super.onBackPressed();
            Utils.changeIntent(this);
        }
    }
}

