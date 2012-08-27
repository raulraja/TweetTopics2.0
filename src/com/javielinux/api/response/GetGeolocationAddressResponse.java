package com.javielinux.api.response;

import android.location.Address;
import java.util.ArrayList;

public class GetGeolocationAddressResponse implements BaseResponse {

    private ArrayList<Address> address_list;
    private boolean single_result;

    public ArrayList<Address> getAddressList() {
        return this.address_list;
    }

    public void setAddressList(ArrayList<Address> address_list) {
        this.address_list = address_list;
    }

    public boolean getSingleResult() {
        return this.single_result;
    }

    public void setSingleResult(boolean single_result) {
        this.single_result = single_result;
    }

    @Override
    public boolean isError() {
        return false;
    }
}
