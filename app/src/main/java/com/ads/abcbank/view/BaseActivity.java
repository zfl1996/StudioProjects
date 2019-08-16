package com.ads.abcbank.view;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;

import com.ads.abcbank.activity.ReInitActivity;
import com.ads.abcbank.bean.CmdpollResultBean;
import com.ads.abcbank.bean.CmdresultBean;
import com.ads.abcbank.service.DownloadService;
import com.ads.abcbank.utils.HTTPContants;
import com.ads.abcbank.utils.HandlerUtil;
import com.ads.abcbank.utils.Logger;
import com.ads.abcbank.utils.ToastUtil;
import com.ads.abcbank.utils.Utils;
import com.alibaba.fastjson.JSONObject;

public class BaseActivity extends AppCompatActivity {
    private final String CONNECTIVITY_CHANGE_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
    //    private NetChangeReceiver netChangeReceiver;//网络状态
    private int netType = -1;
    private boolean hasNet = false;
    private boolean hasInit = false;
    private Runnable reInitRunnable;
    private IView iView;
    public static Activity mActivity;
    private DownloadStatus downloadStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        netChangeReceiver = new NetChangeReceiver();
        mActivity = this;
        if (this instanceof IView) {
            registerDateTransReceiver();
            registerDowloadStatusReceiver();


        }
    }

    private void registerDowloadStatusReceiver() {
        downloadStatus = new DownloadStatus();
        IntentFilter f = new IntentFilter();
        f.addAction(DownloadService.TASKS_CHANGED);
        registerReceiver(downloadStatus, new IntentFilter(f));
    }

    private void registerDateTransReceiver() {
//        IntentFilter filter = new IntentFilter();
//        filter.addAction(CONNECTIVITY_CHANGE_ACTION);
//        filter.setPriority(1000);
//        registerReceiver(netChangeReceiver, filter);
    }

    public void startServices(String type) {
        Utils.getExecutorService().submit(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent();
                intent.putExtra("type", type);
                intent.setAction(DownloadService.ADD_MULTI_DOWNTASK);
                intent.setPackage(DownloadService.PACKAGE);
                startService(intent);
            }
        });
    }

    private void startDownloadService() {
        Intent intent = new Intent();
        intent.setAction(DownloadService.START_QUEUE_DOWNTASK);
        intent.setPackage(DownloadService.PACKAGE);
        startService(intent);
    }

    private int getNetworkType() {
        ConnectivityManager connectMgr = (ConnectivityManager) this
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectMgr == null) {
            return -1;
        }
        NetworkInfo info = connectMgr.getActiveNetworkInfo();
        if (info != null) {
            return info.getType();
        } else {
            return -1;
        }
    }

//    private class NetChangeReceiver extends BroadcastReceiver {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = null;
//            try {
//                action = intent.getAction();
//            } catch (Exception e) {
//                Logger.e("1", e.toString());
//            }
//            if (TextUtils.equals(action, CONNECTIVITY_CHANGE_ACTION)) {
//                netType = getNetworkType();
//                if (netType == ConnectivityManager.TYPE_WIFI || netType == ConnectivityManager.TYPE_MOBILE) {
//                    if (hasInit && !hasNet) {
//                        if (reInitRunnable != null) {
//                            reInitRunnable.run();
//                        }
//                    }
//                    hasNet = true;
//                } else {
//                    if (!hasInit) {
//                        hasNet = false;
//                    }
//                }
//                hasInit = true;
//            }
//        }
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        try {
//            unregisterReceiver(netChangeReceiver);
//        } catch (Exception e) {
//            Logger.e(BaseActivity.class.toString(), e.toString());
//        }
        if (this instanceof IView) {
            try {
                if (null != downloadStatus)
                    unregisterReceiver(downloadStatus);
            } catch (Exception e) {
                Logger.e(BaseActivity.class.toString(), e.toString());
            }
        }
        Utils.changeIntent(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (this instanceof IView) {
            startDownloadService();
            startHandler();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (this instanceof IView) {
            stopHandler();
        }
    }

    @Override
    public void onBackPressed() {
    }

    public void setReInitRunnable(Runnable reInitRunnable) {
        this.reInitRunnable = reInitRunnable;
    }

    public IView getiView() {
        return iView;
    }

    public void setiView(IView iView) {
        this.iView = iView;
    }

    private long mLastClickTime;
    private int clickTimes;

    public void toMainView(View view) {
        if ((System.currentTimeMillis() - mLastClickTime) > 1000) {
            mLastClickTime = System.currentTimeMillis();
            clickTimes = 0;
        } else {
            if (clickTimes < 1) {
                clickTimes++;
            } else {
                clickTimes = 0;
                BaseTempFragment.tempView = null;
                BaseTempFragment.tempView2 = null;
                startActivity(new Intent(this, ReInitActivity.class));
                finish();
            }
        }
    }

    public void getCmdResult(CmdpollResultBean bean) {
        if (bean != null) {
            HandlerUtil.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if ("prtsc".equals(bean.data.cmd)) {
                        CmdresultBean cmdresultBean = new CmdresultBean();
                        cmdresultBean.data.cmd = bean.data.cmd;
                        cmdresultBean.data.cmdresult = Utils.screenShot(BaseActivity.this);
                        cmdresultBean.flowNum = (TextUtils.isEmpty(bean.flowNum) ? 1 : Integer.parseInt(bean.flowNum)) + 1;
                        Utils.getAsyncThread().httpService(HTTPContants.CODE_CMDRESULT,
                                JSONObject.parseObject(JSONObject.toJSONString(cmdresultBean)), HandlerUtil.noCheckGet(), 0);
                    } else if (!TextUtils.isEmpty(bean.data.cmd) && "kill9".equals(bean.data.cmd)) {
                        ToastUtil.showToast(BaseActivity.this, "程序即将退出，cmd命令结果" + bean.data.cmd);
                        CmdresultBean cmdresultBean = new CmdresultBean();
                        cmdresultBean.data.cmd = bean.data.cmd;
                        cmdresultBean.data.cmdresult = "";
                        cmdresultBean.flowNum = (TextUtils.isEmpty(bean.flowNum) ? 1 : Integer.parseInt(bean.flowNum)) + 1;
                        Utils.getAsyncThread().httpService(HTTPContants.CODE_CMDRESULT,
                                JSONObject.parseObject(JSONObject.toJSONString(cmdresultBean)), handler, 0);
                    }
                }
            }, 500);
        }
    }

    @SuppressWarnings("ALL")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
