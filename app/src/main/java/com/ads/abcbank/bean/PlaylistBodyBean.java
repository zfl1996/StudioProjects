package com.ads.abcbank.bean;

import android.text.TextUtils;

import com.ads.abcbank.utils.ActivityManager;
import com.ads.abcbank.utils.Utils;
import com.alibaba.fastjson.JSON;

import java.util.List;

public class PlaylistBodyBean extends RequestBean {
    public String id;
    public String name;
    public String playDate;
    public String stopDate;
    public String downloadTimeslice;
    public String contentType;
    public String md5;
    public String isUrg;
    public String lastModified;
    public String downloadLink;
    public String onClickLink;
    public List<QR> QRCode;

    public static class QR {
        public String QRLink;
        public String QRPosInDial;
        public String QRTip;
    }

    public PlaylistBodyBean(){
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
