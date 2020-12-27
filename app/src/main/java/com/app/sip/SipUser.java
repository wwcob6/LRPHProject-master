package com.app.sip;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.PowerManager;
import android.util.Log;

import com.app.R;
import com.app.db.DatabaseInfo;
import com.app.ftp.Ftp;
import com.app.ftp.FtpListener;
import com.app.groupvoice.GroupInfo;
import com.app.model.App;
import com.app.model.Constant;
import com.app.model.Device;
import com.app.model.MailInfo;
import com.app.model.MessageEvent;
import com.app.model.Msg;
import com.app.model.MyFile;
import com.app.video.VideoInfo;
import com.punuo.sys.app.sercet.SHA1;

import org.greenrobot.eventbus.EventBus;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.zoolu.sip.address.NameAddress;
import org.zoolu.sip.address.SipURL;
import org.zoolu.sip.message.Message;
import org.zoolu.sip.provider.SipProvider;
import org.zoolu.sip.provider.Transport;
import org.zoolu.sip.provider.TransportConnId;

import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;



/**
 * Author chzjy
 * Date 2016/12/19.
 */

public class SipUser extends SipProvider {
    MediaPlayer music;
    private Context context;
    public static String TAG = "SipUser";
    public static String[] PROTOCOLS = {"udp"};
    //线程池
    private ExecutorService pool = Executors.newFixedThreadPool(3);
    //用户或设备上线监听
    private LoginNotifyListener loginNotifyListener;
    //密码修改监听
    private ChangePWDListener changePWDListener;
    //消息监听
    private MessageListener messageListener;
    //总监听
    private TotalListener totalListener;
    //主界面底部消息数量监听
    private BottomListener bottomListener;
    //app图标路径
    private String sdPath;
    private ClusterNotifyListener clusterNotifyListener;

//    private QinliaoUpdateListener qinliaoUpdateListener;




    public SipUser(String via_addr, int host_port, Context context) {
        super(via_addr, host_port, PROTOCOLS, null);
        this.context = context;
        sdPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/PNS9/download/icon/";

    }

    public TransportConnId sendMessage(Message msg) {
        return sendMessage(msg, SipInfo.serverIp, SipInfo.SERVER_PORT_USER);
    }

    public TransportConnId sendMessage(final Message msg, final String destAddr, final int destPort) {
        Log.i(TAG, "<----------send sip message---------->");
        Log.i(TAG, msg.toString());
        TransportConnId id = null;
        try {
                id = pool.submit(new Callable<TransportConnId>() {
                    public TransportConnId call() {
                        return sendMessage(msg, "udp", destAddr, destPort, 0);
                    }
                }).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }catch (RejectedExecutionException e){

        }
        return id;
    }

    //结束线程池
    public void shutdown(){
        pool.shutdown();
    }
    public synchronized void onReceivedMessage(Transport transport, Message msg) {
        Log.i(TAG, "<----------received sip message---------->");
        Log.i(TAG, msg.toString());
        //sip消息来源的RemoteProt,6060为设备,6061为用户
        int port = msg.getRemotePort();
        if (port == SipInfo.SERVER_PORT_USER) {
            Log.i(TAG, "PORT = " + port);
            if (msg.isRequest()) {// 请求消息
                requestParse(msg);
            } else { // 响应消息
                int code = msg.getStatusLine().getCode();
                switch (code) {
                    case 200:
                        responseParse(msg);
                        break;
                    case 401://密码错误
                        SipInfo.loginTimeout = false;
                        SipInfo.isAccountExist = true;
                        SipInfo.passwordError = true;
                        break;
                    case 402://账号不存在
                        SipInfo.passwordError = false;
                        SipInfo.loginTimeout = false;
                        SipInfo.isAccountExist = false;
                        break;
                }

            }
        }
    }

