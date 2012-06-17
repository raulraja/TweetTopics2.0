package com.javielinux.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.javielinux.tweettopics2.R;

public class ActivityUtils {

    public static void animationIn(Activity activity, int activityAnimation) {
        if (activityAnimation == Utils.ACTIVITY_ANIMATION_RIGHT) {
            activity.overridePendingTransition(R.anim.pull_in_to_right, R.anim.hold);
        } else {
            activity.overridePendingTransition(R.anim.pull_in_to_left, R.anim.hold);
        }
    }

    public static void animationOut(Activity activity, int activityAnimation) {
        if (activityAnimation == Utils.ACTIVITY_ANIMATION_RIGHT) {
            activity.overridePendingTransition(R.anim.hold, R.anim.push_out_from_right);
        } else {
            activity.overridePendingTransition(R.anim.hold, R.anim.push_out_from_left);
        }
    }

    public static void startAnimationActivity(Activity activity, Class klass, Bundle bundle, int activityAnimation) {
        Intent intent = new Intent(activity, klass);
        if (bundle!=null) intent.putExtras(bundle);
        intent.putExtra(Utils.KEY_ACTIVITY_ANIMATION, activityAnimation);
        activity.startActivity(intent);
    }

}
