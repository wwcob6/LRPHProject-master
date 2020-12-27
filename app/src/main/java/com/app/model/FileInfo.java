package com.app.model;

import android.util.Log;

/**
 *
 * Created by acer on 2016/10/8.
 */

public class FileInfo {
    private FileType fileType;
    private String fileName;
    private String filePath;
    private boolean isfile_selected;

    public FileInfo(String filePath, String fileName, boolean isDirectory) {
        this.filePath = filePath;
        this.fileName = fileName;
        fileType = isDirectory ? FileType.DIRECTORY : FileType.FILE;
        isfile_selected = false;
    }

    String fileSuffix;

    public FileType whichtype() {
        Log.d("FileInfo",fileName);
        if (fileName.lastIndexOf(".") < 0)
            fileSuffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        else fileSuffix = fileName.substring(fileName.lastIndexOf("."));
        if (fileName.lastIndexOf(".") < 0)
            return FileType.UNKNOWN;
        else if (!isDirectory()) {
            if (fileSuffix.equals(".doc"))
                return FileType.DOC;
            if (fileSuffix.equals(".docx"))
                return FileType.DOCX;
            if (fileSuffix.equals(".ppt"))
                return FileType.PPT;
            if (fileSuffix.equals(".pptx"))
                return FileType.PPTX;
            if (fileSuffix.equals(".txt"))
                return FileType.TXT;
            if (fileSuffix.equals(".mp4"))
                return FileType.MP4;
            if (fileSuffix.equals(".mp3"))
                return FileType.MP3;
            if (fileSuffix.equals(".wav"))
                return FileType.WAV;
            if (fileSuffix.equals(".png"))
                return FileType.PNG;
            if (fileSuffix.equals(".xls"))
                return FileType.XLS;
            if (fileSuffix.equals(".xlxs"))
                return FileType.XLXS;
            if (fileSuffix.equals(".pdf"))
                return FileType.PDF;
            if (fileSuffix.equals(".wma"))
                return FileType.PDF;
            if (fileSuffix.equals(".gif"))
                return FileType.PDF;
            if (fileSuffix.equals(".bmp"))
                return FileType.PDF;
            if (fileSuffix.equals(".avi"))
                return FileType.PDF;
            if (fileSuffix.equals(".rar"))
                return FileType.RAR;
            if (fileSuffix.equals(".zip"))
                return FileType.ZIP;
            if (fileSuffix.equals(".jpg"))
                return FileType.JPG;

        }
        return FileType.UNKNOWN;
    }


    public boolean isDirectory() {
        return fileType == FileType.DIRECTORY;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public String toString() {
        return "FileInfo [fileType=" + fileType + ", fileName=" + fileName
                + ", filePath=" + filePath + "]";
    }

    public void setSelected(boolean flag) {
        isfile_selected = flag;
    }

    public boolean isSelected() {
        return isfile_selected;
    }
}

