package com.ads.abcbank.bean;

import android.text.TextUtils;

import com.ads.abcbank.utils.ActivityManager;
import com.ads.abcbank.utils.Utils;
import com.alibaba.fastjson.JSON;

public class DownloadBean extends RequestBean {
    public String id;
    public String name;
    public String started;
    public String secUsed;
    public String status;
    public long startTimestamp;

    public Data data = new Data();

    public DownloadBean() {
        String beanStr = Utils.get(ActivityManager.getInstance().getTopActivity(), Utils.KEY_REGISTER_BEAN, "").toString();
        if (!TextUtils.isEmpty(beanStr)) {
            RegisterBean bean = JSON.parseObject(beanStr, RegisterBean.class);
            appId = bean.appId;
            trCode = "downloadfinish";
            trVersion = bean.trVersion;
            cityCode = bean.cityCode;
            brchCode = bean.brchCode;
            clientVersion = bean.clientVersion;
            terminalId = bean.terminalId;
            uniqueId = bean.uniqueId;
        }
        timestamp = System.currentTimeMillis();
        flowNum = 0;
    }

    public static class Data {
        public String id;
        public String started;
        public String secUsed;
        public String status;
    }

    public Data getData() {
        data.id = id;
        data.started = started;
        data.secUsed = secUsed;
        data.status = status;
        return data;
    }
}
