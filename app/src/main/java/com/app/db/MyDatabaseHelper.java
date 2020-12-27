package com.app.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Author chzjy
 * Date 2016/12/19.
 */

public class MyDatabaseHelper extends SQLiteOpenHelper {
    private Context context;
    private String CREATE_MESSAGE = "create table message (id integer primary key autoincrement, "
            + "msg_id varchar(45), from_userid varchar(32), to_userid varchar(32), time " +
            "" +
            "" +
            "integer, "
            + "is_time_show integer,content varchar(2000),type integer,audiolen float)";
    private String CREATE_FILE = "create table file (id integer primary key autoincrement, "
            + "file_id varchar(45), file_name varchar(45),file_from varchar(32), time integer, size integer, "
            + "local_path varchar(80),ftp_path varchar(80), md5 varchar(45), file_type varchar(10), type integer," +
            "is_file_transfer_finish integer,is_download integer)";
//    private String CREATE_USERS = "create table user (id integer primary key autoincrement, "
//            + "userid varchar(32), friend_count integer, change_time integer)";
    private String CREATE_RECENT_MESSAGE = "create table recentmessage (id integer primary key autoincrement,"
            + "type integer,fri_or_gro_id varchar(32),lastestmsg varchar(2000),lastesttime integer,groupmsgtype integer,newmsgcount integer)";
    private String CREATE_APP = "create table app (id integer primary key autoincrement, "
            + "app_id  varchar(45),app_name varchar(45), app_size integer,local_path varchar(80),state integer)";
    private String CREATE_MAIL = "create table mail (id integer primary key autoincrement, "
            + "mail_id varchar(45), from_userid varchar(32), to_userid varchar(32), time integer, " +
            "content varchar(2000),theme varchar(100),is_read integer,state integer)";
    public static final String CREATE_PERSON="create table Person("+"id integer primary key autoincrement, "
            + "avatorurl text, " + "name text, " + "phonenumber text)";
    public MyDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_APP);
        db.execSQL(CREATE_MESSAGE);
        db.execSQL(CREATE_FILE);
        db.execSQL(CREATE_MAIL);
        db.execSQL(CREATE_RECENT_MESSAGE);
        db.execSQL(CREATE_PERSON);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists message");
        db.execSQL("drop table if exists file");
        db.execSQL("drop table if exists recentmessage");
        db.execSQL("drop table if exists mail");
        db.execSQL(CREATE_MESSAGE);
        db.execSQL(CREATE_FILE);
        db.execSQL(CREATE_RECENT_MESSAGE);
        db.execSQL(CREATE_MAIL);
    }
}
