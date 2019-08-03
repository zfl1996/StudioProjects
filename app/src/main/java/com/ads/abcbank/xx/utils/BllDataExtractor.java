package com.ads.abcbank.xx.utils;

import com.ads.abcbank.bean.PlaylistBodyBean;
import com.ads.abcbank.xx.utils.helper.ResHelper;

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

}
