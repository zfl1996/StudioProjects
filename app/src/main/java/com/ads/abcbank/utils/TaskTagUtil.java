/*
 * Copyright (c) 2017 LingoChamp Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ads.abcbank.utils;


import com.ads.abcbank.bean.DownloadBean;
import com.liulishuo.okdownload.DownloadTask;

public class TaskTagUtil {
    private static final int KEY_STATUS = 0;
    private static final int KEY_OFFSET = 1;
    private static final int KEY_TOTAL = 2;
    private static final int KEY_TASK_NAME = 3;
    private static final int KEY_PRIORITY = 4;
    private static final int KEY_DOWNLOAD_ID = 5;
    private static final int KEY_NAME = 6;
    private static final int KEY_PLAYDATE = 7;
    private static final int KEY_STOPDATE = 8;
    private static final int KEY_MD5 = 9;
    private static final int KEY_DOWNLOAD_BEAN_INDEX = 10;
    private static final int KEY_DOWNLOAD_BEAN = 11;

    public static DownloadBean getDownloadBean(DownloadTask task) {
        final Object bean = task.getTag(KEY_DOWNLOAD_BEAN);
        return bean != null ? (DownloadBean) bean : null;
    }

    public static int getDownloadBeanIndex(DownloadTask task) {
        final Object beanIndex = task.getTag(KEY_DOWNLOAD_BEAN_INDEX);
        return beanIndex != null ? (int) beanIndex : 0;
    }

    public static void saveDownloadBean(DownloadTask task, DownloadBean downloadBean) {
        task.addTag(KEY_DOWNLOAD_BEAN, downloadBean);
    }

    public static String getDownloadId(DownloadTask task) {
        final Object downloadId = task.getTag(KEY_DOWNLOAD_ID);
        return downloadId != null ? (String) downloadId : null;
    }

    public static String getName(DownloadTask task) {
        final Object name = task.getTag(KEY_NAME);
        return name != null ? (String) name : null;
    }

    public static String getPlayDate(DownloadTask task) {
        final Object playDate = task.getTag(KEY_PLAYDATE);
        return playDate != null ? (String) playDate : null;
    }

    public static String getStopDate(DownloadTask task) {
        final Object stopDate = task.getTag(KEY_STOPDATE);
        return stopDate != null ? (String) stopDate : null;
    }

    public static String getMd5(DownloadTask task) {
        final Object md5 = task.getTag(KEY_MD5);
        return md5 != null ? (String) md5 : null;
    }

    public static void saveDownloadId(DownloadTask task, String downloadId) {
        task.addTag(KEY_DOWNLOAD_ID, downloadId);
    }

    public static void saveDownloadBeanIndex(DownloadTask task, int downloadBeanIndex) {
        task.addTag(KEY_DOWNLOAD_BEAN_INDEX, downloadBeanIndex);
    }

    public static void saveName(DownloadTask task, String name) {
        task.addTag(KEY_NAME, name);
    }

    public static void savePlayDate(DownloadTask task, String playDate) {
        task.addTag(KEY_PLAYDATE, playDate);
    }

    public static void saveStopDate(DownloadTask task, String stopDate) {
        task.addTag(KEY_STOPDATE, stopDate);
    }

    public static void saveMD5(DownloadTask task, String md5) {
        task.addTag(KEY_MD5, md5);
    }

    public static void saveStatus(DownloadTask task, String status) {
        task.addTag(KEY_STATUS, status);
    }

    public static String getStatus(DownloadTask task) {
        final Object status = task.getTag(KEY_STATUS);
        return status != null ? (String) status : null;
    }

    public static void saveOffset(DownloadTask task, long offset) {
        task.addTag(KEY_OFFSET, offset);
    }

    public static long getOffset(DownloadTask task) {
        final Object offset = task.getTag(KEY_OFFSET);
        return offset != null ? (long) offset : 0;
    }

    public static void saveTotal(DownloadTask task, long total) {
        task.addTag(KEY_TOTAL, total);
    }

    public static long getTotal(DownloadTask task) {
        final Object total = task.getTag(KEY_TOTAL);
        return total != null ? (long) total : 0;
    }

    public static void saveTaskName(DownloadTask task, String name) {
        task.addTag(KEY_TASK_NAME, name);
    }

    public static String getTaskName(DownloadTask task) {
        final Object taskName = task.getTag(KEY_TASK_NAME);
        return taskName != null ? (String) taskName : null;
    }

    public static void savePriority(DownloadTask task, int priority) {
        task.addTag(KEY_PRIORITY, priority);
    }

    public static int getPriority(DownloadTask task) {
        final Object priority = task.getTag(KEY_PRIORITY);
        return priority != null ? (int) priority : 0;
    }

    public static void clearProceedTask(DownloadTask task) {
        task.removeTag(KEY_STATUS);
        task.removeTag(KEY_OFFSET);
        task.removeTag(KEY_TOTAL);
        task.removeTag(KEY_TASK_NAME);
        task.removeTag(KEY_PRIORITY);
        task.removeTag(KEY_DOWNLOAD_ID);
        task.removeTag(KEY_NAME);
        task.removeTag(KEY_PLAYDATE);
        task.removeTag(KEY_STOPDATE);
        task.removeTag(KEY_MD5);
    }
}