package com.ads.abcbank.bean;

import android.text.TextUtils;

import com.ads.abcbank.utils.ActivityManager;
import com.ads.abcbank.utils.Utils;
import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;

public class PlaylistBean extends RequestBean {
    public Data data = new Data();

    public static class Data {
        public List<String> items = new ArrayList<>();
    }

    public PlaylistBean(){
        String beanStr = Utils.get(ActivityManager.getInstance().getTopActivity(), Utils.KEY_REGISTER_BEAN, "").toString();
        if (!TextUtils.isEmpty(beanStr)) {
            RegisterBean bean = JSON.parseObject(beanStr, RegisterBean.class);
            appId = bean.appId;
            trCode = bean.trCode;
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
}
