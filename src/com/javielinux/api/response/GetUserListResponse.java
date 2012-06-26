package com.javielinux.api.response;

public class GetUserListResponse implements BaseResponse {

    private boolean ready;

    public boolean getReady() {
        return ready;
    }
    public void setReady(boolean ready) {
        this.ready = ready;
    }

    @Override
    public boolean isError() {
        return false;
    }
}