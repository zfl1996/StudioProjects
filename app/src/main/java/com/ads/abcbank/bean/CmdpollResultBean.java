package com.ads.abcbank.bean;

public class CmdpollResultBean extends ResultBean {
    public Data data = new Data();

    public static class Data {
        public String cmd;
    }
}
