package com.javielinux.api.request;

public class GetUserListRequest implements BaseRequest {

    private long userId;
    private String screenName;
    private int userListType;
    private long cursor;

    public GetUserListRequest(long userId, String screenName, int userListType, long cursor) {
        this.userId = userId;
        this.screenName = screenName;
        this.userListType = userListType;
        this.cursor = cursor;
    }

    public long getUserId() {
        return userId;
    }
    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getScreenName() {
        return screenName;
    }
    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public int getUserListType() {
        return userListType;
    }
    public void setUserListType(int userListType) {
        this.userListType = userListType;
    }

    public long getCursor() {
        return cursor;
    }
    public void setCursor(long cursor) {
        this.cursor = cursor;
    }
}
