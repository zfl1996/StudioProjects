package com.ads.abcbank.bean;

import java.util.ArrayList;
import java.util.List;

public class PlaylistResultBean extends ResultBean {
    public Data data = new Data();

    public static class Data {
        public List<PlaylistBodyBean> items = new ArrayList<>();
    }
}
