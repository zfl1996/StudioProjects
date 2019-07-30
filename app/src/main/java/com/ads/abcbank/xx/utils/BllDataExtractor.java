package com.ads.abcbank.xx.utils;

import com.ads.abcbank.bean.PlaylistBodyBean;
import com.ads.abcbank.xx.utils.helper.ResHelper;

public class BllDataExtractor {
    public static String getIdentity(PlaylistBodyBean playlistBodyBean){
        return playlistBodyBean.name;
    }

    /***
     * 0 -- Image
     * 1 -- PDF
     * 2 -- Video
     * 3 -- TEXT
     * 4 -- P3
     */
    public static int getIdentityType(PlaylistBodyBean playlistBodyBean) {
        String resIdentity = getIdentity(playlistBodyBean);
        String suffix = resIdentity.substring(resIdentity.lastIndexOf(".") + 1).toLowerCase();
        switch (suffix) {
            case "mp4":
            case "mkv":
            case "wmv":
            case "avi":
            case "rmvb":
                return 2;
            case "jpg":
            case "png":
            case "bmp":
            case "jpeg":
                return 0;
            case "pdf":
                return 1;
            case "txt":
                return 3;
            case "wps":
            default:
                return -1;
        }
    }

    public static String getIdentityPath(PlaylistBodyBean playlistBodyBean){
        String resIdentity = getIdentity(playlistBodyBean);
        String suffix = resIdentity.substring(resIdentity.lastIndexOf(".") + 1).toLowerCase();
        if (!ResHelper.isNullOrEmpty(resIdentity)) {
            switch (suffix) {
                case "mp4":
                case "mkv":
                case "wmv":
                case "avi":
                case "rmvb":
                    return ResHelper.getRootDir() + "videos/" + resIdentity;
                case "jpg":
                case "png":
                case "bmp":
                case "jpeg":
                    return ResHelper.getRootDir() + "images/" + resIdentity;
                case "pdf":
                case "txt":
                    return ResHelper.getRootDir() + "files/" + resIdentity;
                case "wps":
                    return null;
                default:
                    return ResHelper.getRootDir() + "temp/" + resIdentity;
            }
        }

        return playlistBodyBean.name;
    }

}
