package com.ads.abcbank.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.ads.abcbank.R;
import com.ads.abcbank.bean.DownloadBean;
import com.ads.abcbank.bean.PlaylistBodyBean;
import com.ads.abcbank.bean.PlaylistResultBean;
import com.ads.abcbank.bean.RegisterBean;
import com.ads.abcbank.service.DownloadService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.apache.commons.validator.routines.UrlValidator;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

/**
 * @date 2019/5/4
 */

public class Utils {
    public static final boolean IS_TEST = false;
    public static final boolean IS_CHECK_MD5 = false;
    public static final String WEBURL = "WEBURL";
    public static final String USER_INFO = "userInfo";//xml文件名称，记录主要内容
    public static ProgressDialog mProgressDialog;

    public static final String KEY_PLAY_LIST = "playList";
    public static final String KEY_PLAY_LIST_DOWNLOAD = "playListDownload";
    public static final String KEY_PLAY_LIST_DOWNLOAD_FINISH = "playListDownloadFinish";
    public static final String KEY_PLAY_LIST_ALL = "playListAll";
    public static final String KEY_PRESET = "preset";
    public static final String KEY_CONTENT_TYPE_START = "contentTypeStart";
    public static final String KEY_CONTENT_TYPE_MIDDLE = "contentTypeMiddle";
    public static final String KEY_CONTENT_TYPE_END = "contentTypeEnd";
    public static final String KEY_REGISTER_BEAN = "registerBean";
    public static final String KEY_CMD_POLL = "cmdPoll";

    public static final String KEY_TIME_CMD = "timeCmd";//记录获取cmd命令的分钟数
    public static final String KEY_TIME_PRESET = "timePreset";//记录获取汇率的分钟数
    public static final String KEY_TIME_PLAYLIST = "timePlaylist";//记录获取播放列表的分钟数

    public static final String KEY_TIME_CURRENT_CMD = "timeCurrentCmd";//记录当前获取cmd命令的分钟数
    public static final String KEY_TIME_CURRENT_PRESET = "timeCurrentPreset";//记录当前获取汇率的分钟数
    public static final String KEY_TIME_CURRENT_PLAYLIST = "timeCurrentPlaylist";//记录当前获取播放列表的分钟数

    public static final String KEY_TIME_TAB_PRESET = "timeTabPreset";//记录切换汇率tab的秒数
    public static final String KEY_TIME_TAB_IMG = "timeTabImg";//记录切换图片tab的秒数
    public static final String KEY_TIME_TAB_PDF = "timeTabPdf";//记录切换pdf文件的秒数
    public static final String KEY_TIME_FILE = "timeFile";//记录过期文件要手动删除的天数

    public static final String KEY_SPEED_DOWNLOAD = "speedDownload";//记录文件下载限制的速度

    public static final String KEY_FRAME_SET_NO = "frameSetNo";//记录模板

    public static String TIME_PLAYLIST;
    public static String TIME_PRESET;
    public static String TIME_CMD;
    private static AsyncThread asyncThread;

    public static void setRegisterBean(Context context, RegisterBean bean) {
        put(context, KEY_REGISTER_BEAN, JSONObject.toJSONString(bean));
    }

    public static RegisterBean getRegisterBean(Context context) {
        return JSON.parseObject(get(context, KEY_REGISTER_BEAN, "").toString(), RegisterBean.class);
    }

    public static final void showProgressDialog(Context context) {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
        mProgressDialog = ProgressDialog.show(context, context.getString(R.string
                .s_title), context.getString(R.string.s_loading), true, true);
    }

    public static final void hideProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    public static String getStringFromAssets(String fileName, Context context) {
        StringBuilder stringBuilder = new StringBuilder();

        try {
            AssetManager assetManager = context.getAssets();
            BufferedReader bf = new BufferedReader(new InputStreamReader(assetManager.open(fileName)));

            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException var6) {
            var6.printStackTrace();
        }

        return stringBuilder.toString();
    }

    public static final void changeIntent(Context context) {
        ((Activity) context).overridePendingTransition(R.anim.new_view_in, R.anim.old_view_out);
    }

