package com.punuo.sys.app.util;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by han.chen.
 * Date on 2019-06-09.
 **/
public class TimeUtils {

    /**
     * 仿qq或微信的时间显示
     * 时间比较
     * date 当前时间
     * strTime 获取的时间
     */
    public static String getTimes(String date, String strTime) {
        // TODO Auto-generated method stub
        String intIime = "";
        long i = -1;//获取相差的天数
        long i1 = -1;//获取相差的小时
        long i2 = -1;//获取相差的分
        long i3 = -1;//获取相差的
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            ParsePosition pos = new ParsePosition(0);
            ParsePosition pos1 = new ParsePosition(0);
            Date dt1 = formatter.parse(date, pos);
            Date dt2 = formatter.parse(strTime, pos1);
            long l = dt1.getTime() - dt2.getTime();

            i = l / (1000 * 60 * 60 * 24);//获取的如果是0，表示是当天的，如果>0的话是以前发的
            if (0 == i) {//今天发的
                i1 = l / (1000 * 60 * 60);
                if (0 == i1) {//xx分之前发的
                    i2 = l / (1000 * 60);
                    if (0 == i2) {//xx秒之前发的
                        i3 = l / (1000);
                        intIime = i3 + "秒钟以前";
                    } else {
                        intIime = i2 + "分钟以前";
                    }
                } else {
                    intIime = i1 + "小时以前";//xx小时之前发的
                }
            } else {//以前发的
                intIime = i + "天以前";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return intIime;
    }
}
