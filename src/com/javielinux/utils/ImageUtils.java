package com.javielinux.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.*;
import com.javielinux.tweettopics2.R;
import com.javielinux.tweettopics2.ThemeManager;

public class ImageUtils {


    public static Drawable createGradientDrawableSelected(Context cnt, int colorLine) {
        ThemeManager theme = new ThemeManager(cnt);

        return createStateListDrawable(cnt, theme.getColor("tweet_color_selected"), colorLine);
    }

    public static Drawable createGradientDrawableMention(Context cnt, int colorLine) {
        ThemeManager theme = new ThemeManager(cnt);

        return createStateListDrawable(cnt, Color.parseColor(theme.getColors().get(PreferenceUtils.getColorMentions(cnt))), colorLine);
    }

    public static Drawable createGradientDrawableFavorite(Context cnt, int colorLine) {
        ThemeManager theme = new ThemeManager(cnt);

        return createStateListDrawable(cnt, Color.parseColor(theme.getColors().get(PreferenceUtils.getColorFavorited(cnt))), colorLine);
    }

    public static Drawable createStateListDrawable(Context cnt, int color) {
        return createStateListDrawable(cnt, color, 0);
    }

    public static Drawable createStateListDrawable(Context cnt, int color, int colorLine) {
        StateListDrawable states = new StateListDrawable();

        states.addState(new int[]{ -android.R.attr.state_window_focused }, createBackgroundDrawable( cnt, color, false, colorLine ) );
        states.addState(new int[]{ android.R.attr.state_pressed }, createBackgroundDrawable( cnt, color, true, 0 ) );

        return states;
    }

    public static Drawable createBackgroundDrawable(Context cnt, int color, boolean stroke, int colorLine) {
        return createBackgroundDrawable(cnt, color, stroke, colorLine, GradientDrawable.Orientation.BOTTOM_TOP);
    }

    public static Drawable createBackgroundDrawable(Context cnt, int color, boolean stroke, int colorLine, GradientDrawable.Orientation orientation) {

        int mBubbleColor = color;
        int mBubbleColor2 = color;
        if ( Utils.getPreference(cnt).getBoolean("prf_use_gradient", true)) {
            float[] hsv = new float[3];
            Color.colorToHSV(mBubbleColor, hsv);
            if (hsv[2]-.09f>0) hsv[2]=hsv[2]-.09f;
            mBubbleColor2=Color.HSVToColor(hsv);
        }
        GradientDrawable mDrawable = new GradientDrawable(orientation,
                new int[] { mBubbleColor2, mBubbleColor, mBubbleColor });
        mDrawable.setShape(GradientDrawable.RECTANGLE);
        if (stroke) {
            mDrawable.setStroke(2, cnt.getResources().getColor(R.color.button_focused_border) );
        }
        mDrawable.setGradientRadius((float)(Math.sqrt(2) * 60));
        if (colorLine!=0 && !stroke && Utils.getPreference(cnt).getBoolean("prf_use_no_read", true)) {
            Drawable[] d = new Drawable[2];
            d[0] = new InsetDrawable(new ColorDrawable(colorLine), 4, 0, 0, 0);
            Rect bounds = new Rect();
            bounds.left = 4;
            mDrawable.setBounds(bounds);
            d[1] = mDrawable;
            LayerDrawable layer = new LayerDrawable(d);
            return layer;
        } else {
            return mDrawable;
        }

    }

    public static Drawable createDividerDrawable(Context cnt, int color) {

        int mBubbleColor = color;
        int mBubbleColor2 = color;
        float[] hsv = new float[3];
        Color.colorToHSV(mBubbleColor, hsv);
        if (hsv[2]-.08f>0) hsv[2]=hsv[2]-.08f;
        mBubbleColor2=Color.HSVToColor(hsv);
        GradientDrawable mDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
                new int[] { mBubbleColor2, mBubbleColor, mBubbleColor2 });
        mDrawable.setShape(GradientDrawable.RECTANGLE);

        mDrawable.setGradientRadius((float)(Math.sqrt(2) * 60));
        return mDrawable;

    }

}