    /**
     * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
     */
    public static void put(Context context, String key, Object object) {

        try {
            SharedPreferences sp = context.getSharedPreferences(USER_INFO, Context
                    .MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();

            if (object instanceof String) {
                editor.putString(key, (String) object);
            } else if (object instanceof Integer) {
                editor.putInt(key, (Integer) object);
            } else if (object instanceof Boolean) {
                editor.putBoolean(key, (Boolean) object);
            } else if (object instanceof Float) {
                editor.putFloat(key, (Float) object);
            } else if (object instanceof Long) {
                editor.putLong(key, (Long) object);
            } else {
                editor.putString(key, object.toString());
            }
            SharedPreferencesCompat.apply(editor);
        } catch (Exception e) {
            Logger.e(e.toString());
        }
    }

    /**
     * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
     */
    public static Object get(Context context, String key, Object defaultObject) {
        try {
            SharedPreferences sp = context.getSharedPreferences(USER_INFO, Context
                    .MODE_PRIVATE);
            if (sp == null) {
                return defaultObject;
            }
            if (defaultObject instanceof String) {
                return sp.getString(key, (String) defaultObject);
            } else if (defaultObject instanceof Integer) {
                return sp.getInt(key, (Integer) defaultObject);
            } else if (defaultObject instanceof Boolean) {
                return sp.getBoolean(key, (Boolean) defaultObject);
            } else if (defaultObject instanceof Float) {
                return sp.getFloat(key, (Float) defaultObject);
            } else if (defaultObject instanceof Long) {
                return sp.getLong(key, (Long) defaultObject);
            }
        } catch (Exception e) {
            Logger.e(e.toString());
        }

        return defaultObject;
    }

    /**
     * 移除某个key值已经对应的值
     */
    public static void remove(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(USER_INFO, Context
                .MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        SharedPreferencesCompat.apply(editor);
    }

    /**
     * 清除所有数据
     */
    public static void clear(Context context) {
        SharedPreferences sp = context.getSharedPreferences(USER_INFO, Context
                .MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        SharedPreferencesCompat.apply(editor);
    }

    /**
     * 创建一个解决SharedPreferencesCompat.apply方法的一个兼容类
     */
    private static class SharedPreferencesCompat {
        private static final Method S_APPLY_METHOD = findApplyMethod();

        /**
         * 反射查找apply的方法
         */
        private static Method findApplyMethod() {
            try {
                Class clz = SharedPreferences.Editor.class;
                return clz.getMethod("apply");
            } catch (NoSuchMethodException e) {
                Logger.e(e.toString());
            }
            return null;
        }

        /**
         * 如果找到则使用apply执行，否则使用commit
         */
        public static void apply(SharedPreferences.Editor editor) {
            try {
                if (S_APPLY_METHOD != null) {
                    S_APPLY_METHOD.invoke(editor);
                    return;
                }
            } catch (Exception e) {
                Logger.e(e.toString());
            }
            editor.commit();
        }
    }

    // 将px值转换为sp值
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 关闭软键盘
     */
    public static final void closeKeyboard(Context context, View view) {
        try {
            if (view != null) {
                view.setFocusable(true);
                view.setFocusableInTouchMode(true);
                view.requestFocus();
            }
            InputMethodManager imm = (InputMethodManager) context
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                View view1 = ((Activity) context).getCurrentFocus();
                if (view1 != null && view1.getWindowToken() != null) {
                    try {
                        imm.hideSoftInputFromWindow(view1.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    } catch (Exception e) {
                        Logger.e(e.toString());
                    }
                }
            }
        } catch (Exception e) {
            Logger.e(e.toString());
        }
    }

    public static void setContentTypeStart(Context content, String contentType) {
        put(content, KEY_CONTENT_TYPE_START, contentType);
    }

    public static String getContentTypeStart(Context context) {
        return get(context, KEY_CONTENT_TYPE_START, "").toString();
    }

    public static void setContentTypeMiddle(Context content, String contentType) {
        put(content, KEY_CONTENT_TYPE_MIDDLE, contentType);
    }

    public static String getContentTypeMiddle(Context context) {
        return get(context, KEY_CONTENT_TYPE_MIDDLE, "").toString();
    }

    public static void setContentTypeEnd(Context content, String contentType) {
        put(content, KEY_CONTENT_TYPE_END, contentType);
    }

    public static String getContentTypeEnd(Context context) {
        return get(context, KEY_CONTENT_TYPE_END, "").toString();
    }

    public static void loadImage(ImageView imageView, String url) {
        if (imageView != null) {
            int placeholderId = R.mipmap.bg_land;
            if ("V".equals(getContentTypeMiddle(imageView.getContext()))
                    || "H,L".equals(getContentTypeStart(imageView.getContext()))
                    || "N".equals(getContentTypeStart(imageView.getContext()))) {
                placeholderId = R.mipmap.bg_port;
            }
            WeakReference<ImageView> reference = new WeakReference(imageView);
            ImageView target = reference.get();
            if (target != null) {
                target.setImageDrawable(null);
                if (!TextUtils.isEmpty(url)) {
                    Glide.with(imageView.getContext()).load(url).placeholder(placeholderId).error(placeholderId).diskCacheStrategy(DiskCacheStrategy.RESULT).dontAnimate().into(target);
                } else {
                    imageView.setImageResource(placeholderId);
                }
            }
        }
    }

    public static void loadImage(ImageView imageView, Uri url) {
        if (imageView != null) {
            int placeholderId = R.mipmap.bg_land;
            if ("V".equals(getContentTypeMiddle(imageView.getContext()))
                    || "H,L".equals(getContentTypeStart(imageView.getContext()))
                    || "N".equals(getContentTypeStart(imageView.getContext()))) {
                placeholderId = R.mipmap.bg_port;
            }
            WeakReference<ImageView> reference = new WeakReference(imageView);
            ImageView target = reference.get();
            if (target != null) {
                target.setImageDrawable(null);
                if (url != null) {
                    Glide.with(imageView.getContext()).load(url).placeholder(placeholderId).error(placeholderId).diskCacheStrategy(DiskCacheStrategy.RESULT).dontAnimate().into(target);
                } else {
                    imageView.setImageResource(placeholderId);
                }
            }
        }
    }

    //版本号
    public static String getVersionName(Context context) {
        //获取包管理器
        PackageManager pm = context.getPackageManager();
        //获取包信息
        try {
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
            //返回版本号
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Logger.e(e.toString());
        }
        return "";
    }


    private static String getMacDefault(Context context) {
        String mac = "";
        if (context == null) {
            return mac;
        }
        WifiManager wifi = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = null;
        if (wifi != null) {
            try {
                info = wifi.getConnectionInfo();
            } catch (Exception e) {
                Logger.e(e.toString());
            }
        }

        if (info == null) {
            return null;
        }
        mac = info.getMacAddress();
        if (!TextUtils.isEmpty(mac)) {
            mac = mac.toUpperCase(Locale.ENGLISH);
        }
        return mac;
    }

    /**
     * Android 6.0-Android 7.0 获取mac地址
     */
    private static String getMacAddress() {
        String macSerial = null;
        String str = "";

        try {
            Process pp = Runtime.getRuntime().exec("cat/sys/class/net/wlan0/address");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);

            while (null != str) {
                str = input.readLine();
                if (str != null) {
                    macSerial = str.trim();//去空格
                    break;
                }
            }
        } catch (IOException ex) {
            // 赋予默认值
            ex.printStackTrace();
        }

        return macSerial;
    }

    /**
     * Android 7.0之后获取Mac地址
     * 遍历循环所有的网络接口，找到接口是 wlan0
     * 必须的权限 <uses-permission android:name="android.permission.INTERNET"></uses-permission>
     *
     * @return
     */
    private static String getMacFromHardware() {
        try {
            Enumeration<NetworkInterface> all = NetworkInterface.getNetworkInterfaces();
            ArrayList<NetworkInterface> netAlls = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : netAlls) {
                if (!"wlan0".equals(nif.getName().toLowerCase())) {
                    continue;
                }
                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }
                StringBuilder res1 = new StringBuilder();
                for (Byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }
                if (!TextUtils.isEmpty(res1)) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception e) {
            Logger.e(e.toString());
        }

        return "";
    }

    /**
     * 获取mac地址（适配所有Android版本）
     *
     * @return
     */
    public static String getMac(Context context) {
        String mac = "";
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mac = getMacDefault(context);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            mac = getMacAddress();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mac = getMacFromHardware();
        }
        return mac;
    }

