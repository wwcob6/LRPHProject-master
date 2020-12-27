package com.app.sip;

import org.zoolu.sip.address.NameAddress;
import org.zoolu.sip.address.SipURL;
import org.zoolu.sip.header.AuthorizationHeader;
import org.zoolu.sip.header.CSeqHeader;
import org.zoolu.sip.header.SubjectHeader;
import org.zoolu.sip.header.UserAgentHeader;
import org.zoolu.sip.message.Message;
import org.zoolu.sip.message.MessageFactory;
import org.zoolu.sip.message.SipMethods;
import org.zoolu.sip.provider.SipProvider;

/**
 * Author chenhan
 * Date 2017/7/26
 * 28281标准
 */

public class SipMessageFactoryForHaiKang extends MessageFactory {

    public static SipMessageFactoryForHaiKang instance = null;

    public static SipMessageFactoryForHaiKang getInstance() {
        if (instance == null) {
            instance = new SipMessageFactoryForHaiKang();
        }
        return instance;
    }

    public SipURL requestUri = new SipURL("112.124.122.216", 6061);
//    public SipURL requestUri = new SipURL("101.69.255.132", 6061);
    public String userAgent = "IP Camera";

    /**
     * 注册第一步
     */
    public Message createRegisterRequest(SipProvider sip_provider, NameAddress to, NameAddress from) {
        String via_addr = sip_provider.getViaAddress();
        int host_port = sip_provider.getPort();
        SipURL sipURL = new SipURL(via_addr, host_port);
        NameAddress contact = new NameAddress(sipURL);
        Message message = createRegisterRequest(sip_provider, requestUri, to, from, contact);
        UserAgentHeader userAgentHeader = new UserAgentHeader(userAgent);
        message.setUserAgentHeader(userAgentHeader);
        return message;
    }

    /**
     * 注册第二步
     */
    public Message createRegisterRequest(SipProvider sip_provider, NameAddress to, NameAddress from,
                                         String userName, String realm, String nonce, String response, String algorithm) {
        Message message = createRegisterRequest(sip_provider, to, from);
        AuthorizationHeader authorizationHeader = new AuthorizationHeader("Digest");
        authorizationHeader.addUsernameParam(userName);
        authorizationHeader.addRealmParam(realm);
        authorizationHeader.addNonceParam(nonce);
        authorizationHeader.addUriParam(requestUri.toString());
        authorizationHeader.addResponseParam(response);
        authorizationHeader.addAlgorithParam(algorithm);
        message.setAuthorizationHeader(authorizationHeader);
        CSeqHeader cSeqHeader = new CSeqHeader(2, SipMethods.REGISTER);
        message.setCSeqHeader(cSeqHeader);
        return message;
    }

    /**
     * 心跳
     */
    public Message createMessageRequest(SipProvider sip_provider, NameAddress to, NameAddress from, String contentType, String body) {
        Message message = createMessageRequest(sip_provider, to, from, null, contentType, body);
        UserAgentHeader userAgentHeader = new UserAgentHeader(userAgent);
        CSeqHeader cSeqHeader = new CSeqHeader(20, SipMethods.MESSAGE);
        message.setUserAgentHeader(userAgentHeader);
        message.setCSeqHeader(cSeqHeader);
        return message;
    }

    /**
     * 视频请求
     */
    public Message createInviteRequest(SipProvider sip_provider, NameAddress to, NameAddress from, String body) {
        String via_addr = sip_provider.getViaAddress();
        int host_port = sip_provider.getPort();
        SipURL sipURL = new SipURL(via_addr, host_port);
        NameAddress contact = new NameAddress(sipURL);
        Message message=createInviteRequest(sip_provider,requestUri,to,from,contact,null);
        message.setBody("APPLICATION/SDP", body);
        UserAgentHeader userAgentHeader = new UserAgentHeader(userAgent);
        CSeqHeader cSeqHeader = new CSeqHeader(1, SipMethods.INVITE);
        message.setSubjectHeader(new SubjectHeader("123233"));
        message.setUserAgentHeader(userAgentHeader);
        message.setCSeqHeader(cSeqHeader);
        return message;
    }

    /**
     * 结束视频请求
     */

    public static Message createByeRequest(SipProvider sip_provider,  //结束实时视频
                                           NameAddress to, NameAddress from) {
        String via_addr = sip_provider.getViaAddress();
        int host_port = sip_provider.getPort();
        SipURL sipURL = new SipURL(via_addr, host_port);
        NameAddress contact = new NameAddress(sipURL);
        Message message=createRequest(sip_provider,SipMethods.BYE,to,from,null);
        message.setUserAgentHeader(new UserAgentHeader("IP Camera"));
        message.setCSeqHeader(new CSeqHeader(2,SipMethods.BYE));
        return message;
    }

    /**
     * release
     */
    public void release() {
        if (instance != null) {
            instance = null;
        }
    }
}
