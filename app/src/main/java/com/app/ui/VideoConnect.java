package com.app.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.LoadPicture;
import com.app.R;
import com.app.model.MessageEvent;
import com.app.sip.BodyFactory;
import com.app.sip.SipInfo;
import com.app.sip.SipMessageFactory;
import com.app.view.CircleImageView;
import com.punuo.sys.app.activity.BaseActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.zoolu.sip.address.NameAddress;
import org.zoolu.sip.address.SipURL;
import org.zoolu.sip.message.Message;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.app.camera.FileOperateUtil.TAG;
import static com.app.sip.SipInfo.devName;

/**
 * Created by maojianhui on 2018/6/27.
 */

public class VideoConnect extends BaseActivity implements View.OnClickListener {
    private SharedPreferences.Editor editor;
    private SharedPreferences pref;
    private SoundPool soundPool;
    private int streamId;
    private static String imageName;
    private String response = "";
    private ProgressDialog dialog;
    private static final int PHOTO_REQUEST_TAKEPHOTO = 1;// 拍照
    private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
    private static final int PHOTO_REQUEST_CUT = 3;// 结果
    private static final int UPDATE_FXID = 4;// 结果
    private static final int UPDATE_NICK = 5;// 结果
    private LoadPicture avatarLoader;
    private CircleImageView CIV_avatar;

    String SdCard = Environment.getExternalStorageDirectory().getAbsolutePath();
    String avaPath = SdCard + "/fanxin/Files/Camera/Images/";
    private String picPath;

    String devId = SipInfo.paddevId;
    SipURL sipURL = new SipURL(devId, SipInfo.serverIp, SipInfo.SERVER_PORT_USER);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EventBus.getDefault().register(this);  //注册
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_call);
        getWindow().setBackgroundDrawableResource(R.drawable.jtbackground);
        avatarLoader = new LoadPicture(this, avaPath);


