package com.ads.abcbank.utils;


import android.os.Environment;

import com.ads.abcbank.service.DownloadService;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * @author chenhuan001
 */
public class FileUtil {
    private static final String TAG = "FileUtil";

    /**
     * 创建目录
     *
     * @param dir_path
     */
    public static void mkDir(String dir_path) {
        File myFolderPath = new File(dir_path);
        try {
            if (!myFolderPath.exists()) {
                myFolderPath.mkdir();
            }
        } catch (Exception e) {
            Logger.e(TAG, "新建目录操作出错");
            e.printStackTrace();
        }
    }

    /**
     * 创建文件
     *
     * @param file_path
     */
    public static void createNewFile(String file_path) {
        File myFilePath = new File(file_path);
        try {
            if (!myFilePath.exists()) {
                myFilePath.createNewFile();
            }
        } catch (Exception e) {
            Logger.e(TAG, "新建文件操作出错");
            e.printStackTrace();
        }
    }

    /**
     * @param fromFile 指定的下载目录
     * @param toFile   应用的包路径
     * @return
     */
    public static int copy(String fromFile, String toFile) {
        //要复制的文件目录
        File root = new File(fromFile);
        //如同判断SD卡是否存在或者文件是否存在,如果不存在则 return出去
        if (!root.exists()) {
            return -1;
        }
        //如果存在则获取当前目录下的全部文件 填充数组
        File[] currentFiles = root.listFiles();

        //目标目录
        File targetDir = new File(toFile);
        //创建目录
        if (!targetDir.exists()) {
            targetDir.mkdirs();
        }
        if (currentFiles != null && currentFiles.length > 0) {
            //遍历要复制该目录下的全部文件
            for (File currentFile : currentFiles) {
                if (currentFile.isDirectory()) {
                    //如果当前项为子目录 进行递归
                    copy(currentFile.getPath() + "/", toFile + currentFile.getName() + "/");
                } else {
                    //如果当前项为文件则进行文件拷贝
                    if (currentFile.getName().contains(".so")) {
                        int id = copySdcardFile(currentFile.getPath(), toFile + File.separator + currentFile.getName());
                    }
                }
            }
        }
        return 0;
    }


    //文件拷贝
    //要复制的目录下的所有非子目录(文件夹)文件拷贝
    public static int copySdcardFile(String fromFile, String toFile) {
        try {
            FileInputStream fosfrom = new FileInputStream(fromFile);
            FileOutputStream fosto = new FileOutputStream(toFile);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = -1;
            while ((len = fosfrom.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            // 从内存到写入到具体文件
            fosto.write(baos.toByteArray());
            // 关闭文件流
            baos.close();
            fosto.close();
            fosfrom.close();
            return 0;
        } catch (Exception ex) {
            return -1;
        }
    }

    /**
     * 递归删除文件或者目录
     */

    public static void deleteFile(File file) {
        try {
            if (file.exists() == false) {
                return;
            } else {
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

    public static void writeJsonToFile(String json) {
        try {
            String filePath = getDownSave() + "playlist.json";
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath, true), "UTF-8"));
            bw.write(json);
            bw.flush();
        } catch (IOException e) {
            Logger.e(TAG, "write json to file failed");
        }
    }

    /**
     * 从本地读取json
     */
    public static String readTextFile(String filePath) {
        StringBuilder sb = new StringBuilder();
        try {
            File file = new File(getDownSave() + filePath);
            InputStream in = null;
            in = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(in, "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append("\r\n");
            }
            in.close();
        } catch (Exception e) {
            Logger.e(TAG, e.toString());
        }
        return sb.toString();
    }

    public static String getDownSave() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/abcdownload/");
            if (!file.exists()) {
                boolean r = file.mkdirs();
                if (!r) {
                    return null;
                }
                return file.getAbsolutePath() + "/";
            }
            return file.getAbsolutePath() + "/";
        } else {
            return null;
        }
    }

