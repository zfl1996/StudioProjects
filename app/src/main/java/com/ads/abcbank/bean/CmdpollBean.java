package com.ads.abcbank.bean;

public class CmdpollBean extends RequestBean {
    public Data data = new Data();

    public static class Data {
        public String clientCode;
        public String clientErrMessage;
    }
}
