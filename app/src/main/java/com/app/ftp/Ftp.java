package com.app.ftp;

import android.util.Log;

import com.app.model.Constant;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import static java.io.File.separator;


/**
 * Author chzjy
 * Date 2016/12/9.
 */

public class Ftp {
    /**
     * ftp地址
     */
    private String ftp_url = "101.69.255.132";
    /**
     * ftp端口号
     */
    private int ftp_serverport = 21;
    /**
     * ftp登录账号
     */
    private String ftp_name = "ftpall";
    /**
     * ftp登录密码
     */
    private String ftp_password = "123456";
    /**
     * ftp连接
     */
    private FTPClient ftpClient;
    /**
     * ftp状态监听
     */
    private FtpListener listener;
    private String TAG = "Ftp";

    /**
     * 构造函数
     *
     * @param ftp_url        ftpIp
     * @param ftp_serverport 端口
     * @param ftp_name       登录用户名
     * @param ftp_password   密码
     * @param listener       监听
     */

    public Ftp(String ftp_url, int ftp_serverport, String ftp_name, String ftp_password, FtpListener listener) {
        this.ftp_url = ftp_url;
        this.ftp_serverport = ftp_serverport;
        this.ftp_name = ftp_name;
        this.ftp_password = ftp_password;
        this.ftpClient = new FTPClient();
        this.listener = listener;
    }

    public void setListener(FtpListener listener) {
        this.listener = listener;
    }

