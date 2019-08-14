package com.ads.abcbank.xx.utils.helper;

import android.graphics.Bitmap;
import android.os.Environment;
import android.support.annotation.Nullable;

import com.ads.abcbank.xx.utils.Constants;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class ResHelper {
    public static String savePdfPageFile(Bitmap bm, String fileName, int pageIndex) {
        String cacheFilePath = getPdfCacheFilePath(fileName, pageIndex);

        File myCaptureFile = new File(cacheFilePath);
        if (!createOrExistsFile(myCaptureFile))
            return null;

        BufferedOutputStream bos = null;
        try {
            bos = new BufferedOutputStream(
                    new FileOutputStream(myCaptureFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        bm.compress(Bitmap.CompressFormat.PNG, 100, bos);
        try {
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return myCaptureFile.getAbsolutePath();
    }

    /*
    * 返回 [文件名，扩展名]
    * */
    public static String[] getPDFExtInfo(String fileName){
        try{

            String extName = fileName.substring( fileName.lastIndexOf(".") );
            return new String[] {
                    fileName.substring(0, fileName.indexOf(extName)),
                    extName.substring(1)
            };

        }catch (Exception ex) {
            return new String[0];
        }
    }

    public static String getPdfCacheFilePath(String fileName, int pageIndex) {
        String s = getPdfDir();
        String[] fileExtInfo = getPDFExtInfo(fileName);

        return s + fileExtInfo[0] + "/" + pageIndex +  Constants.COMPRESS_FORMAT; //".jpg";
    }

    public static String getPdfMetadataPath(String fileName) {
        return getPdfCacheFileDir(fileName) + "readme.txt";
    }

    public static String getPdfCacheFileDir(String fileName) {
        String s = getPdfDir();
        String[] fileExtInfo = getPDFExtInfo(fileName);

        return s + fileExtInfo[0] + "/";
    }

    public static String getPdfDir() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + Constants.ROOT_FILE_NAME + "/");
            if (!file.exists()) {
                boolean r = file.mkdirs();
                if (!r) {
                    return null;
                }
                return file.getAbsolutePath() + "/files/";
            }
            return file.getAbsolutePath() + "/files/";
        } else {
            return null;
        }
    }

    public static String getSavePath(String url, String fileKey) {
        String[] fd = getSavePathDataByUrl(url);
        return getRootDir() + fd[1] + fileKey + "." + fd[0];
    }

    /**
     * String[2]
     * 0 -- ext name
     * 1 -- save dir
     * */
    public static String[] getSavePathDataByUrl(String url) {
        try{

            String extName = url.substring( url.lastIndexOf(".") + 1 );
            String resDir = "files/";

            switch (extName) {
                case "mp4":
                case "mkv":
                case "wmv":
                case "avi":
                case "rmvb":
                    resDir = "videos/";
                    break;
                case "jpg":
                case "png":
                case "bmp":
                case "jpeg":
                    resDir = "images/";
                    break;
                case "txt":
                case "pdf":
                    resDir = "files/";
                    break;
                default:
                    break;
            }

            if (ResHelper.isNullOrEmpty(extName))
                return new String[0];

            return new String[] {
                    extName,
//                    getRootDir(),
                    resDir
            };


        }catch (Exception ex) {
            return new String[0];
        }
    }

    public static String getRootDir() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
            return Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + Constants.ROOT_FILE_NAME + "/";
        else
            return null;
    }

    public static String getTempRootDir() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
            return Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + Constants.ROOT_TEMPFILE_NAME + "/";
        else
            return null;
    }


    public static Boolean isNullOrEmpty(@Nullable String str) {
        return (str == null || str.trim().length() == 0);
    }


    /**
     * 将字符串写入文件
     *
     * @param file    文件
     * @param content 写入内容
     * @param append  是否追加在文件末
     * @return {@code true}: 写入成功<br>{@code false}: 写入失败
     */
    public static boolean writeFileFromString(File file, String content, boolean append) {
        if (file == null || content == null) return false;
        if (!createOrExistsFile(file)) return false;
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(file, append));
            bw.write(content);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            CloseHelper.closeIO(bw);
        }
    }


    public static boolean isExistsFile(String path) {
        File file = new File(path);
        if (file == null) return false;
        // 如果存在，是文件则返回true，是目录则返回false
        if (file.exists())
            return file.isFile();

        return false;
    }

    /**
     * 判断文件是否存在，不存在则判断是否创建成功
     *
     * @param file 文件
     * @return {@code true}: 存在或创建成功<br>{@code false}: 不存在或创建失败
     */
    public static boolean createOrExistsFile(File file) {
        if (file == null) return false;
        // 如果存在，是文件则返回true，是目录则返回false
        if (file.exists()) return file.isFile();
        if (!createOrExistsDir(file.getParentFile())) return false;
        try {
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 判断目录是否存在，不存在则判断是否创建成功
     *
     * @param file 文件
     * @return {@code true}: 存在或创建成功<br>{@code false}: 不存在或创建失败
     */
    public static boolean createOrExistsDir(File file) {
        // 如果存在，是目录则返回true，是文件则返回false，不存在则返回是否创建成功
        return file != null && (file.exists() ? file.isDirectory() : file.mkdirs());
    }

    /**
     * 指定编码按行读取文件到字符串中
     *
     * @param file        文件
     * @param charsetName 编码格式
     * @return 字符串
     */
    public static String readFile2String(File file, String charsetName) {
        if (file == null) return null;
        BufferedReader reader = null;
        try {
            StringBuilder sb = new StringBuilder();
            if (isNullOrEmpty(charsetName)) {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            } else {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), charsetName));
            }
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\r\n");// windows系统换行为\r\n，Linux为\n
            }
            // 要去除最后的换行符
            return sb.delete(sb.length() - 2, sb.length()).toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            CloseHelper.closeIO(reader);
        }
    }

    public static String readFile2String(String filePath) {
        return readFile2String(getFileByPath(filePath), "GBK");
    }

    public static File getFileByPath(String filePath) {
        return isNullOrEmpty(filePath) ? null : new File(filePath);
    }

    public static String join(String[] arr, String sep)
    {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String item : arr)
        {
            if (first)
                first = false;
            else
                sb.append(sep);
            sb.append(item);
        }
        return sb.toString();
    }

    public static String[] getTimeString(SimpleDateFormat dateFormat, SimpleDateFormat timeFormat) {
        Calendar calendar = Calendar.getInstance();
        Date date = new Date();
        calendar.setTime(date);
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        int week = calendar.get(Calendar.DAY_OF_WEEK);
        String weekStr = "";
        switch (week) {
            case 1:
                weekStr = "星期日";
                break;
            case 2:
                weekStr = "星期一";
                break;
            case 3:
                weekStr = "星期二";
                break;
            case 4:
                weekStr = "星期三";
                break;
            case 5:
                weekStr = "星期四";
                break;
            case 6:
                weekStr = "星期五";
                break;
            case 7:
                weekStr = "星期六";
                break;
            default:
                break;
        }

        return new String[] {
                timeFormat.format(calendar.getTime()),
                weekStr + "\n" + dateFormat.format(calendar.getTime())
        };
    }

}
