package com.punuo.sys.app.update;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;

import com.punuo.sys.app.R;
import com.punuo.sys.app.util.FileUtil;
import com.punuo.sys.app.util.ToastUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by zhoukuan on 14/12/31.
 */
public class Download implements Runnable {
    private String url;
    private Context context;
    // 通知栏
    private NotificationManager updateNotificationManager = null;
    private NotificationCompat.Builder mBuilder;
    // 通知栏跳转Intent
    private Intent updateIntent = null;
    private PendingIntent updatePendingIntent = null;

    public final static int FILE_CAN_INSTALL = 0;
    public final static int DOWNLOAD_SUCCESSED = 1;
    public final static int DOWNLOAD_FAILED = 2;
    public final static int DOWNLOAD_TIMEOUT = 3;
    public final static int DOWNLOAD_END = 4;
    public File file;
    boolean useSDCard;
    private int id;
    private String fileName;
    private String loadingFileName;
    private VersionModel versionModel;

    private boolean mNeedAutoInstall = true;

    public Download(VersionModel info, Context context, boolean needInstall) {
        this(info, context);
        mNeedAutoInstall = needInstall;
    }

    public Download(VersionModel versionModel, Context context) {
        super();
        this.versionModel = versionModel;
        this.url = versionModel.downloadUrl;
        loadingFileName = url.substring(url.lastIndexOf("/"), url.lastIndexOf("/") + 1) + "loading_" + url.substring(url.lastIndexOf("/") + 1);
        fileName = url.substring(url.lastIndexOf("/"));
        this.context = context;
        id = context.getString(R.string.app_name).hashCode();
        if (AutoUpdateService.getInstance() != null) {
            AutoUpdateService.getInstance().setDownloading(true);
        }
    }

    public Handler updateHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DOWNLOAD_SUCCESSED:
                    if (!useSDCard) {
                        try {
                            String[] args1 = {"chmod", "705", file.getPath()};
                            Runtime.getRuntime().exec(args1);
                            String[] args2 = {"chmod", "604", file.getAbsolutePath()};
                            Runtime.getRuntime().exec(args2);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    Intent installIntent = new Intent(Intent.ACTION_VIEW);
                    installIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    if (Build.VERSION.SDK_INT >= 24) {
                        Uri uri = FileProvider.getUriForFile(context, "com.punuo.sys.app.main.provider", file);
                        installIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        installIntent.setDataAndType(uri, "application/vnd.android.package-archive");
                    } else  {
                        Uri uri = Uri.fromFile(file);
                        installIntent.setDataAndType(uri, "application/vnd.android.package-archive");
                    }
                    // 点击安装PendingIntent
                    updatePendingIntent = PendingIntent.getActivity(context, 0, installIntent, 0);
                    context.startActivity(installIntent);
                    mBuilder.setAutoCancel(true).setOngoing(false).setProgress(0, 0, false).setDefaults(Notification.DEFAULT_SOUND).
                            setContentIntent(updatePendingIntent).setContentTitle(context.getString(R.string.app_name)).
                            setContentText("下载完成").setSmallIcon(R.drawable.logo);
                    updateNotificationManager.notify(id, mBuilder.build());
                    break;
                case DOWNLOAD_FAILED:
                    mBuilder.setAutoCancel(true).setOngoing(false).setProgress(0, 0, false).setContentText("下载失败");
                    updateNotificationManager.notify(id, mBuilder.build());
                    break;
                case FILE_CAN_INSTALL:
                    Intent intent = new Intent(context, UpdateDialogActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("versionModel", versionModel);
                    context.startActivity(intent);
                    break;
                case DOWNLOAD_TIMEOUT:
                    ToastUtils.showToast("连接超时，等会再试试");
                    break;
                default:
                    break;
            }
            if (AutoUpdateService.getInstance() != null) {
                AutoUpdateService.getInstance().setDownloading(false);
            }
        }

    };

