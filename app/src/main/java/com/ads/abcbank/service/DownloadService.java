package com.ads.abcbank.service;

import android.Manifest;
import android.app.Activity;
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
import android.widget.Toast;


import com.ads.abcbank.utils.FileUtil;
import com.ads.abcbank.utils.TaskTagUtil;
import com.ads.abcbank.view.BaseActivity;
import com.liulishuo.okdownload.DownloadContext;
import com.liulishuo.okdownload.DownloadContextListener;
import com.liulishuo.okdownload.DownloadListener;
import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.OkDownload;
import com.liulishuo.okdownload.StatusUtil;
import com.liulishuo.okdownload.UnifiedListenerManager;
import com.liulishuo.okdownload.core.cause.EndCause;
import com.liulishuo.okdownload.core.cause.ResumeFailedCause;
import com.liulishuo.okdownload.core.listener.DownloadListener1;
import com.liulishuo.okdownload.core.listener.DownloadListener2;
import com.liulishuo.okdownload.core.listener.assist.Listener1Assist;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
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
    private String downloadPath;
    private static List<DownloadTask> taskList = new ArrayList<>();
    private DownloadContext context;
    private final DownloadContextListener contextListener = new DownloadContextListener() {
        @Override
        public void taskEnd(@NonNull DownloadContext context, @NonNull DownloadTask task, @NonNull EndCause cause, @Nullable Exception realCause, int remainCount) {
            String status = cause.toString();
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
                    Log.e(TAG, "file is wrong because of md5 is wrong " + realMd5);
                    FileUtil.deleteFile(downloadPath + task.getFilename());
                }
            }*/
            if (cause.equals(EndCause.COMPLETED)) {
                File downloadFile = new File(downloadPath + task.getFilename());
                if (!downloadFile.exists()) {
                    startTasks(true);
                } else {

                    TimerTask timerTask = new TimerTask() {
                        @Override
                        public void run() {
                            openAndroidFile(downloadPath + task.getFilename());
                        }
                    };
                    Timer timer = new Timer();
                    timer.schedule(timerTask, 3000);

                }
            }
        }
    };


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
        FileUtil.mkDir(getDownSave() + "/files/");
        FileUtil.mkDir(getDownSave() + "/conf/");
        FileUtil.mkDir(getDownSave() + "/zip/");
        FileUtil.mkDir(getDownSave() + "/temp/");
        FileUtil.mkDir(getDownSave() + "/screen/");
        FileUtil.createNewFile(getDownSave() + "playlist.json");
        downloadPath = getDownSave() + "/temp/";

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
                ArrayList<String> urls = intent.getStringArrayListExtra("urls");
                addDownloadTask( urls);
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

    private void addDownloadTask( ArrayList<String> urls) {

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
//                .setFlushBufferSize(10)//下载限速
//                .setReadBufferSize(10)
                .build();
        builder.bindSetTask(task);
        manager.attachListener(task, downloadListener);
        this.context = builder.build();
        taskList = Arrays.asList(this.context.getTasks());
        return task;
    }

    // all attach or detach is based on the id of Task in fact.
    UnifiedListenerManager manager = new UnifiedListenerManager();
    DownloadListener downloadListener = new DownloadListener2() {
        @Override
        public void taskStart(@NonNull DownloadTask task) {

        }

        @Override
        public void taskEnd(@NonNull DownloadTask task, @NonNull EndCause cause, @Nullable Exception realCause) {
            if (cause.equals(EndCause.COMPLETED)) {
                openAndroidFile(downloadPath + task.getFilename());
            }
        }
    };

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

}
