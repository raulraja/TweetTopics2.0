package com.javielinux.api.response;

public class LoadTranslateTweetResponse implements BaseResponse {

    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public boolean isError() {
        return false;
    }
}
