package com.app.friendCircleMain.domain;

public class FirstMicroListDatasFirendcomment extends MyBaseBean{
	private String id;
	private String sid;
	private String replyId;//回复人id
	private String replyName;//姓名
	private String isReplyId;//被回复人
	private String isReplyName;
	private String comment;
	private String replytime;
	private String usercommentflag;//是不是自己发的，Y:自己的;N：别人的
	
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
	public String getReplyId() {
		return replyId;
	}
	public void setReplyId(String replyId) {
		this.replyId = replyId;
	}
	public String getReplyName() {
		return replyName;
	}
	public void setReplyName(String replyName) {
		this.replyName = replyName;
	}
	public String getIsReplyId() {
		return isReplyId;
	}
	public void setIsReplyId(String isReplyId) {
		this.isReplyId = isReplyId;
	}
	public String getIsReplyName() {
		return isReplyName;
	}
	public void setIsReplyName(String isReplyName) {
		this.isReplyName = isReplyName;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getReplytime() {
		return replytime;
	}
	public void setReplytime(String replytime) {
		this.replytime = replytime;
	}
	public String getUsercommentflag() {
		return usercommentflag;
	}
	public void setUsercommentflag(String usercommentflag) {
		this.usercommentflag = usercommentflag;
	}
}