    @Override
    public void run() {
        if (!TextUtils.isEmpty(url) && !TextUtils.isEmpty(fileName) && !TextUtils.isEmpty(loadingFileName)) {
            if (!FileUtil.isFileExist(FileUtil.DEFAULT_APK_DIR, fileName)) {
                file = downLoadFile(url);
                if (file != null && file.exists() && file.renameTo(new File(FileUtil.DEFAULT_APK_DIR + File.separator + fileName))) {
                    file = new File(FileUtil.DEFAULT_APK_DIR + File.separator + fileName);
                    versionModel.file = fileName;
                    Message message = new Message();
                    message.what = DOWNLOAD_SUCCESSED;
                    updateHandler.sendMessage(message);
                    // openFile(file);
                } else {
                    Message message = new Message();
                    message.what = DOWNLOAD_FAILED;
                    // 下载失败
                    updateHandler.sendMessage(message);
                }
            } else {
                if (mNeedAutoInstall) {
                    versionModel.file = fileName;
                    Message message = new Message();
                    message.what = FILE_CAN_INSTALL;
                    updateHandler.sendMessage(message);
                } else {
                    Message message = new Message();
                    message.what = DOWNLOAD_END;
                    updateHandler.sendMessage(message);
                }
            }
        } else {
            if (AutoUpdateService.getInstance() != null) {
                AutoUpdateService.getInstance().setDownloading(false);
            }
        }

    }

    protected File downLoadFile(String httpUrl) {
        int downloadCount = 0;
        long totalSize = 0;
        int updateTotalSize = 0;
        this.updateNotificationManager = (NotificationManager) context.getSystemService(Activity.NOTIFICATION_SERVICE);
        updateIntent = new Intent();
        updatePendingIntent = PendingIntent.getActivity(context, 0, updateIntent, 0);

        mBuilder = new NotificationCompat.Builder(context);

                mBuilder.setAutoCancel(false).setOngoing(true).setContentTitle(context.getString(R.string.app_name))
                        .setContentText("下载中").setSmallIcon(R.drawable.logo).setContentIntent(updatePendingIntent).setProgress(0, 0, true);

            updateNotificationManager.notify(id, mBuilder.build());

        File tmpFile = null;
        if (FileUtil.isSDCardAvailableNow()) {
            String filePath = FileUtil.getAppSdcardPath();
            if (filePath != null && !"".equals(filePath)) {
                tmpFile = new File(filePath);
                if (!tmpFile.exists()) {
                    if (!tmpFile.mkdir())
                        tmpFile = context.getFilesDir();
                }
            } else {
                tmpFile = context.getFilesDir();
            }
        } else {
            tmpFile = context.getFilesDir();
        }

        File file = new File(tmpFile.getAbsolutePath() + "/" + loadingFileName);
        if (file.exists()) {
            FileUtil.deleteFile(FileUtil.DEFAULT_APK_DIR, loadingFileName);
            file = new File(tmpFile.getAbsolutePath() + "/" + loadingFileName);
        }

        try {
            URL url = new URL(httpUrl);
            try {
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setInstanceFollowRedirects(true);
                FileOutputStream fos = new FileOutputStream(file);
                byte[] buf = new byte[1024];
                conn.connect();
                if (conn.getResponseCode() != 200) {
                    Message message = new Message();
                    message.what = DOWNLOAD_TIMEOUT;
                    updateHandler.sendMessage(message);
                } else {
                    updateTotalSize = conn.getContentLength();
                    InputStream is = conn.getInputStream();
                    while (true) {
                        if (is != null) {
                            int numRead = is.read(buf);
                            if (totalSize >= updateTotalSize) {
                                break;
                            } else {
                                fos.write(buf, 0, numRead);
                                totalSize += numRead;
                                if ((downloadCount == 0) || (int) (totalSize * 100 / updateTotalSize) - 1 > downloadCount) {
                                    downloadCount += 1;
                                    mBuilder.setProgress(100, (int) (totalSize * 100 / updateTotalSize), false);
                                    mBuilder.setContentText((int) (totalSize * 100 / updateTotalSize) + "%");
                                    updateNotificationManager.notify(id, mBuilder.build());
                                }
                            }
                        } else {
                            break;
                        }
                    }
                    if (is != null) {
                        is.close();
                    }
                }
                conn.disconnect();
                fos.close();
            } catch (IOException e) {
                // #debug debug
                e.printStackTrace();
                file = null;
                FileUtil.deleteFile(FileUtil.DEFAULT_APK_DIR, loadingFileName);
            }
        } catch (MalformedURLException e) {
            // #debug debug
            e.printStackTrace();
            file = null;
            FileUtil.deleteFile(FileUtil.DEFAULT_APK_DIR, loadingFileName);
        } finally {
            if (totalSize < updateTotalSize) {
                FileUtil.deleteFile(FileUtil.DEFAULT_APK_DIR, loadingFileName);
                file = null;
            }
        }

        return file;
    }
}
