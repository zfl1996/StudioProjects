package com.ads.abcbank.xx.utils.net;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.arialyy.annotations.Download;
import com.arialyy.aria.core.Aria;
import com.arialyy.aria.core.download.DownloadTask;

public class AbcDownloader {

    String TAG = "AbcDownloader";

    private Context context;
    private String url;

    public AbcDownloader(Context context) {
        Aria.download(this).register();
        this.context = context;
    }

    public void start(String url) {
        this.url = url;
        Aria.download(this)
                .load(url)
                .addHeader("Accept-Encoding", "gzip, deflate")
//                .setRequestMode(RequestEnum.GET)
                .setFilePath(Environment.getExternalStorageDirectory().getPath() + "/ggsg1.apk")
                .resetState()
                .start();
    }

    void stop() {
        Aria.download(this).load(url).stop();
    }

    void cancel() {
        Aria.download(this).load(url).cancel();
    }

    void unRegister() {
        Aria.download(this).unRegister();
    }


    @Download.onWait void onWait(DownloadTask task) {
        Log.d(TAG, "wait ==> " + task.getDownloadEntity().getFileName());
    }

    @Download.onPre protected void onPre(DownloadTask task) {
        Log.d(TAG, "onPre");
    }

    @Download.onTaskStart void taskStart(DownloadTask task) {
        Log.d(TAG, "onStarted");
    }

    @Download.onTaskRunning protected void running(DownloadTask task) {
        Log.d(TAG, "running");
    }

    @Download.onTaskResume void taskResume(DownloadTask task) {
        Log.d(TAG, "resume");
    }

    @Download.onTaskStop void taskStop(DownloadTask task) {
        Log.d(TAG, "stop");
    }

    @Download.onTaskCancel void taskCancel(DownloadTask task) {
        Log.d(TAG, "cancel");
    }

    @Download.onTaskFail void taskFail(DownloadTask task) {
        Log.d(TAG, "fail");
    }

    @Download.onTaskComplete void taskComplete(DownloadTask task) {

    }

}
