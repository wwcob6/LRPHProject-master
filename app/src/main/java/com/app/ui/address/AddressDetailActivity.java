package com.app.ui.address;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.R;
import com.app.UserInfoManager;
import com.app.model.MessageEvent;
import com.app.model.PNBaseModel;
import com.app.request.AddAddressRequest;
import com.app.request.DeleteAddressRequest;
import com.app.request.UpdateAddressRequest;
import com.app.sip.SipInfo;
import com.app.views.CleanEditText;
import com.hengyi.wheelpicker.listener.OnCityWheelComfirmListener;
import com.hengyi.wheelpicker.ppw.CityWheelPickerPopupWindow;
import com.punuo.sys.app.activity.BaseSwipeBackActivity;
import com.punuo.sys.app.httplib.HttpManager;
import com.punuo.sys.app.httplib.RequestListener;
import com.punuo.sys.app.util.RegexUtils;
import com.punuo.sys.app.util.ToastUtils;

import org.greenrobot.eventbus.EventBus;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.app.sip.SipInfo.addressList;


public class AddressDetailActivity extends BaseSwipeBackActivity {
    @Bind(R.id.back)
    ImageView back;
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.tv_addressSelect)
    TextView tvAddressSelect;
    @Bind(R.id.Rl_address)
    RelativeLayout RlAddress;
    @Bind(R.id.et_detailAddress)
    CleanEditText etDetailAddress;
    @Bind(R.id.et_userName)
    CleanEditText etUserName;
    @Bind(R.id.et_userPhoneNum)
    CleanEditText etUserPhoneNum;
    @Bind(R.id.box1)
    CheckBox box1;
    @Bind(R.id.bt_addressSave)
    Button btAddressSave;
    @Bind(R.id.rl_addressDelete)
    RelativeLayout rlAddressDelete;
    private boolean isDefault;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_address);
        ButterKnife.bind(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//因为不是所有的系统都可以设置颜色的，在4.4以下就不可以。。有的说4.1，所以在设置的时候要检查一下系统版本是否是4.1以上
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.white));
        }
        if (SipInfo.isEditor) {
            title.setText("编辑地址");
            etUserName.setText(addressList.get(SipInfo.listPosition).userName);
            etUserPhoneNum.setText(addressList.get(SipInfo.listPosition).userPhoneNum);
            etDetailAddress.setText(addressList.get(SipInfo.listPosition).detailAddress);
            tvAddressSelect.setText(addressList.get(SipInfo.listPosition).userAddress);
            if (SipInfo.isDefault == 1) {
                isDefault = true;
            } else if (SipInfo.isDefault == 2) {
                isDefault = false;
            }
            box1.setChecked(isDefault);
        } else {
            title.setText("新增地址");
            rlAddressDelete.setVisibility(View.INVISIBLE);
        }
        box1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SipInfo.isDefault = 1;
                    Log.d("是否默认", SipInfo.isDefault + "-----");
                } else
                    SipInfo.isDefault = 2;
                Log.d("是否默认", SipInfo.isDefault + "-----");
            }
        });

        final CityWheelPickerPopupWindow wheelPickerPopupWindow = new CityWheelPickerPopupWindow(this);
        wheelPickerPopupWindow.setListener(new OnCityWheelComfirmListener() {
            @Override
            public void onSelected(String Province, String City, String District, String PostCode) {
                tvAddressSelect.setText(Province + " " + City + " " + District);
                Toast.makeText(getApplicationContext(), Province + City + District, Toast.LENGTH_LONG).show();
            }
        });

        RlAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wheelPickerPopupWindow.show();
            }
        });
    }

    @OnClick({R.id.et_detailAddress, R.id.et_userPhoneNum, R.id.et_userName,
            R.id.back, R.id.bt_addressSave, R.id.rl_addressDelete})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.et_detailAddress:
                break;
            case R.id.bt_addressSave:
                SipInfo.userName = etUserName.getText().toString();
                SipInfo.userPhoneNum = etUserPhoneNum.getText().toString();
                SipInfo.userAddress = tvAddressSelect.getText().toString();
                SipInfo.detailAddress = etDetailAddress.getText().toString();
                if (!checkInput(SipInfo.userAddress, SipInfo.detailAddress, SipInfo.userName, SipInfo.userPhoneNum)) {
                    return;
                }
                if (SipInfo.isEditor) {
                    updateAddress();
                } else {
                    saveAddress();
                }
                break;
            case R.id.rl_addressDelete:
                Dialog dialog = new AlertDialog.Builder(this)
                        .setMessage("确定要删除该地址吗？")
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteAddress();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .create();
                dialog.show();
                break;
            case R.id.back:
                scrollToFinishActivity();
                break;
        }
    }

    private DeleteAddressRequest mDeleteAddressRequest;

    private void deleteAddress() {
        if (mDeleteAddressRequest != null && !mDeleteAddressRequest.isFinish()) {
            return;
        }
        mDeleteAddressRequest = new DeleteAddressRequest();
        mDeleteAddressRequest.addUrlParam("id", UserInfoManager.getUserInfo().id);
        mDeleteAddressRequest.addUrlParam("position", SipInfo.addressPosition);
        mDeleteAddressRequest.setRequestListener(new RequestListener<PNBaseModel>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onSuccess(PNBaseModel result) {
                if (result == null) {
                    return;
                }
                if (result.isSuccess()) {
                    EventBus.getDefault().post(new MessageEvent("刷新"));
                    finish();
                } else {
                    ToastUtils.showToast("删除失败，请重试");
                }
            }

            @Override
            public void onError(Exception e) {

            }
        });
        HttpManager.addRequest(mDeleteAddressRequest);
    }

    private UpdateAddressRequest mUpdateAddressRequest;

    private void updateAddress() {
        if (mUpdateAddressRequest != null && !mUpdateAddressRequest.isFinish()) {
            return;
        }
        mUpdateAddressRequest = new UpdateAddressRequest();
        mUpdateAddressRequest.addUrlParam("id", UserInfoManager.getUserInfo().id);
        mUpdateAddressRequest.addUrlParam("userAddress", SipInfo.userAddress);
        mUpdateAddressRequest.addUrlParam("detailAddress", SipInfo.detailAddress);
        mUpdateAddressRequest.addUrlParam("userName", SipInfo.userName);
        mUpdateAddressRequest.addUrlParam("userPhoneNum", SipInfo.userPhoneNum);
        mUpdateAddressRequest.addUrlParam("position", SipInfo.addressPosition);
        mUpdateAddressRequest.addUrlParam("isDefault", SipInfo.isDefault);
        mUpdateAddressRequest.setRequestListener(new RequestListener<PNBaseModel>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onSuccess(PNBaseModel result) {
                if (result == null) {
                    return;
                }
                if (result.isSuccess()) {
                    EventBus.getDefault().post(new MessageEvent("刷新"));
                    finish();
                } else {
                    ToastUtils.showToast("更新失败，请重试");
                }
            }

            @Override
            public void onError(Exception e) {

            }
        });
        HttpManager.addRequest(mUpdateAddressRequest);
    }

    private AddAddressRequest mAddAddressRequest;

    private void saveAddress() {
        if (mAddAddressRequest != null && !mAddAddressRequest.isFinish()) {
            return;
        }
        mAddAddressRequest = new AddAddressRequest();
        mAddAddressRequest.addUrlParam("id", UserInfoManager.getUserInfo().id);
        mAddAddressRequest.addUrlParam("userAddress", SipInfo.userAddress);
        mAddAddressRequest.addUrlParam("detailAddress", SipInfo.detailAddress);
        mAddAddressRequest.addUrlParam("userName", SipInfo.userName);
        mAddAddressRequest.addUrlParam("userPhoneNum", SipInfo.userPhoneNum);
        mAddAddressRequest.addUrlParam("isDefault", SipInfo.isDefault);
        mAddAddressRequest.setRequestListener(new RequestListener<PNBaseModel>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onSuccess(PNBaseModel result) {
                if (result == null) {
                    return;
                }
                if (result.isSuccess()) {
                    EventBus.getDefault().post(new MessageEvent("刷新"));
                    finish();
                } else {
                    ToastUtils.showToast("保存失败，请重试");
                }
            }

            @Override
            public void onError(Exception e) {

            }
        });
        HttpManager.addRequest(mAddAddressRequest);
    }

    private boolean checkInput(String userAddress, String detailAddress, String userName, String userPhoneNum) {
        if (TextUtils.isEmpty(userPhoneNum)) {
            ToastUtils.showToast("手机号码不能为空");
        } else if (!RegexUtils.checkMobile(userPhoneNum)) {
            ToastUtils.showToast("手机号码格式不正确");
        } else if (TextUtils.isEmpty(userName)) {
            ToastUtils.showToast("请输入收货人姓名");
        } else if (TextUtils.isEmpty(detailAddress)) {
            ToastUtils.showToast("请输入具体收货地址");
        } else if (TextUtils.isEmpty(userAddress)) {
            ToastUtils.showToast("请选择所在地区");
        } else {
            return true;
        }
        return false;
    }
}


