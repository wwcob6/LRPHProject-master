package com.app.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.app.model.App;
import com.app.model.LastestMsg;
import com.app.model.MailInfo;
import com.app.model.Msg;
import com.app.model.MyFile;

import java.util.ArrayList;
import java.util.List;

/**
 * Author chzjy
 * Date 2016/12/19.
 */

public class SQLiteManager {
    private MyDatabaseHelper dbHelper;
    private SQLiteDatabase db;

    public SQLiteManager(MyDatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    //插入app信息
    public boolean insertAppInfo(String appId, String appname, long size, String localPath, int state) {
        db = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("app_id", appId);
        contentValues.put("app_name", appname);
        contentValues.put("app_size", size);
        contentValues.put("local_path", localPath);
        contentValues.put("state", state);
        long i = -1;
        if (!isAppExist(appId)) {
            i = db.insert("app", null, contentValues);
        }
        return i != -1;
    }
    //查看此app数据是否存在
    private boolean isAppExist(String appId) {
        db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from app where app_id = ?",
                new String[]{appId});
        if (cursor.moveToFirst()) {
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }
    }
    //查询app信息
    public App queryApp(String appId) {
        db = dbHelper.getReadableDatabase();
        App app = new App();
        Cursor cursor = db.query("app", new String[]{"app_id", "app_name", "app_size", "local_path", "state"}, "app_id = ? ",
                new String[]{appId}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                app.setAppid(cursor.getString(cursor.getColumnIndex("app_id")));
                app.setAppname(cursor.getString(cursor.getColumnIndex("app_name")));
                app.setSize(cursor.getLong(cursor.getColumnIndex("app_size")));
                app.setLocalPath(cursor.getString(cursor.getColumnIndex("local_path")));
                app.setState(cursor.getInt(cursor.getColumnIndex("state")));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return app;
    }
    //更新app下载状态
    public void updateAppState(String appId, int state) {
        db = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("state", state);
        db.update("app", contentValues, "app_id=?", new String[]{appId});
    }
    //更新app本地路径
    public void updateAppLocalPath(String appId, String localPath) {
        db = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("local_path", localPath);
        db.update("app", contentValues, "app_id=?", new String[]{appId});
    }

    //获取最近一次消息的时间
    public int queryLastTime(String userId1, String userId2) {
        db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from message where (from_userid = ? and to_userid = ?) or (from_userid = ? and to_userid = ?) order by time desc",
                new String[]{userId1, userId2, userId2, userId1});
        if (cursor.moveToFirst()) {
            int time = cursor.getInt(cursor.getColumnIndex("time"));
            cursor.close();
            return time;
        } else {
            cursor.close();
            return -1;
        }
    }

