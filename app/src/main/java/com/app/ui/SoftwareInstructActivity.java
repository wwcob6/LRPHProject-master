package com.app.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.R;
import com.punuo.sys.app.activity.BaseSwipeBackActivity;
import com.punuo.sys.app.update.AutoUpdateService;
import com.punuo.sys.app.util.DeviceHelper;
import com.punuo.sys.app.util.IntentUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SoftwareInstructActivity extends BaseSwipeBackActivity {

    @Bind(R.id.tv_version)
    TextView tv_version;
    @Bind(R.id.tv_introduct)
    TextView tvIntroduct;
    @Bind(R.id.tv_update)
    TextView tvUpdate;
    @Bind(R.id.back)
    ImageView back;
    @Bind(R.id.title)
    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_software_intruct);
        ButterKnife.bind(this);
        title.setText("关于");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //因为不是所有的系统都可以设置颜色的，在4.4以下就不可以。。有的说4.1，所以在设置的时候要检查一下系统版本是否是4.1以上
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.image_bar));
        }
        tv_version.setText("v" + DeviceHelper.getVersionName());
    }

    @OnClick({R.id.tv_introduct, R.id.tv_update, R.id.back})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_introduct:
                Toast.makeText(this, "该功能即将上线", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tv_update:
                Intent intent = new Intent(this, AutoUpdateService.class);
                intent.putExtra("needToast", true);
                IntentUtil.startServiceInSafeMode(this, intent);
                break;
            case R.id.back:
                scrollToFinishActivity();
                break;
            default:
                break;
        }
    }
}
