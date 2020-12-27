package com.app.ftp;

import com.app.model.Constant;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;


/**
 * Author chzjy
 * Date 2016/12/14.
 */

public class ProgressInputStream extends InputStream {
    private static final int TEN_KILOBYTES = 1024 * 50;  //每上传50K返回一次

    private InputStream inputStream;
    /**
     * 进度
     */
    private long progress;
    /**
     * 是否关闭
     */
    private boolean closed;

    private long lastUpdate;

    FtpListener listener;

    private File targetFile;
    public ProgressInputStream(InputStream inputStream, FtpListener listener, File targetFile) {
        this.inputStream = inputStream;
        this.progress = 0;
        this.lastUpdate = 0;
        this.closed = false;
        this.listener = listener;
        this.targetFile=targetFile;
        this.closed=false;
    }

    @Override
    public int read() throws IOException {
        int count = inputStream.read();
        return incrementCounterAndUpdateDisplay(count);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int count = inputStream.read(b, off, len);
        return incrementCounterAndUpdateDisplay(count);
    }

    @Override
    public void close() throws IOException {
        super.close();
        if (closed)
            throw new IOException("already closed");
        closed = true;
    }

    private int incrementCounterAndUpdateDisplay(int count) {
        if (count > 0)
            progress += count;
        lastUpdate = maybeUpdateDisplay(progress, lastUpdate);
        return count;
    }

    private long maybeUpdateDisplay(long progress, long lastUpdate) {
        if (progress - lastUpdate > TEN_KILOBYTES) {
            lastUpdate = progress;
            listener.onUploadProgress(Constant.FTP_UPLOAD_LOADING, progress,targetFile);
        }
        return lastUpdate;
    }
}
