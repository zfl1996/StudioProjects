package com.ads.abcbank.xx.utils.core;

import android.content.Context;
import android.util.Log;

import com.arialyy.annotations.Download;
import com.arialyy.aria.core.Aria;
import com.arialyy.aria.core.download.DownloadTask;

import java.util.List;

public class DownloadModule {
    String TAG = "DownloadModule";
    private Context mContext;
    private String mUrl;
    private DownloadStateLisntener downloadStateLisntener;

    public DownloadModule(Context context, int maxRate, DownloadStateLisntener downloadStateLisntener) {
        this.downloadStateLisntener = downloadStateLisntener;
        this.mContext = context;

        Aria.get(mContext).getDownloadConfig().setMaxSpeed(maxRate);
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
            downloadStateLisntener.onSucc(task.getKey(), task.getFilePath());
        }
    }


    public void start(String url, String path) {
        this.mUrl = url;

        Aria.download(this)
                .load(url)
                .addHeader("Accept-Encoding", "gzip, deflate")
                .useServerFileName(true)
                .setFilePath(path)
                .resetState()
                .start();
    }

    public void start(List<String> urls, String path, List<String> paths) {

        Aria.download(this)
                .loadGroup(urls)
                .addHeader("Accept-Encoding", "gzip, deflate")
                .setDirPath(path)
                .setFileSize(2)
//                .setSubFileName(paths)
                .resetState()
                .start();
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
        public void onSucc(String url, String path);
        public void onFail(String url, String code);
    }
}
