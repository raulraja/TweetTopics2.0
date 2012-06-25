package com.javielinux.api.request;

public class ProfileImageRequest implements BaseRequest {

    private int action = 0;
    private long user_id = 0;

    public ProfileImageRequest(int action, long user_id) {
        this.action = action;
        this.user_id = user_id;
    }

    public int getAction() {
        return action;
    }
    public void setAction(int action) {
        this.action = action;
    }

    public long getUserId() {
        return user_id;
    }
    public void setUserId(long user_id) {
        this.user_id = user_id;
    }
}
