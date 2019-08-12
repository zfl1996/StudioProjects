package com.ads.abcbank.xx.utils.helper;

import com.ads.abcbank.service.DownloadService;
import com.ads.abcbank.utils.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;

public class IOHelper {

    private static final String TAG = "IOHelper";

    /**
     * 递归删除文件或者目录
     */

    public static void deleteFile(File file) {
        try {
            if (file.exists()) {
                if (file.isFile()) {
                    file.delete();
                    return;
                }
                if (file.isDirectory()) {
                    File[] childFile = file.listFiles();
                    if (childFile == null || childFile.length == 0) {
                        file.delete();
                        return;
                    }
                    for (File f : childFile) {
                        deleteFile(f);
                    }
                    file.delete();
                }
            }
        } catch (Exception e) {
            Logger.e(TAG, "删除文件失败");
        }
    }

    /**
     * 递归删除超过12个月文件或者目录
     */

    public static void deleteFile12(File file, DownloadService.Clear12Listener listener) {
        try {
            //计算时间
            long day = 365;
            long hour = 24;
            long minute = 60;
            long second = 60;
            long mmcond = 1000;
            long currTime = System.currentTimeMillis();   //当前时间

            if (file.exists() == false) {
                return;
            } else {
                long lastTime = file.lastModified();     //文件被最后一次修改的时间
                //时间差
                long diffen = currTime - lastTime;

                long thDay = day * hour * minute * second * mmcond;

                if (diffen >= thDay) {
                    if (file.isFile() && !listener.isFileUsed(file.getName())) {
                        file.delete();
                        return;
                    }
                    if (file.isDirectory()) {
                        File[] childFile = file.listFiles();
                        if (childFile == null || childFile.length == 0) {
                            file.delete();
                            return;
                        }
                        for (File f : childFile) {
                            deleteFile12(f, listener);
                        }
                        file.delete();
                    }
                }

            }
        } catch (Exception e) {
            Logger.e(TAG, "删除超过12个月文件失败");
        }
    }

    /**
     * 根据路径递归删除文件或者目录
     */
    public static void deleteFile(String strFile) {
        deleteFile(new File(strFile));
    }

    public static String fileToMD5(String filePath) {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(filePath);
            byte[] buffer = new byte[1024];
            MessageDigest digest = MessageDigest.getInstance("MD5");
            int numRead = 0;
            while (numRead != -1) {
                numRead = inputStream.read(buffer);
                if (numRead > 0) {
                    digest.update(buffer, 0, numRead);
                }
            }
            byte[] md5Bytes = digest.digest();
            return convertHashToString(md5Bytes);
        } catch (Exception ignored) {
            return null;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e) {
                    Logger.e(TAG, "file to md5 failed");
                }
            }
        }
    }

    private static String convertHashToString(byte[] md5Bytes) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++) {
            buf.append(Integer.toString((md5Bytes[i] & 0xff) + 0x100, 16).substring(1));
        }
        return buf.toString().toUpperCase();
    }
}
