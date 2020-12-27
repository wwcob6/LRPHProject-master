package com.app.friendCircleMain.domain;

public class FirstMicroListDatasFirendpraise extends MyBaseBean{
	private String id;
	private String sid;
	private String uid;
	private String uname;
	private String nickname;
	private String praisetype;
	
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
	public String getUname() {
		return uname;
	}
	public void setUname(String uname) {
		this.uname = uname;
	}

	public void setNickname(String nickname){this.nickname=nickname;}

	public  String getNickname(){return nickname;}
	public String getPraisetype() {
		return praisetype;
	}

	public void setPraisetype(String praisetype) {
		this.praisetype = praisetype;
	}

	@Override
	public boolean equals(Object obj){
		if(obj==null){
			return false;
		}
		FirstMicroListDatasFirendpraise o = (FirstMicroListDatasFirendpraise) obj;
//		return this.id.equals(o.id);
		return this.nickname.equals(o.nickname);
	}
}
