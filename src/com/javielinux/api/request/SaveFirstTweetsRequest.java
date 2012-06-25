package com.javielinux.api.request;

public class SaveFirstTweetsRequest implements BaseRequest {

    private long id = 0;

    public SaveFirstTweetsRequest(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
}
