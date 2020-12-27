package com.app.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.app.R;
import com.punuo.sys.app.activity.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ShowLocation extends BaseActivity implements LocationSource, GeocodeSearch.OnGeocodeSearchListener {

    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.mapview)
    MapView mapview;
    private AMap aMap = null;
    public AMapLocationClient mapLocationClient;
    public AMapLocationClientOption mapLocationClientOption;
    private MyLocationStyle myLocationStyle = new MyLocationStyle();
    GeocodeQuery query;
    GeocodeSearch geocodeSearch;
    String location;
    private Marker geoMarker;
    MarkerOptions marker = new MarkerOptions();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_location);
        ButterKnife.bind(this);
        location = getIntent().getStringExtra("location");
        Log.d("111",location);
        title.setText("位置信息");
        mapview.onCreate(savedInstanceState);
        if (aMap == null) {
            aMap = mapview.getMap();
        }
        setUpMap();
        geocodeSearch = new GeocodeSearch(this);
        geocodeSearch.setOnGeocodeSearchListener(this);
        query = new GeocodeQuery(location, null);
        geocodeSearch.getFromLocationNameAsyn(query);
    }

    private void setUpMap() {
        myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));
        myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.icon_myloction));
        aMap.setMyLocationStyle(myLocationStyle);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapview.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapview.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapview.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapview.onSaveInstanceState(outState);
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {

    }

    @Override
    public void deactivate() {

    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {

    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
        if (i == 1000) {
            if (geocodeResult!=null&&geocodeResult.getGeocodeAddressList()!=null &&
                    geocodeResult.getGeocodeAddressList().size()>0){
                GeocodeAddress address=geocodeResult.getGeocodeAddressList().get(0);
                aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(address.getLatLonPoint().getLatitude(),
                        address.getLatLonPoint().getLongitude()),17));
                geoMarker=aMap.addMarker(marker.position(new LatLng(address.getLatLonPoint().getLatitude(),
                        address.getLatLonPoint().getLongitude())));
            }
        }
    }
}
