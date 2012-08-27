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