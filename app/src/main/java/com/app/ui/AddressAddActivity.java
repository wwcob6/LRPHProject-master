package com.app.ui;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.app.R;
import com.app.adapter.MyRecyclerViewAdapter;
import com.app.db.MyDatabaseHelper;
import com.app.model.MessageEvent;
import com.app.view.CircleImageView;
import com.app.views.CleanEditText;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.punuo.sys.app.activity.BaseSwipeBackActivity;
import com.punuo.sys.app.util.PreferenceUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.app.sip.SipInfo.dbHelper;


public class AddressAddActivity extends BaseSwipeBackActivity implements View.OnClickListener {
    @Bind(R.id.add)
    Button add;
    @Bind(R.id.selectavator)
    CircleImageView selectavator;
    String type1;
    String call1;
    @Bind(R.id.edit_name)
    CleanEditText edit_name;
    @Bind(R.id.edit_number)
    CleanEditText edit_number;
    @Bind(R.id.back)
    ImageView back;
    @Bind(R.id.title)
    TextView title;

    private String avatorurl = null;
    private HashMap<String, String> mIatResults = new LinkedHashMap<>();
    private PopupWindow popupWindow;
    private int from = 0;
    private Context mContext;
    private DisplayImageOptions options;
    private MyRecyclerViewAdapter adapter;
    private List<String> images = new ArrayList<>();
    String extra_name;
    String extra_phonenumber;
    String extra_avatorurl;
    private static String imageName = "";
    String SdCard = Environment.getExternalStorageDirectory().getAbsolutePath();
    String avaPath = SdCard + "/fanxin/Files/Camera/Images/";
    private static final int PHOTO_REQUEST_TAKEPHOTO = 1;// 拍照
    private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
    private static final int PHOTO_REQUEST_CUT = 3;// 结果

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addressadd);
        ButterKnife.bind(this);
        mContext = this;
        initView();
    }

    public void initView() {
        title.setText("修改信息");

        dbHelper = new MyDatabaseHelper(this, "member.db", null, 2);
        add.setOnClickListener(this);
        edit_name.setOnClickListener(this);
        edit_number.setOnClickListener(this);
        selectavator.setOnClickListener(this);
//        showUserAvatar(selectavator, vatar_temp);
//        pref = PreferenceManager.getDefaultSharedPreferences(this);
//        editor1 = getSharedPreferences("data", MODE_PRIVATE).edit();
        Intent intent = getIntent();
        extra_avatorurl = intent.getStringExtra("extra_avatorurl");
        extra_name = intent.getStringExtra("extra_name");
        extra_phonenumber = intent.getStringExtra("extra_phonenumber");
        edit_name.setText(extra_name);
        edit_number.setText(extra_phonenumber);

        if (extra_avatorurl != null) {
            ImageLoader.getInstance().displayImage(extra_avatorurl, selectavator);
        }
        if (extra_name != null || extra_phonenumber != null) {
            add.setText("修改");
        } else {
            add.setText("添加");
        }
    }

    @OnClick({R.id.add, R.id.selectavator, R.id.edit_name, R.id.edit_number, R.id.back})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.selectavator:
                Log.d("address", "run: ");
//            showPhotoDialog();
                from = Location.RIGHT.ordinal();
                initPopupWindow();
                break;
            case R.id.add:
                type1 = edit_name.getText().toString();
                call1 = edit_number.getText().toString();

                Log.d("address", avatorurl + "");
                if (type1.equals("") || type1 == null) {
                    Toast.makeText(this, "联系人为空", Toast.LENGTH_SHORT).show();
                } else if (call1.equals("") || call1 == null) {
                    Toast.makeText(this, "电话号码为空", Toast.LENGTH_SHORT).show();
                } else {
                    if (extra_name == null || extra_phonenumber == null) {
                        Log.d("addressedit", "run:1 ");
                        dbHelper.getWritableDatabase();
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        ContentValues values = new ContentValues();
                        values.put("avatorurl", avatorurl);
                        values.put("name", type1);
                        values.put("phonenumber", call1);
                        db.insert("Person", null, values);
                        values.clear();
                        Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show();
                        EventBus.getDefault().post(new MessageEvent("addcompelete"));
                        scrollToFinishActivity();
                    } else {
                        add.setText("修改");
                        Log.d("addressedit", "run:2 ");
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        db.execSQL("delete from Person where name = ?", new String[]{extra_name});
                        dbHelper.getWritableDatabase();
                        ContentValues values = new ContentValues();
                        if (avatorurl != null) {
                            values.put("avatorurl", avatorurl);
                        } else {
                            values.put("avatorurl", extra_avatorurl);
                        }
                        values.put("name", type1);
                        values.put("phonenumber", call1);
                        db.insert("Person", null, values);
                        values.clear();
                        Toast.makeText(this, "修改成功", Toast.LENGTH_SHORT).show();
                        EventBus.getDefault().post(new MessageEvent("addcompelete"));
                        scrollToFinishActivity();
                    }
                }
                break;
            case R.id.edit_name:
                String text = "请输入姓名";
                break;
            case R.id.edit_number:
                String number = "请输入号码";
                break;
            case R.id.back:
                scrollToFinishActivity();
                break;
            default:
                break;
        }
    }

    @SuppressLint("SimpleDateFormat")
    private String getNowTime() {
        DateFormat format = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        // 转换为字符串
        return format.format(new Date());
    }

    @SuppressLint("SdCardPath")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PHOTO_REQUEST_TAKEPHOTO:
                if (resultCode == RESULT_OK) {
                    startPhotoZoom(Uri.fromFile(new File(avaPath, imageName)), 480);
                }
                break;

            case PHOTO_REQUEST_GALLERY:
                if (resultCode == RESULT_OK) {
                    if (data != null)
                        startPhotoZoom(data.getData(), 480);
                }
                break;

            case PHOTO_REQUEST_CUT:
                if (resultCode == RESULT_OK) {
                    Bitmap bitmap = BitmapFactory.decodeFile(avaPath + imageName);
                    PreferenceUtils.setString(mContext, "name", avaPath + imageName);
                    selectavator.setImageBitmap(bitmap);

                    // updateAvatarInServer(imageName);
                }
                break;

        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    @SuppressLint("SdCardPath")
    private void startPhotoZoom(Uri uri1, int size) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri1, "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");

        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        // outputX,outputY 是剪裁图片的宽高
        intent.putExtra("outputX", size);
        intent.putExtra("outputY", size);
        intent.putExtra("return-data", false);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(new File(avaPath,imageName))
