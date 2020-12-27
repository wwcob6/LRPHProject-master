package com.app.ftp;

import java.io.File;

/**
 * Author chzjy
 * Date 2016/12/14.
 */

public interface FtpListener {
    /**
     * 状态变化监听
     * @param currentStep
     */
    public void onStateChange(String currentStep);
    /**
     * 上传进度监听
     */
    public void onUploadProgress(String currentStep, long uploadSize, File targetFile);

    /**
     * 下载进度监听
     */
    public void onDownLoadProgress(String currentStep, long downProcess, File targetFile);

    /**
     * 文件删除监听
     */
    public void onDeleteProgress(String currentStep);

}