    //请求解析
    private boolean requestParse(final Message msg) {
        String body = msg.getBody();
        if (body != null) {
            StringReader sr = new StringReader(body);
            InputSource is = new InputSource(sr);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder;
            Document document;
            try {
                builder = factory.newDocumentBuilder();
                document = builder.parse(is);
                Element root = document.getDocumentElement();
                final String type = root.getTagName();
                switch (type) {
                    case "friends":
//                        NodeList friends = root.getElementsByTagName("friend");
//                        for (int i = 0; i < friends.getLength(); i++) {
//                            Friend friend = new Friend();
//                            Element friendElement = (Element) friends.item(i);
//                            Element friendIdElement = (Element) friendElement.getElementsByTagName("userid").item(0);
//                            Element usernameElement = (Element) friendElement.getElementsByTagName("username").item(0);
//                            Element phoneElement = (Element) friendElement.getElementsByTagName("phone_num").item(0);
//                            Element liveElement = (Element) friendElement.getElementsByTagName("live").item(0);
//                            Element telElement = (Element) friendElement.getElementsByTagName("tel_num").item(0);
//                            Element realnamement = (Element) friendElement.getElementsByTagName("real_name").item(0);
//                            Element unitment = (Element) friendElement.getElementsByTagName("unit").item(0);
//                            friend.setUserId(friendIdElement.getFirstChild().getNodeValue());
//                            friend.setUsername(usernameElement.getFirstChild().getNodeValue());
//                            friend.setPhoneNum(phoneElement.getFirstChild().getNodeValue());
//                            friend.setRealName(realnamement.getFirstChild().getNodeValue());
//                            String tel = telElement.getFirstChild().getNodeValue();
//                            if (!tel.equals("None")) {
//                                friend.setTelNum(tel);
//                            }
//                            friend.setUnit(unitment.getFirstChild().getNodeValue());
//                            if (liveElement.getFirstChild().getNodeValue().equals("1")) {
//                                friend.setLive(true);
//                            } else {
//                                friend.setLive(false);
//                            }
//                            if (!SipInfo.friendList.containsKey(friend.getUnit())) {
//                                SipInfo.friendList.put(friend.getUnit(), new ArrayList<Friend>());
//                            }
//                            SipInfo.friends.add(friend);
//                            SipInfo.friendList.get(friend.getUnit()).add(friend);
//                        }
//                        Message message = SipMessageFactory.createResponse(msg, 200, "OK", "");
//                        SipInfo.sipUser.sendMessage(message);
//                        if (loginNotifyListener != null) {
//                            loginNotifyListener.onUserNotify();
//                        }
                        return true;

                    case "dev_notify"://设备列表
                        Element devsElement = (Element) root.getElementsByTagName("devs").item(0);
                        Element loginElement = (Element) root.getElementsByTagName("login").item(0);
                        if (devsElement != null) {
                            NodeList devs = devsElement.getElementsByTagName("dev");
                            for (int i = 0; i < devs.getLength(); i++) {
                                Device device = new Device();
                                Element devElement = (Element) devs.item(i);
                                Element devIdElement = (Element) devElement.getElementsByTagName("devid").item(0);
                                Element nameElement = (Element) devElement.getElementsByTagName("name").item(0);
                                Element phoneElement = (Element) devElement.getElementsByTagName("phone").item(0);
                                Element devTypeElement = (Element) devElement.getElementsByTagName("dev_type").item(0);
                                Element liveElement = (Element) devElement.getElementsByTagName("live").item(0);
                                device.setDevId(devIdElement.getFirstChild().getNodeValue());
                                device.setName(nameElement.getFirstChild().getNodeValue());
                                device.setPhoneNum(phoneElement.getFirstChild().getNodeValue());
                                device.setDevType(devTypeElement.getFirstChild().getNodeValue());
                                if (liveElement.getFirstChild().getNodeValue().equals("1")) {
                                    device.setLive(true);
                                } else {
                                    device.setLive(false);
                                }
                                if (!device.getDevId().equals(SipInfo.devId)) {
                                    SipInfo.devList.add(device);
                                }
                            }
                            if (loginNotifyListener != null) {
                                loginNotifyListener.onDevNotify();
                            }
                            Log.i(TAG, "当前设备数：" + SipInfo.devList.size());
                            SipInfo.sipUser.sendMessage(SipMessageFactory.createResponse(msg, 200, "OK", ""));
                            return true;
                        } else {
                            Element devIdElement = (Element) loginElement.getElementsByTagName("devid").item(0);
                            Element liveElement = (Element) loginElement.getElementsByTagName("live").item(0);
                            String devid = devIdElement.getFirstChild().getNodeValue();
                            if (!devid.equals(SipInfo.devId)) {
                                Device device = new Device();
                                device.setDevId(devid);
                                int index = SipInfo.devList.indexOf(device);
                                if (index != -1) {
                                    if (liveElement.getFirstChild().getNodeValue().equals("1")) {
                                        SipInfo.devList.get(index).setLive(true);
                                    } else {
                                        SipInfo.devList.get(index).setLive(false);
                                    }
                                }
                                if (loginNotifyListener != null) {
                                    loginNotifyListener.onDevNotify();
                                }
                            }
                            return true;
                        }




                        //亲聊新成员上线
                    case"user_online":
                        Element liveElement=(Element)root.getElementsByTagName("live").item(0);
                        String islive=liveElement.getFirstChild().getNodeValue();
                        if(islive.equals("True")){
                            NodeList users=root.getElementsByTagName("login");
                            list.clear();
                            for (int i = 0; i < users.getLength(); i++) {
                                Log.i(TAG, "111");
//                                Cluster cluster = new Cluster();
                                Element logi1Element = (Element) users.item(i);
                                Element userid1Element = (Element) logi1Element.getElementsByTagName("userid").item(0);
                                list.add(userid1Element.getFirstChild().getNodeValue());
                                Log.i(TAG, "qinliao" + list.get(i));
                            }
                            qinliaoUpdateListener.stausOnUpdate();
                        }else
                            if(islive.equals("False")) {
                                NodeList users = root.getElementsByTagName("login");
                                list.clear();
                                for (int i = 0; i < users.getLength(); i++) {
                                    Log.i(TAG, "111");
//                                Cluster cluster = new Cluster();
                                    Element logi1Element = (Element) users.item(i);
                                    Element userid1Element = (Element) logi1Element.getElementsByTagName("userid").item(0);
                                    list.add(userid1Element.getFirstChild().getNodeValue());
                                    Log.i(TAG, "qinliao" + list.get(i));
                                }
                                qinliaoUpdateListener.stausOffUpdate();
                            }


                        break;



                  //服务器回复亲聊在线成员userid
                    case "cluster_users":
                            Log.i("qqq","111");
                            NodeList  clusters = root.getElementsByTagName("cluster_user");
                            Log.d(TAG, "requestParse111: " + clusters.getLength());
                            list.clear();
                            for (int i = 0; i < clusters.getLength(); i++) {
                                Log.i(TAG, "111");
//                                Cluster cluster = new Cluster();
                                Element clusterElement = (Element) clusters.item(i);
                                Element nameElement = (Element) clusterElement.getElementsByTagName("name").item(0);
                                list.add(nameElement.getFirstChild().getNodeValue());
                                Log.i(TAG, "qinliao" + list.get(i));
                            }
                            list.add(SipInfo.userId);
//                                if (nameElement.getFirstChild().getNodeValue().equals("超级用户")||nameElement.getFirstChild().getNodeValue().equals("None")) {
//                                    continue;
//                                }
////                                cluster.setName(nameElement.getFirstChild().getNodeValue());
////                                SipInfo.cacheClusters.add(cluster);
////                                Log.d(TAG, "requestParse: " + "添加完毕" + SipInfo.cacheClusters.size());
//                            }

//                            Element f = (Element) root.getElementsByTagName("finish").item(0);
//                            int isfinish = Integer.parseInt(f.getFirstChild().getNodeValue());
//                            if (isfinish == 1) {
//                                if (clusterNotifyListener != null ) {
//                                    SipInfo.finish = true;
////                                    Collections.sort(SipInfo.cacheClusters);
//                                    Log.d(TAG, "requestParse: " + "更新");
//                                    clusterNotifyListener.onNotify();
//                                    SipInfo.sipUser.sendMessage(SipMessageFactory.createResponse(msg, 200, "OK", ""));
//                                }else{
////                                    SipInfo.cacheClusters.clear();
////                                    org.zoolu.sip.message.Message query_channel = SipMessageFactory.createNotifyRequest(
////                                            SipInfo.sipUser, SipInfo.user_to, SipInfo.user_from, BodyFactory.createQueryClusterIdBody(SipInfo.userId));
////                                    SipInfo.sipUser.sendMessage(query_channel);
//                                }
//                            } else {
//                                SipInfo.finish = false;
//                                SipInfo.sipUser.sendMessage(SipMessageFactory.createResponse(msg, 200, "OK", ""));

////                            return true;

                        qinliaoUpdateListener.stausOnUpdate();
                        break;


                    case "alarm":{
                        Log.i("maomaomao","111");
                        Element useridElement = (Element) root.getElementsByTagName("userId").item(0);

                        final String userid = useridElement.getFirstChild().getNodeValue();
                        SipURL remote = new SipURL(userid, SipInfo.serverIp, SipInfo.SERVER_PORT_USER);
                        SipInfo.toUser = new NameAddress(userid, remote);
                        SipInfo.sipUser.sendMessage(SipMessageFactory.createNotifyRequest(SipInfo.sipUser, SipInfo.toUser
                                , SipInfo.user_from, BodyFactory.createalarmResBody(200,String.valueOf(System.currentTimeMillis()))));
                        EventBus.getDefault().post(new MessageEvent("警报"));
                        music = MediaPlayer.create(context, R.raw.alarm);
                        music.start();
                        return true;



                    }
                    case "message": {
                        Element idElement = (Element) root.getElementsByTagName("id").item(0);
                        Element fromElement = (Element) root.getElementsByTagName("from").item(0);
                        Element toElement = (Element) root.getElementsByTagName("to").item(0);
                        Element contentElement = (Element) root.getElementsByTagName("content").item(0);
                        Element timeElement = (Element) root.getElementsByTagName("time").item(0);
                        Element typeElement = (Element) root.getElementsByTagName("type").item(0);
                        final String id = idElement.getFirstChild().getNodeValue();
                        final String content = contentElement.getFirstChild().getNodeValue();
                        final int time = Integer.parseInt(timeElement.getFirstChild().getNodeValue());
                        final String fromUserId = fromElement.getFirstChild().getNodeValue();
                        final String toUserId = toElement.getFirstChild().getNodeValue();
                        final int msgtype = Integer.parseInt(typeElement.getFirstChild().getNodeValue());
                        final int isTimeShow;
                        if (time - DatabaseInfo.sqLiteManager.queryLastTime(fromUserId, toUserId) > 300) {
                            isTimeShow = 1;
                        } else {
                            isTimeShow = 0;
                        }
                        if (msgtype != 3) {
                            DatabaseInfo.sqLiteManager.insertMessage(id, fromUserId, toUserId, time,
                                    isTimeShow, content, msgtype, 0);
                        }
                        Message message = SipMessageFactory.createResponse(msg, 200, "OK", BodyFactory.createMessageResBody(id, 200));
                        SipInfo.sipUser.sendMessage(message);
                        if (msgtype == 2) {
                            DatabaseInfo.sqLiteManager.insertLastestMsgFrom(0, fromUserId, "[位置信息]", time);
                        } else if (msgtype != 3) {
                            DatabaseInfo.sqLiteManager.insertLastestMsgFrom(0, fromUserId, content, time);
                        }
                        //消息
                        Msg friendMsg = new Msg();
                        friendMsg.setMsgId(id);
                        friendMsg.setFromUserId(fromUserId);
                        friendMsg.setToUserId(toUserId);
                        friendMsg.setTime(time);
                        friendMsg.setIsTimeShow(isTimeShow);
                        friendMsg.setContent(content);
                        friendMsg.setType(msgtype);
                        if (totalListener != null) {
                            totalListener.onReceivedTotalMessage(friendMsg);
                        }
                        if (bottomListener != null) {
                            bottomListener.onReceivedBottomMessage(friendMsg);
                        }

                        if (messageListener != null) {
                            messageListener.onReceivedMessage(friendMsg);
                        }

//                        if (!mediaPlayer.isPlaying()) {
//                            mediaPlayer.start();
//                        }
                        return true;
                    }
                    case "mail": {
                        Element idElement = (Element) root.getElementsByTagName("id").item(0);
                        Element fromElement = (Element) root.getElementsByTagName("from").item(0);
                        Element toElement = (Element) root.getElementsByTagName("to").item(0);
                        Element contentElement = (Element) root.getElementsByTagName("content").item(0);
                        Element timeElement = (Element) root.getElementsByTagName("time").item(0);
                        Element themeElement = (Element) root.getElementsByTagName("theme").item(0);
                        final String id = idElement.getFirstChild().getNodeValue();
                        final String content = contentElement.getFirstChild().getNodeValue();
                        final int time = Integer.parseInt(timeElement.getFirstChild().getNodeValue());
                        final String fromUserId = fromElement.getFirstChild().getNodeValue();
                        final String toUserId = toElement.getFirstChild().getNodeValue();
                        final String theme = themeElement.getFirstChild().getNodeValue();
                        Message message = SipMessageFactory.createResponse(msg, 200, "OK", BodyFactory.createMailResponseBody(id, 200));
                        SipInfo.sipUser.sendMessage(message);
                        boolean result = DatabaseInfo.sqLiteManager.insertMail(id, fromUserId, toUserId, time, content, theme);
                        //邮件
                        MailInfo mail = new MailInfo();
                        mail.setMailId(id);
                        mail.setFromUserId(fromUserId);
                        mail.setToUserId(toUserId);
                        mail.setTime(time);
                        mail.setContent(content);
                        mail.setTheme(theme);
                        mail.setIsRead(0);
                        if (result) {
                            SipInfo.newMail.sendMessage(new android.os.Message());
                            Intent intent = new Intent("com.app.mail_receive");
                            context.sendBroadcast(intent);
                        }
                        return true;
                    }
                    case "filetransfer": {
                        Element fromElement = (Element) root.getElementsByTagName("from").item(0);
                        Element toElement = (Element) root.getElementsByTagName("to").item(0);
                        Element idElement = (Element) root.getElementsByTagName("id").item(0);
                        Element nameElement = (Element) root.getElementsByTagName("name").item(0);
                        Element fileTypeElement = (Element) root.getElementsByTagName("filetype").item(0);
                        Element timeElement = (Element) root.getElementsByTagName("time").item(0);
                        Element pathElement = (Element) root.getElementsByTagName("path").item(0);
                        Element sizeElement = (Element) root.getElementsByTagName("size").item(0);
                        Element md5Element = (Element) root.getElementsByTagName("md5").item(0);
                        Element typeElement = (Element) root.getElementsByTagName("type").item(0);
                        final String fromUserId = fromElement.getFirstChild().getNodeValue();
                        final String toUserId = toElement.getFirstChild().getNodeValue();
                        final String id = idElement.getFirstChild().getNodeValue();
                        final String name = nameElement.getFirstChild().getNodeValue();
                        final String fileType = fileTypeElement.getFirstChild().getNodeValue();
                        final int time = Integer.parseInt(timeElement.getFirstChild().getNodeValue());
                        final String ftppath = pathElement.getFirstChild().getNodeValue();
                        final String size = sizeElement.getFirstChild().getNodeValue();
                        final String md5 = md5Element.getFirstChild().getNodeValue();
                        final int typeInt = Integer.parseInt(typeElement.getFirstChild().getNodeValue());
                        final int isTimeShow;
                        if (time - DatabaseInfo.sqLiteManager.queryLastTime(fromUserId, toUserId) > 300) {
                            isTimeShow = 1;
                        } else {
                            isTimeShow = 0;
                        }
                        String content = "[文件]";
                        switch (typeInt) {
                            case 0:
                                content = "[语音消息]";
                                break;
                            case 1:
                                content = "[小视频]";
                                break;
                            case 2:
                                content = "[文件]";
                                break;
                            case 3:
                                content = "[图片]";
                                break;
                            case 5:
                                content = "[位置]";
                                break;
                        }
                        if (typeInt != 3) {
                            DatabaseInfo.sqLiteManager.insertMessage(id, fromUserId, toUserId, time,
                                    isTimeShow, content, 1, 0);
                            DatabaseInfo.sqLiteManager.insertFile(id, name, fromUserId, fileType, time, null,
                                    ftppath, Long.parseLong(size), md5, typeInt, 0, 0);
                            Message message = SipMessageFactory.createResponse(msg, 200, "OK",
                                    BodyFactory.createFileTransferResBody(id, 200));
                            SipInfo.sipUser.sendMessage(message);
                            DatabaseInfo.sqLiteManager.insertLastestMsgFrom(0, fromUserId, content, time);
                            final Msg friendMsg = new Msg();
                            friendMsg.setMsgId(id);
                            friendMsg.setFromUserId(fromUserId);
                            friendMsg.setToUserId(toUserId);
                            friendMsg.setTime(time);
                            friendMsg.setIsTimeShow(isTimeShow);
                            friendMsg.setContent(content);
                            friendMsg.setType(1);

                            if (messageListener != null) {
                                messageListener.onReceivedMessage(friendMsg);
                            }
                            if (bottomListener != null) {
                                bottomListener.onReceivedBottomMessage(friendMsg);
                            }
                            if (totalListener != null) {
                                totalListener.onReceivedTotalMessage(friendMsg);
                            }
                        } else {
                            FtpListener listener = new FtpListener() {
                                @Override
                                public void onStateChange(String currentStep) {

                                }

                                @Override
                                public void onUploadProgress(String currentStep, long uploadSize, File targetFile) {

                                }

                                @Override
                                public void onDownLoadProgress(String currentStep, long downProcess, File targetFile) {
                                    if (currentStep.equals(Constant.FTP_DOWN_SUCCESS)) {
                                        DatabaseInfo.sqLiteManager.insertMessage(id, fromUserId, toUserId, time,
                                                isTimeShow, "[图片]", 1, 0);
                                        DatabaseInfo.sqLiteManager.insertFile(id, name, fromUserId, fileType, time, null,
                                                ftppath, Long.parseLong(size), md5, typeInt, 0, 0);
                                        Message message = SipMessageFactory.createResponse(msg, 200, "OK",
                                                BodyFactory.createFileTransferResBody(id, 200));
                                        SipInfo.sipUser.sendMessage(message);
                                        DatabaseInfo.sqLiteManager.insertLastestMsgFrom(0, fromUserId, "[图片]", time);
                                        final Msg friendMsg = new Msg();
                                        friendMsg.setMsgId(id);
                                        friendMsg.setFromUserId(fromUserId);
                                        friendMsg.setToUserId(toUserId);
                                        friendMsg.setTime(time);
                                        friendMsg.setIsTimeShow(isTimeShow);
                                        friendMsg.setContent("[图片]");
                                        friendMsg.setType(1);

                                        if (messageListener != null) {
                                            messageListener.onReceivedMessage(friendMsg);
                                        }
                                        if (bottomListener != null) {
                                            bottomListener.onReceivedBottomMessage(friendMsg);
                                        }
                                        if (totalListener != null) {
                                            totalListener.onReceivedTotalMessage(friendMsg);
                                        }
                                    }
                                }

                                @Override
                                public void onDeleteProgress(String currentStep) {

                                }
                            };
                            final Ftp mFtp = new Ftp(SipInfo.serverIp, 21, "ftpaller", "123456", listener);
                            new Thread() {
                                @Override
                                public void run() {
                                    String thumnailpath = ftppath.replace(context.getString(R.string.Image), context.getString(R.string.Thumbnail));
                                    try {
                                        mFtp.download(thumnailpath, SipInfo.localSdCard + "Files/Camera/Thumbnail/");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }.start();
                        }
                        return true;
                    }
                    case "session_notify":
                        if (SipInfo.instance != null) {
                            SipInfo.instance.finish();
                            SipInfo.instance = null;
                        }
                        if (SipInfo.myCamera != null) {
                            SipInfo.myCamera.finish();
                            SipInfo.myCamera = null;
                        }
                        if (SipInfo.movieRecord != null) {
                            SipInfo.movieRecord.onBackPressed();
                            SipInfo.movieRecord = null;
                        }
                        if (SipInfo.loginReplace != null) {
                            SipInfo.loginReplace.sendEmptyMessage(0x1111);
                        }
                        return true;
                }
            } catch (Exception e) {
                Log.e(TAG, "requestParse: ", e);
            }
        } else {
            Log.i(TAG + "requestParse", "BODY IS NULL");
            return true;
        }
        return false;
    }

    //响应解析
    private boolean responseParse(Message msg) {
        String body = msg.getBody();
        if (body != null) {
            StringReader sr = new StringReader(body);
            InputSource is = new InputSource(sr);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder;
            Document document;
            try {
                builder = factory.newDocumentBuilder();
                document = builder.parse(is);
                Element root = document.getDocumentElement();
                final String type = root.getTagName();
                Element codeElement;
                String code;
                switch (type) {
                    case "negotiate_response":/*注册第一步响应*/
                        Element seedElement = (Element) root.getElementsByTagName("seed").item(0);
                        Element userIdElement = (Element) root.getElementsByTagName("user_id").item(0);
                        if (userIdElement != null) {//如果掉线服务器会当成设备注册第一步
                            Element saltElement = (Element) root.getElementsByTagName("salt").item(0);
                            Element phoneNumElement = (Element) root.getElementsByTagName("phone_num").item(0);
                            Element realNameElement = (Element) root.getElementsByTagName("real_name").item(0);
                            SipInfo.userId = userIdElement.getFirstChild().getNodeValue();
                            //SipInfo.devId= TelephonyManager.getDeviceId():
                            SipInfo.userRealname = realNameElement.getFirstChild().getNodeValue();
                            SipURL local = new SipURL(SipInfo.userId, SipInfo.serverIp, SipInfo.SERVER_PORT_USER);
                            SipInfo.user_from.setAddress(local);
                            SipInfo.userPhoneNumber = phoneNumElement.getFirstChild().getNodeValue();
                            SipInfo.seed = seedElement.getFirstChild().getNodeValue();
                            SipInfo.salt = saltElement.getFirstChild().getNodeValue();
                            Log.i(TAG, "收到用户注册第一步响应");
                            SHA1 sha1 = SHA1.getInstance();
                            if((SipInfo.passWord==null)||(SipInfo.passWord.equals(""))){
                                Message register = SipMessageFactory.createRegisterRequest(
                                        SipInfo.sipUser, SipInfo.user_to, SipInfo.user_from,
                                        BodyFactory.createRegisterBody("pass"));
                                SipInfo.sipUser.sendMessage(register);
                            }else {
                                String password = sha1.hashData(SipInfo.salt + SipInfo.passWord);
                                password = sha1.hashData(SipInfo.seed + password);
                                Message register = SipMessageFactory.createRegisterRequest(
                                        SipInfo.sipUser, SipInfo.user_to, SipInfo.user_from,
                                        BodyFactory.createRegisterBody(password));
                                SipInfo.sipUser.sendMessage(register);
                            }


                        } else {
                            Log.e(TAG, "掉线");
                            SipInfo.userLogined = false;
                            SipURL local = new SipURL(SipInfo.REGISTER_ID, SipInfo.serverIp, SipInfo.SERVER_PORT_USER);
                            NameAddress from = new NameAddress(SipInfo.userAccount, local);
                            Message register = SipMessageFactory.createRegisterRequest(
                                    SipInfo.sipUser, SipInfo.user_to, from);
                            SipInfo.sipUser.sendMessage(register);
                        }
                        return true;
                    case "login_response":
                        if (SipInfo.userLogined) {
                            SipInfo.user_heartbeatResponse = true;
                            Log.i(TAG, "收到用户心跳回复");
                            Log.i(TAG, "用户在线!");
                        } else {
                            //获取电源锁,用于防止手机静默之后,心跳线程暂停
                            PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
                            GroupInfo.wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, context.getClass().getCanonicalName());
                            GroupInfo.wakeLock.acquire();
                            SipInfo.userLogined = true;
                            SipInfo.loginTimeout = false;
                            Log.i(TAG, "用户注册成功");
//                            /*请求好友列表*/
//                            SipInfo.friendCount = 0;
//                            SipInfo.friendList.clear();
//                            SipInfo.friends.clear();

//                            SipInfo.sipUser.sendMessage(SipMessageFactory.createSubscribeRequest(SipInfo.sipUser,
//                                    SipInfo.user_to, SipInfo.user_from, BodyFactory.createFriendsQueryBody(0, 0)));

                        }
                        return true;
                    case "group_bind_code":
                        codeElement = (Element) root.getElementsByTagName("code").item(0);
                        code = codeElement.getFirstChild().getNodeValue();
                        if (code.equals("200")) {
//                            Element idElement = (Element) root.getElementsByTagName("padduserid").item(0);
//                            SipInfo.paduserid = idElement.getFirstChild().getNodeValue();
//                            Element portElement=(Element)root.getElementsByTagName("cluster_id").item(0);
//                            GroupInfo.port=Integer.parseInt(portElement.getFirstChild().getNodeValue())-1+7000;
                            Log.i(TAG, "用户总数：" + SipInfo.friendCount);
                        }
                    case "port_get":
                        Element portElement = (Element) root.getElementsByTagName("port").item(0);
                        String port= portElement.getFirstChild().getNodeValue();
                        GroupInfo.port=Integer.parseInt(port);
                        Log.d(TAG,"亲聊端口号为"+GroupInfo.port);
                        break;


                    case "friends_query":
                        codeElement = (Element) root.getElementsByTagName("code").item(0);
                        code = codeElement.getFirstChild().getNodeValue();
                        if (code.equals("200")) {
                            Element numElement = (Element) root.getElementsByTagName("num").item(0);
                            SipInfo.friendCount = Integer.parseInt(numElement.getFirstChild().getNodeValue());
                            Log.i(TAG, "用户总数：" + SipInfo.friendCount);
                        }
                        return true;
                    case "dev_count"://设备数量
                        SipInfo.devCount = Integer.parseInt(root.getFirstChild().getNodeValue());
                        Log.i(TAG, "设备总数：" + SipInfo.devCount);
                        return true;
                    case "update_password_response":
                        Element resultElement = (Element) root.getElementsByTagName("result").item(0);
                        String result = resultElement.getFirstChild().getNodeValue();
                        if (result.equals("0")) {
                            if (changePWDListener != null) {
                                changePWDListener.onChangePWD(1);
                            }
                        } else {
                            if (changePWDListener != null) {
                                changePWDListener.onChangePWD(0);
                            }
                        }
                        return true;
                    case "app_query":
                        codeElement = (Element) root.getElementsByTagName("code").item(0);
                        code = codeElement.getFirstChild().getNodeValue();
                        if (code.equals("200")) {
                            Element appsElement = (Element) root.getElementsByTagName("apps").item(0);
                            NodeList apps = appsElement.getElementsByTagName("app");
                            if (apps.getLength() > 0) {
                                for (int i = 0; i < apps.getLength(); i++) {
                                    Element appElement = (Element) apps.item(i);
                                    Element appidElement = (Element) appElement.getElementsByTagName("appid").item(0);
                                    Element appnameElement = (Element) appElement.getElementsByTagName("appname").item(0);
                                    Element sizeElement = (Element) appElement.getElementsByTagName("size").item(0);
                                    Element urlElement = (Element) appElement.getElementsByTagName("url").item(0);
                                    Element iconurlElement = (Element) appElement.getElementsByTagName("iconurl").item(0);
                                    Element descElement = (Element) appElement.getElementsByTagName("desc").item(0);
                                    Element nameElement = (Element) appElement.getElementsByTagName("name").item(0);
                                    Element iconnameElement = (Element) appElement.getElementsByTagName("iconname").item(0);
                                    String appid = appidElement.getFirstChild().getNodeValue();
                                    String appname = appnameElement.getFirstChild().getNodeValue();
                                    long size = Long.parseLong(sizeElement.getFirstChild().getNodeValue());
                                    String url = urlElement.getFirstChild().getNodeValue();
                                    String iconurl = iconurlElement.getFirstChild().getNodeValue();
                                    String desc = descElement.getFirstChild().getNodeValue();
                                    String apkname = nameElement.getFirstChild().getNodeValue();
                                    String iconname = iconnameElement.getFirstChild().getNodeValue();
                                    App app = new App();
                                    app.setAppid(appid);
                                    app.setAppname(appname);
                                    app.setSize(size);
                                    app.setUrl(url);
                                    app.setIconUrl(iconurl);
                                    app.setDesc(desc);
                                    app.setApkname(apkname);
                                    app.setIconname(iconname);
                                    SipInfo.applist.add(app);
                                    DatabaseInfo.sqLiteManager.insertAppInfo(appid, appname, size, "", 0);
                                }
                                for (int i = 0; i < SipInfo.applist.size(); i++) {
                                    final Ftp mFtp = new Ftp(SipInfo.serverIp, 21, "ftpaller", "123456", new FtpListener() {
                                        @Override
                                        public void onStateChange(String currentStep) {
                                            Log.d(TAG, currentStep);
                                        }

                                        @Override
                                        public void onUploadProgress(String currentStep, long uploadSize, File targetFile) {

                                        }

                                        @Override
                                        public void onDownLoadProgress(String currentStep, long downProcess, File targetFile) {

                                        }

                                        @Override
                                        public void onDeleteProgress(String currentStep) {

                                        }
                                    });
                                    final int finalI = i;
                                    Thread thread = new Thread() {
                                        @Override
                                        public void run() {
                                            File file = new File(sdPath + SipInfo.applist.get(finalI).getIconname());
                                            if (!file.exists()) {
                                                try {
                                                    mFtp.download(SipInfo.applist.get(finalI).getIconUrl(), sdPath);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    };
                                    thread.start();
                                }
                            }

                        }
                        return true;
                    case "query_response":
                        Element resolutionElement = (Element) root.getElementsByTagName("resolution").item(0);
                        VideoInfo.resultion=resolutionElement.getFirstChild().getNodeValue();
                        switch (VideoInfo.resultion) {
                            case "CIF":
                                VideoInfo.width = 352;
                                VideoInfo.height = 288;
                                VideoInfo.videoType = 2;

                                break;
                            case "QCIF_MOBILE_SOFT":
                                VideoInfo.width = 176;
                                VideoInfo.height = 144;
                                VideoInfo.videoType = 3;
                                break;
                            case "MOBILE_S6":
                                VideoInfo.width = 320;
                                VideoInfo.height = 240;
                                VideoInfo.videoType = 4;
                                break;
                            case "MOBILE_S9":
                                VideoInfo.width = 320;
                                VideoInfo.height = 240;
                                VideoInfo.videoType = 5;
                                break;
                            default:
                                break;
                        }
                        SipInfo.queryResponse = true;
                        return true;
                    case "media":
                        Element peerElement = (Element) root.getElementsByTagName("peer").item(0);
                        Element magicElement = (Element) root.getElementsByTagName("magic").item(0);
                        String peer = peerElement.getFirstChild().getNodeValue();
                        VideoInfo.rtpIp = peer.substring(0, peer.indexOf("UDP")).trim();
                        VideoInfo.rtpPort = Integer.parseInt(peer.substring(peer.indexOf("UDP") + 3).trim());
                        String magic = magicElement.getFirstChild().getNodeValue();
                        VideoInfo.magic = new byte[magic.length() / 2 + magic.length() % 2];
                        for (int i = 0; i < VideoInfo.magic.length; i++) {
                            try {
                                VideoInfo.magic[i] = (byte) (0xff & Integer.parseInt(magic.substring(i * 2, i * 2 + 2), 16));
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                        }
                        SipInfo.inviteResponse = true;
                         /*
                        VideoInfo.media_info_ip = VideoInfo.rtpIp;
                        VideoInfo.media_info_port = VideoInfo.rtpPort;
                        VideoInfo.media_info_magic = VideoInfo.magic;
                        */

                        return true;
                }
            } catch (Exception e) {
                Log.e(TAG, "responseParse: ", e);
            }
        } else {
            Log.i(TAG + "responseParse", "BODY IS NULL");
            return true;
        }
        return false;
    }


    public void setLoginNotifyListener(LoginNotifyListener loginNotifyListener) {
        this.loginNotifyListener = loginNotifyListener;
    }

    public interface LoginNotifyListener {
        void onDevNotify();

        void onUserNotify();
    }

    public void setChangePWDListener(ChangePWDListener changePWDListener) {
        this.changePWDListener = changePWDListener;
    }

    public interface ChangePWDListener {
        void onChangePWD(int i);
    }


    public void setMessageListener(MessageListener messageListener) {
        this.messageListener = messageListener;
    }

    public interface MessageListener {
        void onReceivedMessage(Msg msg);
    }

    public interface TotalListener {
//        void onReceivedTotalNotice(Notice notice);

        void onReceivedTotalMessage(Msg msg);

        void onReceivedTotalFileshare(MyFile myfile);
    }

    public void setTotalListener(TotalListener totalListener) {
        this.totalListener = totalListener;
    }

    public interface BottomListener {
//        void onReceivedBottomNotice(Notice notice);

        void onReceivedBottomMessage(Msg msg);

        void onReceivedBottomFileshare(MyFile myfile);
    }

    public void setBottomListener(BottomListener bottomListener) {
        this.bottomListener = bottomListener;
    }
    public interface StopMonitor{
        void stopVideo();
    }

    public StopMonitor monitor=new StopMonitor() {
        @Override
        public void stopVideo() {
        }
    };
    public void setMonitor(StopMonitor monitor){
        this.monitor = monitor;
    }

//    public static String[] qinliaouserid=new String[8];
    public static List<String> list=new ArrayList<>();

    public interface ClusterNotifyListener{
        void onNotify();
    }

    //定义一个接口
    public interface QinliaoUpdateListener{
        void stausOnUpdate();
        void stausOffUpdate();
    }

    //创建接口类型变量
    public QinliaoUpdateListener qinliaoUpdateListener;


    //暴露接口设置得方法
    public void setQinliaoUpdateListener(QinliaoUpdateListener qinliaoUpdateListener){
        this.qinliaoUpdateListener=qinliaoUpdateListener;
    }

}