    /**
     * 打开ftp服务
     */
    private boolean openConnect() throws IOException {
        // 中文转码
        ftpClient.setControlEncoding("UTF-8");
        int reply; // 服务器响应值
        // 连接至服务器
        ftpClient.connect(ftp_url, ftp_serverport);
        // 获取响应值
        reply = ftpClient.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            // 断开连接
            ftpClient.disconnect();
            Log.e(TAG, "connect fail reply: " + reply);
            return false;
        }
        // 登录到服务器
        ftpClient.login(ftp_name, ftp_password);
        // 获取响应值
        reply = ftpClient.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            // 断开连接
            ftpClient.disconnect();
            Log.e(TAG, "connect fail reply: " + reply);
            return false;
        } else {
            // 获取登录信息
            FTPClientConfig config = new FTPClientConfig(ftpClient
                    .getSystemType().split(" ")[0]);
            config.setServerLanguageCode("zh");
            ftpClient.configure(config);
            // 使用被动传输模式
            ftpClient.enterLocalPassiveMode();
            // 二进制文件支持
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            ftpClient.setBufferSize(1024);
        }
        return true;
    }

    /**
     * 关闭ftp服务
     */
    private void closeConnect() throws IOException {
        if (ftpClient != null) {
            //退出ftp
            ftpClient.logout();
            //断开连接
            ftpClient.disconnect();
        }
    }

    /**
     * 连接ftp
     *
     * @throws IOException
     */
    private void prepare() throws IOException {
        try {
            if (openConnect()) {
                listener.onStateChange(Constant.FTP_CONNECT_SUCCESSS);
            } else {
                listener.onStateChange(Constant.FTP_CONNECT_FAIL);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 断开连接
     *
     * @throws IOException
     */
    private void finish() throws IOException {
        this.closeConnect();
        listener.onStateChange(Constant.FTP_DISCONNECT_SUCCESS);
    }

    /**
     * 在ftp上创建文件夹
     */
    private boolean mikDir(String ftpUrl) throws IOException {
        prepare();
        // 设置模式
        ftpClient.setFileTransferMode(FTPClient.BINARY_FILE_TYPE);
        // FTP下创建文件夹
        // 对传入的路径进行拆分，ftp里创建路径要一层一层的创建。
        String[] argPath = ftpUrl.split("/");
        // 一层一层的开始创建路径
        boolean isSucced = false;
        for (String name : argPath) {
            ftpClient.makeDirectory(name);
            //创建一层就切换到该目录下
            isSucced = ftpClient.changeWorkingDirectory(name);
        }
        return isSucced;
    }

    /**
     * 上传单个文件
     *
     * @param localFileUrl 本地文件的路径(/mnt/sdcard/abc.txt)
     * @param ftpFileDir   上传到ftp的目录 (/文件夹)
     * @return 是否成功
     * @throws IOException
     */
    public boolean upload(String localFileUrl, String ftpFileDir) throws IOException {
        //上传前在ftp上创建文件夹
        if (mikDir(ftpFileDir)) {
            listener.onStateChange(Constant.FTP_MIKEDIR_SUCCESS);
        } else {
            listener.onStateChange(Constant.FTP_MIKEDIR_FAIL);
            return false;
        }
        boolean isSucced = false;
        //目标文件
        File targetFile = new File(localFileUrl);
        if (targetFile.exists()) {
            BufferedInputStream buffIn = new BufferedInputStream(
                    new FileInputStream(targetFile));
            ProgressInputStream progressInput = new ProgressInputStream(buffIn, listener, targetFile);
            isSucced = ftpClient.storeFile(targetFile.getName(), progressInput);
            buffIn.close();
        } else {
            Log.e(TAG, "目标文件不存在!");
        }
        if (isSucced) {
            listener.onUploadProgress(Constant.FTP_UPLOAD_SUCCESS, 0, null);
        } else {
            listener.onUploadProgress(Constant.FTP_UPLOAD_FAIL, 0, null);
        }
        //上传结束
        finish();
        return isSucced;
    }

    /**
     * 上传多个文件
     *
     * @param fileUrlList 本地文件路径列表
     * @param ftpFileDir  ftp上的路径
     * @return 是否成功
     * @throws IOException
     */
    public boolean uploadMultiFile(ArrayList<String> fileUrlList, String ftpFileDir) throws IOException {
        boolean isSucced = false;
        for (String localFileUrl : fileUrlList) {
            isSucced = upload(localFileUrl, ftpFileDir);
        }
        return isSucced;
    }

    /**
     * 下载单个文件
     *
     * @param ftpFileUrl   ftp文件的路径(/文件夹/abc.txt)
     * @param localFileDir 下载到本地的目录(/文件夹/)
     * @throws IOException
     */
    public void download(String ftpFileUrl, String localFileDir) throws IOException {
        //连接ftp
        prepare();
        //分离文件名和目录
        int separatorIndex = ftpFileUrl.lastIndexOf(separator);
        String targetDir = ftpFileUrl.substring(0, separatorIndex);
        String targetFileName = (separatorIndex < 0) ? ftpFileUrl : ftpFileUrl.substring(separatorIndex + 1, ftpFileUrl.length());
        //判断服务器文件是否存在
        FTPFile[] files = ftpClient.listFiles(ftpFileUrl);
        Log.d("fileslength   ",""+files.length);
        if (files.length != 0) {
            listener.onStateChange(Constant.FTP_FILE_EXISTS);
        } else {
            listener.onStateChange(Constant.FTP_FILE_NOTEXISTS);
            //文件不存在,断开连接
            finish();
            return;
        }

        //创建本地文件夹
        File localDir = new File(localFileDir);
        if (!localDir.exists()) {
            localDir.mkdirs();
        }

        //本地文件路径
        String localFileUrl = localFileDir + targetFileName;

        //获取远程文件的大小
        long targetFileSize = files[0].getSize();
        //本地文件大小
        long localSize = 0;
        File localFile = new File(localFileUrl);

        //进度
        long step = targetFileSize / 100;
        if (step == 0) {
            step = targetFileSize % 100;
        }
        long progress = 0;
        long currentSize = 0;

//        //获取本地文件大小
//        localSize = localFile.exists() ? localFile.length() : 0;
//        //判断是否断点下载
//        if (localSize < targetFileSize) {
//            currentSize = localSize;
//        } else {
        //删除本地文件
        localFile.delete();
        localSize = 0;
//        }
        //开始准备下载文件
        OutputStream out = new FileOutputStream(localFile, true);
        //开始下载
        ftpClient.setRestartOffset(localSize);
        InputStream input = ftpClient.retrieveFileStream(ftpFileUrl);
        byte[] b = new byte[1024];
        int length = 0;
        //判断是否到文件的末尾
        while ((length = input.read(b)) != -1) {
            out.write(b, 0, length);
            currentSize = currentSize + length;
            if (currentSize / step != progress) {
                progress = (currentSize / step) > 100 ? 100 : (currentSize / step);
                listener.onDownLoadProgress(Constant.FTP_DOWN_LOADING, progress, null);
            }
        }
        out.flush();
        out.close();
        input.close();
        //此方法是来确保流处理完毕,如果没有此方法,可能会造成现程序死掉
        if (ftpClient.completePendingCommand()) {
            listener.onDownLoadProgress(Constant.FTP_DOWN_SUCCESS, 0, null);
        } else {
            listener.onDownLoadProgress(Constant.FTP_DOWN_FAIL, 0, null);
        }
        //断开连接
        finish();
    }

    /**
     * 删除文件
     *
     * @param ftpFileUrl ftp上的文件地址
     * @throws IOException
     */
    public void deleteFile(String ftpFileUrl) throws IOException {
        //连接ftp
        prepare();
        // 判断服务器文件是否存在
        FTPFile[] files = ftpClient.listFiles(ftpFileUrl);
        if (files.length == 0) {
            listener.onStateChange(Constant.FTP_FILE_NOTEXISTS);
            finish();
            return;
        }
        //删除
        boolean isSucced = true;
        isSucced = ftpClient.deleteFile(ftpFileUrl);
        if (isSucced) {
            listener.onDeleteProgress(Constant.FTP_DELETEFILE_SUCCESS);
        } else {
            listener.onDeleteProgress(Constant.FTP_DELETEFILE_FAIL);
        }
        //断开连接
        finish();
    }
}