    private static String convertHashToString(byte[] md5Bytes) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++) {
            buf.append(Integer.toString((md5Bytes[i] & 0xff) + 0x100, 16).substring(1));
        }
        return buf.toString().toUpperCase();
    }


    /*
     * 得到一个文件夹下所有文件
     */
    public static List<String> getAllFileNameInFold(String fold_path) {
        List<String> file_paths = new ArrayList<String>();

        LinkedList<String> folderList = new LinkedList<String>();
        folderList.add(fold_path);
        while (folderList.size() > 0) {
            File file = new File(folderList.peekLast());
            folderList.removeLast();
            File[] files = file.listFiles();
            ArrayList<File> fileList = new ArrayList<File>();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    folderList.add(files[i].getPath());
                } else {
                    fileList.add(files[i]);
                }
            }
            for (File f : fileList) {
                file_paths.add(f.getAbsoluteFile().getPath());
            }
        }
        return file_paths;
    }

    private static final String[][] MIME_MapTable = {
            //{后缀名，    MIME类型}
            {".3gp", "video/3gpp"},
            {".apk", "application/vnd.android.package-archive"},
            {".asf", "video/x-ms-asf"},
            {".avi", "video/x-msvideo"},
            {".bin", "application/octet-stream"},
            {".bmp", "image/bmp"},
            {".c", "text/plain"},
            {".class", "application/octet-stream"},
            {".conf", "text/plain"},
            {".cpp", "text/plain"},
            {".doc", "application/msword"},
            {".exe", "application/octet-stream"},
            {".gif", "image/gif"},
            {".gtar", "application/x-gtar"},
            {".gz", "application/x-gzip"},
            {".h", "text/plain"},
            {".htm", "text/html"},
            {".html", "text/html"},
            {".jar", "application/java-archive"},
            {".java", "text/plain"},
            {".jpeg", "image/jpeg"},
            {".jpg", "image/jpeg"},
            {".js", "application/x-javascript"},
            {".log", "text/plain"},
            {".m3u", "audio/x-mpegurl"},
            {".m4a", "audio/mp4a-latm"},
            {".m4b", "audio/mp4a-latm"},
            {".m4p", "audio/mp4a-latm"},
            {".m4u", "video/vnd.mpegurl"},
            {".m4v", "video/x-m4v"},
            {".mov", "video/quicktime"},
            {".mp2", "audio/x-mpeg"},
            {".mp3", "audio/x-mpeg"},
            {".mp4", "video/mp4"},
            {".mpc", "application/vnd.mpohun.certificate"},
            {".mpe", "video/mpeg"},
            {".mpeg", "video/mpeg"},
            {".mpg", "video/mpeg"},
            {".mpg4", "video/mp4"},
            {".mpga", "audio/mpeg"},
            {".msg", "application/vnd.ms-outlook"},
            {".ogg", "audio/ogg"},
            {".pdf", "application/pdf"},
            {".png", "image/png"},
            {".pps", "application/vnd.ms-powerpoint"},
            {".ppt", "application/vnd.ms-powerpoint"},
            {".prop", "text/plain"},
            {".rar", "application/x-rar-compressed"},
            {".rc", "text/plain"},
            {".rmvb", "audio/x-pn-realaudio"},
            {".rtf", "application/rtf"},
            {".sh", "text/plain"},
            {".tar", "application/x-tar"},
            {".tgz", "application/x-compressed"},
            {".txt", "text/plain"},
            {".wav", "audio/x-wav"},
            {".wma", "audio/x-ms-wma"},
            {".wmv", "audio/x-ms-wmv"},
            {".wps", "application/vnd.ms-works"},
            //{".xml",    "text/xml"},
            {".xml", "text/plain"},
            {".z", "application/x-compress"},
            {".zip", "application/zip"},
            {"", "*/*"}
    };

    public static String getMIMEType(File file) {

        String type = "*/*";
        String fName = file.getName();
        //获取后缀名前的分隔符"."在fName中的位置。
        int dotIndex = fName.lastIndexOf(".");
        if (dotIndex < 0) {
            return type;
        }
        /* 获取文件的后缀名 */
        String fileType = fName.substring(dotIndex, fName.length()).toLowerCase();
        if (fileType == null || "".equals(fileType)) {
            return type;
        }
        //在MIME和文件类型的匹配表中找到对应的MIME类型。
        for (int i = 0; i < MIME_MapTable.length; i++) {
            if (fileType.equals(MIME_MapTable[i][0])) {
                type = MIME_MapTable[i][1];
            }
        }
        return type;
    }
}
