package com.javielinux.api.request;

public class LoadImageAutoCompleteRequest implements BaseRequest {

    private String url;

    public LoadImageAutoCompleteRequest(String url) {
        this.url = url;

    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
