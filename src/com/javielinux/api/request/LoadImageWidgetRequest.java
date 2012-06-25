package com.javielinux.api.request;

public class LoadImageWidgetRequest implements BaseRequest {

    private String url;

    public LoadImageWidgetRequest(String url) {
        this.url = url;

    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
