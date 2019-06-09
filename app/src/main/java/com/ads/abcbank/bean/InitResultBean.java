package com.ads.abcbank.bean;

public class InitResultBean extends ResultBean {
    public InitBodyBean data = new InitBodyBean();

    public static class InitBodyBean {
        public String latestClientVersion;
        public String serverTime;
        public String downloadLink;
    }
}
