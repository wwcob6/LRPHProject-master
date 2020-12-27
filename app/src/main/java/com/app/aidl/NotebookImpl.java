package com.app.aidl;

import android.os.RemoteException;

import com.app.db.DatabaseInfo;
import com.app.model.Friend;
import com.app.model.MailInfo;
import com.app.model.UserInfo;
import com.app.sip.BodyFactory;
import com.app.sip.SipInfo;
import com.app.sip.SipMessageFactory;
import com.app.NotebookAidlInterface;

import org.zoolu.sip.address.NameAddress;
import org.zoolu.sip.address.SipURL;
import org.zoolu.sip.message.Message;

import java.util.ArrayList;
import java.util.List;

import static com.app.sip.SipInfo.friends;
import static com.app.sip.SipInfo.notificationManager;
import static com.app.sip.SipInfo.sipUser;
import static com.app.sip.SipInfo.toUser;
import static com.app.sip.SipInfo.userAccount;
import static com.app.sip.SipInfo.userId;
import static com.app.sip.SipInfo.userRealname;
import static com.app.sip.SipInfo.user_from;


/**
 * Author chzjy
 * Date 2016/12/19.
 */

public class NotebookImpl extends NotebookAidlInterface.Stub {


    public NotebookImpl() {
    }

    @Override
    public List<MailInfo> getMailInfo() throws RemoteException {
        notificationManager.cancel(0x1124);
        List<MailInfo> mailInfos = new ArrayList<>();
        SipInfo.maillist = DatabaseInfo.sqLiteManager.queryMail();
        mailInfos.addAll(SipInfo.maillist);
        SipInfo.maillist.clear();

        return mailInfos;
    }

    @Override
    public List<Friend> getFriendList() throws RemoteException {
        return friends;
    }

    @Override
    public UserInfo getUserInfo() throws RemoteException {
        UserInfo userInfo=new UserInfo();
        userInfo.setUserId(userId);
        userInfo.setUserName(userAccount);
        userInfo.setUserRealName(userRealname);
        return userInfo;
    }

    @Override
    public void sendMail(String mailId, String fromId, String toId, String theme, String content) throws RemoteException {
        SipURL remote = new SipURL(toId, SipInfo.serverIp, SipInfo.SERVER_PORT_USER);
        Friend friend = new Friend();
        friend.setUserId(toId);
        int index = friends.indexOf(friend);
        if (index != -1) {
            toUser = new NameAddress(friends.get(index).getPhoneNum(), remote);
            Message mail = SipMessageFactory.createNotifyRequest(sipUser, toUser
                    , user_from, BodyFactory.createMailBody(mailId, fromId, toId, theme, content));
            sipUser.sendMessage(mail);
        }
    }

    @Override
    public void deleteMail(String mailId) throws RemoteException {
        DatabaseInfo.sqLiteManager.deleteMailById(mailId);
    }
}
