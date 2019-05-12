package com.ads.abcbank.bean;

import java.util.ArrayList;
import java.util.List;

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
