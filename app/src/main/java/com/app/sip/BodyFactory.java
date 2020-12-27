package com.app.sip;

/**
 * Author chzjy
 * Date 2016/12/19.
 */

public class BodyFactory {
    public static String createRegisterBody(String password) {
        StringBuilder body = new StringBuilder(
                "<?xml version=\"1.0\"?>\r\n<login_request>\r\n<password>");
        body.append(password);
        body.append("</password>\r\n</login_request>\r\n");
        return body.toString();
    }
    //心跳包
    public static String createHeartbeatBody() {
        return "<?xml version=\"1.0\"?>\r\n<heartbeat_request></heartbeat_request>\r\n";
    }

    //登出
    public static String createLogoutBody() {
        StringBuilder body = new StringBuilder();
        body.append("<?xml version=\"1.0\"?>\r\n" +
                "<logout></logout>\r\n");
        return body.toString();
    }

    /**
     * @param num  本地好友数量 0
     * @param time 本地更新储存好友数量的时间 0 (本地并没有储存)
     *             参数其实并没有用
     * @return 消息体
     */
    //好友列表查询
    public static String createFriendsQueryBody(int num, int time) {
        StringBuilder body = new StringBuilder();
        body.append("<?xml version=\"1.0\"?>\r\n");
        body.append("<friends_query>\r\n<num>");
        body.append(num);
        body.append("</num>\r\n<time>");
        body.append(time);
        body.append("</time>\r\n</friends_query>\r\n");
        return body.toString();
    }
    //设备列表请求
    public static String createDevsQueryBody() {
        return "<?xml version=\"1.0\"?>\r\n<devs_query></devs_query>\r\n";
    }

    /**
     * @param password_old 一次加密的旧密码
     * @param password_new 一次加密的新密码
     * @return 消息体
     */
    //密码修改
    public static String cretePasswordChange(String password_old, String password_new) {
        StringBuilder body = new StringBuilder();
        body.append("<?xml version=\"1.0\"?>\r\n<password_change>\r\n<password_old>");
        body.append(password_old);
        body.append("</password_old>\r\n<password_new>");
        body.append(password_new);
        body.append("</password_new>\r\n</password_change>\r\n");
        return body.toString();
    }

    //请求第三方应用
    public static String createAppsQueryBody() {
        return "<?xml version=\"1.0\"?>\r\n<apps_query></apps_query>\r\n";
    }

    //请求设备参数
    public static String createQueryBody(String devType) {
        return "<?xml version=\"1.0\"?>\r\n" +
                "<query>\r\n" +
                "<variable>MediaInfo_Video</variable>\r\n" +
                "<dev_type>" +
                devType +
                "</dev_type>\r\n" +
                "</query>\r\n";
    }

    /**
     * @param resultion 分辨率相关
     * @param video     视频流格式
     * @param audio     音频流格式
     * @param dev_type  设备类型
     * @return 消息体
     */
    //视频请求第二步
    public static String createMediaBody(String resultion, String video, String audio, String dev_type) {
        StringBuilder body = new StringBuilder();
        body.append("<?xml version=\"1.0\"?>\r\n");
        body.append("<media>\r\n<resolution>");
        body.append(resultion);
        body.append("</resolution>\r\n<video>");
        body.append(video);
        body.append("</video>\r\n<audio>");
        body.append(audio);
        body.append("</audio>\r\n");
        body.append("<kbps>800</kbps>\r\n");
        body.append("<self>192.168.1.129 UDP 5200</self>\r\n");
        body.append("<mode>active</mode>\r\n");
        body.append("<magic>01234567890123456789012345678901</magic>\r\n");
        body.append("<dev_type>");
        body.append(dev_type);
        body.append("</dev_type>\r\n</media>\r\n");
        return body.toString();
    }

    //消息回复
    public static String createMessageResBody(String id, int code) {
        StringBuilder body = new StringBuilder();
        body.append("<?xml version=\"1.0\"?>\r\n");
        body.append("<message>\r\n<id>");
        body.append(id);
        body.append("</id>\r\n<code>");
        body.append(code);
        body.append("</code>\r\n</message>\r\n");
        return body.toString();
    }
    //报警回复
    public static String createalarmResBody( int code,String time) {
        StringBuilder body = new StringBuilder();
        body.append("<?xml version=\"1.0\"?>\r\n");
        body.append("<alarm_response>\r\n<code>");
        body.append(code);
        body.append("</code>\r\n<time>");
        body.append(time);
        body.append("</time>\r\n</alarm_response>\r\n");
        return body.toString();
    }
    //视频控制
    public static String createVideoControlResBody(String operate){
        StringBuilder body=new StringBuilder();
        body.append("<?xml version=\"1.0\"?>\r\n");
        body.append("<direction_control>\r\n<operate>");
        body.append(operate);
        body.append("</operate>\r\n</direction_control>\r\n");
        return body.toString();
    }
    //邮件回复
    public static String createMailResponseBody(String mailId, int code) {
        StringBuilder body = new StringBuilder();
        body.append("<?xml version=\"1.0\"?>\r\n");
        body.append("<mail>\r\n<id>");
        body.append(mailId);
        body.append("</id>\r\n<code>");
        body.append(code);
        body.append("</code>\r\n</mail>\r\n");
        return body.toString();
    }

