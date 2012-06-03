package com.javielinux.utils;

import android.content.Context;
import android.location.*;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationUtils {

    static public Location getLastLocation(Context cnt) {
        LocationManager locationmanager = (LocationManager)cnt.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        String provider = locationmanager.getBestProvider(criteria,true);
        if (provider != null) {
            return locationmanager.getLastKnownLocation(provider);
        }
        return null;
    }

    static public String getAddressFromLastLocation(Context cnt) {
        String address = "";
        Geocoder geoCoder = new Geocoder(cnt, Locale.getDefault());
        try {
            Location loc = getLastLocation(cnt);
            if (loc!=null) {
                List<Address> addresses = geoCoder.getFromLocation(
                        loc.getLatitude(),
                        loc.getLongitude(), 1);

                if (addresses.size() > 0) {
                    for (int i = 0; i < addresses.get(0).getMaxAddressLineIndex(); i++) {
                        address += addresses.get(0).getAddressLine(i) + " ";
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return address;
    }

}