    @SuppressLint("MissingPermission")
    public static String getUUID(Context context) {
        String token = null;
        try {
            // 获取devicetoken
            TelephonyManager tm = (TelephonyManager) context
                    .getSystemService(Activity.TELEPHONY_SERVICE);
            if (tm != null) {
                token = tm.getDeviceId();
                // 获取IMSI
                String imsi = tm.getSubscriberId();
                // 获取SIM卡序列号
                String simNumber = tm.getSimSerialNumber();
                // 获取MAC地址
                WifiManager wifi = (WifiManager) context.getApplicationContext()
                        .getSystemService(Context.WIFI_SERVICE);
                if (wifi != null) {
                    WifiInfo info = wifi.getConnectionInfo();
                    String macAddress = info.getMacAddress();
                    if (null != token) {
                        if (null != imsi && !"".equals(imsi.trim())) {
                            // 若IMSI不为空，则使用deviceID拼接IMSI号作为设备唯一标识上传，不再拼接其他标识
                            token = token.concat(imsi.trim());
                        } else if (!TextUtils.isEmpty(simNumber)) {
                            // 若IMSI为空，但sim卡序列号不为空，则使用deviceID拼接sim卡序列号作为设备唯一标识上传，不再拼接其他标识
                            token = token.concat(simNumber.trim());
                        } else if (!TextUtils.isEmpty(macAddress)) {
                            // 若IMSI和sim卡序列号都为空，但MAC address不为空，则使用deviceID拼接MAC
                            // address作为设备唯一标识上传，不再拼接其他标识
                            token = token.concat(macAddress.trim());
                        }
                        token = token.replace(' ', '-');
                        token = token.replace(':', '-');
                    } else {
                        token = Settings.Secure.getString(context.getContentResolver(),
                                Settings.Secure.ANDROID_ID);
                    }
                } else {
                    token = Settings.Secure.getString(context.getContentResolver(),
                            Settings.Secure.ANDROID_ID);
                }
            }
        } catch (Exception e) {
            Logger.e(e.toString());
        }
        return token;
    }

