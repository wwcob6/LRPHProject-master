package com.app.sip;

import android.app.NotificationManager;
import android.net.sip.SipAudioCall;
import android.os.Handler;

import com.app.db.MyDatabaseHelper;
import com.app.model.AddressItem;
import com.app.model.App;
import com.app.model.Device;
import com.app.model.Device1;
import com.app.model.Familymember;
import com.app.model.Friend;
import com.app.model.LastestMsg;
import com.app.model.MailInfo;
import com.app.model.MessageNotify;
import com.app.model.TaskInfo;
import com.app.service.SipService;
import com.app.ui.MakeSmallVideo;
import com.app.ui.MovieRecord;
import com.app.ui.MyCamera;

import org.zoolu.sip.address.NameAddress;
import org.zoolu.sip.message.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Author chzjy
 * Date 2016/12/19.
 */

public class SipInfo {
    //心跳线程开启与否
    public static boolean running = false;
    //服务器名
    public static String SERVER_NAME = "rvsup";
    //服务器ID
    public static String SERVER_ID = "330100000010000090";
    //用户端口
    public static int SERVER_PORT_USER = 6061;
    //设备端口
    public static int SERVER_PORT_DEV = 6060;
    //用户注册获取用户ID使用
    public static String REGISTER_ID = "330100000010000190";
    //服务器ip
    public static String serverIp = "sip.qinqingonline.com";
    public static String serverIptest = "sip.qinqingonline.com";
    public static String serverIptest1 = "118.31.71.150";

    //验证码
    public static String code;
    //用户账号
    public static String userAccount;
    public static String userAccount2;
    //用户密码
    public static String passWord;
    public static String passWord2;
    //设备id
    public static String devId;

    //设备用户id
    public static String paduserid;

    public static String paddevId;
    //用户id
    public static String userId;
    //用户真实姓名
    public static String userRealname;
    //中心号码
    public static String centerPhoneNumber;
    //网络是否连接
    public static boolean isNetworkConnected;
    //用户账号是否存在
    public static boolean isAccountExist;
    //密码错误标志
    public static boolean passwordError;
    //用户登录状态标志
    public static boolean userLogined;
    //设备登录状态标志
    public static boolean devLogined;
    //登录超时标志
    public static boolean loginTimeout;
    //设备登录超时标志
    public static boolean dev_loginTimeout;
    //sip消息From地址
    public static NameAddress user_from;
    public static NameAddress user_from2;
    //sip消息To地址
    public static NameAddress user_to;
    public static NameAddress user_to2;
    //sip消息(聊天消息)To好友地址
    public static NameAddress toUser;
    //sip消息(设备)To地址
    public static NameAddress dev_to;
    //sip消息(设备)From地址
    public static NameAddress dev_from;
    //sip消息(用户)请求视频设备地址
    public static NameAddress toDev;
    //用户sip对象
    public static SipUser sipUser;
    //设备sip对象
    public static SipDev sipDev;
    //用户心跳保活
    public static KeepAlive keepUserAlive;
    //设备心跳保活
    public static KeepAlive keepDevAlive;
    //用户IP电话号码
    public static String userPhoneNumber;
    //一次加密种子
    public static String seed;
    //二次加密种子
    public static String salt;
    //用户心跳回复
    public static boolean user_heartbeatResponse;
    //设备心跳回复
    public static boolean dev_heartbeatResponse;
    //好友列表
    public static ArrayList<Friend> friends = new ArrayList<>();
    //好友数量
    public static int friendCount;
    //分组列表
    public static HashMap<String, List<Friend>> friendList = new HashMap<String, List<Friend>>();
    //设备名
    public static String devName;
    //设备数量
    public static int devCount;
    //设备列表
    public static ArrayList<Device> devList = new ArrayList<>();

    //地址列表
    public static List<AddressItem> addressList = new ArrayList<>();

    public static ArrayList<Friend> friendsList = new ArrayList<>();
    //设备列表
    public static ArrayList<Device1> devList1 = new ArrayList<>();

    //消息列表
    public static ArrayList<MessageNotify> messageNotifys=new ArrayList<>();
    //sip电话服务对象
    public static SipService sipService;
    //上一个电话对象
    public static SipAudioCall lastCall;

    public static Handler Phone = new Handler();
    //第三方应用
    public static ArrayList<App> applist = new ArrayList<>();
    //视频信息请求回复
    public static boolean queryResponse;
    //视频请求回复
    public static boolean inviteResponse;
    //视频编码状态
    public static boolean decoding = false;

    //根目录
    public static String localSdCard;
    //新邮件(关于NoteBook)
    public static Handler newMail;
    //群组用户id
    public static String groupid;
    //异地登录
    public static Handler loginReplace;
    //新工单(关于TaskApp)
    public static Handler newTask;
    //最近消息列表
    public static List<LastestMsg> lastestMsgs = new ArrayList<>();
    //
    public static MakeSmallVideo instance;

    public static MyCamera myCamera;

    public static MovieRecord movieRecord;
    //是否允许调用摄像头
    public static boolean flag = true;

    public static int messageCount;

    public static Message msg;

    public static Handler notifymedia;

    public static List<TaskInfo> tasklist = new ArrayList<>();
    //邮件列表
    public static List<MailInfo> maillist = new ArrayList<>();

    public static List<Familymember> farmilymemberList = new ArrayList<>();
    public static MyDatabaseHelper dbHelper;

    public static NotificationManager notificationManager;

    public static boolean Recording = false;

    public static boolean isWaitingFeedback = false;
    public static boolean IsVideoOn=false;
    public static boolean finish=false;
    public static boolean  single=false;
    public static String port;

    public static float  bitErrorRate;
    public static int previewWidth;
    public static int previewHeight;
    public static String phoneType;
    public static boolean isEditor=false;

    //添加收货地址相关
    public static String userAddress;
    public static String detailAddress;
    public static String userName;
    public static String userPhoneNum;
    //编辑的地址在手机列表的位置
    public static int listPosition;
    //编辑的地址在数据库中的position
    public static int addressPosition;
    //是否为默认地址；1-默认、0-非默认
    public static int  isDefault;

    public static int width;
    public static int height;

    //是否是验证码登录，在修改密码的时候作区别
    public static boolean  isVericodeLogin=false;
    //新收到的评论数目
    public static int commentsItems=0;
    public static int addLikesItems=0;
    public static String month;
}
