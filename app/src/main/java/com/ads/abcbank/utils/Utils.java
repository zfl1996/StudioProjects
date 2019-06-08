package com.ads.abcbank.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.ads.abcbank.R;
import com.ads.abcbank.bean.PlaylistBodyBean;
import com.ads.abcbank.bean.PlaylistResultBean;
import com.ads.abcbank.bean.RegisterBean;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.net.NetworkInterface;
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
    public static final String WEBURL = "WEBURL";
    public static final String USER_INFO = "userInfo";//xml文件名称，记录主要内容
    public static ProgressDialog mProgressDialog;

    public static final String KEY_PLAY_LIST = "playList";
    public static final String KEY_PLAY_LIST_DOWNLOAD = "playListDownload";
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
    public static final String KEY_TIME_FILE = "timeFile";//记录切换pdf文件的秒数

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
        if (mProgressDialog != null)
            mProgressDialog.dismiss();
        mProgressDialog = ProgressDialog.show(context, context.getString(R.string
                .s_title), context.getString(R.string.s_loading), true, true);
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
    }

    /**
     * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
     */
    public static Object get(Context context, String key, Object defaultObject) {
        SharedPreferences sp = context.getSharedPreferences(USER_INFO, Context
                .MODE_PRIVATE);

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
        private static final Method sApplyMethod = findApplyMethod();

        /**
         * 反射查找apply的方法
         */
        private static Method findApplyMethod() {
            try {
                Class clz = SharedPreferences.Editor.class;
                return clz.getMethod("apply");
            } catch (NoSuchMethodException e) {
            }
            return null;
        }

        /**
         * 如果找到则使用apply执行，否则使用commit
         */
        public static void apply(SharedPreferences.Editor editor) {
            try {
                if (sApplyMethod != null) {
                    sApplyMethod.invoke(editor);
                    return;
                }
            } catch (Exception e) {
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
            imm.hideSoftInputFromWindow(((Activity) context).getCurrentFocus()
                    .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
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
        int[] vIds = {R.mipmap.v_grzyhb, R.mipmap.v_sxdhb, R.mipmap.v_wkqk, R.mipmap.v_zysys};
        int[] hIds = {R.mipmap.h_jjdt, R.mipmap.h_zyxykfq, R.mipmap.h_zsyhxc};
        int index = (int) (Math.random() * hIds.length);
        int index2 = (int) (Math.random() * vIds.length);
        int random = hIds[index];
        int random2 = vIds[index2];
        if (imageView != null) {
            int placeholderId = random;
            if (getContentTypeMiddle(imageView.getContext()).equals("V")
                    || getContentTypeStart(imageView.getContext()).equals("H,L")
                    || getContentTypeStart(imageView.getContext()).equals("N")
                    ) {
                placeholderId = random2;
            }
            WeakReference<ImageView> reference = new WeakReference(imageView);
            ImageView target = (ImageView) reference.get();
            if (target != null) {
                target.setImageDrawable((Drawable) null);
                if (!TextUtils.isEmpty(url)) {
                    Glide.with(imageView.getContext()).load(url).placeholder(placeholderId).diskCacheStrategy(DiskCacheStrategy.RESULT).dontAnimate().into(target);
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
            e.printStackTrace();
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
        try {
            info = wifi.getConnectionInfo();
        } catch (Exception e) {
            e.printStackTrace();
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
                if (!nif.getName().toLowerCase().equals("wlan0"))
                    continue;
                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) return "";
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
            e.printStackTrace();
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
            token = tm.getDeviceId();
            // 获取IMSI
            String imsi = tm.getSubscriberId();
            // 获取SIM卡序列号
            String simNumber = tm.getSimSerialNumber();
            // 获取MAC地址
            WifiManager wifi = (WifiManager) context.getApplicationContext()
                    .getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wifi.getConnectionInfo();
            String macAddress = info.getMacAddress();
            if (null != token) {
                if (null != imsi && !imsi.trim().equals("")) {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return token;
    }

    public static String getTxtString(Context context, String fileName) throws IOException {// 转码
        File file = new File(context.getCacheDir(), fileName);
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
        String lineTxt = null;
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
                    if (read >= 0xF0)
                        break;
                    if (0x80 <= read && read <= 0xBF) // 单独出现BF以下的，也算是GBK
                        break;
                    if (0xC0 <= read && read <= 0xDF) {
                        read = bis.read();
                        if (0x80 <= read && read <= 0xBF) // 双字节 (0xC0 - 0xDF)
                            // (0x80
                            // - 0xBF),也可能在GB编码内
                            continue;
                        else
                            break;
                    } else if (0xE0 <= read && read <= 0xEF) {// 也有可能出错，但是几率较小
                        read = bis.read();
                        if (0x80 <= read && read <= 0xBF) {
                            read = bis.read();
                            if (0x80 <= read && read <= 0xBF) {
                                charset = "UTF-8";
                                break;
                            } else
                                break;
                        } else
                            break;
                    }
                }
            }
            bis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return charset;
    }

    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd HH:mm");
    private static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    public static boolean isInDownloadTime(PlaylistBodyBean bean) {
        String downloadTimeslice = bean.downloadTimeslice;
        if (TextUtils.isEmpty(downloadTimeslice)) {
            return true;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        String[] strs = downloadTimeslice.split("-");
        int _week = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (_week < 0)
            _week = 0;
        if (_week == 0) _week = 7;
        if (("," + strs[0] + ",").indexOf("," + _week + ",") >= 0) {
            //判断当前时间是否在工作时间段内
            Date startDt = null;
            Date endDt = null;
            Date nowDt = new Date();
            try {
                startDt = timeFormat.parse(strs[1]);
                Calendar ca = Calendar.getInstance();
                ca.setTime(startDt);
                ca.add(Calendar.MINUTE, Integer.parseInt(strs[2]));
                endDt = ca.getTime();

                if (timeFormat.format(nowDt).compareTo(timeFormat.format(startDt)) >= 0
                        && timeFormat.format(nowDt).compareTo(timeFormat.format(endDt)) <= 0) {
                    return true;
                }
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    public static boolean isInPlayTime(PlaylistBodyBean bean) {
        String currentDate = simpleDateFormat.format(new Date());
        if (!TextUtils.isEmpty(bean.playDate) && !TextUtils.isEmpty(bean.stopDate)
                && currentDate.compareTo(bean.playDate) >= 0 && currentDate.compareTo(bean.stopDate) < 0) {
            return true;
        }
        return false;
    }

    public static boolean isNeedDel(Context context, PlaylistBodyBean bean) {
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

    public static void delOneAllBean(Context context, PlaylistBodyBean bean) {
        String allBeanStr = get(context, KEY_PLAY_LIST_ALL, "").toString();
        if (TextUtils.isEmpty(allBeanStr)) return;
        PlaylistResultBean allBean = JSON.parseObject(allBeanStr, PlaylistResultBean.class);
        List<PlaylistBodyBean> allList = allBean.data.items;
        for (int i = 0; i < allList.size(); i++) {
            if (containSame(allList, bean)) {
                allList.remove(i);
                allBeanStr = JSONObject.toJSONString(allBean);
                put(context, KEY_PLAY_LIST_ALL, allBeanStr);
                return;
            }
        }
    }


    public static void delOneDownloadBean(Context context, PlaylistBodyBean bean) {
        String allBeanStr = get(context, KEY_PLAY_LIST_DOWNLOAD, "").toString();
        if (TextUtils.isEmpty(allBeanStr)) return;
        PlaylistResultBean allBean = JSON.parseObject(allBeanStr, PlaylistResultBean.class);
        List<PlaylistBodyBean> allList = allBean.data.items;
        for (int i = 0; i < allList.size(); i++) {
            if (containSame(allList, bean)) {
                allList.remove(i);
                allBeanStr = JSONObject.toJSONString(allBean);
                put(context, KEY_PLAY_LIST_DOWNLOAD, allBeanStr);
                return;
            }
        }
    }

    public static void megerAllBean(Context context, String jsonStr) {
        if (TextUtils.isEmpty(jsonStr)) return;
        String allBeanStr = get(context, KEY_PLAY_LIST_ALL, "").toString();
        if (TextUtils.isEmpty(allBeanStr)) {
            allBeanStr = jsonStr;
        } else {
            PlaylistResultBean allBean = JSON.parseObject(allBeanStr, PlaylistResultBean.class);
            PlaylistResultBean addBean = JSON.parseObject(jsonStr, PlaylistResultBean.class);
            List<PlaylistBodyBean> allList = allBean.data.items;
            List<PlaylistBodyBean> addList = addBean.data.items;
            for (int i = 0; i < addList.size(); i++) {
                if (!containSame(allList, addList.get(i))) {
                    allList.add(addList.get(i));
                }
            }
            allBeanStr = JSONObject.toJSONString(allBean);
        }
        put(context, KEY_PLAY_LIST_ALL, allBeanStr);
    }

    private static boolean containSame(List<PlaylistBodyBean> allList, PlaylistBodyBean bean) {
        for (int i = 0; i < allList.size(); i++) {
            PlaylistBodyBean bodyBean = allList.get(i);
            if (bodyBean.id.equals(bean.id) && bodyBean.name.equals(bean.name)) {
                if (bodyBean.lastModified.equals(bean.lastModified)) {
                    return true;
                } else {
                    allList.remove(bodyBean);
                    allList.add(i, bean);
                    return true;
                }
            }
        }
        return false;
    }

    public static void megerDownloadBean(Context context, String jsonStr) {
        if (TextUtils.isEmpty(jsonStr)) return;
        String allBeanStr = get(context, KEY_PLAY_LIST_DOWNLOAD, "").toString();
        if (TextUtils.isEmpty(allBeanStr)) {
            allBeanStr = jsonStr;
        } else {
            PlaylistResultBean allBean = JSON.parseObject(allBeanStr, PlaylistResultBean.class);
            PlaylistResultBean addBean = JSON.parseObject(jsonStr, PlaylistResultBean.class);
            List<PlaylistBodyBean> allList = allBean.data.items;
            List<PlaylistBodyBean> addList = addBean.data.items;
            for (int i = 0; i < addList.size(); i++) {
                if (!containSame(allList, addList.get(i))) {
                    allList.add(addList.get(i));
                }
            }
            allBeanStr = JSONObject.toJSONString(allBean);
        }
        put(context, KEY_PLAY_LIST_DOWNLOAD, allBeanStr);
    }
}
