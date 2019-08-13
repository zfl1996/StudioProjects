package com.ads.abcbank.xx.utils.core;

import android.content.Context;

import com.ads.abcbank.utils.Logger;
import com.ads.abcbank.utils.Utils;
import com.ads.abcbank.xx.utils.helper.ResHelper;
import com.arialyy.annotations.DownloadGroup;
import com.arialyy.aria.core.Aria;
import com.arialyy.aria.core.download.DownloadEntity;
import com.arialyy.aria.core.download.DownloadGroupTask;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class DownloadModule {
    String TAG = "DownloadModule";
    private Context mContext;
    private String mUrl;
    private DownloadStateLisntener downloadStateLisntener;
    private ConcurrentHashMap<String, Integer> waitForFeedback = new ConcurrentHashMap<>();

    public DownloadModule(Context context, int maxRate, DownloadStateLisntener downloadStateLisntener) {
        this.downloadStateLisntener = downloadStateLisntener;
        this.mContext = context;

        Aria.get(mContext).getDownloadConfig()
//                .setMaxTaskNum(1)
//                .setThreadNum(1)
                .setMaxSpeed(maxRate)
//                .setConvertSpeed(true)
                ;
        Aria.download(this).register();
    }

    @DownloadGroup.onSubTaskStop void onSubTaskRunning(DownloadGroupTask groupTask, DownloadEntity subEntity) {
        if (null == downloadStateLisntener)
            return;

        Utils.getExecutorService().submit(() -> {
            tryFeedbackTask(subEntity);
        });
    }

    @DownloadGroup.onSubTaskComplete void subTaskComplete(DownloadGroupTask groupTask, DownloadEntity subEntity) {
        if (null == downloadStateLisntener)
            return;

        Utils.getExecutorService().submit(() -> {
            tryFeedbackTask(subEntity);
        });

    }

    @DownloadGroup.onTaskComplete void taskComplete(DownloadGroupTask task) {
        Logger.e(TAG, "DownloadGroup.onTaskComplete-->taskKey:" + task.getKey() + " "
                + System.currentTimeMillis() + " tid:" + Thread.currentThread().getId() + "\r\n"
                + ResHelper.join(task.getEntity().getUrls().toArray(new String[task.getEntity().getUrls().size()]), "@@\r\n")
        );

        if (null == downloadStateLisntener)
            return;

        Utils.getExecutorService().submit(() -> {
            List<DownloadEntity> subTasks = task.getEntity().getSubEntities();
            for (DownloadEntity subtask : subTasks) {
                tryFeedbackTask(subtask);
            }
        });
    }

    @DownloadGroup.onTaskStop void taskStop(DownloadGroupTask task) {
        Logger.e(TAG, "DownloadGroup.onTaskStop--> " + task.getKey() + " " + System.currentTimeMillis() + " --"
                + Thread.currentThread().getId());

        if (null == downloadStateLisntener)
            return;

        Utils.getExecutorService().submit(() -> {
            List<DownloadEntity> subTasks = task.getEntity().getSubEntities();
            for (DownloadEntity subtask : subTasks) {
                tryFeedbackTask(subtask);
            }
        });
    }

    private boolean isTaskFeedback(String fileKey) {
        return waitForFeedback.containsKey(fileKey) && waitForFeedback.get(fileKey) == 1;
    }

    private void tryFeedbackTask(DownloadEntity subEntity) {
        if (subEntity.isComplete()) {
            if (!isTaskFeedback(subEntity.getKey())) {
                waitForFeedback.put(subEntity.getKey(), 1);
                downloadStateLisntener.onSucc(subEntity.getKey(), subEntity.getFilePath());
            }
        }
    }

    public void start(String url, String path, String identity) {
        this.mUrl = url;

        Aria.download(this)
                .load(url)
                .addHeader("Accept-Encoding", "gzip, deflate")
                .useServerFileName(true)
                .setFilePath(path)
//                .setExtendField(identity)
                .resetState()
                .start();
    }

    public void start(List<String> urls, String path, List<String> paths) {
        Logger.e(TAG, "tid:" + Thread.currentThread().getId() + " nums:" + urls.size());

        Utils.getExecutorService().submit(() -> {
            int nums = urls.size();
            for (int i=0;i<nums;i++) {
                waitForFeedback.put(urls.get(i), 0);
            }

            try {
                Aria.download(this)
                        .loadGroup(urls)
                        .addHeader("Accept-Encoding", "gzip, deflate")
                        .setDirPath(path)
                        .setFileSize(114981416)
                        .setSubFileName(paths)
                        .resetState()
                        .start();
            } catch (Exception e) {
                Logger.e(TAG, "download task err:" + e.getMessage());
            }

            Logger.e(TAG, "tid:" + Thread.currentThread().getId());
        });
    }

    public void stop() {
        Aria.download(this).stopAllTask();
//        else
//            Aria.download(this).load(mUrl).stop();
    }

    public void cancel(String url) {
        Aria.download(this).load(url).cancel();
    }

    public void unRegister() {
        Aria.download(this).unRegister();
    }

    public interface DownloadStateLisntener {
        public void onSucc(/*String identity, */String url, String path);
        public void onFail(String url, String code);
    }
}
