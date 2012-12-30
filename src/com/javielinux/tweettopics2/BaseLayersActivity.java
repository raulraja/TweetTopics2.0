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

import android.content.Intent;
import android.os.Bundle;
import com.javielinux.utils.Utils;

public abstract class BaseLayersActivity extends BaseActivity {

    protected int activityAnimation = Utils.ACTIVITY_ANIMATION_LEFT;
    protected long userActive = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        if (extras!=null) {
            if (extras.containsKey(Utils.KEY_ACTIVITY_ANIMATION)) {
                activityAnimation = extras.getInt(Utils.KEY_ACTIVITY_ANIMATION);
            }
            if (extras.containsKey(Utils.KEY_ACTIVITY_USER_ACTIVE)) {
                userActive = extras.getLong(Utils.KEY_ACTIVITY_USER_ACTIVE);
            }
        }

        if (!(this instanceof TweetTopicsActivity)) {
            if (activityAnimation == Utils.ACTIVITY_ANIMATION_RIGHT) {
                overridePendingTransition(R.anim.pull_in_to_right, R.anim.hold);
            } else {
                overridePendingTransition(R.anim.pull_in_to_left, R.anim.hold);
            }
        }

    }

    @Override
    protected void onPause() {
        if (!(this instanceof TweetTopicsActivity)) {
            if (activityAnimation == Utils.ACTIVITY_ANIMATION_RIGHT) {
                overridePendingTransition(R.anim.hold, R.anim.push_out_from_right);
            } else {
                overridePendingTransition(R.anim.hold, R.anim.push_out_from_left);
            }
        }
        super.onPause();
    }

    public void startAnimationActivity(Class klass, Bundle bundle) {
        Intent intent = new Intent(this, klass);
        //if (bundle!=null) intent.putExtras(bundle);
        intent.putExtra(Utils.KEY_EXTRAS_INFO, bundle);
        intent.putExtra(Utils.KEY_ACTIVITY_ANIMATION, (activityAnimation==Utils.ACTIVITY_ANIMATION_RIGHT)?Utils.ACTIVITY_ANIMATION_LEFT:Utils.ACTIVITY_ANIMATION_RIGHT);
        if (this instanceof TweetTopicsActivity) {
            intent.putExtra(Utils.KEY_ACTIVITY_USER_ACTIVE, ((TweetTopicsActivity)this).getUserOwnerCurrentColumn());
        } else {
            intent.putExtra(Utils.KEY_ACTIVITY_USER_ACTIVE, userActive);
        }
        startActivity(intent);
    }

}