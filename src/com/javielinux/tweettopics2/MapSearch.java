package com.javielinux.tweettopics2;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;

public class MapSearch extends MapActivity {

	private MapViewer mMap;
	private MapController mMapController;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		double latitude = 0;
		double longitude = 0;
		
		if (savedInstanceState != null) {
			if (savedInstanceState.containsKey("latitude")) latitude = savedInstanceState.getDouble("latitude");
        	if (savedInstanceState.containsKey("longitude")) longitude = savedInstanceState.getDouble("longitude");
       	} else {
       		Bundle extras = getIntent().getExtras();  
       		if (extras != null) {
       			if (extras.containsKey("latitude")) latitude = extras.getDouble("latitude");
       			if (extras.containsKey("longitude")) longitude = extras.getDouble("longitude");
       		}
       	}
		
		boolean isSearch = false;
		
		if (longitude==0&&latitude==0) isSearch = true;
		
		if (isSearch) {
			mMap = new MapViewer(this, Utils.KEY_MAPS);
		} else {
			mMap = new MapViewer(this, Utils.KEY_MAPS, latitude, longitude);
		}
		
		mMap.setClickable(true);
		
		mMap.setBuiltInZoomControls(true);
		
		mMapController = mMap.getController();
		
		if (!isSearch) {
			GeoPoint gp = new GeoPoint((int)(latitude*1E6), (int)(longitude*1E6));
			mMapController.animateTo(gp);
		}
		
		setContentView(mMap);
		
		
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
    		Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
    		intent.putExtra("latitude", mMap.getLatitude());
    		intent.putExtra("longitude", mMap.getLongitude());
    		setResult(RESULT_OK, intent);
    		finish();
        }
        return super.onKeyDown(keyCode, event);
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	Utils.saveStatusWorkApp(this, true);
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        Utils.saveStatusWorkApp(this, false);
    }

}
