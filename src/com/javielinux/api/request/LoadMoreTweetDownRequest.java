package com.javielinux.api.request;

public class LoadMoreTweetDownRequest implements BaseRequest {

    private long sinceId;
    private long maxId;
    private long userId;

    public LoadMoreTweetDownRequest(long userId, long sinceId, long maxId) {
        this.userId = userId;
        this.sinceId = sinceId;
        this.maxId = maxId;
    }


    public long getSinceId() {
        return sinceId;
    }

    public void setSinceId(long sinceId) {
        this.sinceId = sinceId;
    }

    public long getMaxId() {
        return maxId;
    }

    public void setMaxId(long maxId) {
        this.maxId = maxId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}
