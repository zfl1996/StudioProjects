package com.ads.abcbank.bean;

import java.util.List;

public class PlaylistBodyBean extends RequestBean {
    public String id;
    public String name;
    public String playDate;
    public String stopDate;
    public String downloadTimeslice;
    public String contentType;
    public String md5;
    public String lastModified;
    public String downloadLink;
    public String onClickLink;
    public List<QR> QRCode;

    public static class QR {
        public String QRLink;
        public String QRPosInDial;
        public String QRTip;
    }
}