    public static String getTxtString(Context context, String fileName) throws IOException {// 转码
        File file = new File(DownloadService.downloadFilePath, fileName);
        if (!file.exists()) {
            InputStream asset = context.getAssets().open(fileName);
            FileOutputStream output = new FileOutputStream(file);
            final byte[] buffer = new byte[1024];
            int size;
            while ((size = asset.read(buffer)) != -1) {
                output.write(buffer, 0, size);
            }
            asset.close();
            output.close();
        }
        BufferedReader reader;
        String text = "";
        StringBuilder builder = new StringBuilder();
        String encoding = getFilecharset(file);
        InputStreamReader read = new InputStreamReader(
                new FileInputStream(file), encoding);//考虑到编码格式
        BufferedReader bufferedReader = new BufferedReader(read);
        String lineTxt;
        while ((lineTxt = bufferedReader.readLine()) != null) {
            boolean previousWasASpace = false;
            for (char c : (lineTxt + "\n").toCharArray()) {
                if (c == ' ') {
                    if (previousWasASpace) {
                        builder.append(" ");
                        previousWasASpace = false;
                        continue;
                    }
                    previousWasASpace = true;
                } else {
                    previousWasASpace = false;
                }
                builder.append(c);
            }
        }
        read.close();
        return builder.toString();
    }

