package com.javielinux.api.request;

import android.content.Context;

public class GetGeolocationAddressRequest implements BaseRequest {

    private Context context = null;
    private String text = "";
    private boolean single_result;

    public GetGeolocationAddressRequest(Context context, String text, boolean single_result) {
        this.context = context;
        this.text = text;
        this.single_result = single_result;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean getSingleResult() {
        return single_result;
    }

    public void setSingleResult(boolean single_result) {
        this.single_result = single_result;
    }
}
