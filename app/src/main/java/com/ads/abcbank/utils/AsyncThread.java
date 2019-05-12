package com.ads.abcbank.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.ads.abcbank.MyApplication;

import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AsyncThread {
    //    private final ExecutorService executorService = Executors.newFixedThreadPool(5);
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmss");
//	private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public static final int ConnectTimeout = 3;
    public static final int ConnectReadTimeout = 3;

    public void httpService(String url, final JSONObject jsonString, final Handler handler, final int wath) {
        try {
            Date date = new Date();
            jsonString.put("reqDate", dateFormat.format(date));
            jsonString.put("reqTime", timeFormat.format(date));
            jsonString.put("channel", "01");
            jsonString.put("busCode", url);
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(ConnectTimeout, TimeUnit.SECONDS)
                    .readTimeout(ConnectReadTimeout, TimeUnit.SECONDS).build();//创建OkHttpClient对象。
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");//数据类型为json格式，

            RequestBody body = RequestBody.create(JSON, jsonString.toString());
            Request request = new Request.Builder()
                    .url(url)
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
                    Log.e("数据交互出错", e.toString());
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
                            Log.e("数据交互出错", e.toString());
                        }
                    }
                }
            });//此处省略回调方法。
        } catch (Exception e) {
            Log.e("数据交互出错", e.toString());
        }
    }
}
