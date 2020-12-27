package com.app.friendCircleMain.util;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.app.R;
import com.app.UserInfoManager;
import com.app.friendCircleMain.domain.FirstMicroListDatasFirendcomment;
import com.app.friendCircleMain.domain.FirstMicroListDatasFirendpraise;
import com.app.friendCircleMain.domain.FirstMicroListDatasFirendpraiseType;
import com.app.friendCircleMain.domain.FriendMicroListDatas;
import com.app.friendCircleMain.event.FriendRefreshEvent;
import com.app.friendCircleMain.viewholder.FriendPraiseViewHolder;
import com.app.request.AddCommentRequest;
import com.app.request.AddLikeRequest;
import com.app.request.DeleteLikeRequest;
import com.app.request.UpdateLikeRequest;
import com.punuo.sys.app.httplib.HttpManager;
import com.punuo.sys.app.httplib.RequestListener;
import com.punuo.sys.app.util.CommonUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by han.chen.
 * Date on 2019-06-10.
 **/
public class PopupWindowUtil {
    private static final String TAG = "PopupWindowUtil";

    @Bind(R.id.btnPraise)
    ImageView mBtnPraise;
    @Bind(R.id.express1)
    ImageView mExpress1;
    @Bind(R.id.express2)
    ImageView mExpress2;
    @Bind(R.id.express3)
    ImageView mExpress3;
    @Bind(R.id.btnComment)
    LinearLayout mBtnComment;

    private PopupWindow mPopupWindow;
    private View mView;
    private FriendMicroListDatas bean;
    private int position; //bean 在列表中的位置
    private Context mContext;
    private MyCustomDialog mCommentDialog;

