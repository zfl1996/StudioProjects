package com.ads.abcbank.xx.utils.net;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AbcDownloadManager {
    private Map<String, ResInfo> resInfoList;
    private List<ResInfo> waitForList;

    private Context context;

    public AbcDownloadManager(Context context) {
        this.context = context;

        resInfoList = new HashMap<>();
        waitForList = new ArrayList<>();
    }

    public void addTask(ResInfo resInfo) {
        waitForList.add(resInfo);
        Collections.sort(waitForList);
    }

    public boolean updateTaskPriority(String url, int priority) {
        boolean hasExist = false;

        for (ResInfo resInfo : waitForList) {
            if (url.equals(resInfo.url)) {
                resInfo.priority = priority;
                hasExist = true;

                break;
            }
        }

        if (hasExist && resInfoList.containsKey(url)) {
            ResInfo resInfo = resInfoList.get(url);

            if (null != resInfo)
                resInfo.priority = priority;
        }

        return hasExist;
    }



}
