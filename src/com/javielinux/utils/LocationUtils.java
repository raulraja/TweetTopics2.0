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
