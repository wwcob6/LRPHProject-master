package com.app.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.R;
import com.app.UserInfoManager;
import com.app.Util;
import com.app.model.PNBaseModel;
import com.app.model.PNUserInfo;
import com.app.model.UploadAvatarResult;
import com.app.request.UpdateSexRequest;
import com.app.request.UploadAvatarRequest;
import com.app.sip.BodyFactory;
import com.app.sip.SipInfo;
import com.app.sip.SipMessageFactory;
import com.app.view.CircleImageView;
import com.bumptech.glide.Glide;
import com.punuo.sys.app.activity.BaseSwipeBackActivity;
import com.punuo.sys.app.httplib.HttpManager;
import com.punuo.sys.app.httplib.RequestListener;
import com.punuo.sys.app.util.ProviderUtil;
import com.punuo.sys.app.util.ToastUtils;
import com.punuo.sys.app.util.ViewUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.zoolu.sip.address.NameAddress;
import org.zoolu.sip.address.SipURL;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.app.sip.SipInfo.devName;

@SuppressLint("SdCardPath")
public class UserInfoActivity extends BaseSwipeBackActivity implements View.OnClickListener {

    private static final int CAMERA_REQUEST_CODE = 1;
    private RelativeLayout re_avatar;
    private RelativeLayout re_name;
    private RelativeLayout re_sex;
    private TextView tv_sex1;
    private ImageView back;
    private TextView title;
    private CircleImageView iv_avatar;
    private TextView tv_name;
    private static String imageName;
    private static final int PHOTO_REQUEST_TAKEPHOTO = 1;// 拍照
    private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
    private static final int PHOTO_REQUEST_CUT = 3;// 结果
    private static final int UPDATE_FXID = 4;// 结果
    private static final int UPDATE_NICK = 5;// 结果
    String SdCard = Environment.getExternalStorageDirectory().getAbsolutePath();
    String avaPath = SdCard + "/fanxin/Files/Camera/Image/";
    private View inflate;
    private Uri imageUri;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myinfo);
        initView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//因为不是所有的系统都可以设置颜色的，在4.4以下就不可以。。有的说4.1，所以在设置的时候要检查一下系统版本是否是4.1以上
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.image_bar));
        }
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    private void initView() {
        re_avatar = (RelativeLayout) findViewById(R.id.re_avatar);
        re_name = (RelativeLayout) findViewById(R.id.re_name);
        re_sex = (RelativeLayout) findViewById(R.id.re_sex);
        iv_avatar = (CircleImageView) findViewById(R.id.iv_avatar);
        tv_name = (TextView) findViewById(R.id.ttv_name);
        tv_sex1 = (TextView) findViewById(R.id.tv_sex1);
        title = (TextView) findViewById(R.id.title);
        back = (ImageView) findViewById(R.id.back);

        back.setOnClickListener(this);
        re_avatar.setOnClickListener(this);
        re_name.setOnClickListener(this);
        re_sex.setOnClickListener(this);
        title.setText("个人资料");
        tv_name.setText(UserInfoManager.getUserInfo().nickname);

        Glide.with(this).load(Util.getImageUrl(UserInfoManager.getUserInfo().avatar))
                .into(iv_avatar);
        ViewUtil.setText(tv_name, "昵称：" + UserInfoManager.getUserInfo().nickname);
        ViewUtil.setText(tv_sex1, UserInfoManager.getUserInfo().gender);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.re_avatar:
                showPhotoDialog();
                break;
            case R.id.re_name:
                startActivityForResult(new Intent(UserInfoActivity.this,
                        UpdateNickActivity.class), UPDATE_NICK);
                break;
            case R.id.re_sex:
                showChooseDialog();
                break;
            case R.id.back:
                scrollToFinishActivity();
                break;
            default:
                break;
        }
    }


    private String[] sexArray = new String[]{"男", "女"};

    /*性别选择*/
    private void showChooseDialog() {
        String gender = UserInfoManager.getUserInfo().gender;
        int index = sexArray[0].equals(gender) ? 0 : 1;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setSingleChoiceItems(sexArray, index, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                updateSex(sexArray[which]);
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private UpdateSexRequest mUpdateSexRequest;

    private void updateSex(String gender) {
        if (mUpdateSexRequest != null && !mUpdateSexRequest.isFinish()) {
            return;
        }
        showLoadingDialog();
        mUpdateSexRequest = new UpdateSexRequest();
        mUpdateSexRequest.addUrlParam("id", UserInfoManager.getUserInfo().id);
        mUpdateSexRequest.addUrlParam("gender", gender);
        mUpdateSexRequest.setRequestListener(new RequestListener<PNBaseModel>() {
            @Override
            public void onComplete() {
                dismissLoadingDialog();
            }

            @Override
            public void onSuccess(PNBaseModel result) {
                if (result == null) {
                    return;
                }
                if (result.isSuccess()) {
                    tv_sex1.setText(gender);
                    UserInfoManager.getInstance().refreshUserInfo();
                } else {
                    if (!TextUtils.isEmpty(result.msg)) {
                        ToastUtils.showToast(result.msg);
                    }
                }
            }

            @Override
            public void onError(Exception e) {

            }
        });
        HttpManager.addRequest(mUpdateSexRequest);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(PNUserInfo.UserInfo userInfo) {
        ViewUtil.setText(tv_name, "昵称：" + UserInfoManager.getUserInfo().nickname);
        ViewUtil.setText(tv_sex1, UserInfoManager.getUserInfo().gender);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    private void showPhotoDialog() {
        final Dialog dlg = new Dialog(this, R.style.ActionSheetDialogStyle);
        inflate = LayoutInflater.from(this).inflate(R.layout.alertdialog, null);
        dlg.setContentView(inflate);
        Window window = dlg.getWindow();
        // *** 主要就是在这里实现这种效果的.
        // 设置窗口的内容页面,shrew_exit_dialog.xml文件中定义view内容
//        window.setContentView(R.layout.alertdialog);
        Display display = getWindowManager().getDefaultDisplay();
        //设置Dialog从窗体底部弹出
        window.setGravity(Gravity.BOTTOM);
        //获得窗体的属性
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = (int) display.getWidth();
        lp.y = 20;//设置Dialog距离底部的距离
//       将属性设置给窗体
        window.setAttributes(lp);
        dlg.show();
        ;//显示对话框
        // 为确认按钮添加事件,执行退出应用操作
        TextView tv_paizhao = (TextView) window.findViewById(R.id.tv_content1);
        tv_paizhao.setText("拍照");
        tv_paizhao.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SdCardPath")
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(UserInfoActivity.this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(UserInfoActivity.this, new String[]
                            {Manifest.permission.CAMERA}, 1001);
                } else {
                    imageName = getNowTime() + ".jpg";
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File file = new File(avaPath, imageName);

                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                        imageUri = Uri.fromFile(file);
                    } else {
                        imageUri = FileProvider.getUriForFile(UserInfoActivity.this, ProviderUtil.getFileProviderName(UserInfoActivity.this), file);
                    }
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivityForResult(intent, PHOTO_REQUEST_TAKEPHOTO);
                    dlg.cancel();
                }
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
        TextView tv_quxiao = (TextView) window.findViewById(R.id.tv_content3);
        tv_quxiao.setText("取消");
        tv_quxiao.setTextColor(0xff7f7f7f);
        tv_quxiao.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
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
                    startPhotoZoom(imageUri, 480);
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
                    iv_avatar.setImageBitmap(bitmap);
                    updateAvatarInServer(imageName);
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

        //需要加上这两句话：  uri权限
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        // outputX,outputY 是剪裁图片的宽高
        intent.putExtra("outputX", size);
        intent.putExtra("outputY", size);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(avaPath + imageName)));
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }

    @SuppressLint("SimpleDateFormat")
    private String getNowTime() {
        DateFormat format = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        // 转换为字符串
        return format.format(new Date());
    }

    @SuppressLint("SdCardPath")
    private void updateAvatarInServer(final String image) {
        uploadAvatar();
    }

    private UploadAvatarRequest mUploadAvatarRequest;

    private void uploadAvatar() {
        if (mUploadAvatarRequest != null && !mUploadAvatarRequest.isFinish()) {
            return;
        }
        showLoadingDialog("正在更新...");
        mUploadAvatarRequest = new UploadAvatarRequest();
        mUploadAvatarRequest.addEntityParam("pic", new File(avaPath + imageName));
        mUploadAvatarRequest.addEntityParam("avatar", UserInfoManager.getUserInfo().avatar);
        mUploadAvatarRequest.addEntityParam("id", UserInfoManager.getUserInfo().id);
        mUploadAvatarRequest.setRequestListener(new RequestListener<UploadAvatarResult>() {
            @Override
            public void onComplete() {
                dismissLoadingDialog();
            }

            @Override
            public void onSuccess(UploadAvatarResult result) {
                if (result == null) {
                    return;
                }
                if (result.isSuccess()) {
                    UserInfoManager.getInstance().refreshUserInfo();

                    //这块代码的意义值得考虑
                    File oldfile = new File(avaPath + imageName);
                    File newfile = new File(SdCard + "/fanxin/Files/Camera/Image/" + result.avatar);
                    oldfile.renameTo(newfile);
                    //这个广播的目的就是更新图库，发了这个广播进入相册就可以找到你保存的图片了！，记得要传你更新的file哦
                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    Uri uri = Uri.fromFile(newfile);
                    intent.setData(uri);
                    sendBroadcast(intent);

                    ToastUtils.showToast("头像上传成功");
                    String devId = SipInfo.paddevId;
                    SipURL sipURL = new SipURL(devId, SipInfo.serverIp, SipInfo.SERVER_PORT_USER);
                    SipInfo.toDev = new NameAddress(devName, sipURL);
                    org.zoolu.sip.message.Message query = SipMessageFactory.createNotifyRequest(SipInfo.sipUser, SipInfo.toDev,
                            SipInfo.user_from, BodyFactory.createListUpdate("addsuccess"));
                    SipInfo.sipUser.sendMessage(query);
                    finish();
                } else {
                    ToastUtils.showToast("头像上传失败");
                }
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
        HttpManager.addRequest(mUploadAvatarRequest);
    }

    private void requestCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]
                        {Manifest.permission.CAMERA}, 1001);
            } else {

            }
        }
    }

    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // 第一次请求权限时，用户如果拒绝，下一次请求shouldShowRequestPermissionRationale()返回true
            // 向用户解释为什么需要这个权限
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                new AlertDialog.Builder(this)
                        .setMessage("申请相机权限")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //申请相机权限
                                ActivityCompat.requestPermissions(UserInfoActivity.this,
                                        new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
                            }
                        })
                        .show();
            } else {
                //申请相机权限
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
            }
        } else {
            Toast.makeText(this, "相机权限已申请", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "相机权限已申请", Toast.LENGTH_SHORT).show();
            } else {
                //用户勾选了不再询问
                //提示用户手动打开权限
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                    Toast.makeText(this, "相机权限已被禁止", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}