//                );
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(avaPath + imageName)));
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        finish();
    }

    class popupDismissListener implements PopupWindow.OnDismissListener {

        @Override
        public void onDismiss() {
            backgroundAlpha(1f);
        }

    }

    protected void initPopupWindow() {
        final View popupWindowView = getLayoutInflater().inflate(R.layout.avatarchoose, null);
        //内容，高度，宽度
        if (Location.BOTTOM.ordinal() == from) {
            popupWindow = new PopupWindow(popupWindowView, ActionBar.LayoutParams.FILL_PARENT, ActionBar.LayoutParams.WRAP_CONTENT, true);
        } else {
            popupWindow = new PopupWindow(popupWindowView, ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.FILL_PARENT, true);
        }
        //动画效果
        if (Location.LEFT.ordinal() == from) {
            popupWindow.setAnimationStyle(R.style.AnimationLeftFade);
        } else if (Location.RIGHT.ordinal() == from) {
            popupWindow.setAnimationStyle(R.style.AnimationRightFade);
        } else if (Location.BOTTOM.ordinal() == from) {
            popupWindow.setAnimationStyle(R.style.AnimationBottomFade);
        }
        //菜单背景色
        ColorDrawable dw = new ColorDrawable(0xffffffff);
        popupWindow.setBackgroundDrawable(dw);

        if (Location.LEFT.ordinal() == from) {
            popupWindow.showAtLocation(getLayoutInflater().inflate(R.layout.activity_addressadd, null), Gravity.LEFT, 0, 500);
        } else if (Location.RIGHT.ordinal() == from) {
            popupWindow.showAtLocation(getLayoutInflater().inflate(R.layout.activity_addressadd, null), Gravity.RIGHT, 0, 500);
        } else if (Location.BOTTOM.ordinal() == from) {
            popupWindow.showAtLocation(getLayoutInflater().inflate(R.layout.activity_addressadd, null), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        }
        //设置背景半透明
        backgroundAlpha(0.5f);
        //关闭事件
        popupWindow.setOnDismissListener(new popupDismissListener());

        RecyclerView recyclerView = (RecyclerView) popupWindowView.findViewById(R.id.rvavator);
        GridLayoutManager glm = new GridLayoutManager(mContext, 3);//定义3列的网格布局
        recyclerView.setLayoutManager(glm);
        recyclerView.addItemDecoration(new RecyclerViewItemDecoration(10, 3));//初始化子项距离和列数
        options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.pictureloading)
                .showImageOnLoading(R.drawable.pictureloading)
                .showImageOnFail(R.drawable.pictureloading)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .displayer(new FadeInBitmapDisplayer(1))
                .build();
        adapter = new MyRecyclerViewAdapter(images, mContext, options, glm);
        recyclerView.setAdapter(adapter);
        initData();
        adapter.setmOnItemClickListener(new MyRecyclerViewAdapter.OnItemClickListener() {

            @Override
            public void onClick(View view, int position) {
                avatorurl = images.get(position);
                ImageLoader.getInstance().displayImage(avatorurl, selectavator);
                popupWindow.dismiss();
            }
        });
    }

    /**
     * 设置添加屏幕的背景透明度
     */
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getWindow().setAttributes(lp);
    }

    /**
     * 菜单弹出方向
     */
    public enum Location {
        LEFT,
        RIGHT,
        TOP,
        BOTTOM
    }

    public class RecyclerViewItemDecoration extends RecyclerView.ItemDecoration {
        private int itemSpace;//定义子项间距
        private int itemColumnNum;//定义子项的列数

        public RecyclerViewItemDecoration(int itemSpace, int itemColumnNum) {
            this.itemSpace = itemSpace;
            this.itemColumnNum = itemColumnNum;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.bottom = itemSpace;//底部留出间距
            if (parent.getChildPosition(view) % itemColumnNum == 0)//每行第一项左边不留间距，其他留出间距
            {
                outRect.left = 0;
            } else {
                outRect.left = itemSpace;
            }

        }
    }

    private void initData() {
        images.clear();
        for (int i = 0; i < 2; i++) {

            images.add("https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=1108144574,916173858&fm=27&gp=0.jpg");
            images.add("https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=1764363895,1207146238&fm=27&gp=0.jpg");
            images.add("https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=3571687204,1985673515&fm=27&gp=0.jpg");
        }
        adapter.notifyDataSetChanged();
    }

}

