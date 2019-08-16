package com.ads.abcbank.xx.utils;

public class Constants {
    public final static Boolean SYS_CONFIG_IS_CHECKMD5 = false;

    public final static String ROOT_FILE_NAME = "ibcsPlayerData";
    public final static String ROOT_TEMPFILE_NAME = "TempplData";

    public final static String COMPRESS_FORMAT = ".png";

    public static final String PDF_CACHE_FILENAME = "PDF_Cache_FileName";

    public static final int SLIDER_RATE_DISPLAY_MODE_D = 0x100;
    public static final int SLIDER_RATE_DISPLAY_MODE_Q = 0x101;

    public static final int SLIDER_STATUS_CODE_OK = 0x200;
    public static final int SLIDER_STATUS_CODE_INIT = 0x201;
    public static final int SLIDER_STATUS_CODE_UPDATE = 0x202;
    public static final int SLIDER_STATUS_CODE_WELCOME_DEFAULT = 0x203;
    public static final int SLIDER_STATUS_CODE_RATE_LOADED = 0x204;
    public static final int SLIDER_STATUS_CODE_PROGRESS = 0x205;
    public static final int SLIDER_STATUS_CODE_DOWNSUCC = 0x206;
    public static final int SLIDER_STATUS_CODE_PDF_CACHED = 0x207;
    public static final int SLIDER_STATUS_CODE_WELCOME_LOADED = 0x208;
    public static final int SLIDER_STATUS_CODE_PLAYLIST_LOADED = 0x209;

    public static final int SLIDER_HOLDER_IMAGE = 0;
    public static final int SLIDER_HOLDER_PDF = 1;
    public static final int SLIDER_HOLDER_VIDEO = 2;
    public static final int SLIDER_HOLDER_TEXT = 3;
    public static final int SLIDER_HOLDER_RATE_SAVE = 4;
    public static final int SLIDER_HOLDER_RATE_LOAN = 5;
    public static final int SLIDER_HOLDER_RATE_BUY = 6;
    public static final int SLIDER_HOLDER_RATE_SAVE_ITEM = 7;
    public static final int SLIDER_HOLDER_RATE_LOAN_ITEM = 8;
    public static final int SLIDER_HOLDER_RATE_BUY_ITEM = 9;


    public static final int SLIDER_PROGRESS_CODE_PLAYLIST_PRE = 0x0;
    public static final int SLIDER_PROGRESS_CODE_FILE = 0x1;
    public static final int SLIDER_PROGRESS_CODE_PRESET_PRE = 0x2;
    public static final int SLIDER_PROGRESS_CODE_PRESET_OK = 0x3;
    public static final int SLIDER_PROGRESS_CODE_PLAYLIST_OK = 0x4;
    public static final int SLIDER_PROGRESS_CODE_PLAYLIST_EMPTY = 0x5;

    public static final int NET_MANAGER_INIT = 0x300;
    public static final int NET_MANAGER_DATA_FINISHNOTIFY = 0x301;
    public static final int NET_MANAGER_DATA_CMDPOLL = 0x0;
    public static final int NET_MANAGER_DATA_PLAYLIST = 0x1;
    public static final int NET_MANAGER_DATA_PRESET = 0x2;

    public static final String MM_STATUS_FINISHED_TASKID = "finished_taskid";
    public static final String MM_STATUS_KEY_PLAYLIST_INIT = "playlist_init";
    public static final String MM_STATUS_KEY_PRESET_INIT = "preset_init";
    public static final String MM_STATUS_KEY_PLAYLIST_LOADED = "playlist_status";
    public static final String MM_STATUS_KEY_PRESET_LOADED = "preset_status";
    public static final String MM_STATUS_KEY_WELCOME_LOADED = "welcome_status";
    public static final String MM_STATUS_KEY_IS_INTEGRATION_PRESET = "integrationPresetData";
    public static final String MM_STATUS_KEY_PLAYLIST_DOWNLOADED = "empty_playlist";

    public static final String DOWNLOADER_KEY_TASK = "DownloadItem";

}
