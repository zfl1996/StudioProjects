package com.ads.abcbank.xx.utils.core;

import android.content.Context;
import android.util.Log;

import com.ads.abcbank.utils.Logger;
import com.ads.abcbank.utils.Utils;
import com.ads.abcbank.xx.utils.helper.ResHelper;
import com.arialyy.annotations.Download;
import com.arialyy.annotations.DownloadGroup;
import com.arialyy.aria.core.Aria;
import com.arialyy.aria.core.download.DownloadEntity;
import com.arialyy.aria.core.download.DownloadGroupTask;
import com.arialyy.aria.core.download.DownloadTask;

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

    @Download.onWait void onWait(DownloadTask task) {
        Log.d(TAG, "wait ==> " + task.getDownloadEntity().getFileName());
    }

    @Download.onPre protected void onPre(DownloadTask task) {
        Log.d(TAG, "onPre");
    }

    @Download.onTaskStart void taskStart(DownloadTask task) {
        Log.d(TAG, "onStart");
    }

    @Download.onTaskRunning protected void running(DownloadTask task) {
        Log.d(TAG, "running");
    }

    @Download.onTaskResume void taskResume(DownloadTask task) {
        Log.d(TAG, "resume");
    }

    @Download.onTaskStop void taskStop(DownloadTask task) {
        if (null != downloadStateLisntener) {
            downloadStateLisntener.onFail(task.getKey(), "stop");
        }
    }

    @Download.onTaskCancel void taskCancel(DownloadTask task) {
        if (null != downloadStateLisntener) {
            downloadStateLisntener.onFail(task.getKey(), "cancel");
        }
    }

    @Download.onTaskFail void taskFail(DownloadTask task) {
        if (null != downloadStateLisntener) {
            downloadStateLisntener.onFail(task.getKey(), "fail");
        }
    }

    @Download.onTaskComplete void taskComplete(DownloadTask task) {
        if (null != downloadStateLisntener) {
            downloadStateLisntener.onSucc(/*task.getExtendField(), */task.getKey(), task.getFilePath());
        }
    }

    @DownloadGroup.onSubTaskComplete void subTaskComplete(DownloadGroupTask groupTask, DownloadEntity subEntity) {
        if (null == downloadStateLisntener)
            return;

        Utils.getExecutorService().submit(() -> {
            if (subEntity.isComplete()) {
                long time = System.currentTimeMillis();
                Logger.e(TAG, subEntity.getFilePath()
                        + "(" + isTaskFeedback(subEntity.getKey()) + ") " + subEntity.getKey() + " at: " + subEntity.getCompleteTime()
                        + "=" + time
                        + "-->" + subEntity.getPercent()
                        + " tid:" + Thread.currentThread().getId() + " (" + subEntity.getSpeed() + ")"
                );
                if (!isTaskFeedback(subEntity.getKey())){
                    waitForFeedback.put(subEntity.getKey(), 1);
                    downloadStateLisntener.onSucc(subEntity.getKey(), subEntity.getFilePath());
                }
            }
        });

    }

    @DownloadGroup.onTaskComplete void taskComplete(DownloadGroupTask task) {
        long time = System.currentTimeMillis();
        Logger.e(TAG, "DownloadGroup.onTaskComplete-->"
                + time + " tid:" + Thread.currentThread().getId() + "\r\n"
                + ResHelper.join(task.getEntity().getUrls().toArray(new String[task.getEntity().getUrls().size()]), "@@\r\n")
        );

        if (null == downloadStateLisntener)
            return;

        Utils.getExecutorService().submit(() -> {
            Logger.e(TAG, "DownloadGroup.onTaskComplete-->getExecutorService " + System.currentTimeMillis() + " --" + Thread.currentThread().getId());
            List<DownloadEntity> subTasks = task.getEntity().getSubEntities();
            for (DownloadEntity subtask : subTasks) {
                if (subtask.isComplete() && !isTaskFeedback(subtask.getKey())) {
                    waitForFeedback.put(subtask.getKey(), 1);
                    downloadStateLisntener.onSucc(subtask.getKey(), subtask.getFilePath());

                    Logger.e(TAG, "notify --> " + subtask.getKey() + " tid:" + Thread.currentThread().getId() + "");
                }
            }
        });
    }

    @DownloadGroup.onTaskStop void taskStop(DownloadGroupTask task) {
        Utils.getExecutorService().submit(() -> {
            Logger.e(TAG, "DownloadGroup.onTaskComplete-->getExecutorService " + System.currentTimeMillis() + " --" + Thread.currentThread().getId());
            List<DownloadEntity> subTasks = task.getEntity().getSubEntities();
            for (DownloadEntity subtask : subTasks) {
                if (subtask.isComplete() && !isTaskFeedback(subtask.getKey())) {
                    waitForFeedback.put(subtask.getKey(), 1);
                    downloadStateLisntener.onSucc(subtask.getKey(), subtask.getFilePath());

                    Logger.e(TAG, "onTaskStop --> " + subtask.getKey() + " isComplete:" + subtask.isComplete() + " hasFeedback:" + isTaskFeedback(subtask.getKey()));
                }
            }
        });
    }

    private boolean isTaskFeedback(String fileKey) {
        return waitForFeedback.containsKey(fileKey) && waitForFeedback.get(fileKey) == 1;
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

            Aria.download(this)
                    .loadGroup(urls)
                    .addHeader("Accept-Encoding", "gzip, deflate")
                    .setDirPath(path)
                    .setFileSize(114981416)
                    .setSubFileName(paths)
                    .resetState()
                    .start();

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
