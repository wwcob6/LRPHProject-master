package com.app.sip;

import org.zoolu.sip.address.NameAddress;
import org.zoolu.sip.address.SipURL;
import org.zoolu.sip.message.Message;
import org.zoolu.sip.message.MessageFactory;
import org.zoolu.sip.message.SipMethods;
import org.zoolu.sip.provider.SipProvider;

import static com.app.sip.SipInfo.SERVER_PORT_USER;
import static com.app.sip.SipInfo.serverIp;

/**
 * Author chzjy
 * Date 2016/12/19.
 */

public class SipMessageFactory extends MessageFactory {
    private static SipURL requestUri = new SipURL(serverIp, SERVER_PORT_USER);

    public static Message createRegisterRequest(SipProvider sip_provider,  //注册第一步
                                                NameAddress to, NameAddress from) {
        String via_addr = sip_provider.getViaAddress();
        int host_port = sip_provider.getPort();
        SipURL sipURL = new SipURL(via_addr, host_port);
        NameAddress contact = new NameAddress(sipURL);
        return createRegisterRequest(sip_provider, requestUri, to, from, contact);
    }

    public static Message createRegisterRequest(SipProvider sip_provider,  //注册第二步；心跳
                                                NameAddress to, NameAddress from, String body) {
        Message msg = createRegisterRequest(sip_provider, to, from);
        msg.setBody("application/xml", body);
        return msg;
    }

    public static Message createNotifyRequest(SipProvider sip_provider,
                                              NameAddress to, NameAddress from, String body) {
        String via_addr = sip_provider.getViaAddress();
        int host_port = sip_provider.getPort();
        SipURL sipURL = new SipURL(via_addr, host_port);
        NameAddress contact = new NameAddress(sipURL);
        Message msg = createRequest(sip_provider, SipMethods.NOTIFY, requestUri, to, from, contact, null);
        msg.setBody("application/xml", body);
        return msg;
    }

    private static Message createSubscribeRequest(SipProvider sip_provider,  //设备列表
                                                 NameAddress to, NameAddress from) {
        String via_addr = sip_provider.getViaAddress();
        int host_port = sip_provider.getPort();
        SipURL sipURL = new SipURL(via_addr, host_port);
        NameAddress contact = new NameAddress(sipURL);
        return createRequest(sip_provider, SipMethods.SUBSCRIBE, requestUri, to, from, contact, null);
    }

    public static Message createSubscribeRequest(SipProvider sip_provider,  //设备列表
                                                 NameAddress to, NameAddress from, String body) {
        Message msg = createSubscribeRequest(sip_provider, to, from);
        msg.setBody("application/xml", body);
        return msg;
    }

    public static Message createOptionsRequest(SipProvider sip_provider,  //视频信息查询
                                               NameAddress to, NameAddress from, String body) {
        String via_addr = sip_provider.getViaAddress();
        int host_port = sip_provider.getPort();
        SipURL sipURL = new SipURL(via_addr, host_port);
        NameAddress contact = new NameAddress(sipURL);
        Message msg = createRequest(sip_provider, SipMethods.OPTIONS, requestUri, to, from, contact, null);
        msg.setBody("application/xml", body);
        return msg;
    }

    public static Message createInviteRequest(SipProvider sip_provider,  //实时视频
                                              NameAddress to, NameAddress from, String body) {
        String via_addr = sip_provider.getViaAddress();
        int host_port = sip_provider.getPort();
        SipURL sipURL = new SipURL(via_addr, host_port);
        NameAddress contact = new NameAddress(sipURL);
        Message msg = createInviteRequest(sip_provider, requestUri, to, from, contact, null);
        msg.setBody("application/xml", body);
        return msg;
    }

    public static Message createByeRequest(SipProvider sip_provider,  //结束实时视频
                                           NameAddress to, NameAddress from) {
        String via_addr = sip_provider.getViaAddress();
        int host_port = sip_provider.getPort();
        SipURL sipURL = new SipURL(via_addr, host_port);
        NameAddress contact = new NameAddress(sipURL);
        return createRequest(sip_provider, SipMethods.BYE, requestUri, to, from, contact, null);
    }

    public static Message createResponse(Message req, int code, String reason, String body) {
        return createResponse(req, code, reason, null, null, "application/xml", body);
    }
}
