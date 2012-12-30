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

package com.javielinux.fragments;

import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.javielinux.adapters.AddressAdapter;
import com.javielinux.api.APIDelegate;
import com.javielinux.api.APITweetTopics;
import com.javielinux.api.request.GetGeolocationAddressRequest;
import com.javielinux.api.response.BaseResponse;
import com.javielinux.api.response.ErrorResponse;
import com.javielinux.api.response.GetGeolocationAddressResponse;
import com.javielinux.database.EntitySearch;
import com.javielinux.tweettopics2.R;

import java.util.ArrayList;

public class SearchGeoFragment extends Fragment implements APIDelegate<BaseResponse> {

    public static final int ACTIVITY_MAPSEARCH = 0;

    private EntitySearch search_entity;

    public CheckBox useGeo;
    private RadioGroup typeGeo;
    public RadioButton typeGeoGPS;
    public RadioButton typeGeoMap;
    private AutoCompleteTextView place;
    public EditText latitude;
    public EditText longitude;
    public SeekBar distance;
    private TextView distanceTxt;
    private RadioGroup typeDistance;
    public RadioButton typeDistanceMiles;
    public RadioButton typeDistanceKM;
    private LinearLayout llLocation;
    private LinearLayout llMap;
    private LinearLayout llDistance;

    private AddressAdapter address_adapter;
    private ArrayList<Address> address_list;
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

        llLocation = (LinearLayout)view.findViewById(R.id.ll_location);
        llMap = (LinearLayout)view.findViewById(R.id.ll_map);
        llDistance = (LinearLayout)view.findViewById(R.id.ll_distance);

        place = (AutoCompleteTextView)view.findViewById(R.id.et_place);

        address_list = new ArrayList<Address>();
        address_adapter = new AddressAdapter(getActivity(), address_list);
        place.setAdapter(address_adapter);

        place.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Address address = address_adapter.getAddressItem(i);

                if (address != null) {
                    latitude.setText(String.valueOf(address.getLatitude()));
                    longitude.setText(String.valueOf(address.getLongitude()));
                }
            }
        });

        latitude = (EditText)view.findViewById(R.id.et_latitude);
        longitude = (EditText)view.findViewById(R.id.et_longitude);
        distance = (SeekBar)view.findViewById(R.id.sb_distance);
        distanceTxt = (TextView)view.findViewById(R.id.distance);

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

        place.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String currentText = place.getText().toString();

                if (currentText.length() >= 3)
                    APITweetTopics.execute(getActivity(), getLoaderManager(), SearchGeoFragment.this, new GetGeolocationAddressRequest(getActivity(), currentText, false));
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void afterTextChanged(Editable editable) {}
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
        place.setVisibility(View.VISIBLE);
        latitude.setVisibility(View.VISIBLE);
        longitude.setVisibility(View.VISIBLE);
    }

    private void hideFieldsMap() {
        place.setVisibility(View.GONE);
        latitude.setVisibility(View.GONE);
        longitude.setVisibility(View.GONE);
    }

    private void showFields() {
        llLocation.setVisibility(View.VISIBLE);
        llMap.setVisibility(View.VISIBLE);
        llDistance.setVisibility(View.VISIBLE);
        typeGeo.setVisibility(View.VISIBLE);
        typeDistance.setVisibility(View.VISIBLE);
    }

    private void hideFields() {
        llLocation.setVisibility(View.GONE);
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

    @Override
    public void onResults(BaseResponse response) {
        GetGeolocationAddressResponse result = (GetGeolocationAddressResponse)response;

        if (result.getSingleResult()) {

            if (result.getAddressList().size() > 0) {
                Address address = result.getAddressList().get(0);

                String text = address.getAddressLine(0);

                if (address.getCountryName() != null)
                    text = text + " (" + address.getCountryName() + ")";

                place.setText(text);
                latitude.setText(String.valueOf(address.getLatitude()));
                longitude.setText(String.valueOf(address.getLongitude()));
            }
        } else {
            address_list.clear();

            for(Address address: result.getAddressList()) {
                address_list.add(address);
            }

            address_adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onError(ErrorResponse error) {
        Log.d(getActivity().getResources().getString(R.string.app_name) + ".0",error.getMsgError());
        error.getError().printStackTrace();
    }
}