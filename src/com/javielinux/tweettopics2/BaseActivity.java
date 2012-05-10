package com.javielinux.tweettopics2;

import android.app.Activity;
import android.os.Bundle;

public abstract class BaseActivity extends Activity {

    // Call to GDActivity with Dashboard property for show icon PhotoParty forever in top bar

    public BaseActivity() {

    }
    /*
    public LoaderManager getLoaderManager() {
        return getLoaderManager();
    };
    */
    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

}