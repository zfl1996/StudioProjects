package com.ads.abcbank.utils;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class ZipUtil {
    public static final String TAG = "zip file";

    /**
     * 在/data/data/下创建一个res文件夹，存放4个文件
     */
    public static void copyDbFile(Context context, String fileName) {
        InputStream in = null;
        FileOutputStream out = null;
        String path = "/data/data/" + context.getPackageName() + "/file/res/";
        File file = new File(path + fileName);

        //创建文件夹
        File filePath = new File(path);
        if (!filePath.exists()) {
            filePath.mkdirs();
        }

        if (file.exists()) {
            return;
        }

        try {
            in = context.getAssets().open(fileName); // 从assets目录下复制
            out = new FileOutputStream(file);
            int length;
            byte[] buf = new byte[1024];
            while ((length = in.read(buf)) != -1) {
                out.write(buf, 0, length);
            }
            out.flush();
        } catch (Exception e) {
            Logger.e(e.toString());
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    public static void compressFile(View view) {
        //压缩到此处
        String path = "/data/data/" + view.getContext().getPackageName() + "/file/";
        //要压缩的文件的路径
        File file = new File(path + "res.zip");

        //创建文件夹
        File filePath = new File(path);
        if (!filePath.exists()) {
            filePath.mkdirs();
        }

        if (file.exists()) {
            return;
        }

        try {
            ZipOutputStream zipOutputStream = new ZipOutputStream(new CheckedOutputStream(new FileOutputStream(file), new CRC32()));
            zip(zipOutputStream, "res", new File(path + "res/"));
            zipOutputStream.flush();
            zipOutputStream.close();
        } catch (Exception e) {
            Logger.e(e.toString());
        }
        Toast.makeText(view.getContext(), "压缩完成", Toast.LENGTH_SHORT).show();
    }

    public static void unZip(View view) {
        String path = "/data/data/" + view.getContext().getPackageName() + "/file/unzip/";
        File file = new File("/data/data/" + view.getContext().getPackageName() + "/file/res.zip");
        try {
            upZipFile(file, path);
        } catch (IOException e) {
            Logger.e(e.toString());
        }
        Toast.makeText(view.getContext(), "解压完成", Toast.LENGTH_SHORT).show();
    }

    public static void zip(ZipOutputStream zipOutputStream, String name, File fileSrc) throws IOException {

        if (fileSrc.isDirectory()) {
            System.out.println("需要压缩的地址是目录");
            File[] files = fileSrc.listFiles();

            name = name + "/";
            zipOutputStream.putNextEntry(new ZipEntry(name));  // 建一个文件夹
            System.out.println("目录名: " + name);

            for (File f : files) {
                zip(zipOutputStream, name + f.getName(), f);
                System.out.println("目录: " + name + f.getName());
            }

        } else {
            System.out.println("需要压缩的地址是文件");
            zipOutputStream.putNextEntry(new ZipEntry(name));
            System.out.println("文件名: " + name);
            FileInputStream input = new FileInputStream(fileSrc);
            System.out.println("文件路径: " + fileSrc);
            byte[] buf = new byte[1024];
            int len;

            while ((len = input.read(buf)) != -1) {
                zipOutputStream.write(buf, 0, len);
            }

            zipOutputStream.flush();
            input.close();
        }
    }

    /**
     * 解压缩
     * 将zipFile文件解压到folderPath目录下.
     *
     * @param zipFile    zip文件
     * @param folderPath 解压到的地址
     * @throws IOException
     */
    public static void upZipFile(File zipFile, String folderPath) throws IOException {
        ZipFile zfile = new ZipFile(zipFile);
        Enumeration zList = zfile.entries();
        ZipEntry ze;
        byte[] buf = new byte[1024];
        while (zList.hasMoreElements()) {
            ze = (ZipEntry) zList.nextElement();
            if (ze.isDirectory()) {
                Logger.e(TAG, "ze.getName() = " + ze.getName());
                String dirstr = folderPath + ze.getName();
                dirstr = new String(dirstr.getBytes("8859_1"), "GB2312");
                Logger.e(TAG, "str = " + dirstr);
                File f = new File(dirstr);
                f.mkdir();
                continue;
            }
            Logger.e(TAG, "ze.getName() = " + ze.getName());
            OutputStream os = new BufferedOutputStream(new FileOutputStream(getRealFileName(folderPath, ze.getName())));
            InputStream is = new BufferedInputStream(zfile.getInputStream(ze));
            int readLen;
            while ((readLen = is.read(buf, 0, 1024)) != -1) {
                os.write(buf, 0, readLen);
            }
            is.close();
            os.close();
        }
        zfile.close();
    }

    /**
     * 给定根目录，返回一个相对路径所对应的实际文件名.
     *
     * @param baseDir     指定根目录
     * @param absFileName 相对路径名，来自于ZipEntry中的name
     * @return java.io.File 实际的文件
     */
    public static File getRealFileName(String baseDir, String absFileName) {
        String[] dirs = absFileName.split("/");
        File ret = new File(baseDir);
        String substr;
        if (dirs.length > 1) {
            for (int i = 0; i < dirs.length - 1; i++) {
                substr = dirs[i];
                try {
                    substr = new String(substr.getBytes("8859_1"), "GB2312");
                } catch (UnsupportedEncodingException e) {
                    Logger.e(e.toString());
                }
                ret = new File(ret, substr);

            }
            Logger.e(TAG, "1ret = " + ret);
            if (!ret.exists()) {
                ret.mkdirs();
            }
            substr = dirs[dirs.length - 1];
            try {
                substr = new String(substr.getBytes("8859_1"), "GB2312");
                Logger.e(TAG, "substr = " + substr);
            } catch (UnsupportedEncodingException e) {
                Logger.e(e.toString());
            }
            ret = new File(ret, substr);
            Logger.e(TAG, "2ret = " + ret);
            return ret;
        }
        return ret;
    }
}
