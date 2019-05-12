package com.ads.abcbank.bean;

import java.util.ArrayList;
import java.util.List;

public class PlaylistBean extends RequestBean {
    public Data data = new Data();

    public static class Data {
        public List<String> items = new ArrayList<>();
    }
}
