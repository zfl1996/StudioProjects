package com.ads.abcbank.service;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.widget.Toast;


import com.ads.abcbank.bean.DownloadBean;
import com.ads.abcbank.bean.PlaylistBean;
import com.ads.abcbank.bean.PlaylistBodyBean;
import com.ads.abcbank.bean.PlaylistResultBean;
import com.ads.abcbank.bean.RegisterBean;
import com.ads.abcbank.utils.FileUtil;
import com.ads.abcbank.utils.HTTPContants;
import com.ads.abcbank.utils.HandlerUtil;
import com.ads.abcbank.utils.Logger;
import com.ads.abcbank.utils.TaskTagUtil;
import com.ads.abcbank.utils.Utils;
import com.ads.abcbank.view.BaseActivity;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.liulishuo.okdownload.DownloadContext;
import com.liulishuo.okdownload.DownloadContextListener;
import com.liulishuo.okdownload.DownloadListener;
import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.OkDownload;
import com.liulishuo.okdownload.SpeedCalculator;
import com.liulishuo.okdownload.StatusUtil;
import com.liulishuo.okdownload.core.Util;
import com.liulishuo.okdownload.core.breakpoint.BlockInfo;
import com.liulishuo.okdownload.core.breakpoint.BreakpointInfo;
import com.liulishuo.okdownload.core.cause.EndCause;
import com.liulishuo.okdownload.core.cause.ResumeFailedCause;
import com.liulishuo.okdownload.core.listener.DownloadListener1;
import com.liulishuo.okdownload.core.listener.DownloadListener2;
import com.liulishuo.okdownload.core.listener.DownloadListener4WithSpeed;
import com.liulishuo.okdownload.core.listener.DownloadListenerBunch;
import com.liulishuo.okdownload.core.listener.assist.Listener1Assist;
import com.liulishuo.okdownload.core.listener.assist.Listener4SpeedAssistExtend;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;


public class DownloadService extends Service {
    public static final String ADD_UPDATE_DOWNTASK = "com.ads.abcbank.downtaskadd";
    public static final String ADD_MULTI_DOWNTASK = "com.ads.abcbank.multidowntaskadd";
    public static final String REMOVE_DOWNTASK = "com.ads.abcbank.removetask";
    public static final String CANCEL_QUEUE_DOWNTASK = "com.ads.abcbank.cancelqueuetask";
    public static final String START_QUEUE_DOWNTASK = "com.ads.abcbank.startqueuetask";
    public static final String DELETE_FILE_12 = "com.ads.abcbank.deletefile12";
    public static final String PACKAGE = "com.ads.abcbank";
    public static final String TASKS_CHANGED = "com.ads.abcbank.taskchanged";


    private static final String TAG = "DownloadService";
    public static String downloadPath;
    public static String downloadFilePath;
    public static String downloadVideoPath;
    public static String downloadApkPath;
    public static String downloadImagePath;
    public static String rootPath;
    public final static String ROOT_FILE_NAME = "ibcsPlayerData";

    private String type;
    private static List<DownloadTask> taskList = new ArrayList<>();
    private DownloadContext context;

    public class MyBinder extends Binder {

        public DownloadService getService() {
            return DownloadService.this;
        }
    }

