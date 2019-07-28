package com.ads.abcbank.xx.service;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.ParcelFileDescriptor;
import android.support.annotation.Nullable;

import com.ads.abcbank.utils.Logger;
import com.ads.abcbank.xx.utils.Constants;
import com.ads.abcbank.xx.utils.helper.ResHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class CachePdfService extends IntentService {

    private static final String TAG = "CachePdfService";

    public CachePdfService() {
        super("CachePdfService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (null != intent) {
            String fileName = intent.getStringExtra(Constants.PDF_CACHE_FILENAME);
            if (ResHelper.isNullOrEmpty(fileName))
                return;


            try {
                // ensure base file esists
                File file = new File(ResHelper.getPdfDir() + fileName);
                if (!file.exists())
                    return;

                // start cache step by step...
                ParcelFileDescriptor mFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);

                if (mFileDescriptor != null) {
                    PdfRenderer mPdfRenderer = new PdfRenderer(mFileDescriptor);
                    int maxCache = mPdfRenderer.getPageCount();

                    // already cached
                    File cacheFile = new File(ResHelper.getPdfCacheFilePath(fileName, maxCache - 1));
                    if (!cacheFile.exists()){
                        for (int i=0; i<maxCache; i++) {
                            try {

                                File curCacheFile = new File(ResHelper.getPdfCacheFilePath(fileName, i));
                                if (curCacheFile.exists())
                                    continue;

                                if (i ==0) {
                                    File metaFile = new File(ResHelper.getPdfMetadataPath(fileName));
                                    ResHelper.writeFileFromString(metaFile, "" + maxCache, false);
                                }

                                PdfRenderer.Page mCurrentPage = mPdfRenderer.openPage(i);

                                Bitmap bitmap = Bitmap.createBitmap(mCurrentPage.getWidth(), mCurrentPage.getHeight(),
                                        Bitmap.Config.ARGB_8888);
                                mCurrentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_PRINT);
                                ResHelper.savePdfPageFile(bitmap, fileName, i);

                                if (null != mCurrentPage) {
                                    try {
                                        mCurrentPage.close();
                                    } catch (Exception e) {
                                        Logger.e(TAG, e.toString());
                                    }
                                }
                            } catch (Exception ex) {
                                Logger.e(TAG, ex.getMessage());

                                continue;
                            }

                        }
                    }


                    if (mPdfRenderer != null) {
                        try {
                            mPdfRenderer.close();
                        } catch (Exception e) {
                            Logger.e(e.toString());
                        }
                    }
                    if (mFileDescriptor != null) {
                        try {
                            mFileDescriptor.close();
                        } catch (IOException e) {
                            Logger.e(e.toString());
                        }
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
