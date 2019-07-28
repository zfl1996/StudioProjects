package com.ads.abcbank.xx.utils.net;

import android.support.annotation.NonNull;

public class ResInfo implements Comparable<ResInfo> {
    public String url;
    public String type;

    /*
    * 优先级，越高级别越高
    * 0 默认
    * */
    public Integer priority = 0;

    /*
    *   0：失败；1：完成；2：停止；3：等待；
        4：正在执行；5：预处理；6：预处理完成；7：取消任务
        -99: 初始
    * */
    public int status;


    @Override
    public int compareTo(@NonNull ResInfo resInfo) {
        return this.priority.compareTo(resInfo.priority);
    }
}