    public PopupWindowUtil(Context context) {
        mContext = context;
        mView = LayoutInflater.from(context).inflate(R.layout.parise_pop_layout, null);
        ButterKnife.bind(this, mView);
        mPopupWindow = new PopupWindow(mView, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setTouchable(true);
        mPopupWindow.setOutsideTouchable(true);
        mView.setFocusableInTouchMode(true);
        mView.setFocusable(true);
        initView();
    }

    private void initView() {
        //评论按钮
        mBtnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //显示评论的对话框
                if (mCommentDialog != null && mCommentDialog.isShowing()) {
                    return;
                }
                mCommentDialog = new MyCustomDialog(mContext, R.style.add_dialog, "评论" +
                        bean.getNickname() + "的说说", new MyCustomDialog.OnCustomDialogListener() {
                    //点击对话框'提交'以后
                    public void back(String content) {
                        if (!TextUtils.isEmpty(content)) {
                            addComment(content);//提交评论
                        }
                    }
                });
                mCommentDialog.setCanceledOnTouchOutside(true);
                mCommentDialog.show();
                mPopupWindow.dismiss();
            }
        });
        //点赞按钮       praise:是否已经点赞了
        // true:已经点赞了，这样textView上面应该显示“取消”；false:没有点赞，textView上面应该显示“点赞”；默认为false
        mBtnPraise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitPraise(FriendPraiseViewHolder.TYPE_DIANZAN);
            }
        });

        mExpress1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitPraise(FriendPraiseViewHolder.TYPE_WEIXIAO);
            }
        });

        mExpress2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitPraise(FriendPraiseViewHolder.TYPE_DAXIAO);
            }
        });

        mExpress3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitPraise(FriendPraiseViewHolder.TYPE_KUXIAO);
            }
        });
    }

    public void setFriendMicroListDatas(FriendMicroListDatas bean, int position) {
        this.bean = bean;
        this.position = position;
    }

    private List<FirstMicroListDatasFirendpraise> friendPraise;
    private List<FirstMicroListDatasFirendpraiseType> ownType;
    private FirstMicroListDatasFirendpraiseType own;

    /**
     * @param praiseType 点击的类型
     */
    private void submitPraise(String praiseType) {
        friendPraise = bean.getAddlike_nickname();
        if (friendPraise == null) {
            friendPraise = new ArrayList<>();
            bean.setAddlike_nickname(friendPraise);
        }
        ownType = bean.getOwntype();
        if (ownType == null) {
            ownType = new ArrayList<>();
            bean.setOwntype(ownType);
        }
        String prePraiseType = "N";
        if (!ownType.isEmpty()) {
            own = ownType.get(0);
            prePraiseType = own.getPraisetype();
        } else {
            own = new FirstMicroListDatasFirendpraiseType();
        }
        FirstMicroListDatasFirendpraise praise = new FirstMicroListDatasFirendpraise();
        praise.setNickname(UserInfoManager.getUserInfo().nickname);
        if ("N".equals(bean.getPraiseflag())) {
            addLike(praiseType);
        } else if ("Y".equals(bean.getPraiseflag())) {
            if (praiseType.equals(prePraiseType)) {
                deleteLike(praiseType);
            } else {
                updateLike(praiseType);
            }
        }
    }

    private AddLikeRequest mAddLikeRequest;

    private void addLike(String praiseType) {
        if (mAddLikeRequest != null && !mAddLikeRequest.isFinish()) {
            return;
        }
        mAddLikeRequest = new AddLikeRequest();
        mAddLikeRequest.addUrlParam("postid", bean.getPostid());
        mAddLikeRequest.addUrlParam("id", UserInfoManager.getUserInfo().id);
        mAddLikeRequest.addUrlParam("praisetype", praiseType);
        mAddLikeRequest.setRequestListener(new RequestListener<String>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onSuccess(String result) {
                if (result == null) {
                    return;
                }
                FirstMicroListDatasFirendpraise praise = new FirstMicroListDatasFirendpraise();
                praise.setId(UserInfoManager.getUserInfo().id);
                praise.setNickname(UserInfoManager.getUserInfo().nickname);
                praise.setPraisetype(praiseType);
                own.setPraisetype(praiseType);
                ownType.add(own);
                friendPraise.add(praise);
                bean.setPraiseflag("Y");
                EventBus.getDefault().post(new FriendRefreshEvent());
                mPopupWindow.dismiss();
            }

            @Override
            public void onError(Exception e) {

            }
        });
        HttpManager.addRequest(mAddLikeRequest);
    }

    private DeleteLikeRequest mDeleteLikeRequest;

    private void deleteLike(String praiseType) {
        if (mDeleteLikeRequest != null && !mDeleteLikeRequest.isFinish()) {
            return;
        }
        mDeleteLikeRequest = new DeleteLikeRequest();
        mDeleteLikeRequest.addUrlParam("postid", bean.getPostid());
        mDeleteLikeRequest.addUrlParam("id", UserInfoManager.getUserInfo().id);
        mDeleteLikeRequest.addUrlParam("praisetype", praiseType);
        mDeleteLikeRequest.setRequestListener(new RequestListener<String>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onSuccess(String result) {
                if (result == null) {
                    return;
                }
                FirstMicroListDatasFirendpraise praise = new FirstMicroListDatasFirendpraise();
                praise.setId(UserInfoManager.getUserInfo().id);
                praise.setNickname(UserInfoManager.getUserInfo().nickname);
                int index = friendPraise.indexOf(praise);
                if (index != -1) {
                    friendPraise.remove(index);
                }
                bean.setPraiseflag("N");
                ownType.clear();
                EventBus.getDefault().post(new FriendRefreshEvent());
                mPopupWindow.dismiss();
            }

            @Override
            public void onError(Exception e) {

            }
        });
        HttpManager.addRequest(mDeleteLikeRequest);
    }

    private UpdateLikeRequest mUpdateLikeRequest;

    private void updateLike(String praiseType) {
        if (mUpdateLikeRequest != null && !mUpdateLikeRequest.isFinish()) {
            return;
        }
        mUpdateLikeRequest = new UpdateLikeRequest();
        mUpdateLikeRequest.addUrlParam("postid", bean.getPostid());
        mUpdateLikeRequest.addUrlParam("id", UserInfoManager.getUserInfo().id);
        mUpdateLikeRequest.addUrlParam("praisetype", praiseType);
        mUpdateLikeRequest.setRequestListener(new RequestListener<String>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onSuccess(String result) {
                if (result == null) {
                    return;
                }
                FirstMicroListDatasFirendpraise praise = new FirstMicroListDatasFirendpraise();
                praise.setId(UserInfoManager.getUserInfo().id);
                praise.setNickname(UserInfoManager.getUserInfo().nickname);
                int index = friendPraise.indexOf(praise);
                if (index != -1) {
                    FirstMicroListDatasFirendpraise firstMicroListDatasFirendpraise = friendPraise.get(index);
                    firstMicroListDatasFirendpraise.setPraisetype(praiseType);
                    own.setPraisetype(praiseType);
                }
                bean.setPraiseflag("Y");
                EventBus.getDefault().post(new FriendRefreshEvent());
                mPopupWindow.dismiss();
            }

            @Override
            public void onError(Exception e) {

            }
        });
        HttpManager.addRequest(mUpdateLikeRequest);
    }

    private AddCommentRequest mAddCommentRequest;

    private void addComment(String content) {
        if (mAddCommentRequest != null && !mAddCommentRequest.isFinish()) {
            return;
        }
        mAddCommentRequest = new AddCommentRequest();
        mAddCommentRequest.addUrlParam("id", UserInfoManager.getUserInfo().id);
        mAddCommentRequest.addUrlParam("postid", bean.getPostid());
        mAddCommentRequest.addUrlParam("content", content);
        mAddCommentRequest.setRequestListener(new RequestListener<String>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onSuccess(String result) {
                List<FirstMicroListDatasFirendcomment> firendcomments = bean.getFriendComment();
                if (null == firendcomments) {
                    firendcomments = new ArrayList<>();
                    bean.setFriendcomment(firendcomments);
                }
                FirstMicroListDatasFirendcomment comments = new FirstMicroListDatasFirendcomment();
                comments.setId(UserInfoManager.getUserInfo().id);
                comments.setReplyName(UserInfoManager.getUserInfo().nickname);
                comments.setComment(content);
                firendcomments.add(comments);
                EventBus.getDefault().post(new FriendRefreshEvent());
            }

            @Override
            public void onError(Exception e) {

            }
        });
        HttpManager.addRequest(mAddCommentRequest);
    }

    public void show(View view) {
        if (bean == null) {
            Log.e(TAG, "data is null");
            return;
        }
        mPopupWindow.showAsDropDown(view, -CommonUtil.dip2px(170f), -CommonUtil.dip2px(70f));
    }
}