//        等待接听音效
        soundPool=new SoundPool(10, android.media.AudioManager.STREAM_MUSIC,5);
        final int sourceid=soundPool.load(this,R.raw.videowait,1);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {

                    public void onLoadComplete(
                            SoundPool soundPool,
                            int sampleId, int status) {
                        // TODO Auto-generated method stub
                        Log.d("xx", "11");
                        streamId=soundPool.play(sourceid, 1, 1, 0,
                                4, 1);
                    }
                });

        init();
        //改变状态栏颜色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//因为不是所有的系统都可以设置颜色的，在4.4以下就不可以。。有的说4.1，所以在设置的时候要检查一下系统版本是否是4.1以上
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.image_bar));
        }
    }



    public void init() {
//        String vatar_temp = UserInfoManager.getInstance(VideoConnect.this)
//                .getUserInfo("avatar");
        pref= PreferenceManager.getDefaultSharedPreferences(this);
//         pref=getSharedPreferences("data",MODE_PRIVATE);
        String vatar_temp=pref.getString("name","");
        Log.w("zzzzzzzzzz.....", "头像为" + vatar_temp);

//        CIV_avatar = (CircleImageView) findViewById(R.id.CIV_avatar);
//        CIV_avatar.setOnClickListener(this);
//        showUserAvatar(CIV_avatar, vatar_temp);
        ImageView bt1 = (ImageView) findViewById(R.id.bt_accept);
        bt1.setOnClickListener(this);
        ImageView bt2 = (ImageView) findViewById(R.id.bt_refuse);
        bt2.setOnClickListener(this);
        TextView tv_videostaus=(TextView)findViewById(R.id.tv_videostaus1);
        tv_videostaus.setText("对方邀请您视频通话...");
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if(event.getMessage().equals("开始视频")) {
            Log.i(TAG, "111message is " + event.getMessage());
            // 更新界面
            finish();
//            Toast.makeText(this,"对方已取消",Toast.LENGTH_SHORT).show();
            Log.d(TAG,"关闭connect");
        }
        else if(event.getMessage().equals("取消")){
            finish();
        }
    }

    public void stopSound(int id){
        soundPool.stop(id);
    }
    @Override
    protected void onDestroy() {
        stopSound(streamId);
        super.onDestroy();
        // 注销订阅者
        EventBus.getDefault().unregister(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.bt_accept:
                SipInfo.toDev = new NameAddress(devName, sipURL);
                Message response1 = SipMessageFactory.createNotifyRequest(SipInfo.sipUser, SipInfo.toDev,
                        SipInfo.user_from, BodyFactory.createCallReply("agree"));
                SipInfo.sipUser.sendMessage(response1);
                break;
            case R.id.bt_refuse:
                SipInfo.toDev = new NameAddress(devName, sipURL);
                Message response2 = SipMessageFactory.createNotifyRequest(SipInfo.sipUser, SipInfo.toDev,
                        SipInfo.user_from, BodyFactory.createCallReply("refuse"));
                SipInfo.sipUser.sendMessage(response2);
                finish();
                break;
            default:
                Log.i("response", "error ");
        }
    }

    private void showPhotoDialog() {
        final AlertDialog dlg = new AlertDialog.Builder(this).create();
        dlg.show();
        Window window = dlg.getWindow();
        // *** 主要就是在这里实现这种效果的.
        // 设置窗口的内容页面,shrew_exit_dialog.xml文件中定义view内容
        window.setContentView(R.layout.alertdialog);
        // 为确认按钮添加事件,执行退出应用操作
        TextView tv_paizhao = (TextView) window.findViewById(R.id.tv_content1);
        tv_paizhao.setText("拍照");
        tv_paizhao.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SdCardPath")
            public void onClick(View v) {
                imageName = getNowTime() + ".jpg";
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //Intent intent = new Intent(UserInfoActivity.this, MyCamera.class);
                //intent.putExtra("type", 1);
                // 指定调用相机拍照后照片的储存路径
                intent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(new File(avaPath, imageName)));
                startActivityForResult(intent, PHOTO_REQUEST_TAKEPHOTO);
                dlg.cancel();
            }
        });
        TextView tv_xiangce = (TextView) window.findViewById(R.id.tv_content2);
        tv_xiangce.setText("相册");
        tv_xiangce.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                getNowTime();
                imageName = getNowTime() + ".jpg";

                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
                dlg.cancel();
            }
        });

    }
    @SuppressLint("SdCardPath")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PHOTO_REQUEST_TAKEPHOTO:
                if (resultCode == RESULT_OK) {
//                Uri localUri = Uri.fromFile( new File("/sdcard/fanxin/", imageName));
//                Intent localIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, localUri);
//
//                sendBroadcast(localIntent);
                    //picPath = data.getStringExtra("picpath");
                    //Uri uri = Uri.parse(picPath);
                    //picPath = data.getStringExtra("picpath");
                    startPhotoZoom(Uri.fromFile(new File(avaPath, imageName)), 480);
                    //startPhotoZoom(Uri.fromFile(new File(picPath)), 480);
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
                    // BitmapFactory.Options options = new BitmapFactory.Options();
                    //
                    // /**
                    // * 最关键在此，把options.inJustDecodeBounds = true;
                    // * 这里再decodeFile()，返回的bitmap为空
                    // * ，但此时调用options.outHeight时，已经包含了图片的高了
                    // */
                    // options.inJustDecodeBounds = true;
//                    Bitmap bitmap = BitmapFactory.decodeFile(avaPath
//                            + imageName);
                    Bitmap bitmap = BitmapFactory.decodeFile(avaPath + imageName);
                    editor=pref.edit();
//                     editor=getSharedPreferences("data",MODE_PRIVATE);
                    Log.i("aazz",avaPath+imageName);
                    editor.putString("name",avaPath+imageName);
                    editor.apply();
//                    CIV_avatar.setImageBitmap(bitmap);

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


    @SuppressLint("SimpleDateFormat")
    private String getNowTime() {
        //Date date = new Date(System.currentTimeMillis());
        //SimpleDateFormat dateFormat = new SimpleDateFormat("MMddHHmmssSS");
        DateFormat format = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        // 转换为字符串
        String formatDate = format.format(new Date());
        return formatDate;
    }

}
