package com.ads.abcbank.xx.model;

public class PlayItem {
    String md5;
    String url;
    Object attData;
    /*
     * 0 -- Image
     * 1 -- PDF
     * 2 -- Video
     * 3 -- TEXT
     * 4 -- P3
     * */
    int mediaType;
    int Order;

    String playDate;
    String stopDate;
    String clickLink;

    public PlayItem() {

    }

    public PlayItem(String md5,
                    String url,
                    int mediaType,
                    String playDate,
                    String stopDate,
                    String clickLink,
                    Object attData) {
        this.md5 = md5;
        this.url = url;
        this.mediaType = mediaType;
        this.playDate = playDate;
        this.stopDate = stopDate;
        this.clickLink = clickLink;
        this.attData = attData;
    }

    public PlayItem(int mediaType, Object attData) {
        this.mediaType = mediaType;
        this.attData = attData;
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

    public Object getAttData() {
        return attData;
    }

    public void setAttData(Object attData) {
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

    public String getPlayDate() {
        return playDate;
    }

    public void setPlayDate(String playDate) {
        this.playDate = playDate;
    }

    public String getStopDate() {
        return stopDate;
    }

    public void setStopDate(String stopDate) {
        this.stopDate = stopDate;
    }

    public String getClickLink() {
        return clickLink;
    }

    public void setClickLink(String clickLink) {
        this.clickLink = clickLink;
    }
}
