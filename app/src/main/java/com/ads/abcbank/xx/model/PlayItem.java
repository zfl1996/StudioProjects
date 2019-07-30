package com.ads.abcbank.xx.model;

public class PlayItem {
    String md5;
    String url;
    String attData;
    /*
     * 0 -- Image
     * 1 -- PDF
     * 2 -- Video
     * 3 -- TEXT
     * 4 -- P3
     * */
    int mediaType;
    int Order;

    public PlayItem() {

    }

    public PlayItem(String md5, String url, int mediaType) {
        this.md5 = md5;
        this.url = url;
        this.mediaType = mediaType;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAttData() {
        return attData;
    }

    public void setAttData(String attData) {
        this.attData = attData;
    }

    public int getMediaType() {
        return mediaType;
    }

    public void setMediaType(int mediaType) {
        this.mediaType = mediaType;
    }

    public int getOrder() {
        return Order;
    }

    public void setOrder(int order) {
        Order = order;
    }
}
