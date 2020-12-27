package com.app.ui;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.app.R;
import com.app.adapter.PhoneRecyclerViewAdapter;
import com.app.db.MyDatabaseHelper;
import com.app.model.Familymember;
import com.app.model.MessageEvent;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.punuo.sys.app.activity.BaseActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.app.sip.SipInfo.farmilymemberList;


public class FriendCallActivity extends BaseActivity {
    private String TAG = "FriendCallActivity";
    private RecyclerView rv;
    private List<String> images = new ArrayList<>();//图片地址
    private Context mContext;
    private DisplayImageOptions options;
    private PhoneRecyclerViewAdapter adapter;
    private HashMap<Integer, float[]> xyMap = new HashMap<Integer, float[]>();//所有子项的坐标
    private int screenWidth;//屏幕宽度
    private int screenHeight;//屏幕高度
    private int callPosition;
    MyDatabaseHelper dbHelper;
    Familymember far;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_call);
        mContext = this;
        EventBus.getDefault().register(this);
        initView();
        dbHelper = new MyDatabaseHelper(this, "member.db", null, 2);
//       initData();
        putdata();

        setEvent();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//因为不是所有的系统都可以设置颜色的，在4.4以下就不可以。。有的说4.1，所以在设置的时候要检查一下系统版本是否是4.1以上
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.newbackground));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if (event.getMessage().equals("addcompelete")) {
            Log.d(TAG, "receicer");
            farmilymemberList.clear();
            putdata();
//            farmilyAdapter.refreshDatas(farmilymemberList);
        }
    }

    private void putdata() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("Person", null, null, null, null, null, null);
        Log.d("ton", "11111");
        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String phonenumber = cursor.getString(cursor.getColumnIndex("phonenumber"));
                String avatorurl = cursor.getString(cursor.getColumnIndex("avatorurl"));
                far = new Familymember(name, phonenumber, avatorurl);
                farmilymemberList.add(far);
                adapter.notifyDataSetChanged();
                Log.d("tongxunlu", name + " " + phonenumber);
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        screenWidth = wm.getDefaultDisplay().getWidth();
        screenHeight = wm.getDefaultDisplay().getHeight();
    }

    /**
     * recyclerView item点击事件
     */
    private void setEvent() {
        adapter.setmOnItemClickListener(new PhoneRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                callPosition=position;
                if (hasSimCard(FriendCallActivity.this)) {
                    if (ContextCompat.checkSelfPermission(mContext, android.Manifest.permission
                            .CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(FriendCallActivity.this, new String[]{
                                android.Manifest.permission.CALL_PHONE
                        }, 102);
                    } else {
                        call(position);
                    }
                } else {
                    Toast.makeText(FriendCallActivity.this, "请插入SIM卡", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void call(int position) {
        try {
            Intent intent = new Intent(Intent.ACTION_CALL);
            Uri data = Uri.parse("tel:" + farmilymemberList.get(position).getPhonenumber());
            intent.setData(data);

//                Intent intent=new Intent(mContext,AlbumSecondActivity.class);
//                intent.putStringArrayListExtra("urls", (ArrayList<String>) images);
            intent.putExtra("position", position);


            xyMap.clear();//每一次点击前子项坐标都不一样，所以清空子项坐标

            //子项前置判断，是否在屏幕内，不在的话获取屏幕边缘坐标
            View view0 = rv.getChildAt(0);
            int position0 = rv.getChildPosition(view0);
            if (position0 > 0) {
                for (int j = 0; j < position0; j++) {
                    float[] xyf = new float[]{(1 / 6.0f + (j % 3) * (1 / 3.0f)) * screenWidth, 0};//每行3张图，每张图的中心点横坐标自然是屏幕宽度的1/6,3/6,5/6
                    xyMap.put(j, xyf);
                }
            }

            //其余子项判断
            for (int i = position0; i < rv.getAdapter().getItemCount(); i++) {
                View view1 = rv.getChildAt(i - position0);
                if (rv.getChildPosition(view1) == -1)//子项末尾不在屏幕部分同样赋值屏幕底部边缘
                {
                    float[] xyf = new float[]{(1 / 6.0f + (i % 3) * (1 / 3.0f)) * screenWidth, screenHeight};
                    xyMap.put(i, xyf);
                } else {
                    int[] xy = new int[2];
                    view1.getLocationOnScreen(xy);
                    float[] xyf = new float[]{xy[0] * 1.0f + view1.getWidth() / 2, xy[1] * 1.0f + view1.getHeight() / 2};
                    xyMap.put(i, xyf);
                }
            }
            intent.putExtra("xyMap", xyMap);
//                        if (ContextCompat.checkSelfPermission(FriendCallActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
//                                || ContextCompat.checkSelfPermission(FriendCallActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//                            startActivity(intent);
//                        }
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void initView() {
        rv = (RecyclerView) findViewById(R.id.rv);
        GridLayoutManager glm = new GridLayoutManager(mContext, 3);//定义3列的网格布局
        rv.setLayoutManager(glm);
        rv.addItemDecoration(new FriendCallActivity.RecyclerViewItemDecoration(30, 3));//初始化子项距离和列数
        options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.pictureloading)
                .showImageOnLoading(R.drawable.pictureloading)
                .showImageOnFail(R.drawable.pictureloading)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .displayer(new FadeInBitmapDisplayer(1))
                .build();
        adapter = new PhoneRecyclerViewAdapter(images, mContext, options, glm);
        rv.setAdapter(adapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FriendCallActivity.this, AddressAddActivity.class));
            }
        });
    }

    public class RecyclerViewItemDecoration extends RecyclerView.ItemDecoration
    {
        private int itemSpace;//定义子项间距
        private int itemColumnNum;//定义子项的列数

        public RecyclerViewItemDecoration(int itemSpace, int itemColumnNum) {
            this.itemSpace = itemSpace;
            this.itemColumnNum = itemColumnNum;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.bottom=itemSpace;//底部留出间距
            if(parent.getChildPosition(view)%itemColumnNum==0)//每行第一项左边不留间距，其他留出间距
            {
                outRect.left=0;
            }
            else
            {
                outRect.left=itemSpace;
            }

        }
    }

    /**
     * 重写startActivity方法，禁用activity默认动画
     * @param intent
     */
    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(0,0);
    }

    @Override
    protected void onDestroy() {
        farmilymemberList.clear();
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    public static boolean hasSimCard(Context context) {
        TelephonyManager telMgr = (TelephonyManager)
                context.getSystemService(Context.TELEPHONY_SERVICE);
        int simState = telMgr.getSimState();
        boolean result = true;
        switch (simState) {
            case TelephonyManager.SIM_STATE_ABSENT:
                result = false; // 没有SIM卡
                break;
            case TelephonyManager.SIM_STATE_UNKNOWN:
                result = false;
                break;
        }
        Log.d("try", result ? "有SIM卡" : "无SIM卡");
        return result;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults){
        switch (requestCode){
            case 102:
                if (grantResults.length > 0 && grantResults[0] ==PackageManager.PERMISSION_GRANTED) {
                    call(callPosition);
                }else{
                    Toast.makeText(this,"You denied the permission",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }



}
