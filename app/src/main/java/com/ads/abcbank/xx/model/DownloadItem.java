package com.ads.abcbank.xx.model;

import android.os.Parcel;
import android.os.Parcelable;

public class DownloadItem implements Parcelable {
    private String md5;
    private String url;
    private int resType;
    private int priority;

    public DownloadItem(){}

    public DownloadItem(String md5, String url, int resType, int priority) {
        this.md5 = md5;
        this.url = url;
        this.resType = resType;
        this.priority = priority;
    }

    public DownloadItem(Parcel in) {
        this.md5 = in.readString();
        this.url = in.readString();
        this.resType = in.readInt();
        this.priority = in.readInt();
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

    public int getResType() {
        return resType;
    }

    public void setResType(int resType) {
        this.resType = resType;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public static final Creator<DownloadItem> CREATOR = new Creator<DownloadItem>() {
        @Override
        public DownloadItem createFromParcel(Parcel source) {
            return new DownloadItem(source);
        }

        @Override
        public DownloadItem[] newArray(int size) {
            return new DownloadItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(md5);
        dest.writeString(url);
        dest.writeInt(resType);
        dest.writeInt(priority);
    }
}
