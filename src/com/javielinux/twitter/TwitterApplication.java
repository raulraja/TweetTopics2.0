package com.javielinux.twitter;

import android.app.Application;
import com.androidquery.callback.BitmapAjaxCallback;
import com.javielinux.utils.ErrorReporter;

public class TwitterApplication extends Application {
    private ErrorReporter mErrs;

    @Override
    public void onLowMemory() {
        BitmapAjaxCallback.clearCache();
        super.onLowMemory();
    }
}
