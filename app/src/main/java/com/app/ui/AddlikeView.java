package com.app.ui;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.app.R;
import com.app.UserInfoManager;
import com.app.friendCircleMain.adapter.CommentAdapter;
import com.app.http.GetPostUtil;
import com.app.model.Comments;
import com.app.model.Constant;
import com.app.model.MessageEvent;
import com.app.sip.SipInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AddlikeView extends AppCompatActivity {

    @Bind(R.id.rv_addlikes)
    RecyclerView rvAddlikes;
    @Bind(R.id.tv_noAddlikes)
    TextView tvNoAddlikes;
    private SimpleDateFormat df;
    List<Comments>  commentsList=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addlike_view);
        ButterKnife.bind(this);
        if(SipInfo.addLikesItems==0){
            tvNoAddlikes.setVisibility(View.VISIBLE);
        }else {
            tvNoAddlikes.setVisibility(View.INVISIBLE);
        }
        df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        initView();
//        sendRequestWithOkHttp();
//        recyclerView=(RecyclerView)findViewById(R.id.rv_comments);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvAddlikes.setLayoutManager(layoutManager);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//因为不是所有的系统都可以设置颜色的，在4.4以下就不可以。。有的说4.1，所以在设置的时候要检查一下系统版本是否是4.1以上
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.newbackground));
        }
    }

    private void initView() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String response = GetPostUtil.sendGet1111(Constant.URL_getNewLikes,
                        "id=" + UserInfoManager.getUserInfo().id + "&currentTime=" + df.format(new Date()));
                if ((response != null) && !("".equals(response))) {
                    JSONObject obj= JSON.parseObject(response);
                    String msg=obj.getString("msg");
                    if(msg.equals("success")){
                        praseJSONWithGSON(response);
                    }else {
                        handler.sendEmptyMessage(0x222);
                    }
                }
            }
        }).start();
    }

    private void praseJSONWithGSON(String response) {
        String jsonData = "[" + response.split("\\[")[1].split("\\]")[0] + "]";
        Gson gson = new Gson();
        commentsList = gson.fromJson(jsonData, new TypeToken<List<Comments>>() {
        }.getType());
        for (int i = 0; i < commentsList.size(); i++) {
            Comments comments = new Comments();
           comments.setPraisetype(commentsList.get(i).getPraisetype());
            comments.setReplyName(commentsList.get(i).getReplyName());
            comments.setCreate_time(commentsList.get(i).getCreate_time());
            comments.setId(commentsList.get(i).getId());
            comments.setAvatar(commentsList.get(i).getAvatar());
            comments.setPic(commentsList.get(i).getPic());
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                handler.sendEmptyMessage(0x111);
            }
        }).start();
    }
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            switch (msg.what){
                case 0x111:
                    tvNoAddlikes.setVisibility(View.INVISIBLE);
                    CommentAdapter adapter = new CommentAdapter(commentsList);
                    rvAddlikes.setAdapter(adapter);
                    SipInfo.addLikesItems=0;
                    EventBus.getDefault().post(new MessageEvent("取消新点赞提示"));
                    break;
                case 0x222:
                    tvNoAddlikes.setVisibility(View.VISIBLE);
                    SipInfo.addLikesItems=0;
                    break;
                    default:
                        break;
            }
        }
    };
}
