package com.app.friendcircle;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 一个图片对象
 *
 * @author Administrator
 */
public class ImageItem implements Parcelable {
    public String imageId;
    public String thumbnailPath;
    public String imagePath;
    public boolean isSelected = false;

    public ImageItem() {

    }

    protected ImageItem(Parcel in) {
        imageId = in.readString();
        thumbnailPath = in.readString();
        imagePath = in.readString();
        isSelected = in.readByte() != 0;
    }

    public static final Creator<ImageItem> CREATOR = new Creator<ImageItem>() {
        @Override
        public ImageItem createFromParcel(Parcel in) {
            return new ImageItem(in);
        }

        @Override
        public ImageItem[] newArray(int size) {
            return new ImageItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(imageId);
        dest.writeString(thumbnailPath);
        dest.writeString(imagePath);
        dest.writeByte((byte) (isSelected ? 1 : 0));
    }
}
