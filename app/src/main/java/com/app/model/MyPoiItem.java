package com.app.model;

import com.amap.api.services.core.PoiItem;

/**
 * Created by acer on 2016/11/4.
 */

public class MyPoiItem {
    PoiItem poiItem;
    boolean isselect;

    public PoiItem getPoiItem() {
        return poiItem;
    }

    public void setPoiItem(PoiItem poiItem) {
        this.poiItem = poiItem;
    }

    public boolean isselect() {
        return isselect;
    }

    public void setIsselect(boolean isselect) {
        this.isselect = isselect;
    }
}
