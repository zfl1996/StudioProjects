package com.ads.abcbank.xx.model;

public class MaterialInfo {

    String id;
    String playDate;
    String stopDate;


    public MaterialInfo(String id, String playDate, String stopDate) {
        this.id = id;
        this.playDate = playDate;
        this.stopDate = stopDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
}
