package com.javielinux.api.request;

public class TwitterUserRequest implements BaseRequest {

    private int column;
    private long userId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TwitterUserRequest)) return false;

        TwitterUserRequest that = (TwitterUserRequest) o;

        if (column != that.column) return false;
        if (userId != that.userId) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = column;
        result = 31 * result + (int) (userId ^ (userId >>> 32));
        return result;
    }

    public TwitterUserRequest(int column, long userId) {
        this.column = column;
        this.userId = userId;
    }

    public int getColumn() {
        return column;
    }
    public void setColumn(int column) {
        this.column = column;
    }


    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}
