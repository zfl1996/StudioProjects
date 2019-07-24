package com.ads.abcbank.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ads.abcbank.R;
import com.ads.abcbank.bean.PlaylistBodyBean;
import com.ads.abcbank.utils.ActivityManager;
import com.ads.abcbank.utils.Utils;
import com.ads.abcbank.view.BaseTempFragment;

public class WebFragment extends BaseTempFragment {
    private View view;
    private WebView content;
    private PlaylistBodyBean bean;

    @Override
    protected View initView(LayoutInflater inflater) {
        view = inflater.inflate(R.layout.fragment_web, null);
        getViews();
        return view;
    }

    private void getViews() {
        content = view.findViewById(R.id.content);
    }

    @Override
    public void initData() {
        if (bean != null && content != null) {
            content.getSettings().setJavaScriptEnabled(true);
            content.setInitialScale(25);
            WebSettings settings = content.getSettings();
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
                default:
                    break;
            }
            settings.setDefaultZoom(zoomDensity);
            settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
            settings.setLoadWithOverviewMode(true);
            settings.setBuiltInZoomControls(true);
            content.setWebChromeClient(new WebChromeClient() {

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
            content.setWebViewClient(new WebViewClient() {
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
                }

            });
            content.loadUrl(bean.downloadLink);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            handler.removeCallbacks(runnable);
            handler.postDelayed(runnable, delayTime);
//        } else {
//            handler.removeCallbacks(runnable);
        }
    }

    private long delayTime = Utils.KEY_TIME_IMG_DEFAULT * 1000;
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (!isVisiable) {
                handler.removeCallbacks(runnable);
                return;
            }
            if (tempView != null && ActivityManager.getInstance().getTopActivity() == tempView.getContext()) {
                tempView.nextPlay();
                handler.removeCallbacks(runnable);
                handler.postDelayed(runnable, delayTime);
            } else {
                handler.removeCallbacks(runnable);
                handler.postDelayed(runnable, delayTime);
            }
        }
    };

    @Override
    public void setBean(PlaylistBodyBean bean) {
        this.bean = bean;
        initData();
        showQRs(bean);
    }

    @Override
    public PlaylistBodyBean getBean() {
        return bean;
    }

}
