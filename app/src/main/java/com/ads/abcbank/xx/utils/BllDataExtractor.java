package com.ads.abcbank.xx.utils;

import android.content.Context;
import android.text.TextUtils;

import com.ads.abcbank.bean.PlaylistBodyBean;
import com.ads.abcbank.bean.PlaylistResultBean;
import com.ads.abcbank.utils.Logger;
import com.ads.abcbank.utils.Utils;
import com.ads.abcbank.xx.utils.helper.ResHelper;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class BllDataExtractor {
    public static String getIdentity(PlaylistBodyBean playlistBodyBean){
        return playlistBodyBean.id;
    }

    /***
     * 0 -- Image
     * 1 -- PDF
     * 2 -- Video
     * 3 -- TEXT
     * 4 -- P3
     */
    public static int getIdentityType(PlaylistBodyBean playlistBodyBean) {
//        String resIdentity = getIdentity(playlistBodyBean);
        String suffix = playlistBodyBean.downloadLink.substring(playlistBodyBean.downloadLink.lastIndexOf(".") + 1).toLowerCase();
        return getIdentityType(suffix);
    }

//    public static String getIdentityPath(PlaylistBodyBean playlistBodyBean){
////        String resIdentity = getIdentity(playlistBodyBean);
////        String suffix = resIdentity.substring(resIdentity.lastIndexOf(".") + 1).toLowerCase();
////        if (!ResHelper.isNullOrEmpty(resIdentity)) {
////            return getIdentitySavePath(resIdentity, suffix);
////        }
//
////        String[] pathSegments = ResHelper.getSavePathDataByUrl(playlistBodyBean.downloadLink);
////        if (pathSegments.length > 0){
////            ResHelper.getSavePath(playlistBodyBean.downloadLink, playlistBodyBean.id);
////        }
//        return ResHelper.getSavePath(playlistBodyBean.downloadLink, playlistBodyBean.id);
//
//        return playlistBodyBean.id;
//    }

    /**
     * 根据后缀返回资源类型值
     * 0 -- Image
     * 1 -- PDF
     * 2 -- Video
     * 3 -- TEXT
     * 4 -- P3
     * */
    public static int getIdentityType(String suffix) {
        switch (suffix) {
            case "mp4":
            case "mkv":
            case "wmv":
            case "avi":
            case "rmvb":
                return Constants.SLIDER_HOLDER_VIDEO;
            case "jpg":
            case "png":
            case "bmp":
            case "jpeg":
                return Constants.SLIDER_HOLDER_IMAGE;
            case "pdf":
                return Constants.SLIDER_HOLDER_PDF;
            case "txt":
                return Constants.SLIDER_HOLDER_TEXT;
            case "wps":
            default:
                return -1;
        }
    }

    /**
     * 根据文件名和后缀返回完整的保存路径
     * */
    public static String getIdentitySavePath(String fileName, String suffix) {
        switch (suffix) {
            case "mp4":
            case "mkv":
            case "wmv":
            case "avi":
            case "rmvb":
                return ResHelper.getRootDir() + "videos/" + fileName;
            case "jpg":
            case "png":
            case "bmp":
            case "jpeg":
                return ResHelper.getRootDir() + "images/" + fileName;
            case "pdf":
            case "txt":
                return ResHelper.getRootDir() + "files/" + fileName;
            case "wps":
                return null;
            default:
                return ResHelper.getRootDir() + "temp/" + fileName;
        }
    }

    public static boolean isInPlayTime(String playDate, String stopDate) {
        try {
            if (ResHelper.isNullOrEmpty(playDate) || ResHelper.isNullOrEmpty(stopDate)) {
                return false;
            }

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
            String currentDate = simpleDateFormat.format(new Date());
            if (currentDate.compareTo(playDate) >= 0 && currentDate.compareTo(stopDate) < 0) {
                return true;
            } else {
                Logger.e("BllDataExtractor", "isInPlayTime:" + currentDate + ".." + playDate + ".." + stopDate + "-->currentDate.compareTo(playDate)"
                    + (currentDate.compareTo(playDate) >= 0) + "-->currentDate.compareTo(stopDate)" + (currentDate.compareTo(stopDate)));
            }
        } catch (Exception e) {
            Logger.e(e.toString());
        }
        return false;
    }

    public static boolean isInFilter(String filters, PlaylistBodyBean bodyBean, String contentTypeMiddle, String contentTypeEnd) {
//        String contentTypeMiddle = Utils.getContentTypeMiddle(context);
//        String contentTypeEnd = Utils.getContentTypeEnd(context);
        if ("*".equals(contentTypeEnd)) {
            if (bodyBean.contentType.substring(1, 2).equals(contentTypeMiddle) &&
                    filters.contains(bodyBean.contentType.substring(0, 1))) {
                return true;
            }
        } else {
            if (bodyBean.contentType.endsWith(contentTypeEnd) &&
                    bodyBean.contentType.substring(1, 2).equals(contentTypeMiddle) &&
                    filters.contains(bodyBean.contentType.substring(0, 1))) {
                return true;
            }
        }

        return false;
    }


    //是否在允许下载的时间段内
    public static boolean isInDownloadTime(PlaylistBodyBean bean) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        String downloadTimeslice = bean.downloadTimeslice;
        if (TextUtils.isEmpty(downloadTimeslice)) {
            return true;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        String[] strs = downloadTimeslice.split("-");
        int week = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (week < 0) {
            week = 0;
        }
        if (week == 0) {
            week = 7;
        }
        if (("," + strs[0] + ",").indexOf("," + week + ",") >= 0) {
            //判断当前时间是否在工作时间段内
            Date startDt;
            Date endDt;
            Date nowDt = new Date();
            try {
//                startDt = timeFormat.parse(strs[1]);
//                Calendar ca = Calendar.getInstance();
//                ca.setTime(startDt);
//                ca.add(Calendar.MINUTE, Integer.parseInt(strs[2]));
//                endDt = ca.getTime();
//
//                if (timeFormat.format(nowDt).compareTo(timeFormat.format(startDt)) >= 0
//                        && ((!"00:00".equals(timeFormat.format(endDt)) && timeFormat.format(nowDt).compareTo(timeFormat.format(endDt)) <= 0)
//                        || "00:00".equals(timeFormat.format(endDt)))) {
//                    return true;
//                }

                Calendar ca = Calendar.getInstance();
                long c = ca.getTimeInMillis();

                ca.set(Calendar.HOUR_OF_DAY, Integer.parseInt(strs[1].substring(0, strs[1].indexOf(":"))));
                ca.set(Calendar.MINUTE, Integer.parseInt(strs[1].substring(strs[1].indexOf(":") + 1)));
                long s = ca.getTimeInMillis();

                ca.add(Calendar.MINUTE, Integer.parseInt(strs[2]));
                long e = ca.getTimeInMillis();

                if (c >= s && c <= e)
                    return true;

            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    public static boolean needDownload(Context context, String jsonString) {
        String oldItemsData = Utils.get(context, Utils.KEY_PLAY_LIST, "").toString();
        if (ResHelper.isNullOrEmpty(jsonString)) {
            if (!ResHelper.isNullOrEmpty(oldItemsData)) {
                Utils.put(context, Utils.KEY_PLAY_LIST, "");
                return true;
            }

            return false;
        }

        PlaylistResultBean bean = JSON.parseObject(jsonString, PlaylistResultBean.class);
        if (bean != null && bean.data != null && bean.data.items != null) {
            String newItemsData = JSONObject.toJSONString(bean.data.items);

            if (!oldItemsData.equals(newItemsData)) {
                Utils.put(context, Utils.KEY_PLAY_LIST, newItemsData);
                return true;
            }
        }

        return false;
    }

}
