package com.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.app.R;
import com.app.adapter.FileChooserAdapter;
import com.app.model.FileInfo;
import com.punuo.sys.app.activity.BaseActivity;

import java.io.File;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.app.sip.SipUser.TAG;

/**
 * Author chzjy
 * Date 2016/12/19.
 */

public class FileChooserActivity extends BaseActivity {
    @Bind(R.id.back)
    ImageButton back;
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.tvEmptyHint)
    TextView tvEmptyHint;
    @Bind(R.id.gvFileChooser)
    GridView gvFileChooser;
    @Bind(R.id.imgBackFolder)
    Button imgBackFolder;
    @Bind(R.id.upload)
    Button upload;

    private String mSdcardRootPath;  //sdcard 根路径
    private String mLastFilePath;    //当前显示的路径
    private int lastposition = -1;
    private ArrayList<FileInfo> mFileLists;
    private FileChooserAdapter mAdatper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filechooser_show);
        ButterKnife.bind(this);

        init();
    }

    private void init() {
        mSdcardRootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        gvFileChooser.setEmptyView(findViewById(R.id.tvEmptyHint));
        setGridViewAdapter(mSdcardRootPath);
        gvFileChooser.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FileInfo fileInfo = mAdatper.getItem(position);
                if (fileInfo.isDirectory()) {   //点击项为文件夹, 显示该文件夹下所有文件
                    updateFileItems(fileInfo.getFilePath());
                    lastposition = -1;
                } else {
                    FileInfo file = mFileLists.get(position);
                    if (file.isSelected()) {
                        file.setSelected(false);
                        lastposition = -1;
                    } else {
                        file.setSelected(true);
                        if (lastposition >= 0) {
                            mFileLists.get(lastposition).setSelected(false);
                        }
                        lastposition = position;
                    }
                    mAdatper.notifyDataSetChanged();
                }
            }
        });
    }

    @OnClick({R.id.back, R.id.imgBackFolder, R.id.upload})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                setResult(RESULT_CANCELED);
                finish();
                break;
            case R.id.imgBackFolder:
                backProcess();
                break;
            case R.id.upload:
                if (lastposition != -1) {
                    FileInfo fileInfo = mFileLists.get(lastposition);
                    Intent intent = new Intent();
                    intent.putExtra("FilePath", fileInfo.getFilePath());
                    Log.i(TAG, fileInfo.getFilePath());
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    Toast.makeText(FileChooserActivity.this, "请选择要上传的文件", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    //配置适配器
    private void setGridViewAdapter(String filePath) {
        updateFileItems(filePath);
        mAdatper = new FileChooserAdapter(this, mFileLists);
        gvFileChooser.setAdapter(mAdatper);

    }

    //返回上一层目录的操作
    public void backProcess() {
        //判断当前路径是不是sdcard路径,如果不是，则返回到上一层。
        if (lastposition >= 0) {
            mFileLists.get(lastposition).setSelected(false);
        }
        if (!mLastFilePath.equals(mSdcardRootPath)) {
            File thisFile = new File(mLastFilePath);
            String parentFilePath = thisFile.getParent();
            updateFileItems(parentFilePath);
            lastposition = -1;
        } else {   //是sdcard路径 ，直接结束
            setResult(RESULT_CANCELED);
            finish();
        }
    }

    //根据路径更新数据，并且通知Adatper数据改变
    private void updateFileItems(String filePath) {
        mLastFilePath = filePath;
        title.setText(mLastFilePath);

        if (mFileLists == null)
            mFileLists = new ArrayList<FileInfo>();
        if (!mFileLists.isEmpty())
            mFileLists.clear();

        File[] files = folderScan(filePath);
        if (files == null)
            return;

        for (int i = 0; i < files.length; i++) {
            if (files[i].isHidden())  // 不显示隐藏文件
                continue;

            String fileAbsolutePath = files[i].getAbsolutePath();
            String fileName = files[i].getName();
            boolean isDirectory = false;
            if (files[i].isDirectory()) {
                isDirectory = true;
            }
            FileInfo fileInfo = new FileInfo(fileAbsolutePath, fileName, isDirectory);
            mFileLists.add(fileInfo);
        }
        //When first enter , the object of mAdatper don't initialized
        if (mAdatper != null)
            mAdatper.notifyDataSetChanged();  //重新刷新
    }

    //获得当前路径的所有文件
    private File[] folderScan(String path) {
        File file = new File(path);
        return file.listFiles();
    }
}
