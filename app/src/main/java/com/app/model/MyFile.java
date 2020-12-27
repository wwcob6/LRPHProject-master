package com.app.model;

/**
 *
 * Created by acer on 2016/6/17.
 */
public class MyFile {
    private String fileId;
    private String fileName;
    private String fileType;
    private String localPath;
    private String ftpPath;
    private long size;
    private int isDownloadFinish;
    private int isTransferFinish;
    private String from;
    private int time;
    private String groupId;
    private int progress;
    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getFtpPath() {
        return ftpPath;
    }

    public int getIsDownloadFinish() {
        return isDownloadFinish;
    }

    public int getIsTransferFinish() {
        return isTransferFinish;
    }

    public void setIsTransferFinish(int isTransferFinish) {
        this.isTransferFinish = isTransferFinish;
    }

    public void setIsDownloadFinish(int isDownloadFinish) {
        this.isDownloadFinish = isDownloadFinish;
    }

    public String getLocalPath() {
        return localPath;
    }

    public long getSize() {
        return size;
    }

    public String getFileId() {
        return fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public void setFtpPath(String ftpPath) {
        this.ftpPath = ftpPath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o != null && o.getClass() == MyFile.class) {
            MyFile myfile = (MyFile) o;
            if (this.getFileId().equals(myfile.getFileId())) {
                return true;
            }
        }
        return false;
    }
}