    /*文字聊天*/
    public static String createMessageBody(String id, String from, String to, String content, String time, int type) {
        StringBuilder body = new StringBuilder();
        body.append("<?xml version=\"1.0\"?>\r\n");
        body.append("<message>\r\n<id>");
        body.append(id);
        body.append("</id>\r\n<from>");
        body.append(from);
        body.append("</from>\r\n<to>");
        body.append(to);
        body.append("</to>\r\n<content>");
        body.append(content);
        body.append("</content>\r\n<time>");
        body.append(time);
        body.append("</time>\r\n<type>");
        body.append(type);
        body.append("</type>\r\n</message>\r\n");
        return body.toString();
    }

    public static String createFileTransferBody(String from, String to, String id, String name,
                                                String fileType, String time, String path, long size,
                                                String md5, int type) {
        StringBuilder body = new StringBuilder();
        body.append("<?xml version=\"1.0\"?>\r\n");
        body.append("<filetransfer>\r\n<from>");
        body.append(from);
        body.append("</from>\r\n<to>");
        body.append(to);
        body.append("</to>\r\n<id>");
        body.append(id);
        body.append("</id>\r\n<name>");
        body.append(name);
        body.append("</name>\r\n<filetype>");
        body.append(fileType);
        body.append("</filetype>\r\n<time>");
        body.append(time);
        body.append("</time>\r\n<path>");
        body.append(path);
        body.append("</path>\r\n<size>");
        body.append(size);
        body.append("</size>\r\n<md5>");
        body.append(md5);
        body.append("</md5>\r\n<type>");
        body.append(type);
        body.append("</type>\r\n</filetransfer>\r\n");
        return body.toString();
    }

    public static String createFileTransferResBody(String id, int code) {
        StringBuilder body = new StringBuilder();
        body.append("<?xml version=\"1.0\"?>\r\n");
        body.append("<filetransfer>\r\n<id>");
        body.append(id);
        body.append("</id>\r\n<code>");
        body.append(code);
        body.append("</code>\r\n</filetransfer>\r\n");
        return body.toString();
    }
    /*设备部分*/

    public static String createGroupSubscribeBody(String dev_id) {
        StringBuilder body = new StringBuilder();
        body.append("<?xml version=\"1.0\"?>\r\n<subscribe_grouppn>\r\n<dev_id>\r\n");
        body.append(dev_id);
        body.append("</dev_id>\r\n</subscribe_grouppn>\r\n");
        return body.toString();
    }

    public static String createOptionsBody(String resolution) {
        StringBuilder body = new StringBuilder();
        body.append("<?xml version=\"1.0\"?>\r\n");
        body.append("<query_response>\r\n<variable>MediaInfo_Video</variable>\r\n");
        body.append("<result>0</result>\r\n");
        body.append("<video>H.264</video>\r\n");
        body.append("<resolution>"+resolution+"</resolution>\r\n");
        body.append("<framerate>25</framerate>\r\n");
        body.append("<bitrate>256</bitrate>\r\n");
        body.append("<bright>51</bright>\r\n");
        body.append("<contrast>49</contrast>\r\n");
        body.append("<saturation>50</saturation>\r\n</query_response>\r\n");
        return body.toString();
    }


    public static String createMediaResponseBody(String resolution) {
        StringBuilder body = new StringBuilder();
        body.append("<?xml version=\"1.0\"?>\r\n");
        body.append("<media>\r\n<resolution>");
        body.append(resolution);
        body.append("</resolution>\r\n");
        body.append("<video>H.264</video>\r\n");
        body.append("<audio>G.722</audio>\r\n");
        body.append("<kbps>800</kbps>\r\n");
        body.append("<self>192.168.1.129 UDP 5000</self>\r\n");
        body.append("<mode>active</mode>\r\n");
        body.append("<magic>01234567890123456789012345678901</magic>\r\n");
        body.append("<dev_type>2</dev_type>\r\n</media>\r\n");
        return body.toString();
    }

