package com.ads.abcbank.xx.fragment;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ads.abcbank.R;
import com.ads.abcbank.bean.PlaylistBodyBean;
import com.ads.abcbank.service.DownloadService;
import com.ads.abcbank.utils.ActivityManager;
import com.ads.abcbank.utils.Logger;
import com.ads.abcbank.utils.ToastUtil;
import com.ads.abcbank.view.BaseTempFragment;
import com.ads.abcbank.xx.model.PdfCacheInfo;
import com.ads.abcbank.xx.service.CachePdfService;
import com.ads.abcbank.xx.ui.adapter.BannerAdapter;
import com.ads.abcbank.xx.ui.widget.RecyclerPagerView;
import com.ads.abcbank.xx.utils.Constants;
import com.ads.abcbank.xx.utils.helper.ResHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class PdfContFragment extends BaseTempFragment {
    private View view;
//    private ImageView content;
    private PlaylistBodyBean bean;
    private long delayTime = 5000;
    private HandlerThread readerThread;
    private int totalCount = 0;

    private static final String TAG = "PdfContFragment";
    private static final int OK = 0x101;
    private static final int ERR = 0x102;
    private static final int OPEN = 0x0;
    private RecyclerPagerView rpv;
    private TextView txtHint;


    @Override
    protected View initView(LayoutInflater inflater) {
        view = inflater.inflate(R.layout.fragment_pdf_cache, null);
//        content = view.findViewById(R.id.content);
        rpv  = view.findViewById(R.id.rpPdf);
        txtHint = view.findViewById(R.id.txtHint);
        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
            mainHandler.postDelayed(() -> openPdf(0) , 300);
        } else {
            if (null != readerThread)
                readerThread.quit();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void initData() {

    }

    @Override
    public void setBean(PlaylistBodyBean bean) {
        this.bean = bean;
//        Intent serviceIntent = new Intent(getContext(), CachePdfService.class);
//
//        serviceIntent.putExtra(Constants.PDF_CACHE_FILENAME, bean.name);
//        getContext().startService(serviceIntent);

        initData();
        showQRs(bean);
    }

    void showContent(PdfCacheInfo cacheInfo){
        BannerAdapter bannerAdapter = new BannerAdapter(context);

        LinearLayoutManager lm = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        rpv.setLayoutManager(lm);
        rpv.setAdapter(bannerAdapter);


        bannerAdapter.setDataSource(Arrays.asList( cacheInfo.allCachePaths));
        rpv.setOnPageChangeListener(new PagerChangeListener(cacheInfo.pageCount));
        rpv.startPlay();
    }



    @Override
    public PlaylistBodyBean getBean() {
        return bean;
    }

    void openPdf(int pageIndex){
        readerThread = new HandlerThread("pdfThread");
        readerThread.start();

        readerHandler = new Handler(readerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                switch (msg.what) {
                    case OPEN:
                        openRenderer((int)msg.obj);
                        break;

                    default:
                        break;

                }
            }
        };

        readerHandler.sendMessage(buildMessage(OPEN, pageIndex, false));
    }

    private void openRenderer(int pageIndex) {
        // check cache status
        String metadata = ResHelper.readFile2String(ResHelper.getPdfMetadataPath(bean.name));
        if (ResHelper.isNullOrEmpty(metadata)) {
            Intent serviceIntent = new Intent(PdfContFragment.this.getContext(), CachePdfService.class);

            serviceIntent.putExtra(Constants.PDF_CACHE_FILENAME, bean.name);
            PdfContFragment.this.getContext().startService(serviceIntent);

            mainHandler.sendMessage(buildMessage(ERR, null, true));
            return;
        }

        // build cache data
        PdfCacheInfo cacheInfo = new PdfCacheInfo(Integer.parseInt(metadata.trim()), ResHelper.getPdfCacheFileDir(bean.name));
        cacheInfo.allCachePaths = new String[cacheInfo.pageCount];
        for (int i=0; i<cacheInfo.pageCount; i++) {
            cacheInfo.allCachePaths[i] = cacheInfo.cachePath + i + Constants.COMPRESS_FORMAT;
        }

        // send to ui
        mainHandler.sendMessage(buildMessage(OK, cacheInfo, true));
    }

    Handler readerHandler;


    Handler mainHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {

                case OK:
                    if (msg.obj instanceof PdfCacheInfo) {
                        PdfCacheInfo cacheInfo = (PdfCacheInfo) msg.obj;
                        totalCount = cacheInfo.pageCount;

                        showContent(cacheInfo);
                    }

                    break;

                case ERR:

                    break;

                default:
                    break;

            }
        }
    };

    Message buildMessage(int w, Object obj, boolean isMain) {
        Message msg = isMain ? mainHandler.obtainMessage() : readerHandler.obtainMessage();
        msg.what = w;
        msg.obj = obj;

        return msg;
    }

    public class  PagerChangeListener extends RecyclerPagerView.OnPageChangeListener {

//        private TextView txtHint;
        private int size;

        public PagerChangeListener(/*ImageView imgView, */int size) {
//            this.imgView = imgView;
            this.size = size;
        }

        @Override
        public void onPageSelection(int position) {
            int pos = (position%size+1);

            if (pos == size)
                mainHandler.postDelayed(() -> {
                    if (isVisiable && null != tempView && ActivityManager.getInstance().getTopActivity() == tempView.getContext()){
                        tempView.nextPlay();
                    }
                }, 5000);

            txtHint.setText(pos + " / " + size + " / " + position);
//            ToastUtil.showToast(/*imgView.getContext()*/context, (position%size+1) + "/" + size);
        }
    }

}
