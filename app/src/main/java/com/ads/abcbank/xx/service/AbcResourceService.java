package com.ads.abcbank.xx.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.ads.abcbank.xx.model.DownloadItem;
import com.ads.abcbank.xx.utils.Constants;
import com.ads.abcbank.xx.utils.helper.ResHelper;

public class AbcResourceService extends IntentService {

    private static final String TAG = "AbcResourceService";

    public AbcResourceService() {
        super("AbcResourceService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (null == intent)
            return;

        DownloadItem downloadItem = intent.getParcelableExtra(Constants.DOWNLOADER_KEY_TASK);
        if (null == downloadItem || ResHelper.isNullOrEmpty(downloadItem.getUrl()))
            return;

//        String[] savePathData = ResHelper.getSavePathDataByUrl(downloadItem.getUrl());
//        if (savePathData.length <= 0)
//            return;

//        Aria.download(this).register();
//        Aria.download(this)
//                .load(downloadItem.getUrl())
//                .setFilePath(savePathData[1] + downloadItem.getMd5() + savePathData[0])
//                .start();

    }
}
