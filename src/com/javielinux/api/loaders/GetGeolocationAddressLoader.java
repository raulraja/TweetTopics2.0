package com.javielinux.api.loaders;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import com.javielinux.api.AsynchronousLoader;
import com.javielinux.api.request.GetGeolocationAddressRequest;
import com.javielinux.api.response.GetGeolocationAddressResponse;
import com.javielinux.api.response.BaseResponse;
import com.javielinux.api.response.ErrorResponse;
import com.javielinux.twitter.ConnectionManager;
import twitter4j.TwitterException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
