package com.ads.abcbank.xx.utils.helper;

import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.ParcelFileDescriptor;

import com.ads.abcbank.utils.Logger;
import com.ads.abcbank.xx.model.PlayItem;
import com.ads.abcbank.xx.utils.Constants;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PdfHelper {
    static String TAG = "PdfHelper";

    public static List<PlayItem> getCachedPdfImage(String fileName, String playDate, String stopDate,
                                                   String clickLink,
                                                   Object attData) {
        String metadata = ResHelper.readFile2String(ResHelper.getPdfMetadataPath(fileName));

        List<PlayItem> list = new ArrayList<>();
        int size = Integer.parseInt(metadata.trim());

        for (int i=0; i<size; i++) {
            list.add(new PlayItem(ResHelper.getFileExtInfo(fileName)[0],
                    ResHelper.getPdfCacheFilePath(fileName, i),
                    Constants.SLIDER_HOLDER_IMAGE,
                    playDate, stopDate,
                    clickLink, attData));
        }

        return list;
    }

    public static List<PlayItem> cachePdfToImage(String fileName, String fileKey, String playDate, String stopDate,
                                                 String clickLink,
                                                 Object attData) {
        List<PlayItem> list = new ArrayList<>();

        if (ResHelper.isNullOrEmpty(fileName))
            return list;

        try {
            // ensure base file esists
            File file = new File(ResHelper.getPdfDir() + fileName);
            if (!file.exists())
                return list;

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
                            String fileAbsPath = ResHelper.savePdfPageFile(bitmap, fileName, i);

                            if (null != mCurrentPage) {
                                try {
                                    mCurrentPage.close();
                                } catch (Exception e) {
                                    Logger.e(TAG, e.toString());
                                }
                            }

                            list.add(new PlayItem(fileKey + i,
                                    fileAbsPath,
                                    Constants.SLIDER_HOLDER_IMAGE,
                                    playDate, stopDate,
                                    clickLink, attData));
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
                        Logger.e(TAG, e.toString());
                    }
                }
                if (mFileDescriptor != null) {
                    try {
                        mFileDescriptor.close();
                    } catch (IOException e) {
                        Logger.e(TAG, e.toString());
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Logger.e(TAG, e.toString());
        } catch (IOException e) {
            e.printStackTrace();
            Logger.e(TAG, e.toString());
        }

        return list;
    }
}
