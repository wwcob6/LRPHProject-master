package com.app.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.app.R;
import com.app.db.DatabaseInfo;
import com.app.model.App;
import com.app.sip.SipInfo;
import com.punuo.sys.app.util.FileSizeUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Author chzjy
 * Date 2016/12/19.
 */

public class AppAdapter extends BaseAdapter {
    private Context context;
    private String TAG = "AppAdapter";
    private String sdPath;
    private DownloadListener mDownloadListener;
    private OpenFileListener mOpenFileListener;
    public AppAdapter(Context context,DownloadListener downloadListener, OpenFileListener openFileListener) {
        this.context = context;
        mDownloadListener=downloadListener;
        mOpenFileListener=openFileListener;
        sdPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/PNS9/download/icon/";
    }

    @Override
    public int getCount() {
        return SipInfo.applist.size();
    }

    @Override
    public Object getItem(int position) {
        return SipInfo.applist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        final App app = SipInfo.applist.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.appitem, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final String currentAppId = app.getAppid();
        final App currentApp = DatabaseInfo.sqLiteManager.queryApp(currentAppId);
        viewHolder.appIcon.setImageBitmap(getLoacalBitmap(sdPath + app.getIconname()));
        viewHolder.appName.setText(app.getAppname());
        viewHolder.appSize.setText(FileSizeUtil.FormetFileSize(app.getSize()));
        viewHolder.appDesc.setText(app.getDesc());

        //根据情况设置按钮名字显示
        if (currentApp.getLocalPath() == null) {
            viewHolder.download.setText("下载");
        } else {
            File apk = new File(currentApp.getLocalPath());
            if (apk.exists()) {
                viewHolder.download.setText("安装");
            } else {
                viewHolder.download.setText("下载");
            }
        }
        switch (currentApp.getState()) {
            case 1:
                viewHolder.progress.setVisibility(View.GONE);
                break;
            case 2:
                viewHolder.download.setText("下载中");
                viewHolder.progress.setVisibility(View.VISIBLE);
                viewHolder.progress.setProgress(app.getProgress());
                break;
        }
        final ViewHolder finalViewHolder = viewHolder;
        viewHolder.download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (finalViewHolder.download.getText().toString()) {
                    case "下载":
                        DatabaseInfo.sqLiteManager.updateAppState(currentAppId, 2);
                        finalViewHolder.progress.setVisibility(View.VISIBLE);
                        finalViewHolder.progress.setProgress(0);
                        mDownloadListener.onDownload(app.getAppid(), app.getUrl(),app.getApkname());
                        break;
                    case "安装":
                        File file=new File(currentApp.getLocalPath());
                        if (file.exists()) {
                            mOpenFileListener.OpenFile(file);
                        }else{
                            Toast.makeText(context,"安装包不存在",Toast.LENGTH_SHORT).show();
                            DatabaseInfo.sqLiteManager.updateAppLocalPath(app.getAppid(), null);
                            notifyDataSetChanged();
                        }
                        break;
                }
            }
        });


        return convertView;
    }

    private Bitmap getLoacalBitmap(String url) {
        try {
            FileInputStream fis = new FileInputStream(url);
            return BitmapFactory.decodeStream(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    static class ViewHolder {
        @Bind(R.id.app_icon)
        ImageView appIcon;
        @Bind(R.id.app_name)
        TextView appName;
        @Bind(R.id.app_size)
        TextView appSize;
        @Bind(R.id.app_desc)
        TextView appDesc;
        @Bind(R.id.download)
        Button download;
        @Bind(R.id.progress)
        ProgressBar progress;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public interface DownloadListener {
        public void onDownload(String appId, String appPath, String appName);
    }
    public interface OpenFileListener {
        public void OpenFile(File file);
    }
}