    //邮件
    public static String createMailBody(String mailId, String fromId, String toId, String theme, String content) {
        StringBuilder body = new StringBuilder();
        body.append("<?xml version=\"1.0\"?>\r\n");
        body.append("<mail>\r\n<id>");
        body.append(mailId);
        body.append("</id>\r\n<from>");
        body.append(fromId);
        body.append("</from>\r\n<to>");
        body.append(toId);
        body.append("</to>\r\n<theme>");
        body.append(theme);
        body.append("</theme>\r\n<content>");
        body.append(content);
        body.append("</content>\r\n</mail>\r\n");
        return body.toString();
    }


    //养护
    public static String createMaintEvent(String taskId, int maintEventSeq, String direction, String lane, String roadCondition) {
        StringBuilder body = new StringBuilder();
        body.append("<?xml version=\"1.0\"?>\r\n");
        body.append("<maint_event>\r\n<task_id>");
        body.append(taskId);
        body.append("</task_id>\r\n<maint_event_seq>");
        body.append(maintEventSeq);
        body.append("</maint_event_seq>\r\n<direction>");
        body.append(direction);
        body.append("</direction>\r\n<lane>");
        body.append(lane);
        body.append("</lane>\r\n<road_condition>");
        body.append(roadCondition);
        body.append("</road_condition>\r\n</maint_event>\r\n");
        return body.toString();
    }

    public static String createTaskResponse() {
        StringBuffer body = new StringBuffer(
                "<?xml version=\"1.0\"?>\r\n<task_response>\r\n" +
                        "<result>0</result>" +
                        "\r\n</task_response>\r\n");
        return body.toString();
    }

    public static String createTaskCheck(String taskId) {
        StringBuffer body = new StringBuffer();
        body.append("<?xml version=\"1.0\"?>\r\n");
        body.append("<task_check>\r\n<task_id>");
        body.append(taskId);
        body.append("</task_id>\r\n</task_check>");
        return body.toString();

    }

    public static String createTaskReplyTimeBody(String devId, String taskId, String timeType, String time) {
        StringBuilder body = new StringBuilder();
        body.append("<?xml version=\"1.0\"?>\r\n");
        body.append("<task_reply_time>\r\n<dev_id>");
        body.append(devId);
        body.append("</dev_id>\r\n<task_id>");
        body.append(taskId);
        body.append("</task_id>\r\n<time_type>");
        body.append(timeType);
        body.append("</time_type>\r\n<time>");
        body.append(time);
        body.append("</time>\r\n</task_reply_time>\r\n");
        return body.toString();
    }

    public static String createTaskReplyFeeBody(String taskId, String accarNum, String fee, String feeActual) {
        StringBuilder body = new StringBuilder();
        body.append("<?xml version=\"1.0\"?>\r\n");
        body.append("<task_reply_fee>\r\n<task_id>");
        body.append(taskId);
        body.append("</task_id>\r\n<accar_num>");
        body.append(accarNum);
        body.append("</accar_num>\r\n<fee>");
        body.append(fee);
        body.append("</fee>\r\n<fee_actual>");
        body.append(feeActual);
        body.append("</fee_actual>\r\n</task_reply_fee>\r\n");
        return body.toString();
    }

    public static String createTaskReplySatisfactionBody(String devId, String taskId, String sat) {
        StringBuilder body = new StringBuilder();
        body.append("<?xml version=\"1.0\"?>\r\n");
        body.append("<task_reply_satisfaction>\r\n<dev_id>");
        body.append(devId);
        body.append("</dev_id>\r\n<task_id>");
        body.append(taskId);
        body.append("</task_id>\r\n<satisfaction>");
        body.append(sat);
        body.append("</satisfaction>\r\n");
        body.append("</task_reply_satisfaction>");
        return body.toString();
    }