//            ToastUtil.showToast(BaseActivity.this, "程序即将退出");
            HandlerUtil.postDelayed(new Runnable() {
                @Override
                public void run() {
//                    ActivityManager.getInstance().finishAllActivity();
//                    System.exit(0);
                }
            }, 1000);
        }
    };

    private class DownloadStatus extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = null;
            try {
                action = intent.getAction();
            } catch (Exception e) {
                Logger.e("2", e.toString());
            }
            if (action != null) {
                switch (action) {
                    case DownloadService.TASKS_CHANGED:
                        Logger.e("有文件下载完成播放列表即将更新" + getiView());
                        if (getiView() != null) {
                            Logger.e("有文件下载完成播放列表正在更新");
//                            getiView().updateMainDate(new JSONObject());
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    }

//    @SuppressLint("HandlerLeak")
//    public static Handler baseHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            Activity activity = ActivityManager.getInstance().getTopActivity();
//            switch (msg.what) {
//                case 0: {
//                    int handlerTime = Utils.getNumberForString(Utils.get(activity, Utils.KEY_TIME_CMD, Utils.KEY_TIME_CMD_TIME + "").toString(), Utils.KEY_TIME_CMD_TIME);
//                    baseHandler.postDelayed(cmdRunnable, handlerTime * 60 * 1000);
//                    Logger.e("getCmdPoll", "获取cmdpoll轮询命令返回数据====" + msg.obj);
//                    if (msg.obj != null) {
//                        Utils.put(activity, Utils.KEY_CMD_POLL, msg.obj.toString());
//                        if (activity instanceof BaseActivity) {
//                            ((BaseActivity) activity).getCmdResult(JSON.parseObject(msg.obj.toString(), CmdpollResultBean.class));
//                        }
//                    }
//                }
//                break;
//                case 1: {
//                    int handlerTime = Utils.getNumberForString(Utils.get(activity, Utils.KEY_TIME_PLAYLIST, Utils.KEY_TIME_PLAYLIST_TIME + "").toString(), Utils.KEY_TIME_PLAYLIST_TIME);
//                    baseHandler.postDelayed(playlistRunnable, handlerTime * 60 * 1000);
//                    if (Utils.IS_TEST) {
//                        msg.obj = Utils.getStringFromAssets("playlist.json", activity).toString();
//                        FileUtil.writeJsonToFile(msg.obj.toString(), false);
//                        if (Utils.getNewPlayList(activity, msg.obj.toString())) {
//                            if (activity != null && activity instanceof IView) {
//                                ((IView) activity).updateMainDate(JSONObject.parseObject(msg.obj.toString()));
//                            } else if (activity != null && activity instanceof BaseActivity && ((BaseActivity) activity).getiView() != null) {
//                                ((BaseActivity) activity).getiView().updateMainDate(JSONObject.parseObject(msg.obj.toString()));
//                            }
//                        }
//                        return;
//                    }
//                    if (msg.obj != null) {
//                        FileUtil.writeJsonToFile(msg.obj.toString());
//                        if (activity instanceof BaseActivity) {
//                            if (Utils.getNewPlayList(activity, msg.obj.toString())) {
//                                if (activity != null && activity instanceof IView) {
//                                    ((IView) activity).updateMainDate(JSONObject.parseObject(msg.obj.toString()));
//                                } else if (activity != null && activity instanceof BaseActivity && ((BaseActivity) activity).getiView() != null) {
//                                    ((BaseActivity) activity).getiView().updateMainDate(JSONObject.parseObject(msg.obj.toString()));
//                                }
//                            }
//                        }
//                    }
//                }
//                break;
//                case 2: {
//                    int handlerTime = Utils.getNumberForString(Utils.get(activity, Utils.KEY_TIME_PRESET, Utils.KEY_TIME_PRESET_TIME + "").toString(), Utils.KEY_TIME_PRESET_TIME);
//                    baseHandler.postDelayed(presetRunnable, handlerTime * 60 * 1000);
//                    if (Utils.IS_TEST) {
//                        msg.obj = Utils.getStringFromAssets("json.json", activity);
//                        Utils.put(activity, Utils.KEY_PRESET, msg.obj.toString());
//                        if (activity != null && activity instanceof IView) {
//                            ((IView) activity).updatePresetDate(JSONObject.parseObject(msg.obj.toString()));
//                        } else if (activity != null && activity instanceof BaseActivity && ((BaseActivity) activity).getiView() != null) {
//                            ((BaseActivity) activity).getiView().updatePresetDate(JSONObject.parseObject(msg.obj.toString()));
//                        }
//                        return;
//                    }
//                    if (msg.obj != null && !TextUtils.isEmpty(msg.obj.toString())) {
//                        try {
//                            PresetBean bean = JSON.parseObject(msg.obj.toString(), PresetBean.class);
//                            if (!"0".equals(bean.resCode)) {
//                                return;
//                            }
//                        } catch (Exception e) {
//                            return;
//                        }
//
//                        Utils.put(ActivityManager.getInstance().getTopActivity(), Utils.KEY_PRESET, msg.obj.toString());
//                        if (activity != null && activity instanceof IView) {
//                            ((IView) activity).updatePresetDate(JSONObject.parseObject(msg.obj.toString()));
//                        } else if (activity != null && activity instanceof BaseActivity && ((BaseActivity) activity).getiView() != null) {
//                            ((BaseActivity) activity).getiView().updatePresetDate(JSONObject.parseObject(msg.obj.toString()));
//                        }
//                    }
//                }
//                break;
//                default:
//                    break;
//            }
//        }
//    };
//    public static Runnable cmdRunnable = new Runnable() {
//        @Override
//        public void run() {
//            Utils.getAsyncThread().httpService(HTTPContants.CODE_CMDPOLL, JSONObject.parseObject(JSONObject.toJSONString(new CmdpollBean())), baseHandler, 0);
//        }
//    };
//    public static Runnable playlistRunnable = new Runnable() {
//        @Override
//        public void run() {
//            Utils.getAsyncThread().httpService(HTTPContants.CODE_PLAYLIST, JSONObject.parseObject(JSONObject.toJSONString(DownloadService.getPlaylistBean())), baseHandler, 1);
//        }
//    };
//    public static Runnable presetRunnable = new Runnable() {
//        @Override
//        public void run() {
//            RequestBean requestBean = new RequestBean();
//            String beanStr = Utils.get(ActivityManager.getInstance().getTopActivity(), Utils.KEY_REGISTER_BEAN, "").toString();
//            if (!TextUtils.isEmpty(beanStr)) {
//                RegisterBean bean = JSON.parseObject(beanStr, RegisterBean.class);
//                requestBean.appId = bean.appId;
//                requestBean.trCode = bean.trCode;
//                requestBean.trVersion = bean.trVersion;
//                requestBean.cityCode = bean.cityCode;
//                requestBean.brchCode = bean.brchCode;
//                requestBean.clientVersion = bean.clientVersion;
//                requestBean.terminalId = bean.terminalId;
//                requestBean.uniqueId = bean.uniqueId;
//            }
//            requestBean.timestamp = System.currentTimeMillis();
//            requestBean.flowNum = 0;
//            Utils.getAsyncThread().httpService(HTTPContants.CODE_PRESET, JSONObject.parseObject(JSONObject.toJSONString(requestBean)), baseHandler, 2);
//        }
//    };

    public void startHandler() {
//        try {
//            baseHandler.post(cmdRunnable);
//            baseHandler.post(playlistRunnable);
//            baseHandler.post(presetRunnable);
//        } catch (Exception e) {
//        }
//        netTaskManager.init();

    }

    public void stopHandler() {
//        try {
//            baseHandler.removeCallbacks(cmdRunnable);
//            baseHandler.removeCallbacks(playlistRunnable);
//            baseHandler.removeCallbacks(presetRunnable);
//        } catch (Exception e) {
//        }

//        netTaskManager.cancalTask();
    }
}
