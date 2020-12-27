// NotebookAidlInterface.aidl
package com.app;

// Declare any non-default types here with import statements
import com.app.model.MailInfo;
import com.app.model.Friend;
import com.app.model.UserInfo;
interface NotebookAidlInterface {
    List<MailInfo> getMailInfo();
    List<Friend> getFriendList();
    UserInfo getUserInfo();
    void sendMail(String mailId,String fromId,String toId,String theme,String content);
    void deleteMail(String mailId);
}