    //通过binder实现了 调用者（client）与 service之间的通信
    private DownloadService.MyBinder binder = new DownloadService.MyBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return false;
    }

    private final DownloadContextListener contextListener = new DownloadContextListener() {
        @Override
        public void taskEnd(@NonNull DownloadContext context, @NonNull DownloadTask task, @NonNull EndCause cause, @Nullable Exception realCause, int remainCount) {
            String status = cause.toString();
            if (cause.equals(EndCause.COMPLETED)) {
                Logger.e(TAG, task.getFilename() + "下载完成");
            } else {
                Logger.e(TAG, task.getFilename() + "下载出错，状态：" + status +
                        ">>>downloadLink=" + task.getUrl() + "，异常信息：" + realCause);
            }

            /*if (cause.equals(EndCause.COMPLETED)) {
                File downloadFile = new File(downloadPath + task.getFilename());
                if (!downloadFile.exists()) {
                    startTasks(true);
                } else {
                    openAndroidFile(downloadPath + task.getFilename());
                }
            }*/
        }

        @Override
        public void queueEnd(@NonNull DownloadContext context) {
            if (context.getTasks().length < tasksSize()) {
                startTasks(true);
            } else {
                stopTasks();
            }
        }
    };
    private DownloadContext.Builder builder;
    private static ArrayList<DownloadTask> prepareTaskList = new ArrayList<>();
    private Context mContext;
    private final DownloadListener listener = new DownloadListener1() {
        long startTime;
        long endTime;

        @Override
        public void taskStart(@NonNull DownloadTask task, @NonNull Listener1Assist.Listener1Model model) {
            TaskTagUtil.saveStatus(task, "taskStart");
            DownloadBean downloadBean = TaskTagUtil.getDownloadBean(task);
            if (downloadBean != null) {
                downloadBean.started = dateToString();
                downloadBean.status = "taskStart";
                startTime = System.currentTimeMillis();
                addDowloadBean(downloadBean);
                Logger.e(TAG, task.getFilename() + "开始下载");
            }
        }

        @Override
        public void retry(@NonNull DownloadTask task, @NonNull ResumeFailedCause cause) {

            TaskTagUtil.saveStatus(task, "retry");
        }

        @Override
        public void connected(@NonNull DownloadTask task, int blockCount, long currentOffset, long totalLength) {
            DownloadBean downloadBean = TaskTagUtil.getDownloadBean(task);
            if (downloadBean != null) {
                downloadBean.status = "connected";
                addDowloadBean(downloadBean);
                TaskTagUtil.saveStatus(task, "connected");
            }
        }

        @Override
        public void progress(@NonNull DownloadTask task, long currentOffset, long totalLength) {
            TaskTagUtil.saveStatus(task, "progress");
            DownloadBean downloadBean = TaskTagUtil.getDownloadBean(task);
            if (downloadBean != null) {
                downloadBean.status = "progress";
                addDowloadBean(downloadBean);
                TaskTagUtil.saveOffset(task, currentOffset);
                TaskTagUtil.saveTotal(task, totalLength);
            }
        }

        @Override
        public void taskEnd(@NonNull DownloadTask task, @NonNull EndCause cause, @Nullable Exception realCause, @NonNull Listener1Assist.Listener1Model model) {
//            TaskTagUtil.saveStatus(task, cause.toString());
            if (cause == EndCause.COMPLETED && Utils.IS_CHECK_MD5) {
                if (!FileUtil.fileToMD5(task.getFile().getAbsolutePath()).equalsIgnoreCase(TaskTagUtil.getMd5(task))) {
                    Logger.e(TAG, "file is wrong because of md5 is wrong " + FileUtil.fileToMD5(task.getFile().getAbsolutePath()));
                    switch (Utils.getFileExistType(task.getFilename())) {
                        case 1:
                            FileUtil.deleteFile(downloadPath + task.getFilename());
                            break;
                        case 2:
                            FileUtil.deleteFile(downloadFilePath + task.getFilename());
                            break;
                        case 3:
                            FileUtil.deleteFile(downloadImagePath + task.getFilename());
                            break;
                        case 4:
                            FileUtil.deleteFile(downloadVideoPath + task.getFilename());
                            break;
                        case 5:
                            FileUtil.deleteFile(downloadApkPath + task.getFilename());
                            break;
                        default:
                            break;
                    }
                }
            }
            endTime = System.currentTimeMillis();
            try {
                DownloadBean downloadBean = TaskTagUtil.getDownloadBean(task);
                if (downloadBean != null) {
                    Logger.i(task.getFilename() + "---index---" + TaskTagUtil.getDownloadBeanIndex(task));
                    downloadBean.status = cause.toString();
                    downloadBean.secUsed = String.valueOf((endTime - startTime) / 1000);
                    addDowloadBean(downloadBean);
                    if (cause.equals(EndCause.COMPLETED)) {
                        File downloadFile = new File(downloadPath + task.getFilename());
                        if (!downloadFile.exists()) {
                            startTasks(true);
                        } else {
                            downloadBean.status = "finish";
                            Logger.e(task.getFilename() + "--下载完成，通知服务端下载完成");
                            Utils.getAsyncThread().httpService(HTTPContants.CODE_DOWNLOAD_FINISH, JSONObject.parseObject(JSONObject.toJSONString(downloadBean)), HandlerUtil.noCheckGet(), 1);
                            //通知更新UI
                            sendIntent(TASKS_CHANGED);
                        }

                        {
                            String json = Utils.get(mContext, Utils.KEY_PLAY_LIST_DOWNLOAD, "").toString();
                            PlaylistResultBean allDownResultBean = null;
                            if (!TextUtils.isEmpty(json)) {
                                allDownResultBean = JSON.parseObject(json, PlaylistResultBean.class);
                            }
                            if (allDownResultBean != null && allDownResultBean.data != null && allDownResultBean.data.items != null) {
                                for (int i = 0; i < allDownResultBean.data.items.size(); i++) {
                                    if (allDownResultBean.data.items.get(i).id.equals(downloadBean.id) &&
                                            allDownResultBean.data.items.get(i).name.equals(task.getFilename())) {
                                        allDownResultBean.data.items.remove(i);
                                        break;
                                    }
                                }
                                Utils.put(mContext, Utils.KEY_PLAY_LIST_DOWNLOAD, JSONObject.toJSONString(allDownResultBean));
                                Utils.fileDownload(DownloadService.this, downloadBean);
                            }
                        }
                    } else if (cause.equals(EndCause.ERROR)) {
                        try {
                            getPlaylistBean().data.items.remove(downloadBean);
                            removeTask(downloadBean.id);
                        } catch (Exception e) {
                            Logger.e(TAG, e.toString());
                        }
                    }
                    Logger.e(task.getFilename() + "--下载开始时间:" + downloadBean.started);
                    Logger.e(task.getFilename() + "--下载状态:" + downloadBean.status);
                    Logger.e(task.getFilename() + "--下载用时:" + downloadBean.secUsed);
                }
            } catch (Exception e) {
                Logger.e(e.toString());
            }
            Logger.e("--下载列表状态:" + JSONObject.toJSONString(getPlaylistBean()));
        }
    };
    private DownloadListener downloadListener = new DownloadListener4WithSpeed() {

        private long totalLength;
        private String readableTotalLength;

        @Override
        public void taskStart(@NonNull DownloadTask task) {
            DownloadBean downloadBean = TaskTagUtil.getDownloadBean(task);
            downloadBean.started = System.currentTimeMillis() + "";
        }

        @Override
        public void connectStart(@NonNull DownloadTask task, int blockIndex, @NonNull Map<String, List<String>> requestHeaderFields) {
            for (Map.Entry<String, List<String>> entry : requestHeaderFields.entrySet()) {
                StringBuffer stringBuffer = new StringBuffer();
                for (String value :
                        entry.getValue()) {
                    stringBuffer.append(value);

                }
                Logger.e(TAG, "requestHeaderFields---Key = " + entry.getKey() + ",Value=" + stringBuffer.toString());
            }

        }

        @Override
        public void connectEnd(@NonNull DownloadTask task, int blockIndex, int responseCode, @NonNull Map<String, List<String>> responseHeaderFields) {
            for (Map.Entry<String, List<String>> entry : responseHeaderFields.entrySet()) {
                StringBuffer stringBuffer = new StringBuffer();
                for (String value :
                        entry.getValue()) {
                    stringBuffer.append(value);

                }
                Logger.e(TAG, "responseHeaderFields---Key = " + entry.getKey() + ",Value=" + stringBuffer.toString());
            }

        }

        @Override
        public void infoReady(@NonNull DownloadTask task, @NonNull BreakpointInfo info, boolean fromBreakpoint, @NonNull Listener4SpeedAssistExtend.Listener4SpeedModel model) {
            totalLength = info.getTotalLength();
            readableTotalLength = Util.humanReadableBytes(totalLength, true);
        }

        @Override
        public void progressBlock(@NonNull DownloadTask task, int blockIndex, long currentBlockOffset, @NonNull SpeedCalculator blockSpeed) {

        }

        @Override
        public void progress(@NonNull DownloadTask task, long currentOffset, @NonNull SpeedCalculator taskSpeed) {
            final String readableOffset = Util.humanReadableBytes(currentOffset, true);
            final String progressStatus = readableOffset + "/" + readableTotalLength;
            final String speed = taskSpeed.speed();
            final String progressStatusWithSpeed = progressStatus + "(" + speed + ")";

            Logger.e("文件" + task.getFilename() + "---当前下载状态及速度---" + progressStatusWithSpeed);
        }

        @Override
        public void blockEnd(@NonNull DownloadTask task, int blockIndex, BlockInfo info, @NonNull SpeedCalculator blockSpeed) {

        }

        @Override
        public void taskEnd(@NonNull DownloadTask task, @NonNull EndCause cause, @Nullable Exception realCause, @NonNull SpeedCalculator taskSpeed) {

        }
    };
    DownloadListener combinedListener = new DownloadListenerBunch.Builder()
            .append(listener)
            .append(downloadListener)
            .build();
    private DownloadListener updateListener = new DownloadListener2() {//更新下载apk
        @Override
        public void taskStart(@NonNull DownloadTask task) {
            Logger.e(TAG, task.getFilename() + "开始下载");
            DownloadBean downloadBean = TaskTagUtil.getDownloadBean(task);
            if (downloadBean != null) {
                downloadBean.started = System.currentTimeMillis() + "";
            }
        }

        @Override
        public void taskEnd(@NonNull DownloadTask task, @NonNull EndCause cause, @Nullable Exception realCause) {
            if (cause.equals(EndCause.COMPLETED)) {
                Logger.e(TAG, task.getFilename() + "下载完成");
                File downloadFile = new File(downloadPath + task.getFilename());
                if (!downloadFile.exists()) {
                    addUpdateTask(updateFileName, updateUrl);
                } else if (task.getFilename() != null && task.getFilename().toLowerCase().endsWith(".apk")) {
                    openAndroidFile(downloadPath + task.getFilename());
                }
            } else {
                Logger.e(TAG, task.getFilename() + "下载出错，>>>downloadLink=" + task.getUrl() + "，异常信息：" + realCause);
            }
        }
    };
    private PlaylistResultBean playlistResultBean;
    private RegisterBean registerBean;
    private String updateUrl;
    private String updateFileName;
    private volatile static PlaylistBean playlistBean = null;

    public static PlaylistBean getPlaylistBean() {
        if (playlistBean == null) {
            synchronized (PlaylistBean.class) {
                if (playlistBean == null) {
                    playlistBean = new PlaylistBean();
                }
            }
        }
        return playlistBean;
    }

    public static ArrayList<DownloadTask> getPrepareTasks() {
        for (DownloadTask task : taskList) {
            boolean isCompleted = StatusUtil.isCompleted(task);
            if (!isCompleted) {
                prepareTaskList.add(task);
            }
        }
        return prepareTaskList;
    }

    private String dateToString() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
        return simpleDateFormat.format(new Date());
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        createFilePath();
        initTasks(mContext, contextListener);
    }


    private void createFilePath() {
        rootPath = getDownSave();
        downloadPath = rootPath + "temp/";
        downloadFilePath = rootPath + "files/";
        downloadImagePath = rootPath + "images/";
        downloadVideoPath = rootPath + "videos/";
        downloadApkPath = rootPath + "zip/";
        createDir();
    }

    private void createDir() {
        FileUtil.mkDir(downloadPath);
        FileUtil.mkDir(downloadFilePath);
        FileUtil.mkDir(downloadImagePath);
        FileUtil.mkDir(downloadVideoPath);
        FileUtil.mkDir(downloadApkPath);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = null;
        try {
            action = intent.getAction();
        } catch (NullPointerException e) {
            Logger.e(e.toString());
        }
        if (action == null) {
            return super.onStartCommand(intent, flags, startId);
        }
        switch (action) {
            case ADD_UPDATE_DOWNTASK:
                updateFileName = intent.getStringExtra("name");
                updateUrl = intent.getStringExtra("url");
                addUpdateTask(updateFileName, updateUrl);
                break;
            case ADD_MULTI_DOWNTASK:
                type = intent.getStringExtra("type");
                String beanStr = Utils.get(this, Utils.KEY_REGISTER_BEAN, "").toString();
                if (!TextUtils.isEmpty(beanStr)) {
                    registerBean = JSON.parseObject(beanStr, RegisterBean.class);
                }
                getPlaylistBean();
                removeAllTasks();
                addDownloadTasks();
                startTasks(true);
                break;

            case REMOVE_DOWNTASK:
                String taskid = intent.getStringExtra("downloadid");
                removeTask(taskid);
                break;
            case CANCEL_QUEUE_DOWNTASK:
                stopTasks();
                break;
            case DELETE_FILE_12:
                clear12(rootPath + "/files/");
                clear12(rootPath + "/conf/");
                clear12(rootPath + "/zip/");
                clear12(rootPath + "/temp/");
                break;
            case START_QUEUE_DOWNTASK:
                boolean needStart = false;
                for (DownloadTask task : taskList) {
                    if (!StatusUtil.isCompleted(task)) {
                        needStart = true;
                    }

                }
                if (needStart) {
                    startTasks(true);
                }

                break;
            default:
                break;
        }


        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

//    @Nullable
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }

    /**
     * @param domain 域名
     * @param port   端口号
     * @param url    url路径
     * @return
     */
    public static String replaceDomainAndPort(String domain, String port, String url) {
        String urlBak = "";
        if (url.indexOf("//") != -1) {
            String[] splitTemp = url.split("//");
            urlBak = splitTemp[0] + "//";
            if (port != null) {
                urlBak = urlBak + domain + ":" + port;
            } else {
                urlBak = urlBak + domain;
            }

            if (splitTemp.length >= 1 && splitTemp[1].indexOf("/") != -1) {
                String[] urlTemp2 = splitTemp[1].split("/");
                if (urlTemp2.length > 1) {
                    for (int i = 1; i < urlTemp2.length; i++) {
                        urlBak = urlBak + "/" + urlTemp2[i];
                    }
                }
                System.out.println("url_bak:" + urlBak);
            } else {
                System.out.println("url_bak:" + urlBak);
            }
        }
        return urlBak;
    }

    private String getDownSave() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + ROOT_FILE_NAME + "/");
            if (!file.exists()) {
                boolean r = file.mkdirs();
                if (!r) {
                    Toast.makeText(mContext, "储存卡无法创建文件", Toast.LENGTH_SHORT).show();
                    return null;
                }
                return file.getAbsolutePath() + "/";
            }
            return file.getAbsolutePath() + "/";
        } else {
            Toast.makeText(mContext, "没有储存卡", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private boolean isCanConnected = true;
    private boolean isCanConnected2 = true;

    private void addDownloadTasks() {
        String json = Utils.get(mContext, Utils.KEY_PLAY_LIST_DOWNLOAD, "").toString();
        if (!TextUtils.isEmpty(json)) {
            playlistResultBean = JSON.parseObject(json, PlaylistResultBean.class);
        }
        String jsonFinish = Utils.get(mContext, Utils.KEY_PLAY_LIST_DOWNLOAD_FINISH, "").toString();
        if (!TextUtils.isEmpty(jsonFinish)) {
            JSONArray jsonArray = JSON.parseArray(jsonFinish);
            List<PlaylistBodyBean> finished = new ArrayList<>();
            for (int i = 0; i < jsonArray.size(); i++) {
                DownloadBean bean = JSON.parseObject(jsonArray.getString(i), DownloadBean.class);
                List<PlaylistBodyBean> list = playlistResultBean.data.items;

                for (int j = 0; j < list.size(); j++) {
                    if (bean.id != null && list.get(j).id != null && bean.id.equals(list.get(j).id) && Utils.getFileExistType(bean.name) > 0) {
                        finished.add(list.get(j));
                        break;
                    }
                }
            }
            playlistResultBean.data.items.removeAll(finished);
        }
        if (playlistResultBean == null || playlistResultBean.data == null || playlistResultBean.data.items == null) {
            return;
        }
        for (int i = 0; i < playlistResultBean.data.items.size(); i++) {
            PlaylistBodyBean bodyBean = playlistResultBean.data.items.get(i);
            if (!TextUtils.isEmpty(bodyBean.downloadLink)) {
                if (Utils.isInDownloadTime(bodyBean)) {
//                    if (Utils.isInPlayTime(bodyBean)) {
                    DownloadBean downloadBean = new DownloadBean();
                    String downloadUrl;
                    if (Utils.IS_TEST) {
                        downloadUrl = bodyBean.downloadLink;
                    } else {
                        downloadUrl = replaceDomainAndPort(registerBean.data.cdn, null, bodyBean.downloadLink);
                    }
                    if (Utils.existHttpPath(downloadUrl)) {
//                            DownloadTask task = addDownloadTask(bodyBean.name, replaceDomainAndPort(registerBean.data.server, null, bodyBean.downloadLink), bodyBean.isUrg);
                        DownloadTask task = addDownloadTask(bodyBean.name, downloadUrl, bodyBean.isUrg);
                        if (task != null) {
                            TaskTagUtil.saveMD5(task, bodyBean.md5);
                            downloadBean.id = bodyBean.id;
                            downloadBean.name = bodyBean.name;
                            addDowloadBean(downloadBean);
                            TaskTagUtil.saveDownloadId(task, bodyBean.id);
                            TaskTagUtil.saveDownloadBeanIndex(task, i);
                            TaskTagUtil.saveDownloadBean(task, downloadBean);
                        }
                    } else {
                        Logger.e("下载链接为空或路径非法");
                    }
//                        existHttpPath(bodyBean.downloadLink);
                      /*  if (isCanConnected2) {
                            DownloadTask task = addDownloadTask(bodyBean.name, bodyBean.downloadLink, bodyBean.isUrg);
                            downloadBean.id = bodyBean.id;
                            addDowloadBean(downloadBean);
                            TaskTagUtil.saveDownloadId(task, bodyBean.id);
                            TaskTagUtil.saveDownloadBeanIndex(task, i);
                            TaskTagUtil.saveDownloadBean(task, downloadBean);
                        }*/
//                    } else {
//                        Logger.e(TAG, "文件" + bodyBean.name + "不在播放时间内");

//                    }
                } else {
                    Logger.e(TAG, "文件" + bodyBean.name + "不在下载时间内");
                }
            } else {
                Logger.e(TAG, "文件" + bodyBean.name + "下载链接为空");

            }

        }
    }


    public void initTasks(@NonNull Context context, @NonNull DownloadContextListener listener) {
        final DownloadContext.QueueSet set = new DownloadContext.QueueSet();
        final File parentFile = new File(downloadPath);
        set.setParentPathFile(parentFile);
        set.setMinIntervalMillisCallbackProcess(200);
        builder = set.commit();
        builder.setListener(listener);
        this.context = builder.build();
        taskList = Arrays.asList(this.context.getTasks());
    }


    public void startTasks(boolean isSerial) {
        if (!this.context.isStarted()) {
            this.context.start(combinedListener, isSerial);
        }

    }

    public void stopTasks() {
        if (this.context.isStarted()) {
            this.context.stop();

        }
    }


    public int tasksSize() {
        return taskList.size();
    }

    public void addUpdateTask(String filename, String url) {
        stopTasks();
        int speedDownload;
        try {
            speedDownload = Integer.parseInt(Utils.get(this, Utils.KEY_SPEED_DOWNLOAD, "50").toString());
        } catch (Exception e) {
            speedDownload = 50;
        }
        if (speedDownload < 50) {
            speedDownload = 50;
            Utils.put(this, Utils.KEY_SPEED_DOWNLOAD, "50");
        }
        int downloadSpeed = speedDownload / 10;
        File parentFile = new File(downloadApkPath);
        final DownloadTask task = new DownloadTask.Builder(url, parentFile)
                .setPriority(10)
                .setFilename(filename)
                .setFlushBufferSize(downloadSpeed)//下载限速
                .setReadBufferSize(downloadSpeed)
                .build();
        task.enqueue(updateListener);
    }


    public DownloadTask addDownloadTask(String filename, String url, String isUrg) {
        /*DownloadTask task = new DownloadTask.Builder(url, parentFile)
                .setFilename(filename)
                .setPriority(priority)
                // the minimal interval millisecond for callback progress
                .setMinIntervalMillisCallbackProcess(10)//通知调用者的频率，避免anr
                // ignore the same task has already completed in the past.
                .setPassIfAlreadyCompleted(false)//如果文件已经下载完成，再次发起下载请求时，是否忽略下载，还是从头开始下载
                .setSyncBufferSize(0)//写入到文件的缓冲区大小，默认65536
//                .setSyncBufferIntervalMillis(1000 * 1000)//写入文件的最小时间间隔
                .setFlushBufferSize(10)//设置写入缓存区大小，默认16384
                .setReadBufferSize(10)//设置读取缓存区大小，默认4096
                .setPreAllocateLength(false) //在获取资源长度后，设置是否需要为文件预分配长度

//                .setWifiRequired(boolean wifiRequired)//只允许wifi下载
                .build();*/
        createFilePath();
        File parentFile = null;
        String suffix = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        switch (suffix) {
            case "mp4":
            case "mkv":
            case "wmv":
            case "avi":
            case "rmvb":
                parentFile = new File(downloadVideoPath);
                break;
            case "jpg":
            case "png":
            case "bmp":
            case "jpeg":
                parentFile = new File(downloadImagePath);
                break;
            case "pdf":
            case "txt":
                parentFile = new File(downloadFilePath);
                break;
            case "wps":
                return null;
            default:
                parentFile = new File(downloadPath);
                break;
        }

        int speedDownload;
        try {
            speedDownload = Integer.parseInt(Utils.get(this, Utils.KEY_SPEED_DOWNLOAD, "50").toString());
        } catch (Exception e) {
            speedDownload = 50;
        }
        if (speedDownload < 50) {
            speedDownload = 50;
            Utils.put(this, Utils.KEY_SPEED_DOWNLOAD, "50");
        }
        int downloadSpeed = speedDownload / 6;
//        int downloadSpeed = speedDownload / 100 + 1;
        isUrg = TextUtils.isEmpty(isUrg) ? "0" : isUrg;
        if (downloadSpeed <= 0) {
            downloadSpeed = 1;
        }

        final DownloadTask task = new DownloadTask.Builder(url, parentFile)
                .setPriority("1".equals(isUrg) ? 10 : 0)
                .setFilename(filename)
                .setFlushBufferSize(downloadSpeed)//下载限速60kb
                .setReadBufferSize(downloadSpeed)
                .build();
        builder.bindSetTask(task);
        this.context = builder.build();
        taskList = Arrays.asList(this.context.getTasks());
        return task;
    }

    public void addDowloadBean(@NonNull DownloadBean downloadBean) {
        final int index = getPlaylistBean().data.items.indexOf(downloadBean);
        if (index >= 0) {
            // replace
            getPlaylistBean().data.items.set(index, downloadBean);
        } else {
            getPlaylistBean().data.items.add(downloadBean);
        }
    }

    private void openAndroidFile(String filepath) {
        Intent intent = new Intent();
        // 这是比较流氓的方法，绕过7.0的文件权限检查
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }

        File file = new File(filepath);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//设置标记
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setAction(Intent.ACTION_VIEW);//动作，查看
        Uri uri;
        /**
         * Android7.0+禁止应用对外暴露file://uri，改为content://uri；具体参考FileProvider
         */
        if (Build.VERSION.SDK_INT >= 24) {
            if (android.os.Build.VERSION.SDK_INT >= 26) {
                boolean hasInstallPermission = mContext.getPackageManager().canRequestPackageInstalls();
                if (!hasInstallPermission) {
                    //请求安装未知应用来源的权限
                    ActivityCompat.requestPermissions(BaseActivity.mActivity, new String[]{Manifest.permission.REQUEST_INSTALL_PACKAGES}, 6666);
                }
            }
            uri = FileProvider.getUriForFile(mContext, "com.ads.abcbank.fileprovider", new File(filepath));
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(new File(filepath));
        }
        intent.setDataAndType(uri, FileUtil.getMIMEType(file));//设置类型
        mContext.startActivity(intent);
    }

    public DownloadTask addDownloadTask(DownloadTask task) {
        File parentFile = new File(downloadPath);
        builder.bindSetTask(task);
        this.context = builder.build();

        taskList = Arrays.asList(this.context.getTasks());
        return task;
    }

    public void removeTask(String downloadTaskId) {
        DownloadTask needRemovedTask = null;
        for (DownloadTask task : taskList) {
            if (downloadTaskId.equals(TaskTagUtil.getDownloadId(task))) {
                needRemovedTask = task;
            }
        }
        if (needRemovedTask != null) {
            FileUtil.deleteFile(downloadPath + needRemovedTask.getFilename());
            OkDownload.with().downloadDispatcher().cancel(needRemovedTask.getId());
            OkDownload.with().breakpointStore().remove(needRemovedTask.getId());
            builder.unbind(needRemovedTask);
        }
        this.context = builder.build();
        taskList = Arrays.asList(this.context.getTasks());
    }

    private boolean isDownTime(String downloadTimeslice) {

        try {
            if (TextUtils.isEmpty(downloadTimeslice)) {
                return true;
            } else {
                String[] strs = downloadTimeslice.split("-");
                Calendar cal = Calendar.getInstance();
                cal.setTime(new Date());
                int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
                int week;
                week = w;
                Logger.e(("," + strs[0] + ",").indexOf("," + week + ",") + "");
                if (strs[0].indexOf(week + "") != -1) {
//                    开始时间
                    int strDateBeginH = Integer.parseInt(strs[1].substring(0, 2));
                    int strDateBeginM = Integer.parseInt(strs[1].substring(3, 5));
                    // 截取结束时间时分
                    int strDateEndH = strDateBeginH + (strDateBeginM + Integer.parseInt(strs[2])) / 60;
                    int strDateEndM = (strDateBeginM + Integer.parseInt(strs[2])) % 60;

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String strDate = sdf.format(new Date());
                    // 截取当前时间时分秒
                    int strDateH = Integer.parseInt(strDate.substring(11, 13));
                    int strDateM = Integer.parseInt(strDate.substring(14, 16));
                    if ((strDateH >= strDateBeginH && strDateH <= strDateEndH)) {
                        // 当前时间小时数在开始时间和结束时间小时数之间
                        if (strDateH > strDateBeginH && strDateH < strDateEndH) {
                            return true;
                            // 当前时间小时数等于开始时间小时数，分钟数在开始和结束之间
                        } else if (strDateH == strDateBeginH && strDateM >= strDateBeginM
                                && strDateM <= strDateEndM) {
                            return true;
                        }
                        // 当前时间小时数大等于开始时间小时数，等于结束时间小时数，分钟数小等于结束时间分钟数
                        else if (strDateH >= strDateBeginH && strDateH == strDateEndH
                                && strDateM <= strDateEndM) {
                            return true;
                        } else {
                            return false;
                        }
                    } else {
                        return false;
                    }
//                    DateTime startDt = DateTime.Parse(strs[1]);
//                    DateTime endDt = startDt.AddMinutes(int.Parse(strs[2]));
//                    if ((DateTime.Now > startDt) && (DateTime.Now < endDt))
//                        return false;
//                    else
//                        return true;
                } else {
                    return false;
                }
            }

        } catch (Exception e) {
            Logger.e(TAG, e.toString());
        }
        return false;
    }

    private boolean isNoPlayTime(String strDateEnd) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm");
            Date stopDate = sdf.parse(strDateEnd);
            Date now = new Date();
            if (stopDate.after(now)) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            Logger.e(TAG, e.toString());
            Logger.e(TAG, "日期格式可能非\"yyyyMMdd HH:mm\"");
            return false;
        }
    }

    public interface Clear12Listener {
        boolean isFileUsed(String fileName);
    }

    /**
     * 清除超过12个月不在播放列表的文件
     *
     * @param path
     */
    public void clear12(String path) {

        File file = new File(path);
        FileUtil.deleteFile12(file, new Clear12Listener() {
            @Override
            public boolean isFileUsed(String fileName) {
                if (playlistResultBean == null || playlistResultBean.data == null || playlistResultBean.data.items == null) {
                    return false;
                }
                for (PlaylistBodyBean bodyBean :
                        playlistResultBean.data.items) {
                    if (bodyBean.name.equals(fileName)) {
                        return true;
                    }

                }
                return false;
            }
        });
    }

    private void sendIntent(String action) {
        Intent intent = new Intent();
        intent.setAction(action);
        intent.setPackage(PACKAGE);
        sendBroadcast(intent);
    }

    public void removeAllTasks() {
        for (DownloadTask task : taskList) {
//            FileUtil.deleteFile(downloadPath + task.getFilename());
            OkDownload.with().downloadDispatcher().cancel(task.getId());
            OkDownload.with().breakpointStore().remove(task.getId());
            builder.unbind(task);
        }
        this.context = builder.build();
        taskList = Arrays.asList(this.context.getTasks());
    }

}
