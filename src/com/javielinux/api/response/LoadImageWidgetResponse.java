package com.javielinux.api.response;


import android.graphics.Bitmap;

public class LoadImageWidgetResponse implements BaseResponse {
    private Bitmap bitmap;
    private String url;

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public boolean isError() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
