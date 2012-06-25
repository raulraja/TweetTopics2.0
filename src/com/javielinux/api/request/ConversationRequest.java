package com.javielinux.api.request;

public class ConversationRequest implements BaseRequest {

    private long userId = 0;
    private long id = 0;

    public ConversationRequest(long userId, long id) {
        this.id = id;
        this.userId = userId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}
