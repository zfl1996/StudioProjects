package com.ads.abcbank.bean;

public class CmdresultBean extends RequestBean {
    public Data data = new Data();

    public static class Data {
        public String cmd;
        public String cmdresult;
    }
}