    //插入新消息
    public boolean insertMessage(String msgId, String fromUserId, String toUserId, int time,
                                 int isTimeShow, String content, int type, float audiolen) {
        db = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("msg_id", msgId);
        contentValues.put("from_userid", fromUserId);
        contentValues.put("to_userid", toUserId);
        contentValues.put("time", time);
        contentValues.put("is_time_show", isTimeShow);
        contentValues.put("content", content);
        contentValues.put("type", type);
        contentValues.put("audiolen", audiolen);
        long i = db.insert("message", null, contentValues);
        return i != -1;
    }
    /**
     * 储存接收到的消息到最近消息表中
     */
    public boolean insertLastestMsgFrom(int type, String friendid, String lastestmsg, int lastesttime) {
        db = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("type", type);
        contentValues.put("fri_or_gro_id", friendid);
        contentValues.put("lastestmsg", lastestmsg);
        contentValues.put("lastesttime", lastesttime);
        long i = -1;
        if (!islastestMsgExist(type, friendid)) {
            contentValues.put("newmsgcount", 1);
            i = db.insert("recentmessage", null, contentValues);
        } else {
            int count = queryLastestMsgCount(type, friendid);
            count += 1;
            updateLastestMsg(friendid, lastestmsg, lastesttime, count);
            i = 0;
        }
        return i != -1;
    }
    //最近的消息是否存在
    public boolean islastestMsgExist(int type, String id) {
        db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from recentmessage where (type = ? and fri_or_gro_id = ?)",
                new String[]{String.valueOf(type), id});
        if (cursor.moveToFirst()) {
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }
    }
    //新消息的数目
    public int queryLastestMsgCount(int type, String id) {
        db = dbHelper.getReadableDatabase();
        int lastestMsgCount = 0;
        Cursor cursor = db.rawQuery("select newmsgcount from recentmessage where (type = ? and fri_or_gro_id = ?)",
                new String[]{String.valueOf(type), id});
        if (cursor.moveToFirst()) {
            do {
                lastestMsgCount = cursor.getInt(cursor.getColumnIndex("newmsgcount"));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lastestMsgCount;
    }
    //更新最近消息
    public void updateLastestMsg(String friendid, String lastestmsg, int lastesttime, int newmsgcount) {
        db = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("lastestmsg", lastestmsg);
        contentValues.put("lastesttime", lastesttime);
        contentValues.put("newmsgcount", newmsgcount);
        db.update("recentmessage", contentValues, "fri_or_gro_id = ?", new String[]{friendid});
    }
    public void updateLastestMsg(String friendid) {
        db = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("newmsgcount", 0);
        db.update("recentmessage", contentValues, "fri_or_gro_id = ?", new String[]{friendid});
    }
    //插入新邮件
    public boolean insertMail(String mailId, String fromUserId, String toUserId, int time, String content, String theme) {
        db = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("mail_id", mailId);
        contentValues.put("from_userid", fromUserId);
        contentValues.put("to_userid", toUserId);
        contentValues.put("time", time);
        contentValues.put("content", content);
        contentValues.put("theme", theme);
        contentValues.put("is_read", 0);
        long i = -1;
        if (!isMailExist(mailId)) {
            i = db.insert("mail", null, contentValues);
        }
        return i != -1;
    }
    //邮件是否存在
    public boolean isMailExist(String mailId) {
        db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from mail where mail_id = ?",
                new String[]{mailId});
        if (cursor.moveToFirst()) {
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }
    }

    public void updateFileDownload(String fileId, int state) {
        db = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("is_download", state);
        db.update("file", contentValues, "file_id=?", new String[]{fileId});
    }

    public void updateLocalPath(String fileId, String localPath) {
        db = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("local_path", localPath);
        db.update("file", contentValues, "file_id=?", new String[]{fileId});
    }

    public List<Msg> queryMessage(String userId1, String userId2, String time, int max) {
        db = dbHelper.getReadableDatabase();
        List<Msg> msgs = new ArrayList<Msg>();
        int i = 0;
        Cursor cursor = db.query("message", new String[]{"msg_id", "from_userid", "to_userid", "time",
                        "is_time_show", "content", "type", "audiolen"}, "time < ? and ((from_userid = ? and to_userid = ?) or (from_userid = ? and to_userid = ?))",
                new String[]{time, userId1, userId2, userId2, userId1}, null, null, "time desc");
        if (cursor.moveToFirst()) {
            do {
                Msg msg = new Msg();
                msg.setMsgId(cursor.getString(cursor.getColumnIndex("msg_id")));
                msg.setFromUserId(cursor.getString(cursor.getColumnIndex("from_userid")));
                msg.setToUserId(cursor.getString(cursor.getColumnIndex("to_userid")));
                msg.setTime(cursor.getInt(cursor.getColumnIndex("time")));
                msg.setIsTimeShow(cursor.getInt(cursor.getColumnIndex("is_time_show")));
                msg.setContent(cursor.getString(cursor.getColumnIndex("content")));
                msg.setType(cursor.getInt(cursor.getColumnIndex("type")));
                msg.setRecordtime(cursor.getFloat(cursor.getColumnIndex("audiolen")));
                msgs.add(0, msg);
                i++;
                if (i == max) {
                    break;
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return msgs;
    }
    /**
     * 储存发送的消息到最近消息表中
     */
    public boolean insertLastestMsgTo(int type, String friendid, String lastestmsg, int lastesttime) {
        db = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("type", type);
        contentValues.put("fri_or_gro_id", friendid);
        contentValues.put("lastestmsg", lastestmsg);
        contentValues.put("lastesttime", lastesttime);
        contentValues.put("newmsgcount", 0);
        long i = -1;
        if (!islastestMsgExist(type, friendid)) {
            i = db.insert("recentmessage", null, contentValues);
        } else {
            updateLastestMsg(friendid, lastestmsg, lastesttime, 0);
            i = 0;
        }
        return i != -1;
    }

    public MyFile queryFile(String fileId) {
        db = dbHelper.getReadableDatabase();
        MyFile myFile = new MyFile();
        Cursor cursor = db.query("file", new String[]{"file_id", "file_name", "file_from", "file_type", "time", "size", "local_path", "ftp_path"
                        , "is_file_transfer_finish", "is_download", "type"}, "file_id = ? ",
                new String[]{fileId}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                myFile.setFileId(cursor.getString(cursor.getColumnIndex("file_id")));
                myFile.setFileName(cursor.getString(cursor.getColumnIndex("file_name")));
                myFile.setFrom(cursor.getString(cursor.getColumnIndex("file_from")));
                myFile.setFileType(cursor.getString(cursor.getColumnIndex("file_type")));
                myFile.setSize(cursor.getLong(cursor.getColumnIndex("size")));
                myFile.setLocalPath(cursor.getString(cursor.getColumnIndex("local_path")));
                myFile.setFtpPath(cursor.getString(cursor.getColumnIndex("ftp_path")));
                myFile.setIsTransferFinish(cursor.getInt(cursor.getColumnIndex("is_file_transfer_finish")));
                myFile.setIsDownloadFinish(cursor.getInt(cursor.getColumnIndex("is_download")));
                myFile.setTime(cursor.getInt(cursor.getColumnIndex("time")));
                myFile.setType(cursor.getInt(cursor.getColumnIndex("type")));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return myFile;
    }

    public void updateIsFileTransferFinish(String fileId, int status) {
        db = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("is_file_transfer_finish", status);
        db.update("file", contentValues, "file_id = ?", new String[]{fileId});
    }
    /*文件列表*/
    public boolean insertFile(String fileId, String fileName, String from, String fileType, int time, String localpath, String ftppath,
                              long size, String md5, int type, int istransferfinish, int isdownloadfinish) {
        db = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("file_id", fileId);
        contentValues.put("file_name", fileName);
        contentValues.put("file_from", from);
        contentValues.put("file_type", fileType);
        contentValues.put("time", time);
        contentValues.put("local_path", localpath);
        contentValues.put("ftp_path", ftppath);
        contentValues.put("size", size);
        contentValues.put("md5", md5);
        contentValues.put("type", type);
        contentValues.put("is_file_transfer_finish", istransferfinish);
        contentValues.put("is_download", isdownloadfinish);
        long i = db.insert("file", null, contentValues);
        return i != -1;
    }
    public List<LastestMsg> queryLastestMsg() {
        db = dbHelper.getReadableDatabase();
        List<LastestMsg> lastestMsgs = new ArrayList<>();
        Cursor cursor = db.query("recentmessage", new String[]{"type", "fri_or_gro_id", "lastestmsg", "lastesttime", "groupmsgtype", "newmsgcount"}, null,
                null, null, null, "lastesttime desc");
        if (cursor.moveToFirst()) {
            do {
                LastestMsg lastestMsg = new LastestMsg();
                lastestMsg.setType(cursor.getInt(cursor.getColumnIndex("type")));
                lastestMsg.setId(cursor.getString(cursor.getColumnIndex("fri_or_gro_id")));
                lastestMsg.setGroupmsgtype(cursor.getInt(cursor.getColumnIndex("groupmsgtype")));
                lastestMsg.setLastestmsg(cursor.getString(cursor.getColumnIndex("lastestmsg")));
                lastestMsg.setLastesttime(cursor.getInt(cursor.getColumnIndex("lastesttime")));
                lastestMsg.setNewMsgCount(cursor.getInt(cursor.getColumnIndex("newmsgcount")));
                lastestMsgs.add(lastestMsg);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lastestMsgs;
    }

    /**
     * 根据mailId彻底删除邮件,从数据库里删除
     */
    public void deleteMailById(String mailId) {
        db = dbHelper.getWritableDatabase();
        db.delete("mail", "mail_id = ?", new String[]{mailId});
    }

    public List<MailInfo> queryMail() {
        db = dbHelper.getReadableDatabase();
        List<MailInfo> mails = new ArrayList<>();
        Cursor cursor = db.query("mail", new String[]{"mail_id", "from_userid", "to_userid", "time",
                "content", "theme", "is_read"}, null, null, null, null, "time asc");
        if (cursor.moveToFirst()) {
            do {
                MailInfo mail = new MailInfo();
                mail.setMailId(cursor.getString(cursor.getColumnIndex("mail_id")));
                mail.setFromUserId(cursor.getString(cursor.getColumnIndex("from_userid")));
                mail.setToUserId(cursor.getString(cursor.getColumnIndex("to_userid")));
                mail.setTime(cursor.getInt(cursor.getColumnIndex("time")));
                mail.setContent(cursor.getString(cursor.getColumnIndex("content")));
                mail.setTheme(cursor.getString(cursor.getColumnIndex("theme")));
                mail.setIsRead(cursor.getInt(cursor.getColumnIndex("is_read")));
                mails.add(0, mail);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return mails;
    }
}
