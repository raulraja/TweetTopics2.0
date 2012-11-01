package com.javielinux.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.javielinux.tweettopics2.TweetActivity;

public class TweetMapFragment extends Fragment {

    private static String KEY_SAVE_LATITUDE = "KEY_SAVE_LATITUDE";
    private static String KEY_SAVE_LONGITUDE = "KEY_SAVE_LONGITUDE";

    private double latitude = 0;
    private double longitude = 0;

    private MapView mMap;
    private MapController mMapController;

    public TweetMapFragment() {
        super();
    }

    public TweetMapFragment(double latitude, double longitude) {
        init(latitude, longitude);
    }

    public void init(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putDouble(KEY_SAVE_LATITUDE, latitude);
        outState.putDouble(KEY_SAVE_LONGITUDE, longitude);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState!=null && savedInstanceState.containsKey(KEY_SAVE_LATITUDE)) {
            init(savedInstanceState.getDouble(KEY_SAVE_LATITUDE), savedInstanceState.getDouble(KEY_SAVE_LONGITUDE));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView( inflater, container, savedInstanceState );
        mMap = ((TweetActivity)getActivity()).getMapView();
        mMapController = mMap.getController();
        //mMap.createMarker(latitude, longitude);
        GeoPoint gp = new GeoPoint((int)(latitude*1E6), (int)(longitude*1E6));
        mMapController.animateTo(gp);
        return mMap;
    }


}
