package com.javielinux.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.javielinux.database.EntitySearch;
import com.javielinux.tweettopics2.MapSearch;
import com.javielinux.tweettopics2.R;
import com.javielinux.utils.Utils;

public class SearchGeoFragment extends Fragment {

    public static final int ACTIVITY_MAPSEARCH = 0;

    private EntitySearch search_entity;

    private CheckBox useGeo;
    private RadioGroup typeGeo;
    private RadioButton typeGeoGPS;
    private RadioButton typeGeoMap;
    private EditText latitude;
    private EditText longitude;
    private SeekBar distance;
    private TextView distanceTxt;
    private RadioGroup typeDistance;
    private RadioButton typeDistanceMiles;
    private RadioButton typeDistanceKM;
    private Button btMap;
    private LinearLayout llMap;
    private LinearLayout llDistance;

    private int distance_value = 0;

    public SearchGeoFragment(EntitySearch search_entity) {
        this.search_entity = search_entity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.search_geo_fragment, null);

        useGeo = (CheckBox)view.findViewById(R.id.cb_use_geo);
        typeGeo = (RadioGroup)view.findViewById(R.id.rg_type_geo);
        typeGeoGPS = (RadioButton)view.findViewById(R.id.rb_use_gps);
        typeGeoMap = (RadioButton)view.findViewById(R.id.rb_use_map);
        typeDistance = (RadioGroup)view.findViewById(R.id.rg_type_distance);
        typeDistanceMiles = (RadioButton)view.findViewById(R.id.rb_distance_miles);
        typeDistanceKM = (RadioButton)view.findViewById(R.id.rb_distance_km);

        llMap = (LinearLayout)view.findViewById(R.id.ll_map);
        llDistance = (LinearLayout)view.findViewById(R.id.ll_distance);

        latitude = (EditText)view.findViewById(R.id.et_latitude);
        longitude = (EditText)view.findViewById(R.id.et_longitude);
        distance = (SeekBar)view.findViewById(R.id.sb_distance);
        distanceTxt = (TextView)view.findViewById(R.id.distance);

        btMap = (Button)view.findViewById(R.id.bt_map);

        distance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
                changeTextDistance(arg1);
            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {}

            @Override
            public void onStopTrackingTouch(SeekBar arg0) {}
        });

        useGeo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,	boolean isChecked) {
                if (isChecked) {
                    showFields();
                } else {
                    hideFields();
                }
            }
        });

        typeGeoGPS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,	boolean isChecked) {
                if (isChecked){
                    hideFieldsMap();
                }
            }
        });

        typeGeoMap.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,	boolean isChecked) {
                if (isChecked){
                    showFieldsMap();
                }
            }
        });

        typeDistanceMiles.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,	boolean isChecked) {
                reloadTextDistance();
            }
        });

        typeDistanceKM.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,	boolean isChecked) {
                reloadTextDistance();
            }
        });

        btMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Utils.showMessage(getActivity(), getActivity().getString(R.string.msg_finger_map));
                Intent newsearch = new Intent(getActivity(), MapSearch.class);
                startActivityForResult(newsearch, ACTIVITY_MAPSEARCH);
            }

        });

        populateFields();

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        switch (requestCode){
            case ACTIVITY_MAPSEARCH:
                if( resultCode != 0 ) {
                    Bundle extras = intent.getExtras();
                    if (extras.containsKey("longitude")) {
                        longitude.setText(extras.getFloat("longitude")+"");
                    }

                    if (extras.containsKey("latitude")) {
                        latitude.setText(extras.getFloat("latitude")+"");
                    }
                }
                break;
        }
    }

    private void reloadTextDistance() {
        changeTextDistance(distance_value);
    }

    private void changeTextDistance(int distance_value) {
        this.distance_value = distance_value;

        String t = getActivity().getString(R.string.distance) + " (" + distance_value;

        if (typeDistanceMiles.isChecked())
            t += " " +  getActivity().getString(R.string.miles) + ")";
        else
            t += " " +  getActivity().getString(R.string.km) + ")";

        distanceTxt.setText(t);
    }

    private void showFieldsMap() {
        latitude.setVisibility(View.VISIBLE);
        longitude.setVisibility(View.VISIBLE);
        btMap.setVisibility(View.VISIBLE);
    }

    private void hideFieldsMap() {
        latitude.setVisibility(View.GONE);
        longitude.setVisibility(View.GONE);
        btMap.setVisibility(View.GONE);
    }

    private void showFields() {
        llMap.setVisibility(View.VISIBLE);
        llDistance.setVisibility(View.VISIBLE);
        typeGeo.setVisibility(View.VISIBLE);
        typeDistance.setVisibility(View.VISIBLE);
    }

    private void hideFields() {
        llMap.setVisibility(View.GONE);
        llDistance.setVisibility(View.GONE);
        typeGeo.setVisibility(View.GONE);
        typeDistance.setVisibility(View.GONE);
    }

    private void populateFields() {
        latitude.setText(search_entity.getString("latitude"));
        longitude.setText(search_entity.getString("longitude"));
        distance.setProgress(search_entity.getInt("distance_value"));

        if (search_entity.getInt("use_geo") == 1) {
            useGeo.setChecked(true);
            showFields();
        }

        if (search_entity.getInt("type_geo") == 1)
            typeGeoGPS.setChecked(true);
        else
            typeGeoMap.setChecked(true);

        if (search_entity.getInt("type_distance")==1)
            typeDistanceKM.setChecked(true);
        else
            typeDistanceMiles.setChecked(true);

        reloadTextDistance();
    }
}
