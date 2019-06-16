package com.ads.abcbank.service;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;


import com.ads.abcbank.bean.PlaylistBodyBean;
import com.ads.abcbank.bean.PlaylistResultBean;
import com.ads.abcbank.utils.FileUtil;
import com.ads.abcbank.utils.Logger;
import com.ads.abcbank.utils.TaskTagUtil;
import com.ads.abcbank.utils.Utils;
import com.ads.abcbank.view.BaseActivity;
import com.ads.abcbank.view.BaseTempFragment;
import com.alibaba.fastjson.JSON;
import com.liulishuo.okdownload.DownloadContext;
import com.liulishuo.okdownload.DownloadContextListener;
import com.liulishuo.okdownload.DownloadListener;
import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.OkDownload;
import com.liulishuo.okdownload.StatusUtil;
import com.liulishuo.okdownload.core.cause.EndCause;
import com.liulishuo.okdownload.core.cause.ResumeFailedCause;
import com.liulishuo.okdownload.core.listener.DownloadListener1;
import com.liulishuo.okdownload.core.listener.assist.Listener1Assist;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class DownloadService extends Service {
    public static final String ADD_DOWNTASK = "com.ads.abcbank.downtaskadd";
    public static final String ADD_MULTI_DOWNTASK = "com.ads.abcbank.multidowntaskadd";
    public static final String REMOVE_DOWNTASK = "com.ads.abcbank.removetask";
    public static final String CANCEL_QUEUE_DOWNTASK = "com.ads.abcbank.cancelqueuetask";
    public static final String START_QUEUE_DOWNTASK = "com.ads.abcbank.startqueuetask";
    public static final String PACKAGE = "com.ads.abcbank";


    private static final String TAG = "DownloadService";
    public static String downloadPath;
    public static String rootPath;

    private String type;
    private static List<DownloadTask> taskList = new ArrayList<>();
    private DownloadContext context;
    private final DownloadContextListener contextListener = new DownloadContextListener() {
        @Override
        public void taskEnd(@NonNull DownloadContext context, @NonNull DownloadTask task, @NonNull EndCause cause, @Nullable Exception realCause, int remainCount) {
            String status = cause.toString();
            Logger.e(TAG, task.getFilename() + "下载结束，状态：" + status +
                    ">>>downloadLink=" + task.getUrl());
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
        @Override
        public void taskStart(@NonNull DownloadTask task, @NonNull Listener1Assist.Listener1Model model) {
            TaskTagUtil.saveStatus(task, "taskStart");
            Logger.e(TAG, task.getFilename() + "开始下载");
        }

        @Override
        public void retry(@NonNull DownloadTask task, @NonNull ResumeFailedCause cause) {
            TaskTagUtil.saveStatus(task, "retry");
        }

        @Override
        public void connected(@NonNull DownloadTask task, int blockCount, long currentOffset, long totalLength) {
            TaskTagUtil.saveStatus(task, "connected");
        }

        @Override
        public void progress(@NonNull DownloadTask task, long currentOffset, long totalLength) {
            TaskTagUtil.saveStatus(task, "progress");
            TaskTagUtil.saveOffset(task, currentOffset);
            TaskTagUtil.saveTotal(task, totalLength);
        }

        @Override
        public void taskEnd(@NonNull DownloadTask task, @NonNull EndCause cause, @Nullable Exception realCause, @NonNull Listener1Assist.Listener1Model model) {
            /*TaskTagUtil.saveStatus(task, cause.toString());
            if (cause == EndCause.COMPLETED) {
                final String realMd5 = FileUtil.fileToMD5(task.getFile().getAbsolutePath());
                if (!realMd5.equalsIgnoreCase(TaskTagUtil.getMd5(task))) {
                    Logger.e(TAG, "file is wrong because of md5 is wrong " + realMd5);
                    FileUtil.deleteFile(downloadPath + task.getFilename());
                }
            }*/
            if (cause.equals(EndCause.COMPLETED)) {
                File downloadFile = new File(downloadPath + task.getFilename());
                if (!downloadFile.exists()) {
                    startTasks(true);
                } else {

                  /*  TimerTask timerTask = new TimerTask() {
                        @Override
                        public void run() {
                            openAndroidFile(downloadPath + task.getFilename());
                        }
                    };
                    Timer timer = new Timer();
                    timer.schedule(timerTask, 3000);*/

                }
            }
        }
    };
    private PlaylistResultBean playlistBean;


    public static ArrayList<DownloadTask> getPrepareTasks() {
        for (DownloadTask task : taskList
        ) {
            boolean isCompleted = StatusUtil.isCompleted(task);
            if (!isCompleted) {
                prepareTaskList.add(task);
            }
        }
        return prepareTaskList;
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
        downloadPath = rootPath + "/temp/";
        FileUtil.mkDir(rootPath + "/files/");
        FileUtil.mkDir(rootPath + "/conf/");
        FileUtil.mkDir(rootPath + "/zip/");
        FileUtil.mkDir(rootPath + "/temp/");
        FileUtil.mkDir(rootPath + "/screen/");
        FileUtil.createNewFile(rootPath + "playlist.json");


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
        }
        String action = null;
        try {
            action = intent.getAction();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        if (action == null) {
            return super.onStartCommand(intent, flags, startId);
        }
        switch (action) {
            case ADD_DOWNTASK:
                String name = intent.getStringExtra("name");
                String url = intent.getStringExtra("url");
                String isUrg = intent.getStringExtra("isUrg");
                addDownloadTask(name, url, isUrg);
                startTasks(true);
                break;
            case ADD_MULTI_DOWNTASK:
                //Todo 根据json添加下载任务，处理相关逻辑
                type = intent.getStringExtra("type");
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
            case START_QUEUE_DOWNTASK:
                boolean needStart = false;
                for (DownloadTask task : taskList
                ) {
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

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private String getDownSave() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/abcdownload/");
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

    private void addDownloadTasks() {
        String playlistJson = FileUtil.readTextFile("playlist.json");
        if (TextUtils.isEmpty(playlistJson)) {
            playlistJson = Utils.getStringFromAssets("playlist.json", mContext);
        }
         playlistBean = JSON.parseObject(playlistJson, PlaylistResultBean.class);
        if (playlistBean == null || playlistBean.data == null || playlistBean.data.items == null)
            return;
        for (int i = 0; i < playlistBean.data.items.size(); i++) {
            PlaylistBodyBean bodyBean = playlistBean.data.items.get(i);
            String contentTypeMiddle = Utils.getContentTypeMiddle(mContext);
            String contentTypeEnd = Utils.getContentTypeEnd(mContext);
//            todo 只下载对应type下的 文件
            if (!TextUtils.isEmpty(bodyBean.downloadLink)) {
                if (isDownTime(bodyBean.downloadTimeslice)) {
                    if (isNoPlayTime(bodyBean.stopDate)) {
                        addDownloadTask(bodyBean.name, bodyBean.downloadLink, bodyBean.isUrg);
                    } else {
                        Logger.e(TAG, "文件" + bodyBean.name + "超过播放时间");

                    }
                } else {
                    Logger.e(TAG, "文件" + bodyBean.name + "不在下载时间内");
                }
            } else {
                Logger.e(TAG, "文件" + bodyBean.name + "下载链接为空");

            }

            if (contentTypeEnd.equals("*")) {
                if (bodyBean.contentType.substring(1, 2).equals(contentTypeMiddle) &&
                        type.contains(bodyBean.contentType.substring(0, 1))) {
                    String suffix = bodyBean.name.substring(bodyBean.name.lastIndexOf(".") + 1).toLowerCase();
                    BaseTempFragment fragment = null;
                    switch (suffix) {
                        case "mp4":
                        case "mkv":
                        case "wmv":
                        case "avi":
                        case "rmvb":
                            break;
                        case "jpg":
                        case "png":
                        case "bmp":
                        case "jpeg":
                            break;
                        case "pdf":
                            break;
                        case "txt":
                            break;
                        default:
                            break;
                    }
                }
            } else {
                if (bodyBean.contentType.endsWith(contentTypeEnd) &&
                        bodyBean.contentType.substring(1, 2).equals(contentTypeMiddle) &&
                        type.contains(bodyBean.contentType.substring(0, 1))) {
                    String suffix = bodyBean.name.substring(bodyBean.name.lastIndexOf(".") + 1).toLowerCase();
                    switch (suffix) {
                        case "mp4":
                        case "mkv":
                        case "wmv":
                        case "avi":
                        case "rmvb":
                            break;
                        case "jpg":
                        case "png":
                        case "bmp":
                        case "jpeg":
                            break;
                        case "pdf":
                            break;
                        case "txt":
                            break;
                        default:
                            break;
                    }
                }
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
            this.context.start(listener, isSerial);
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

                .setWifiRequired(boolean wifiRequired)//只允许wifi下载
                .build();*/
        File parentFile = new File(downloadPath);
        final DownloadTask task = new DownloadTask.Builder(url, parentFile)
                .setPriority("1".equals(isUrg) ? 10 : 0)
                .setFilename(filename)
                .setFlushBufferSize(10)//下载限速60kb
                .setReadBufferSize(10)
                .build();
        builder.bindSetTask(task);
        this.context = builder.build();
        taskList = Arrays.asList(this.context.getTasks());
        return task;
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
        FileUtil.deleteFile(downloadPath + needRemovedTask.getFilename());
        OkDownload.with().downloadDispatcher().cancel(needRemovedTask.getId());
        OkDownload.with().breakpointStore().remove(needRemovedTask.getId());
        builder.unbind(needRemovedTask);
        this.context = builder.build();
        taskList = Arrays.asList(this.context.getTasks());
    }

    private boolean isDownTime(String downloadTimeslice) {

        try {
            if (TextUtils.isEmpty(downloadTimeslice))
                return true;
            else {
                String[] strs = downloadTimeslice.split("-");
                Calendar cal = Calendar.getInstance();
                cal.setTime(new Date());
                int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
                int _week = 7;
                _week = w;
                Logger.e(("," + strs[0] + ",").indexOf("," + _week + ",") + "");
                if (strs[0].indexOf(_week + "") != -1) {
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
            return false;
        }
    }

    public void clear12(String path) {


    }
}
