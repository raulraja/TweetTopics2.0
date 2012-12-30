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

package com.javielinux.api.loaders;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import com.javielinux.api.AsynchronousLoader;
import com.javielinux.api.request.GetGeolocationAddressRequest;
import com.javielinux.api.response.BaseResponse;
import com.javielinux.api.response.ErrorResponse;
import com.javielinux.api.response.GetGeolocationAddressResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GetGeolocationAddressLoader extends AsynchronousLoader<BaseResponse> {

    private GetGeolocationAddressRequest request;

    public GetGeolocationAddressLoader(Context context, GetGeolocationAddressRequest request) {
        super(context);
        this.request = request;
    }

    @Override
    public BaseResponse loadInBackground() {

        try {
            GetGeolocationAddressResponse response = new GetGeolocationAddressResponse();
            response.setSingleResult(request.getSingleResult());

            Geocoder geocoder = new Geocoder(request.getContext());

            List<Address> addresses;

            if (request.getSingleResult())
                addresses = geocoder.getFromLocationName(request.getText(), 1);
            else
                addresses = geocoder.getFromLocationName(request.getText(), 5);

            ArrayList<Address> address_list = new ArrayList<Address>();

            for (Address address : addresses)
                address_list.add(address);

            response.setAddressList(address_list);

            return response;
        } catch (IOException exception) {
            exception.printStackTrace();
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setError(exception, exception.getMessage());
            return errorResponse;
        }
    }
}
