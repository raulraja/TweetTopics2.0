/*
 * Copyright 2012 Javier Pérez Pacheco and Francisco Díaz Rodriguez
 * TweetTopics 2.0
 * javielinux@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.javielinux.tweettopics2;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.javielinux.utils.PreferenceUtils;

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

        mMap = new MapViewer(this, getString(R.string.google_maps_api));

		if (!isSearch) {
			mMap.createMarker(latitude, longitude);
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
        PreferenceUtils.saveStatusWorkApp(this, true);
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        PreferenceUtils.saveStatusWorkApp(this, false);
    }

}
