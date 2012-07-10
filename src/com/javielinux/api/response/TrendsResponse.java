package com.javielinux.api.response;

import twitter4j.Trend;

import java.util.ArrayList;

public class TrendsResponse implements BaseResponse {

    private ArrayList<Trend> trend_list;

    public ArrayList<Trend> getTrends() {
        return trend_list;
    }
    public void setTrends(ArrayList<Trend> trend_list) {
        this.trend_list = trend_list;
    }

    @Override
    public boolean isError() {
        return false;
    }
}