    public static String createTaskCompleteBody(String taskId) {
        StringBuilder body = new StringBuilder();
        body.append("<?xml version=\"1.0\"?>\r\n");
        body.append("<task_complete>\r\n<task_id>");
        body.append(taskId);
        body.append("</task_id>\r\n</task_complete>\r\n");
        return body.toString();
    }
    //用户列表刷新
    public static String createListUpdate(String  addevent){
        StringBuilder body=new StringBuilder();
        body.append("<?xml version=\"1.0\"?>\r\n");
        body.append("<list_update>\r\n<addevent>");
        body.append(addevent);
        body.append("</addevent>\r\n</list_update>\r\n");
        return body.toString();

    }
    //视频请求
    public static String createCallRequest(String request,String devid,String userId){
        StringBuilder body =new StringBuilder();
        body.append("<?xml version=\"1.0\"?>\r\n");
        body.append("<operation>\r\n<operate>");
        body.append(request);
        body.append("</operate>\r\n<devId>");
        body.append(devid);
        body.append("</devId>\r\n<userId>");
        body.append(userId);
        body.append("</userId>\r\n</operation>");
        return  body.toString();
    }
    //视频请求回复
    public static String createCallReply(String result){
        StringBuilder body=new StringBuilder();
        body.append("<?xml version=\"1.0\"?>\r\n");
        body.append("<call_response>\r\n<operate>");
        body.append(result);
        body.append("</operate>\r\n</call_response>\r\n");
        return body.toString();
    }
    //服务电话
    public static String createServiceCall(String item,String phone){
        StringBuilder body=new StringBuilder();
        body.append("<?xml version=\"1.0\"?>\r\n");
        body.append("<service_call>\r\n<item>");
        body.append(item);
        body.append("</item>\r\n<telephone>");
        body.append(phone);
        body.append("</telephone>\r\n</service_call>\r\n");
        return body.toString();
    }
    //上下线提醒
    public static String createOnlineNotify(String status,String userid){
        StringBuilder body=new StringBuilder();
        body.append("<?xml version=\"1.0\"?>\r\n");
        body.append("<user_mode>\r\n<status>");
        body.append(status);
        body.append("</status>\r\n<userId>" );
        body.append(userid);
        body.append("</userId>\r\n</user_mode>\r\n");
        return body.toString();
    }
    //上线提醒
    public static String createOnlineNotify(String userid){
        StringBuilder body=new StringBuilder();
        body.append("<?xml version=\"1.0\"?>\r\n");
        body.append("<user_online>\r\n<userid>");
        body.append(userid);
        body.append("</userid>\r\n</user_online>");
        return body.toString();
    }
    //下线提醒
    public static String createOfflineNotify(String userid){
        StringBuilder body=new StringBuilder();
        body.append("<?xml version=\"1.0\"?>\r\n");
        body.append("<user_offline>\r\n<userid>");
        body.append(userid);
        body.append("</userid>\r\n</user_offline>");
        return body.toString();
    }
    //扫码绑定组呼群组
    public static String createGroupBindNotify(String userid,String padid,String port){
        StringBuilder body=new StringBuilder();
        body.append("<?xml version=\"1.0\"?>\r\n");
        body.append("<group_bind>\r\n<userid>");
        body.append(userid);
        body.append("</userid>\r\n<paduserid>");
        body.append(padid);
        body.append("</paduserid>\r\n<port>");
        body.append(port);
        body.append("</port>\r\n</group_bind>");
        return body.toString();
    }
    //图片分享
    public static String createImageShareNotify(String url){
        StringBuilder body=new StringBuilder();
        body.append("<?xml version=\"1.0\"?>\r\n");
        body.append("<image_share>\r\n<image_url>");
        body.append(url);
        body.append("</image_url>\r\n</image_share>");
        return body.toString();
    }
    //手动添加设备信息获取
    public static String createAdddevNotify(String devid,String userid){
        StringBuilder body=new StringBuilder();
        body.append("<?xml version=\"1.0\"?>\r\n");
        body.append("<group_bind_code>\r\n<devid>");
        body.append(devid);
        body.append("</devid>\r\n<userid>");
        body.append(userid);
        body.append("</userid>\r\n</group_bind_code>");
        return body.toString();
    }
    //视频结束通知
    public static String createStopMonitor(String devId){
        StringBuilder body=new StringBuilder();
        body.append("<?xml version=\"1.0\"?>\n");
        body.append("<stop_monitor>\r\n<devId>");
        body.append(devId);
        body.append("</devId>\r\n");
        body.append("</stop_monitor>");
        return body.toString();
    }
    //监控标志位
    public static String createStartMonitor(Boolean ismonitor,String devid,String userid){
        StringBuilder body=new StringBuilder();
        body.append("<?xml version=\"1.0\"?>\n");
        body.append("<is_monitor>\r\n<ismonitor>");
        body.append(ismonitor);
        body.append("</ismonitor>\r\n<devid>");
        body.append(devid);
        body.append("</devid>\r\n<userid>");
        body.append(userid);
        body.append("</userid>\r\n</is_monitor>");
        return body.toString();
    }
    //获取端口号
    public static String createGetPort(String userid){
        StringBuilder body=new StringBuilder();
        body.append("<?xml version=\"1.0\"?>\n");
        body.append("<port_get>\r\n<userid>");
        body.append(userid);
        body.append("</userid>\r\n");
        body.append("</port_get>");
        return body.toString();
    }
}
