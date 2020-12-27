package com.app.friendCircleMain.domain;

import java.util.List;

public class FriendMicroListDatas extends MyBaseBean{
	private String postid;
	private String id;
	private String content;
	private String create_time;
	private String like_num;
	private String nickname;
	private String avatar;
	private List<FirstMicroListDatasFirendpraise> addlike_nickname;
	private List<FirstMicroListDatasFirendimage> post_pic;//图片
	private List<FirstMicroListDatasFirendpraiseType> owntype;

	public List<FirstMicroListDatasFirendimage> getPost_pic() {
		return post_pic;
	}

	public String getContent() {
		return content;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getCreate_time() {
		return create_time;
	}

	public String getId() {
		return id;
	}

	public String getLike_num() {
		return like_num;
	}

	public List<FirstMicroListDatasFirendpraiseType> getOwntype() {
		return owntype;
	}

	public String getPostid() {
		return postid;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setLike_num(String like_num) {
		this.like_num = like_num;
	}


	public void setPost_pic(List<FirstMicroListDatasFirendimage> post_pic) {
		this.post_pic = post_pic;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public void setPostid(String postid) {
		this.postid = postid;
	}

	public List<FirstMicroListDatasFirendpraise> getAddlike_name(){return addlike_nickname;}
	public void setAddlike_nickname(List<FirstMicroListDatasFirendpraise> addlike_nickname) {
		this.addlike_nickname = addlike_nickname;
	}
	//	private String id;
//	private String content;//内容
//	private String uid;
//	private String uname;
	private String companykey;
//	private String sendtime;//发送时间
	private String userflag;//Y:自己发的;N：别人发的
	private String collectionflag;//收藏标识
	private String praiseflag;//点赞标识
	private String praiseusers;//点赞的用户
	private String mentionusers;//提醒用户串
//	private String usericon;//头像路径
	public synchronized String getPraiseusers() {
		return praiseusers;
	}
	public synchronized void setPraiseusers(String praiseusers) {
		this.praiseusers = praiseusers;
	}
	public synchronized String getMentionusers() {
		return mentionusers;
	}
	public synchronized void setMentionusers(String mentionusers) {
		this.mentionusers = mentionusers;
	}
//	public synchronized String getUsericon() {
//		return usericon;
//	}
//	public synchronized void setUsericon(String usericon) {
//		this.usericon = usericon;
//	}
//	private List<FirstMicroListDatasFirendimage> friendimage;//图片路径
	private List<FirstMicroListDatasFirendcomment> friendcomment;//评论
	private List<FirstMicroListDatasFirendpraise> friendpraise;//点赞
	private List<FirstMicroListDatasFirendmention> friendmention;//@提醒
//
	public String getCollectionflag() {
		return collectionflag;
	}
	public void setCollectionflag(String collectionflag) {
		this.collectionflag = collectionflag;
	}
	public String getPraiseflag() {
		return praiseflag;
	}
	public void setPraiseflag(String praiseflag) {
		this.praiseflag = praiseflag;
	}
//	public String getId() {
//		return id;
//	}
//	public void setId(String id) {
//		this.id = id;
//	}
//	public String getContent() {
//		return content;
//	}
//	public void setContent(String content) {
//		this.content = content;
//	}
//	public String getUid() {
//		return uid;
//	}
//	public void setUid(String uid) {
//		this.uid = uid;
//	}
//	public String getUname() {
//		return uname;
//	}
//	public void setUname(String uname) {
//		this.uname = uname;
//	}
	public String getCompanykey() {
		return companykey;
	}
	public void setCompanykey(String companykey) {
		this.companykey = companykey;
	}
//	public String getSendtime() {
//		return sendtime;
//	}
//	public void setSendtime(String sendtime) {
//		this.sendtime = sendtime;
//	}
	public String getUserflag() {
		return userflag;
	}
	public void setUserflag(String userflag) {
		this.userflag = userflag;
	}
//	public List<FirstMicroListDatasFirendimage> getFriendimage() {
//		return friendimage;
//	}
//	public void setFriendimage(List<FirstMicroListDatasFirendimage> friendimage) {
//		this.friendimage = friendimage;
//	}
public void setOwntype(List<FirstMicroListDatasFirendpraiseType> owntype) {
	this.owntype = owntype;
}
	public List<FirstMicroListDatasFirendpraise> getAddlike_nickname() {
		return addlike_nickname;
	}
	public List<FirstMicroListDatasFirendcomment> getFriendComment() {
		return friendcomment;
	}
	public void setFriendcomment(
			List<FirstMicroListDatasFirendcomment> friendcomment) {
		this.friendcomment = friendcomment;
	}
	public List<FirstMicroListDatasFirendpraise> getFriendpraise() {
		return friendpraise;
	}
	public void setFriendpraise(List<FirstMicroListDatasFirendpraise> friendpraise) {
		this.friendpraise = friendpraise;
	}
	public List<FirstMicroListDatasFirendmention> getFriendmention() {
		return friendmention;
	}
	public void setFriendmention(
			List<FirstMicroListDatasFirendmention> friendmention) {
		this.friendmention = friendmention;
	}
}
