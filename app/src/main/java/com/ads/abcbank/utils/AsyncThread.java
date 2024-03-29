package com.ads.abcbank.utils;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.ads.abcbank.bean.RegisterBean;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AsyncThread {
    public static final int CONNECT_TIMEOUT = 30;
    public static final int CONNECT_READ_TIMEOUT = 30;

    public void httpService(final String url, final JSONObject jsonString, final Handler handler, final int wath) {


        try {
            String beanStr = Utils.get(ActivityManager.getInstance().getTopActivity(), Utils.KEY_REGISTER_BEAN, "").toString();
            if (TextUtils.isEmpty(beanStr)) {
                beanStr = jsonString.toString();
            }
            String urlStr = url;
            if (!TextUtils.isEmpty(beanStr)) {
                RegisterBean bean = JSON.parseObject(beanStr, RegisterBean.class);
                urlStr = "http://" + bean.data.server + url;
            }
            final String realUrl = urlStr;
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                    .readTimeout(CONNECT_READ_TIMEOUT, TimeUnit.SECONDS).build();//创建OkHttpClient对象。
            MediaType json = MediaType.parse("application/json; charset=utf-8");//数据类型为json格式，

            RequestBody body = RequestBody.create(json, jsonString.toString());
            Request request = new Request.Builder()
                    .url(realUrl)
                    .header("User-Agent", "OkHttp Headers.java")
                    .addHeader("Accept", "application/json; q=0.5")
                    .addHeader("Accept", "application/vnd.github.v3+json")
                    .post(body)
                    .build();
            client.dispatcher().setMaxRequestsPerHost(8);
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Message msg = handler.obtainMessage(wath);
                    msg.obj = null;
                    handler.sendMessage(msg);
                    Logger.e("数据交互出错", e.toString());
                    Utils.hideProgressDialog();
                    ToastUtil.showToastLong(ActivityManager.getInstance().getTopActivity(), "获取数据失败：" + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {//回调的方法执行在子线程。
                        Message msg = handler.obtainMessage(wath);
                        try {
                            msg.obj = response.body().string();
                            handler.sendMessage(msg);
                        } catch (Exception e) {
                            msg.obj = null;
                            handler.sendMessage(msg);
                            Logger.e("数据交互出错", e.toString());
                            ToastUtil.showToastLong(ActivityManager.getInstance().getTopActivity(), "获取数据出错：" + e.getMessage());
                        }
                    }
                    Utils.hideProgressDialog();
                }
            });//此处省略回调方法。
        } catch (Exception e) {
            Logger.e("数据交互出错", e.toString());
            Utils.hideProgressDialog();
            ToastUtil.showToastLong(ActivityManager.getInstance().getTopActivity(), "获取数据出现错误：" + e.getMessage());
        }
    }
}
