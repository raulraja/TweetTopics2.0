package com.javielinux.tweettopics2;

import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.androidquery.AQuery;
import com.javielinux.components.ImageViewZoomTouch;

public class ShowImageActivity extends BaseActivity {

    public static String KEY_EXTRA_URL_IMAGE = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String urlImage = "";

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey(KEY_EXTRA_URL_IMAGE)) {
                urlImage = extras.getString(KEY_EXTRA_URL_IMAGE);
            }
        }

        ImageViewZoomTouch image = new ImageViewZoomTouch(this);

        AQuery aQuery = new AQuery(this).recycle(image);
        aQuery.id(image).image(urlImage, true, true, 0, R.drawable.icon_tweet_image_large, aQuery.getCachedImage(R.drawable.icon_tweet_image_large), 0);

        FrameLayout fr = new FrameLayout(this);
        ImageView bg = new ImageView(this);
        bg.setBackgroundColor(Color.parseColor("#cc000000"));

        fr.addView(bg, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
        fr.addView(image, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));

        setContentView(fr, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}