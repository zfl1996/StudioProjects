/**
 * Json.java
 * 乐团
 * Created by 董润宏 on 2014-9-12
 */
package com.ads.abcbank.utils;

public class HTTPContants {
    public static final String SERVICE = "http://omip.abc/ibcs/player/v1";

    public static final String CODE_REGISTER = SERVICE + "/register.json";//设备入网
    public static final String CODE_INIT = SERVICE + "/init.json";//启动检查
    public static final String CODE_PLAYLIST = SERVICE + "/playlist.json";//播放列表
    public static final String CODE_CMDPOLL = SERVICE + "/cmdpoll.json";//修改密码
    public static final String CODE_CMDRESULT = SERVICE + "/cmdresult.json";//忘记密码
    public static final String CODE_PRESET = SERVICE + "/preset.json";//用户信息查询
}