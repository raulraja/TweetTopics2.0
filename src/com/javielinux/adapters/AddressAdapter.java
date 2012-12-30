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

package com.javielinux.adapters;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

public class AddressAdapter extends ArrayAdapter<String> {

    private Context context;
    private Geocoder geocoder;
    private ArrayList<Address> addressList;

    public AddressAdapter(Context context, ArrayList<Address> addressList) {
        super(context, android.R.layout.simple_dropdown_item_1line);

        this.addressList = addressList;
        this.context = context;
        this.geocoder = new Geocoder(context);
    }

    public Address getAddressItem(int index) {
        if (index < addressList.size())
            return addressList.get(index);

        return null;
    }

    @Override
    public int getCount() {
        return addressList.size();
    }

    @Override
    public String getItem(int position) {
        try {
            Address address = addressList.get(position);

            String text = address.getAddressLine(0);

            if (address.getCountryName() != null)
                text = text + " (" + address.getCountryName() + ")";

            return text;
        } catch (Exception exception) {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}