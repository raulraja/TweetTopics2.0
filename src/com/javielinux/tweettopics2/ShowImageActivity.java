/*
 * Copyright 2012 Javier Pérez Pacheco and Francisco Díaz Rodriguez
 * TweetTopics 2.0
 * javielinux@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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