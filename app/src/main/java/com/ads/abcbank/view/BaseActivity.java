package com.ads.abcbank.view;

import android.annotation.SuppressLint;
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
import com.ads.abcbank.bean.CmdpollBean;
import com.ads.abcbank.bean.CmdpollResultBean;
import com.ads.abcbank.bean.CmdresultBean;
import com.ads.abcbank.bean.PlaylistBean;
import com.ads.abcbank.bean.RegisterBean;
import com.ads.abcbank.bean.RequestBean;
import com.ads.abcbank.service.DownloadService;
import com.ads.abcbank.service.TimeCmdService;
import com.ads.abcbank.utils.ActivityManager;
import com.ads.abcbank.utils.FileUtil;
import com.ads.abcbank.utils.HTTPContants;
import com.ads.abcbank.utils.HandlerUtil;
import com.ads.abcbank.utils.Logger;
import com.ads.abcbank.utils.ToastUtil;
import com.ads.abcbank.utils.Utils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.Date;

public class BaseActivity extends AppCompatActivity {
    private final String CONNECTIVITY_CHANGE_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
    private NetChangeReceiver netChangeReceiver;//网络状态
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
        netChangeReceiver = new NetChangeReceiver();
        mActivity = this;
        registerDateTransReceiver();
        registerDowloadStatusReceiver();
    }

    private void registerDowloadStatusReceiver() {
        downloadStatus = new DownloadStatus();
        IntentFilter f = new IntentFilter();
        f.addAction(DownloadService.TASKS_CHANGED);
        registerReceiver(downloadStatus, new IntentFilter(f));
    }

    private void registerDateTransReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(CONNECTIVITY_CHANGE_ACTION);
        filter.setPriority(1000);
        registerReceiver(netChangeReceiver, filter);
    }

    public void startServices(String type) {
        startService(new Intent(this, TimeCmdService.class));
//        startService(new Intent(this, TimePlaylistService.class));
//        startService(new Intent(this, TimePresetService.class));
        Intent intent = new Intent();
        intent.putExtra("type", type);
        intent.setAction(DownloadService.ADD_MULTI_DOWNTASK);
        intent.setPackage(DownloadService.PACKAGE);
        startService(intent);
        {
            Logger.e("TAG", "获取轮询命令服务：" + new Date().toString());
            CmdpollBean cmdpollBean = new CmdpollBean();
            Utils.getAsyncThread().httpService(HTTPContants.CODE_CMDPOLL, JSONObject.parseObject(JSONObject.toJSONString(cmdpollBean)), baseHandler, 0);

            Logger.e("TAG", "启动获取播放列表：" + new Date().toString());
            PlaylistBean playlistBean = DownloadService.getPlaylistBean();
            Logger.e("启动获取播放列表--下载列表状态:" + JSONObject.toJSONString(DownloadService.getPlaylistBean()));
            Utils.getAsyncThread().httpService(HTTPContants.CODE_PLAYLIST, JSONObject.parseObject(JSONObject.toJSONString(playlistBean)), baseHandler, 1);

            Logger.e("TAG", "启动获取预设汇率列表服务：" + new Date().toString());
            RequestBean requestBean = new RequestBean();
            String beanStr = Utils.get(ActivityManager.getInstance().getTopActivity(), Utils.KEY_REGISTER_BEAN, "").toString();
            if (!TextUtils.isEmpty(beanStr)) {
                RegisterBean bean = JSON.parseObject(beanStr, RegisterBean.class);
                requestBean.appId = bean.appId;
                requestBean.trCode = bean.trCode;
                requestBean.trVersion = bean.trVersion;
                requestBean.cityCode = bean.cityCode;
                requestBean.brchCode = bean.brchCode;
                requestBean.clientVersion = bean.clientVersion;
                requestBean.terminalId = bean.terminalId;
                requestBean.uniqueId = bean.uniqueId;
            }
            requestBean.timestamp = System.currentTimeMillis();
            requestBean.flowNum = 0;
            Utils.getAsyncThread().httpService(HTTPContants.CODE_PRESET, JSONObject.parseObject(JSONObject.toJSONString(requestBean)), baseHandler, 2);
        }
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

    private class NetChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.equals(action, CONNECTIVITY_CHANGE_ACTION)) {
                netType = getNetworkType();
                if (netType == ConnectivityManager.TYPE_WIFI || netType == ConnectivityManager.TYPE_MOBILE) {
                    if (hasInit && !hasNet) {
                        if (reInitRunnable != null) {
                            reInitRunnable.run();
                        }
                    }
                    hasNet = true;
                } else {
                    if (!hasInit) {
                        hasNet = false;
                    }
                }
                hasInit = true;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(netChangeReceiver);
        unregisterReceiver(downloadStatus);
        Utils.changeIntent(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startDownloadService();
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
                startActivity(new Intent(this, ReInitActivity.class));
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

    @SuppressLint("HandlerLeak")
    private Handler baseHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Logger.e("getCmdPoll", "获取轮询命令返回数据====" + msg.obj);
                    if (msg.obj != null) {
                        Utils.put(BaseActivity.this, Utils.KEY_CMD_POLL, msg.obj);
                        try {
                            getCmdResult(JSON.parseObject(msg.obj.toString(), CmdpollResultBean.class));
                        } catch (Exception e) {
                            ToastUtil.showToastLong(BaseActivity.this, "获取轮询命令返回结果异常：" + msg.obj.toString());
                            Logger.e("获取轮询命令返回结果异常：" + msg.obj.toString());
                        }
                    }
                    break;
                case 1:
                    Logger.e("getPlayList", "获取播放列表返回数据====" + msg.obj);
                    if (Utils.IS_TEST) {
                        msg.obj = Utils.getStringFromAssets("playlist.json", BaseActivity.this).toString();
                        FileUtil.writeJsonToFile(msg.obj.toString());
                        if (Utils.getNewPlayList(BaseActivity.this, msg.obj.toString())) {
                            if (getiView() != null) {
                                getiView().updateMainDate(JSONObject.parseObject(msg.obj.toString()));
                            } else if (ActivityManager.getInstance().getTopActivity() instanceof IView) {
                                ((IView) ActivityManager.getInstance().getTopActivity()).updateMainDate(JSONObject.parseObject(msg.obj.toString()));
                            }
                        }
                        return;
                    }
                    if (msg.obj != null) {
                        FileUtil.writeJsonToFile(msg.obj.toString());
                        if (Utils.getNewPlayList(BaseActivity.this, msg.obj.toString())) {
                            if (getiView() != null) {
                                getiView().updateMainDate(JSONObject.parseObject(msg.obj.toString()));
                            } else if (ActivityManager.getInstance().getTopActivity() instanceof IView) {
                                ((IView) ActivityManager.getInstance().getTopActivity()).updateMainDate(JSONObject.parseObject(msg.obj.toString()));
                            }
                        }
                    }
                    break;
                case 2:
                    if (Utils.IS_TEST) {
                        msg.obj = Utils.getStringFromAssets("json.json", BaseActivity.this);
                        Utils.put(BaseActivity.this, Utils.KEY_PRESET, msg.obj.toString());
                        if (getiView() != null) {
                            getiView().updatePresetDate(JSONObject.parseObject(msg.obj.toString()));
                        } else if (ActivityManager.getInstance().getTopActivity() instanceof IView) {
                            ((IView) ActivityManager.getInstance().getTopActivity()).updatePresetDate(JSONObject.parseObject(msg.obj.toString()));
                        }
                        return;
                    }
                    if (msg.obj != null) {
                        Utils.put(BaseActivity.this, Utils.KEY_PRESET, msg.obj.toString());
                        if (getiView() != null) {
                            getiView().updatePresetDate(JSONObject.parseObject(msg.obj.toString()));
                        } else if (ActivityManager.getInstance().getTopActivity() instanceof IView) {
                            ((IView) ActivityManager.getInstance().getTopActivity()).updatePresetDate(JSONObject.parseObject(msg.obj.toString()));
                        }

                    }
                    break;
                default:
                    break;
            }
        }
    };

    private class DownloadStatus extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null) {
                switch (action) {
                    case DownloadService.TASKS_CHANGED:
                        Logger.e("有文件下载完成播放列表即将更新" + getiView());
                        if (getiView() != null) {
                            Logger.e("有文件下载完成播放列表正在更新");
                            getiView().updateMainDate(new JSONObject());
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    }
}
