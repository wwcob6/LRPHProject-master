package com.app.friendCircleMain.domain;

public class FirstMicroListDatasFirendmention extends MyBaseBean{
	private String id;
	private String sid;
	private String uid;
	private String uname;//用户名
	private String isclick;//点没点，@提醒
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getSid() {
		return sid;
	}
	public void setSid(String sid) {
		this.sid = sid;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public synchronized String getUname() {
		return uname;
	}
	public synchronized void setUname(String uname) {
		this.uname = uname;
	}
	public synchronized String getIsclick() {
		return isclick;
	}
	public synchronized void setIsclick(String isclick) {
		this.isclick = isclick;
	}
}