    //判断编码格式方法
    private static String getFilecharset(File sourceFile) {
        String charset = "GBK";
        byte[] first3Bytes = new byte[3];
        try {
            boolean checked = false;
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(sourceFile));
            bis.mark(0);
            int read = bis.read(first3Bytes, 0, 3);
            if (read == -1) {
                return charset; //文件编码为 ANSI
            } else if (first3Bytes[0] == (byte) 0xFF
                    && first3Bytes[1] == (byte) 0xFE) {
                charset = "UTF-16LE"; //文件编码为 Unicode
                checked = true;
            } else if (first3Bytes[0] == (byte) 0xFE
                    && first3Bytes[1] == (byte) 0xFF) {
                charset = "UTF-16BE"; //文件编码为 Unicode big endian
                checked = true;
            } else if (first3Bytes[0] == (byte) 0xEF
                    && first3Bytes[1] == (byte) 0xBB
                    && first3Bytes[2] == (byte) 0xBF) {
                charset = "UTF-8"; //文件编码为 UTF-8
                checked = true;
            }
            bis.reset();
            if (!checked) {
                int loc = 0;
                while ((read = bis.read()) != -1) {
                    loc++;
                    if (read >= 0xF0) {
                        break;
                    }
                    // 单独出现BF以下的，也算是GBK
                    if (0x80 <= read && read <= 0xBF) {
                        break;
                    }
                    if (0xC0 <= read && read <= 0xDF) {
                        read = bis.read();
                        // 双字节 (0xC0 - 0xDF)
                        if (0x80 <= read && read <= 0xBF) {
                            // (0x80
                            // - 0xBF),也可能在GB编码内
                            continue;
                        } else {
                            break;
                        }
                    } else if (0xE0 <= read && read <= 0xEF) {
                        // 也有可能出错，但是几率较小
                        read = bis.read();
                        if (0x80 <= read && read <= 0xBF) {
                            read = bis.read();
                            if (0x80 <= read && read <= 0xBF) {
                                charset = "UTF-8";
                                break;
                            } else {
                                break;
                            }
                        } else {
                            break;
                        }
                    }
                }
            }
            bis.close();
        } catch (Exception e) {
            Logger.e(e.toString());
        }
        return charset;
    }

    //是否在允许下载的时间段内
    public static boolean isInDownloadTime(PlaylistBodyBean bean) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        String downloadTimeslice = bean.downloadTimeslice;
        if (TextUtils.isEmpty(downloadTimeslice)) {
            return true;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        String[] strs = downloadTimeslice.split("-");
        int week = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (week < 0) {
            week = 0;
        }
        if (week == 0) {
            week = 7;
        }
        if (("," + strs[0] + ",").indexOf("," + week + ",") >= 0) {
            //判断当前时间是否在工作时间段内
            Date startDt;
            Date endDt;
            Date nowDt = new Date();
            try {
                startDt = timeFormat.parse(strs[1]);
                Calendar ca = Calendar.getInstance();
                ca.setTime(startDt);
                ca.add(Calendar.MINUTE, Integer.parseInt(strs[2]));
                endDt = ca.getTime();

                if (timeFormat.format(nowDt).compareTo(timeFormat.format(startDt)) >= 0
                        && ((!"00:00".equals(timeFormat.format(endDt)) && timeFormat.format(nowDt).compareTo(timeFormat.format(endDt)) <= 0)
                        || "00:00".equals(timeFormat.format(endDt)))) {
                    return true;
                }
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    //是否在播放时间段内
    public static boolean isInPlayTime(PlaylistBodyBean bean) {
        try {
            if (bean == null) {
                return false;
            }
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd HH:mm");
            String currentDate = simpleDateFormat.format(new Date());
            if (!TextUtils.isEmpty(bean.playDate) && !TextUtils.isEmpty(bean.stopDate)
                    && currentDate.compareTo(bean.playDate) >= 0 && currentDate.compareTo(bean.stopDate) < 0) {
                return true;
            }
        } catch (Exception e) {
            Logger.e(e.toString());
        }
        return false;
    }

    //过期需要删除的文件
    public static boolean isNeedDel(Context context, PlaylistBodyBean bean) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd HH:mm");
        int timeFile = Integer.parseInt(get(context, KEY_TIME_FILE, "30").toString());
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DATE, -1 * timeFile);
        Date m = c.getTime();
        String mon = simpleDateFormat.format(m);
        if (mon.compareTo(bean.stopDate) <= 0) {
            return true;
        }
        return false;
    }

    public static long getTimePlaylist() {
        try {
            return Long.parseLong(TIME_PLAYLIST);
        } catch (Exception e) {
            return 20 * 60 * 1000;
        }
    }

    public static void setTimePlaylist(String timePlaylist) {
        TIME_PLAYLIST = timePlaylist;
    }

    public static long getTimePreset() {
        try {
            return Long.parseLong(TIME_PRESET);
        } catch (Exception e) {
            return 30 * 60 * 1000;
        }
    }

    public static void setTimePreset(String timePreset) {
        TIME_PRESET = timePreset;
    }

    public static long getTimeCmd() {
        try {
            return Long.parseLong(TIME_CMD);
        } catch (Exception e) {
            return 5 * 60 * 1000;
        }
    }

    public static void setTimeCmd(String timeCmd) {
        TIME_CMD = timeCmd;
    }

    public static AsyncThread getAsyncThread() {
        if (asyncThread == null) {
            synchronized (AsyncThread.class) {
                if (asyncThread == null) {
                    asyncThread = new AsyncThread();
                }
            }
        }
        return asyncThread;
    }

    //删除播放文件列表中指定文件
    public static void delOneAllBean(Context context, PlaylistBodyBean bean) {
        String allBeanStr = get(context, KEY_PLAY_LIST_ALL, "").toString();
        if (TextUtils.isEmpty(allBeanStr)) {
            return;
        }
        PlaylistResultBean allBean = JSON.parseObject(allBeanStr, PlaylistResultBean.class);
        List<PlaylistBodyBean> allList = allBean.data.items;
        for (int i = 0; i < allList.size(); i++) {
            if (containSame(allList, bean, context)) {
                allList.remove(i);
                allBeanStr = JSONObject.toJSONString(allBean);
                put(context, KEY_PLAY_LIST_ALL, allBeanStr);
                return;
            }
        }
    }

    //删除下载文件列表中指定文件
    public static void delOneDownloadBean(Context context, PlaylistBodyBean bean) {
        String allBeanStr = get(context, KEY_PLAY_LIST_DOWNLOAD, "").toString();
        if (TextUtils.isEmpty(allBeanStr)) {
            return;
        }
        PlaylistResultBean allBean = JSON.parseObject(allBeanStr, PlaylistResultBean.class);
        List<PlaylistBodyBean> allList = allBean.data.items;
        for (int i = 0; i < allList.size(); i++) {
            if (containSame(allList, bean, context)) {
                allList.remove(i);
                allBeanStr = JSONObject.toJSONString(allBean);
                put(context, KEY_PLAY_LIST_DOWNLOAD, allBeanStr);
                return;
            }
        }
    }

    //将新添加的播放文件添加到播放列表中
    public static void megerAllBean(Context context, String jsonStr) {
        if (TextUtils.isEmpty(jsonStr)) {
            return;
        }
        String allBeanStr = get(context, KEY_PLAY_LIST_ALL, "").toString();
        if (TextUtils.isEmpty(allBeanStr)) {
            allBeanStr = jsonStr;
        } else {
            PlaylistResultBean allBean = JSON.parseObject(allBeanStr, PlaylistResultBean.class);
            PlaylistResultBean addBean = JSON.parseObject(jsonStr, PlaylistResultBean.class);
            List<PlaylistBodyBean> allList = allBean.data.items;
            List<PlaylistBodyBean> addList = addBean.data.items;
            for (int i = 0; i < addList.size(); i++) {
                if (!containSame(allList, addList.get(i), context)) {
                    allList.add(addList.get(i));
                }
            }
            //删除过期文件
            List<PlaylistBodyBean> delList = new ArrayList<>();
            for (int i = 0; i < allBean.data.items.size(); i++) {
                PlaylistBodyBean bodyBean = allBean.data.items.get(i);
                if (isNeedDel(context, bodyBean)) {
                    delList.add(bodyBean);
                }
            }
            allBean.data.items.removeAll(delList);
            allBeanStr = JSONObject.toJSONString(allBean);
        }
        put(context, KEY_PLAY_LIST_ALL, allBeanStr);
    }

    private static boolean containSame(List<PlaylistBodyBean> allList, PlaylistBodyBean bean, Context context) {
        for (int i = 0; i < allList.size(); i++) {
            PlaylistBodyBean bodyBean = allList.get(i);
            if (bodyBean.id.equals(bean.id) && bodyBean.name.equals(bean.name)) {
                if (bodyBean.lastModified.equals(bean.lastModified)) {
                    return true;
                } else {
                    allList.remove(bodyBean);
                    removeDownloadTask(context,bodyBean.id);
                    allList.add(i, bean);
                    return true;
                }
            }
        }
        return false;
    }

    //将新添加的下载文件添加到下载列表中
    public static void megerDownloadBean(Context context, String jsonStr) {
        if (TextUtils.isEmpty(jsonStr)) {
            return;
        }
        String allBeanStr = get(context, KEY_PLAY_LIST_DOWNLOAD, "").toString();
        if (TextUtils.isEmpty(allBeanStr)) {
            allBeanStr = jsonStr;
        } else {
            PlaylistResultBean allBean = JSON.parseObject(allBeanStr, PlaylistResultBean.class);
            PlaylistResultBean addBean = JSON.parseObject(jsonStr, PlaylistResultBean.class);
            List<PlaylistBodyBean> allList = allBean.data.items;
            List<PlaylistBodyBean> addList = addBean.data.items;
            for (int i = 0; i < addList.size(); i++) {
                if (!containSame(allList, addList.get(i), context)) {
                    allList.add(addList.get(i));
                }
            }
            allBeanStr = JSONObject.toJSONString(allBean);
        }
        put(context, KEY_PLAY_LIST_DOWNLOAD, allBeanStr);
    }

    public static String viewSaveToImage(View view, String child) {
        /**
         * View组件显示的内容可以通过cache机制保存为bitmap
         * 我们要获取它的cache先要通过setDrawingCacheEnable方法把cache开启，
         * 然后再调用getDrawingCache方法就可 以获得view的cache图片了
         * 。buildDrawingCache方法可以不用调用，因为调用getDrawingCache方法时，
         * 若果 cache没有建立，系统会自动调用buildDrawingCache方法生成cache。
         * 若果要更新cache, 必须要调用destoryDrawingCache方法把旧的cache销毁，才能建立新的。
         */

        // 把一个View转换成图片
        Bitmap cachebmp = loadBitmapFromView(view);

        cachebmp = compressBitmap(cachebmp, 50);
        return bitmap2String(cachebmp);
    }

    private static Bitmap loadBitmapFromView(View v) {
        int w = v.getMeasuredWidth();
        int h = v.getMeasuredHeight();
        Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);

        v.layout(0, v.getTop(), w, v.getTop() + h);
        v.draw(c);

        return bmp;
    }

    /**
     * @param activity
     * @return
     * @brief 截屏
     */
    public static String screenShot(Activity activity) {
        // 获取屏幕
        View dView = activity.getWindow().getDecorView();
        dView.setDrawingCacheEnabled(true);
        dView.buildDrawingCache();
        Bitmap bmp = dView.getDrawingCache();
        if (bmp != null) {
            try {
                bmp = Utils.compressBitmap(bmp, 50);
                // 向后台传递识别数据
                return Utils.bitmap2String(bmp);
            } catch (Exception e) {
                Logger.e(e.toString());
            }
        }
        return "";
    }

    // 压缩图片到指定大小以下
    public static Bitmap compressBitmap(Bitmap bitmap, int size) {
        Bitmap newBitmap = null;
        if (bitmap != null) {
            InputStream is = null;
            try {
                BitmapFactory.Options opt = new BitmapFactory.Options();
                opt.inDither = false;
                opt.inPreferredConfig = Bitmap.Config.RGB_565;
                opt.inSampleSize = 1;
                float bitmapSize = Utils.getSizeOfBitmap(bitmap);
                // 压缩图片到指定大小
//                while (bitmapSize > (size + size / 3)) {
                while (bitmapSize > size) {
                    opt.inSampleSize = opt.inSampleSize + 1;
                    is = bitmap2Is(bitmap);
                    if (is != null) {
                        bitmap = BitmapFactory.decodeStream(is, null, opt);
                        bitmapSize = Utils.getSizeOfBitmap(bitmap);
                    }
                }
            } catch (Exception e) {
                Logger.e(e.toString());
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (Exception ignored) {
                        Logger.e(ignored.toString());
                    }
                }
            }
            newBitmap = bitmap;
        }
        return newBitmap;
    }

    // 获取图片大小
    public static float getSizeOfBitmap(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        long length = baos.toByteArray().length / 1024;
        return length;
    }

    private static InputStream bitmap2Is(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        InputStream stream = null;
        try {
            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            stream = new ByteArrayInputStream(baos.toByteArray());
        } catch (Exception e) {
            Logger.e(e.toString());
        }
        return stream;
    }

    // 图片转换成base64字符串
    public static String bitmap2String(Bitmap bitmap) {
        String string = null;
        try {
            ByteArrayOutputStream bStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bStream);
            byte[] bytes = bStream.toByteArray();
            string = Base64.encodeToString(bytes, Base64.DEFAULT);
            bStream.close();
        } catch (Exception e) {
            Logger.e(e.toString());
        }
        if (null == string) {
            string = "";
        }
        return string;
    }

    //判断播放文件类型是否为设备可播放文件
    public static boolean isCanPlay(Context context, PlaylistBodyBean bodyBean) {
        String start = Utils.getContentTypeStart(context);
        String middle = Utils.getContentTypeMiddle(context);
        String end = Utils.getContentTypeEnd(context);
        String beanStart = bodyBean.contentType.substring(0, 1);
        String beanMiddle = bodyBean.contentType.substring(1, 2);
        String beanEnd = bodyBean.contentType.substring(2, 3);
        if (start.contains(beanStart) && middle.equals(beanMiddle) && ((!"*".equals(end) && end.equals(beanEnd)) || "*".equals(end))) {
            return true;
        }
        return false;
    }

    //得到播放列表后的处理，返回是否需要更新播放列表
    public static boolean getNewPlayList(Context context, String jsonString) {
        if (TextUtils.isEmpty(jsonString)) {
            return false;
        }
        PlaylistResultBean bean = JSON.parseObject(jsonString, PlaylistResultBean.class);
        List<PlaylistBodyBean> bodyBeans = bean.data.items;
        if (JSONObject.toJSONString(bodyBeans).equals(get(context, KEY_PLAY_LIST, ""))) {
            return false;
        } else {
            List<PlaylistBodyBean> playLists = new ArrayList<>();
            //将本设备不支持播放的内容过滤掉
            for (int i = 0; i < bodyBeans.size(); i++) {
                if (isCanPlay(context, bodyBeans.get(i))) {
                    playLists.add(bodyBeans.get(i));
                }
            }
            bean.data.items = playLists;
            megerAllBean(context, JSONObject.toJSONString(bean));
            //TODO 此处添加下载列表的相关处理并继续执行下载任务
            megerDownloadBean(context, JSONObject.toJSONString(bean));
            startDownload(context);
            put(context, KEY_PLAY_LIST, JSONObject.toJSONString(playLists));
            return true;
        }

    }

    private static void startDownload(Context context) {
        Intent intent = new Intent();
        intent.putExtra("type", "");
        intent.setAction(DownloadService.ADD_MULTI_DOWNTASK);
        intent.setPackage(DownloadService.PACKAGE);
        context.startService(intent);
    }

    public static void startUpdateDownloadTask(Context context, String fileName, String downloadLink) {
        Intent intent = new Intent();
        intent.putExtra("name", fileName);
        intent.putExtra("url", downloadLink);
        intent.setAction(DownloadService.ADD_UPDATE_DOWNTASK);
        intent.setPackage(DownloadService.PACKAGE);
        context.startService(intent);
    }

    public static void removeDownloadTask(Context context, String downloadId) {
        Intent intent = new Intent();
        intent.putExtra("downloadid", downloadId);
        intent.setAction(DownloadService.REMOVE_DOWNTASK);
        intent.setPackage(DownloadService.PACKAGE);
        context.startService(intent);
    }

    @SuppressLint("MissingPermission")
    public static String getIPAddress(Context context) {
        if (context != null && context.getSystemService(Context.CONNECTIVITY_SERVICE) != null) {
            try {
                NetworkInfo info = null;
                ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                if (connectivityManager != null) {
                    try {
                        info = connectivityManager.getActiveNetworkInfo();
                    } catch (Exception e) {
                        Logger.e(e.toString());
                    }
                }
                if (info != null && info.isConnected()) {
                    if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                        //当前使用2G/3G/4G网络
                        try {
                            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                                NetworkInterface intf = en.nextElement();
                                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                                    InetAddress inetAddress = enumIpAddr.nextElement();
                                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                        return inetAddress.getHostAddress();
                                    }
                                }
                            }
                        } catch (SocketException e) {
                            Logger.e(e.toString());
                        }
                    } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                        //当前使用无线网络
                        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                        if (wifiManager != null) {
                            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                            if (wifiInfo != null) {
                                //得到IPV4地址
                                String ipAddress = changeToStringIP(wifiInfo.getIpAddress());
                                return ipAddress;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                Logger.e(e.toString());
            }
        }
        return "";
    }

    private static String changeToStringIP(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }

    /**
     * 判断url是否合法
     *
     * @param url url
     * @return url是否合法
     */
    private static boolean isUrlValid(String url) {
        String[] schemas = {"http", "https"};
        UrlValidator urlValidator = new UrlValidator(schemas);
        return urlValidator.isValid(url);
    }

    /**
     * 判断url是否合法
     *
     * @param url
     * @return
     */
    public static boolean existHttpPath(String url) {
        //如果输入的url包含协议地址
        if (url.length() >= 4 && url.substring(0, 4).equals("http")) {
            if (isUrlValid(url)) {
                return true;
            } else {
                return false;
            }
        }
        //如果输入的url不包含协议地址
        else {
            String url1 = "http://" + url;
            String url2 = "https://" + url;
            if (isUrlValid(url1)) {
                return true;
            } else if (isUrlValid(url2)) {
                return true;
            } else {
                return false;
            }
        }
    }

    public static void fileDownload(Context context, DownloadBean downloadBean) {
        String json = get(context, KEY_PLAY_LIST_DOWNLOAD_FINISH, "").toString();
        if (TextUtils.isEmpty(json)) {
            JSONArray jsonArray = new JSONArray();
            JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(downloadBean));
            jsonArray.add(jsonObject);
            put(context, KEY_PLAY_LIST_DOWNLOAD_FINISH, JSONArray.toJSONString(jsonArray));
        } else {
            JSONArray jsonArray = JSON.parseArray(json);
            JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(downloadBean));
            jsonArray.add(jsonObject);
            put(context, KEY_PLAY_LIST_DOWNLOAD_FINISH, JSONArray.toJSONString(jsonArray));
        }
    }

    public static void deleteDownloadFiles() {
        FileUtil.deleteFile(DownloadService.downloadPath);
        FileUtil.deleteFile(DownloadService.downloadFilePath);
        FileUtil.deleteFile(DownloadService.downloadImagePath);
        FileUtil.deleteFile(DownloadService.downloadVideoPath);
        FileUtil.deleteFile(DownloadService.downloadApkPath);
    }

    public static int getFileExistType(String fileName) {
        if (new File(DownloadService.downloadPath + fileName).exists()) {
            return 1;
        } else if (new File(DownloadService.downloadFilePath + fileName).exists()) {
            return 2;
        } else if (new File(DownloadService.downloadImagePath + fileName).exists()) {
            return 3;
        } else if (new File(DownloadService.downloadVideoPath + fileName).exists()) {
            return 4;
        } else if (new File(DownloadService.downloadApkPath + fileName).exists()) {
            return 5;
        }
        return 0;
    }

    public static boolean checkMd5(Context context, PlaylistBodyBean bodyBean) {
        String fileName = bodyBean.name;
        File file = null;
        switch (getFileExistType(fileName)) {
            case 1:
                file = new File(DownloadService.downloadPath + fileName);
                break;
            case 2:
                file = new File(DownloadService.downloadFilePath + fileName);
                break;
            case 3:
                file = new File(DownloadService.downloadImagePath + fileName);
                break;
            case 4:
                file = new File(DownloadService.downloadVideoPath + fileName);
                break;
            case 5:
                file = new File(DownloadService.downloadApkPath + fileName);
                break;
        }
        if (file == null || !file.exists()) {
            return false;
        } else {
            if (IS_CHECK_MD5) {
                String fileMd5 = FileUtil.fileToMD5(file.getAbsolutePath());
                if (!TextUtils.isEmpty(fileMd5) && fileMd5.equalsIgnoreCase(bodyBean.md5)) {
                    return true;
                } else {
                    FileUtil.deleteFile(file);
                    deleteFinishItem(context, fileName);
                    return false;
                }
            } else {
                return true;
            }
        }
    }

    public static void deleteFinishItem(Context context, String name) {
        String json = get(context, KEY_PLAY_LIST_DOWNLOAD_FINISH, "").toString();
        if (!TextUtils.isEmpty(json)) {
            List<DownloadBean> downloadBean = JSON.parseArray(json, DownloadBean.class);
            for (DownloadBean bean : downloadBean) {
                if (bean.name.equals(name)) {
                    downloadBean.remove(bean);
                    break;
                }
            }
            put(context, KEY_PLAY_LIST_DOWNLOAD_FINISH, JSONArray.toJSONString(downloadBean));
        }
    }

    public static int getNumberForString(String string, int defaultValue) {
        int timeCmdInt;
        try {
            timeCmdInt = Integer.parseInt(string);
        } catch (Exception e) {
            timeCmdInt = defaultValue;
        }
        return timeCmdInt;
    }

}
