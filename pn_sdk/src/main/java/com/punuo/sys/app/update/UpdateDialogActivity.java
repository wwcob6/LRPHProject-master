package com.punuo.sys.app.update;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.punuo.sys.app.R;
import com.punuo.sys.app.util.FileUtil;
import com.punuo.sys.app.util.IntentUtil;


/**
 * http://sip.qinqingonline.com:8000/users/updatePhoneNewVersion?versionName=2.3.2&versionCode=2
 * Created by han.chen.
 * Date on 2020/8/3.
 **/
public class UpdateDialogActivity extends Activity {

    private VersionModel versionModel;

    private TextView mTextNewVersion;
    private Button buttonOk;
    private Button buttonCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_dialog);
        versionModel = (VersionModel) getIntent().getExtras().getParcelable("versionModel");
        setupView();
        setupData();
    }

    public void setupView() {
        mTextNewVersion = (TextView) findViewById(R.id.text_new_version);
        buttonOk = (Button) findViewById(R.id.update_btn_ok);
        buttonCancel = (Button) findViewById(R.id.update_btn_cancel);
    }

    private void setupData() {
        if (versionModel == null) {
            finish();
            return;
        }
        mTextNewVersion.setText("V" + versionModel.versionName);
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fileName = versionModel.downloadUrl.substring(versionModel.downloadUrl.lastIndexOf("/"));
                if (FileUtil.isFileExist(FileUtil.DEFAULT_APK_DIR, fileName)) {
                    FileUtil.deleteFile(FileUtil.DEFAULT_APK_DIR, fileName);
                }
                Intent intent = new Intent(UpdateDialogActivity.this, AutoUpdateService.class);
                intent.putExtra("versionModel", versionModel);
                IntentUtil.startServiceInSafeMode(UpdateDialogActivity.this, intent);
                finish();
            }
        });
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AutoUpdateService.getInstance() != null) {
                    AutoUpdateService.getInstance().setDownloading(false);
                }
                finish();
            }
        });
    }

}
