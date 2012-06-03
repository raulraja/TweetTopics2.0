package com.javielinux.tweettopics2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar.OnSeekBarChangeListener;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.javielinux.utils.Utils;

public class TabGeo extends Activity {
	
	public static final int ACTIVITY_MAPSEARCH = 0;
	
	private long mCurrentId = -1;
	
	private CheckBox mUseGeo;
	private RadioGroup mTypeGeo;
	private RadioButton mTypeGeoGPS;
	private RadioButton mTypeGeoMap;
	private EditText mLatitude;
	private EditText mLongitude;
	private SeekBar mDistance;
	private TextView mDistanceTxt;
	private RadioGroup mTypeDistance;
	private RadioButton mTypeDistanceMiles;
	private RadioButton mTypeDistanceKM;
	
	private Button mBtMap;
	
	int distance = 0;
	
	private LinearLayout mLLMap;
	private LinearLayout mLLDistance;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
                
        try {
            DataFramework.getInstance().open(this, Utils.packageName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        if (savedInstanceState != null) {
        	if (savedInstanceState.containsKey(DataFramework.KEY_ID)) mCurrentId = savedInstanceState.getLong(DataFramework.KEY_ID);
       	} else {
       		Bundle extras = getIntent().getExtras();  
       		if (extras != null) {
       			if (extras.containsKey(DataFramework.KEY_ID)) mCurrentId = extras.getLong(DataFramework.KEY_ID);
       		}
       	}
        
        setContentView(R.layout.tab_geo_search);
        
        mUseGeo = (CheckBox) findViewById(R.id.cb_use_geo);
        mTypeGeo = (RadioGroup) findViewById(R.id.rg_type_geo);
        mTypeGeoGPS = (RadioButton) findViewById(R.id.rb_use_gps);
        mTypeGeoMap = (RadioButton) findViewById(R.id.rb_use_map);
        mTypeDistance = (RadioGroup) findViewById(R.id.rg_type_distance);
        mTypeDistanceMiles = (RadioButton) findViewById(R.id.rb_distance_miles);
        mTypeDistanceKM = (RadioButton) findViewById(R.id.rb_distance_km);
        
        mLLMap = (LinearLayout) findViewById(R.id.ll_map);
        mLLDistance = (LinearLayout) findViewById(R.id.ll_distance);
        
        mLatitude = (EditText) findViewById(R.id.et_latitude);
        mLongitude = (EditText) findViewById(R.id.et_longitude);
        mDistance = (SeekBar) findViewById(R.id.sb_distance);
        mDistanceTxt = (TextView) findViewById(R.id.distance);
        
        mBtMap = (Button) findViewById(R.id.bt_map);
                
        mDistance.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
				changeTextDistance(arg1);
			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
			}
        	
        });
        
        mUseGeo.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,	boolean isChecked) {
				if (isChecked) {
					showFields();
				} else {
					hideFields();
				}
			}

        	
        });
        
        mTypeGeoGPS.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,	boolean isChecked) {
				if (isChecked){
					hideFieldsMap();
				}
			}
        	
        });
        
        mTypeGeoMap.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,	boolean isChecked) {
				if (isChecked){
					showFieldsMap();
				}
			}
        	
        });
        
        mTypeDistanceMiles.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,	boolean isChecked) {
				reloadTextDistance();
			}
        	
        });
        
        mTypeDistanceKM.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,	boolean isChecked) {
				reloadTextDistance();
			}
        	
        });
        
        
        mBtMap.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Utils.showMessage(TabGeo.this, TabGeo.this.getString(R.string.msg_finger_map));
				Intent newsearch = new Intent(TabGeo.this, MapSearch.class);
				startActivityForResult(newsearch, ACTIVITY_MAPSEARCH);
			}
        	
        });
        
        
        populateFields();
        
    }
    
    private void reloadTextDistance() {
    	changeTextDistance(distance);
    }
    
    private void changeTextDistance(int distance) {
    	this.distance = distance;
    	String t = TabGeo.this.getString(R.string.distance) + " (" + distance;
		if (mTypeDistanceMiles.isChecked())
			t += " " +  TabGeo.this.getString(R.string.miles) + ")";
		else
			t += " " +  TabGeo.this.getString(R.string.km) + ")";
		mDistanceTxt.setText(t);
    }
    
    private void showFieldsMap() {
    	mLatitude.setVisibility(View.VISIBLE);
    	mLongitude.setVisibility(View.VISIBLE);
    	mBtMap.setVisibility(View.VISIBLE);
    }
    
    private void hideFieldsMap() {
    	mLatitude.setVisibility(View.GONE);
    	mLongitude.setVisibility(View.GONE);
    	mBtMap.setVisibility(View.GONE);
    }
    
    private void showFields() {
    	mLLMap.setVisibility(View.VISIBLE);
    	mLLDistance.setVisibility(View.VISIBLE);
    	mTypeGeo.setVisibility(View.VISIBLE);
    	mTypeDistance.setVisibility(View.VISIBLE);
    }
    
    private void hideFields() {
    	mLLMap.setVisibility(View.GONE);
    	mLLDistance.setVisibility(View.GONE);
    	mTypeGeo.setVisibility(View.GONE);
    	mTypeDistance.setVisibility(View.GONE);
    }
        
    private void populateFields() {
    	if (mCurrentId != -1) {
    		Entity ent = new Entity("search", mCurrentId);
    		mLatitude.setText(ent.getString("latitude"));
    		mLongitude.setText(ent.getString("longitude"));
    		mDistance.setProgress(ent.getInt("distance"));
    		
    		if (ent.getInt("use_geo")==1) {
    			mUseGeo.setChecked(true);
    			showFields();
    		}
    		
    		if (ent.getInt("type_geo")==1) {
    			mTypeGeoGPS.setChecked(true);
    		} else {
    			mTypeGeoMap.setChecked(true);
    		}
    		
    		if (ent.getInt("type_distance")==1) {
    			mTypeDistanceKM.setChecked(true);
    		} else {
    			mTypeDistanceMiles.setChecked(true);
    		}
    		
    	} else {
    		mTypeGeoGPS.setChecked(true);
    		mTypeDistanceMiles.setChecked(true);
    	}
    	reloadTextDistance();
    }
    
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, 
                                    Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
               
        switch (requestCode){
        	case ACTIVITY_MAPSEARCH:
        		if( resultCode != 0 ) {
        			Bundle extras = intent.getExtras();
   					if (extras.containsKey("longitude")) {
						mLongitude.setText(extras.getFloat("longitude")+"");
					}

					if (extras.containsKey("latitude")) {
						mLatitude.setText(extras.getFloat("latitude")+"");
					}

        		}
        	break;
        	
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        DataFramework.getInstance().close();
    }
	
}
