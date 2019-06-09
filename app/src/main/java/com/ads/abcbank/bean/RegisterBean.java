package com.ads.abcbank.bean;

public class RegisterBean extends RequestBean {
    public Data data = new Data();

    public static class Data {
        public String terminalType;
        public String screenDirection;
        public String frameSetNo;
        public String appIpAddress;
        public String server;
        public String cdn;
        public String storeId;
    }
}
