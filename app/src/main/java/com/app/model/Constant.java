package com.app.model;

import com.app.sip.SipInfo;

/**
 * Created by acer on 2016/9/5.
 */
public class Constant {
    //HomeActivity
    public static final int HOME = 0;
    public static final int SHOP = 1;
    public static final int COMMUNITY = 2;
    public static final int MESSAGE = 3;
    public static final int PERSON = 4;

    public static final String FTP_CONNECT_SUCCESSS = "ftp连接成功";
    public static final String FTP_CONNECT_FAIL = "ftp连接失败";
    public static final String FTP_DISCONNECT_SUCCESS = "ftp断开连接";
    public static final String FTP_FILE_NOTEXISTS = "ftp上文件不存在";
    public static final String FTP_FILE_EXISTS = "ftp上文件存在";
    public static final String FTP_UPLOAD_SUCCESS = "ftp文件上传成功";
    public static final String FTP_UPLOAD_FAIL = "ftp文件上传失败";
    public static final String FTP_UPLOAD_LOADING = "ftp文件正在上传";
    public static final String FTP_DOWN_LOADING = "ftp文件正在下载";
    public static final String FTP_DOWN_SUCCESS = "ftp文件下载成功";
    public static final String FTP_DOWN_FAIL = "ftp文件下载失败";
    public static final String FTP_DELETEFILE_SUCCESS = "ftp文件删除成功";
    public static final String FTP_DELETEFILE_FAIL = "ftp文件删除失败";
    public static final String FTP_MIKEDIR_SUCCESS = "ftp路径创建成功";
    public static final String FTP_MIKEDIR_FAIL = "ftp路径创建失败";

    public static final String FORMT = "http://" + SipInfo.serverIp + ":8000/xiaoyupeihu/public/index.php/";
    public static final String URL_ChPhoneNum = FORMT + "users/updateUserPhone";//改绑定账号
    public static final String URL_Bind = FORMT + "devs/bindDev";//绑定设备
    public static final String URL_joinGroup = FORMT + "groups/joinGroup";//加入群组
    public static final String URL_InquireBind = FORMT + "devs/isDevBinded";//查询是否绑定设备
    public static final String URL_getNewComments=FORMT+"posts/getNewComments";
    public static final String URL_countNewComments=FORMT+"posts/countNewComments";
    public static final String URL_getNewLikes=FORMT+"posts/getNewLikes";
    public static final String URL_countNewLikes=FORMT+"posts/countNewLikes";
    public static String groupid;
    public static String groupid1;
    public static String groupid2;
    public static String groupid3;
    public static String devid1;
    public static String devid2;
    public static String devid3;
    public static String currentfriendavatar;
    public static String currentfriendid;
    public static String appdevid1;
    public static String appdevid2;
    public static String appdevid3;

}
