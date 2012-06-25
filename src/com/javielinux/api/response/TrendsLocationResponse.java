package com.javielinux.api.response;

import twitter4j.Location;
import twitter4j.ResponseList;

public class TrendsLocationResponse implements BaseResponse {

    private ResponseList<Location> location_list;

    public ResponseList<Location> getLocationList() {
        return location_list;
    }
    public void setLocationList(ResponseList<Location> location_list) {
        this.location_list = location_list;
    }

    @Override
    public boolean isError() {
        return false;
    }
}
