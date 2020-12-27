package com.app.publish.event;

import java.util.List;

/**
 * Created by han.chen.
 * Date on 2019-06-22.
 **/
public class ChooseImageResultEvent {

    public List<String> mImages;

    public ChooseImageResultEvent(List<String> images) {
        mImages = images;
    }
}
