package com.app.groupvoice;

/**
 * Created by chenblue23 on 2016/7/5.
 */
public class GroupSignaling {
    private String devid;
    private String signal;
    private String start;
    private String end;
    private String level;

    public String getDevid() {
        return devid;
    }

    public String getSignal() {
        return signal;
    }

    public String getStart() {
        return start;
    }

    public String getEnd() {
        return end;
    }

    public String getLevel() {
        return level;
    }

    public void setDevid(String devid) {
        this.devid = devid;
    }

    public void setSignal(String signal) {
        this.signal = signal;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public void setLevel(String level) {
        this.level = level;
    }
    public String toRTPHeartBeatString(){
       return  "{"+"\"devid\":"+"\""+devid+"\""+"}";
    }
    public String toHeartBeatString(){
        return  "{"+"\"signal\":"+"\""+devid+"\""+"}";
    }
}
