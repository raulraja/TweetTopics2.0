package com.javielinux.api.request;

public class ListUserTwitterRequest implements BaseRequest {

    private long userId;
    private String user;

    public ListUserTwitterRequest(long userId, String user) {
        this.user = user;
        this.userId = userId;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}
