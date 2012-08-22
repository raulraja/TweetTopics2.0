package com.javielinux.api.request;

import android.content.Context;

public class SaveFirstTweetsRequest implements BaseRequest {

    private Context context;
    private long id = 0;

    public SaveFirstTweetsRequest(Context context, long id) {
        this.context = context;
        this.id = id;
    }

    public Context getContext() {
        return context;
    }
    public void setContext(Context context) {
        this.context = context;
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
}
