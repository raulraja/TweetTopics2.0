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

package com.javielinux.utils;

import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.*;
import android.media.ExifInterface;
import android.util.Log;
import com.javielinux.tweettopics2.R;
import com.javielinux.tweettopics2.ThemeManager;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

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

        states.addState(new int[]{-android.R.attr.state_window_focused}, createBackgroundDrawable(cnt, color, false, colorLine));
        states.addState(new int[]{android.R.attr.state_pressed}, createBackgroundDrawable(cnt, color, true, 0));

        return states;
    }

    public static Drawable createBackgroundDrawable(Context cnt, int color, boolean stroke, int colorLine) {
        return createBackgroundDrawable(cnt, color, stroke, colorLine, GradientDrawable.Orientation.BOTTOM_TOP);
    }

    public static Drawable createBackgroundDrawable(Context cnt, int color, boolean stroke, int colorLine, GradientDrawable.Orientation orientation) {

        int mBubbleColor = color;
        int mBubbleColor2 = color;
        if (Utils.getPreference(cnt).getBoolean("prf_use_gradient", true)) {
            float[] hsv = new float[3];
            Color.colorToHSV(mBubbleColor, hsv);
            if (hsv[2] - .09f > 0) hsv[2] = hsv[2] - .09f;
            mBubbleColor2 = Color.HSVToColor(hsv);
        }
        GradientDrawable mDrawable = new GradientDrawable(orientation,
                new int[]{mBubbleColor2, mBubbleColor, mBubbleColor});
        mDrawable.setShape(GradientDrawable.RECTANGLE);
        if (stroke) {
            mDrawable.setStroke(2, cnt.getResources().getColor(R.color.button_focused_border));
        }
        mDrawable.setGradientRadius((float) (Math.sqrt(2) * 60));
        if (colorLine != 0 && !stroke && Utils.getPreference(cnt).getBoolean("prf_use_no_read", true)) {
            Drawable[] d = new Drawable[2];
            d[0] = new ColorDrawable(colorLine);
            d[1] = mDrawable;
            LayerDrawable layer = new LayerDrawable(d);
            layer.setLayerInset(1, 4, 0, 0, 0);
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
        if (hsv[2] - .08f > 0) hsv[2] = hsv[2] - .08f;
        mBubbleColor2 = Color.HSVToColor(hsv);
        GradientDrawable mDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
                new int[]{mBubbleColor2, mBubbleColor, mBubbleColor2});
        mDrawable.setShape(GradientDrawable.RECTANGLE);

        mDrawable.setGradientRadius((float) (Math.sqrt(2) * 60));
        return mDrawable;

    }

    static public String getFileAvatar(long id) {
        return Utils.appDirectory + "avatar_" + id + ".jpg";
    }

    static public Bitmap createBitmapSelectedAvatar(long id, int size) {

        int border = 4;
        int sizeAvatar = size - (border * 2);

        Bitmap avatar = getBitmapAvatar(id, sizeAvatar);
        Bitmap bmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);

        if (avatar != null) {
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setShader(new LinearGradient(0, 0, 0, 22, 0xff94c147, 0xff658729, Shader.TileMode.CLAMP));

            avatar = getRoundedCornerBitmap(avatar, size / 2);
            Canvas canvas = new Canvas(bmp);
            canvas.drawCircle(size / 2, size / 2, size / 2, paint);
            canvas.drawBitmap(avatar, border, border, null);
        }

        return bmp;
    }

    static public Bitmap createBitmapUnselectedAvatar(long id, int size) {

        int border = 4;
        int sizeAvatar = size - (border * 2);

        Bitmap avatar = toGrayscale(getBitmapAvatar(id, sizeAvatar));
        Bitmap bmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);

        if (avatar != null) {
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setShader(new LinearGradient(0, 0, 0, 22, 0xffb72121, 0xffe82f2f, Shader.TileMode.CLAMP));

            avatar = getRoundedCornerBitmap(avatar, size / 2);
            Canvas canvas = new Canvas(bmp);
            canvas.drawCircle(size / 2, size / 2, size / 2, paint);
            canvas.drawBitmap(avatar, border, border, null);
        }

        return bmp;
    }

    static public Bitmap getBitmapAvatar(long id, int size) {
        File f = new File(getFileAvatar(id));
        if (f.exists()) {
            try {
                return Bitmap.createScaledBitmap(getBitmapFromFile(getFileAvatar(id), size, true), size, size, true);
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    static public Bitmap toGrayscale(Bitmap bmpOriginal) {
        try {
            int width, height;
            height = bmpOriginal.getHeight();
            width = bmpOriginal.getWidth();

            Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            Canvas c = new Canvas(bmpGrayscale);
            Paint paint = new Paint();
            ColorMatrix cm = new ColorMatrix();
            cm.setSaturation(0);
            ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
            paint.setColorFilter(f);
            c.drawBitmap(bmpOriginal, 0, 0, paint);
            return bmpGrayscale;
        } catch (NullPointerException e) {
            e.printStackTrace();
            return bmpOriginal;
        } catch (Exception e) {
            e.printStackTrace();
            return bmpOriginal;
        }
    }

    static public Drawable colorDrawable(Context cnt, int d, int color) {
        return colorDrawable(cnt.getResources().getDrawable(d), color);
    }

    static public Drawable colorDrawable(Drawable d, int color) {
        ColorFilter c = new LightingColorFilter(color, color);
        d.setColorFilter(c);
        return d;
    }

    static public Bitmap getBitmapNumber(Context cnt, int number, int color, int type) {
        return getBitmapNumber(cnt, number, color, type, 13);
    }

    static public Bitmap getBitmapNumber(Context cnt, int number, int color, int type, int textSize) {
        String text = number + "";
        if (number > 999) {
            text = "999+";
        }
        return getBitmapInBubble(cnt, text, color, type, textSize, -1);
    }

    static public Bitmap getBitmapNumber(Context cnt, int number, int color, int type, int textSize, float height) {
        String text = number + "";
        if (number > 999) {
            text = "999+";
        }
        return getBitmapInBubble(cnt, text, color, type, textSize, height);
    }

    static public Bitmap getBitmapInBubble(Context cnt, String text, int color, int type, int textSize, float bitmap_height) {

        try {
            textSize = Utils.dip2px(cnt, textSize);
            Paint paintFill = new Paint();
            paintFill.setAntiAlias(true);

            if (color == Color.BLUE) {
                paintFill.setShader(new LinearGradient(0, 0, 0, 22, 0xff477ec1, 0xff293d87, Shader.TileMode.CLAMP));
            }

            if (color == Color.GREEN) {
                paintFill.setShader(new LinearGradient(0, 0, 0, 22, 0xff94c147, 0xff658729, Shader.TileMode.CLAMP));
            }

            if (color == Color.RED) {
                paintFill.setShader(new LinearGradient(0, 0, 0, 22, 0xffb72121, 0xffe82f2f, Shader.TileMode.CLAMP));
            }

            if (color == Color.BLACK) {
                paintFill.setShader(new LinearGradient(0, 0, 0, 22, 0xff111111, 0xff222222, Shader.TileMode.CLAMP));
            }

            Paint paintStroke = new Paint();
            paintStroke.setAntiAlias(true);
            paintStroke.setColor(Color.WHITE);

            Paint paintText = new Paint();
            paintText.setAntiAlias(true);
            paintText.setTextSize(textSize);
            paintText.setFakeBoldText(true);
            paintText.setTextAlign(Paint.Align.CENTER);
            paintText.setColor(Color.WHITE);

            if (type == Utils.TYPE_CIRCLE) {
                float width = paintText.measureText(text);
                float height = paintText.descent() - paintText.ascent();

                int size = (int) ((width > height) ? width : height) + 7;
                int offset = (int) ((size > bitmap_height) ? 0 : (bitmap_height - size));
                int radius = (size - 2) / 2;
                //int center = size / 2;
                int center = (int) (size + offset) / 2;
                int ytext = center + (int) paintText.descent() + 2;

                //Bitmap bmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
                Bitmap bmp = Bitmap.createBitmap(size, size + offset, Bitmap.Config.ARGB_8888);
                Canvas c = new Canvas(bmp);

                c.drawCircle(center, center, radius, paintStroke);
                c.drawCircle(center, center, radius - 1, paintFill);
                c.drawText(text, center, ytext, paintText);
                return bmp;

            } else {
                float width = paintText.measureText(text);
                float height = paintText.descent() - paintText.ascent();

                int wBox = (int) width + 10;
                int hBox = (int) height + 4;
                int hBoxFinal = hBox;

                int offset = (int) ((hBoxFinal > bitmap_height) ? 0 : (bitmap_height - hBoxFinal));

                int ytext = (offset / 2) + (hBox / 2) + (int) paintText.descent() + 2;
                int center = wBox / 2;
                //int ytext = (hBox / 2) + (int) paintText.descent() + 2;
                //RectF boxRect = new RectF(1, 1, wBox - 1, hBox - 1);
                RectF boxRect = new RectF(1, 1 + offset / 2, wBox - 1, (offset / 2) + hBox - 1);

                Path pathFill = new Path();
                pathFill.addRoundRect(boxRect, 7, 7, Path.Direction.CCW);
                if (type == Utils.TYPE_BUBBLE) {
                    pathFill.moveTo(7, (offset / 2) + hBox - 2);
                    pathFill.lineTo(7, (offset / 2) + hBox + 4);
                    pathFill.lineTo(12, (offset / 2) + hBox - 2);
                    hBoxFinal = hBox + 6;
                }

                //RectF boxRectStroke = new RectF(0, 0, wBox, hBox);
                RectF boxRectStroke = new RectF(0, (offset / 2), wBox, (offset / 2) + hBox);

                Path pathStroke = new Path();
                pathStroke.addRoundRect(boxRectStroke, 7, 7, Path.Direction.CCW);
                if (type == Utils.TYPE_BUBBLE) {
                    pathStroke.moveTo(5, (offset / 2) + hBox - 2);
                    pathStroke.lineTo(5, (offset / 2) + hBox + 6);
                    pathStroke.lineTo(14, (offset / 2) + hBox);
                }

                //Bitmap bmp = Bitmap.createBitmap(wBox, hBoxFinal, Bitmap.Config.ARGB_4444);
                Bitmap bmp = Bitmap.createBitmap(wBox, offset + hBoxFinal, Bitmap.Config.ARGB_4444);
                Canvas c = new Canvas(bmp);

                c.drawPath(pathStroke, paintStroke);
                c.drawPath(pathFill, paintFill);

                c.drawText(text, center, ytext, paintText);
                return bmp;
            }
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    static public Bitmap getBackgroundBitmapInBubble(Context cnt, int color, int type, float width, float height) {

        try {

            Paint paintFill = new Paint();
            paintFill.setAntiAlias(true);

            if (color == Color.BLUE) {
                paintFill.setShader(new LinearGradient(0, 0, 0, 22, 0xff477ec1, 0xff293d87, Shader.TileMode.CLAMP));
            }

            if (color == Color.GREEN) {
                paintFill.setShader(new LinearGradient(0, 0, 0, 22, 0xff94c147, 0xff658729, Shader.TileMode.CLAMP));
            }

            if (color == Color.RED) {
                paintFill.setShader(new LinearGradient(0, 0, 0, 22, 0xffb72121, 0xffe82f2f, Shader.TileMode.CLAMP));
            }

            Paint paintStroke = new Paint();
            paintStroke.setAntiAlias(true);
            paintStroke.setColor(Color.WHITE);


            if (type == Utils.TYPE_CIRCLE) {
                int size = (int) ((width > height) ? width : height) + 7;
                int radius = (size - 2) / 2;
                int center = size / 2;
                Bitmap bmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
                Canvas c = new Canvas(bmp);
                c.drawCircle(center, center, radius, paintStroke);
                c.drawCircle(center, center, radius - 1, paintFill);
                return bmp;
            } else {
                int size = (int) ((width > height) ? width : height) + 7;
                Bitmap bmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
                Canvas c = new Canvas(bmp);
                RectF boxRect = new RectF(0, 0, size, size);
                c.drawRoundRect(boxRect, 5, 5, paintStroke);
                boxRect = new RectF(1, 1, size - 1, size - 1);
                c.drawRoundRect(boxRect, 5, 5, paintFill);
                return bmp;
            }
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    static public Bitmap getBitmap(Bitmap bitmapOrg, int newHeight) {
        try {

            int width = bitmapOrg.getWidth();
            int height = bitmapOrg.getHeight();
            int newWidth = 0;

            if (width > height) {
                newWidth = (newHeight * width) / height;

            } else {
                newWidth = newHeight;
                newHeight = (newWidth * height) / width;
            }


            float scaleWidth = ((float) newWidth) / width;
            float scaleHeight = ((float) newHeight) / height;

            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHeight);

            return Bitmap.createBitmap(bitmapOrg, 0, 0, width, height, matrix, false);

            /*
            Options opt = new Options();
            opt.inSampleSize = 2;
            Bitmap bm = BitmapFactory.decodeStream(new Utils.FlushedInputStream(bis), null, opt);
            bis.close();
            is.close();

            int width = (height * bm.getWidth()) / bm.getHeight();

            return Bitmap.createScaledBitmap(bm, width, height, true);*/

        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    static public Bitmap getBitmap(String url, int newHeight) {
        try {
            URL urlImage = new URL(url);
            URLConnection conn = urlImage.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);

            Bitmap bitmapOrg = BitmapFactory.decodeStream(new Utils.FlushedInputStream(bis));

            int width = bitmapOrg.getWidth();
            int height = bitmapOrg.getHeight();
            int newWidth = 0;

            if (width > height) {
                newWidth = (newHeight * width) / height;

            } else {
                newWidth = newHeight;
                newHeight = (newWidth * height) / width;
            }

            if (height > newHeight) {
                float scaleWidth = ((float) newWidth) / width;
                float scaleHeight = ((float) newHeight) / height;

                Matrix matrix = new Matrix();
                matrix.postScale(scaleWidth, scaleHeight);

                return Bitmap.createBitmap(bitmapOrg, 0, 0, width, height, matrix, false);
            } else {
                return bitmapOrg;
            }
            /*
            Options opt = new Options();
            opt.inSampleSize = 2;
            Bitmap bm = BitmapFactory.decodeStream(new Utils.FlushedInputStream(bis), null, opt);
            bis.close();
            is.close();

            int width = (height * bm.getWidth()) / bm.getHeight();

            return Bitmap.createScaledBitmap(bm, width, height, true);*/

        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    static public Bitmap getResizeBitmapFromFile(String file, int newHeight) {
        try {

            Bitmap bitmapOrg = getBitmapFromFile(file, newHeight, false);

            int width = bitmapOrg.getWidth();
            int height = bitmapOrg.getHeight();
            int newWidth = 0;

            if (width > height) {
                newWidth = (newHeight * width) / height;

            } else {
                newWidth = newHeight;
                newHeight = (newWidth * height) / width;
            }

            if (height > newHeight) {
                float scaleWidth = ((float) newWidth) / width;
                float scaleHeight = ((float) newHeight) / height;

                Matrix matrix = new Matrix();
                matrix.postScale(scaleWidth, scaleHeight);

                ExifInterface exif = new ExifInterface(file);

                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);

                if (orientation == 3) {
                    matrix.postRotate(180);
                } else if (orientation == 6) {
                    matrix.postRotate(90);
                } else if (orientation == 8) {
                    matrix.postRotate(270);
                }

                return Bitmap.createBitmap(bitmapOrg, 0, 0, width, height, matrix, true);
            } else {
                return bitmapOrg;
            }

        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    static public Bitmap getBitmapFromFile(String file, int height, boolean crop) {
        try {

            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(file), null, o);

//Find the correct scale value. It should be the power of 2.
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;

            while (true) {
                if (width_tmp / 2 < height || height_tmp / 2 < height)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

//decode with inSampleSize
            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inSampleSize = scale;

            Bitmap bm = BitmapFactory.decodeFile(file, opt);

            if (crop) {

                Bitmap b = null;

                if (bm.getWidth() > bm.getHeight()) {
                    int x = (bm.getWidth() - bm.getHeight()) / 2;
                    b = Bitmap.createBitmap(bm, x, 0, bm.getHeight(), bm.getHeight());
                } else {
                    int y = (bm.getHeight() - bm.getWidth()) / 2;
                    b = Bitmap.createBitmap(bm, 0, y, bm.getWidth(), bm.getWidth());
                }

                return Bitmap.createScaledBitmap(b, height, height, true);
            } else {
                int width = (height * bm.getWidth()) / bm.getHeight();
                return Bitmap.createScaledBitmap(bm, width, height, true);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Bitmap saveAvatar(String u, File file) {
        URL url;
        try {
            url = new URL(u);
            Bitmap bmp = BitmapFactory.decodeStream(new Utils.FlushedInputStream(url.openStream()));
            if (bmp != null) {
                bmp = getRoundedCornerBitmap(bmp, 5);
                FileOutputStream out = new FileOutputStream(file);
                bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
                return bmp;
            }
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap savePhotoInScale(Context context, String image) {

        Log.d(Utils.TAG, "Image " + image);

        int s = Integer.parseInt(Utils.getPreference(context).getString("prf_size_photo", "2"));

        Bitmap bmp = null;

        int size = 0;

        if (s == 1) {
            size = Utils.HEIGHT_PHOTO_SIZE_SMALL;
        } else if (s == 2) {
            size = Utils.HEIGHT_PHOTO_SIZE_MIDDLE;
        } else {
            size = Utils.HEIGHT_PHOTO_SIZE_LARGE;
        }

        bmp = getResizeBitmapFromFile(image, size);

        if (bmp != null) {
            Matrix matrix = new Matrix();

            float aux = bmp.getWidth();
            if (bmp.getHeight() > bmp.getWidth()) aux = bmp.getHeight();

            float scale = size / aux;
            matrix.setScale(scale, scale);
            Log.d(Utils.TAG, "Scale: " + scale);

            try {
                ExifInterface exif = new ExifInterface(image);
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);

                if (orientation == 3) {
                    matrix.postRotate(180);
                } else if (orientation == 6) {
                    matrix.postRotate(90);
                } else if (orientation == 8) {
                    matrix.postRotate(270);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (bmp != null) {
                int w = bmp.getWidth();
                int h = bmp.getHeight();
                Log.d(Utils.TAG, "Original size w=" + w + " h=" + h);
                try {
                    bmp = Bitmap.createBitmap(bmp, 0, 0, w, h, matrix, false);
                } catch (OutOfMemoryError e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (bmp != null) {
                Log.d(Utils.TAG, "Image scale to: w=" + bmp.getWidth() + " h=" + bmp.getHeight());

                try {
                    FileOutputStream out = new FileOutputStream(image);
                    bmp.compress(Bitmap.CompressFormat.JPEG, 90, out);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } else {
            Log.d(Utils.TAG, "bmp == null");
        }

        return bmp;

    }

    public static Bitmap getAvatar(String u) {
        URL url;
        try {
            url = new URL(u);
            return getRoundedCornerBitmap(BitmapFactory.decodeStream(new Utils.FlushedInputStream(url.openStream())), 5);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }
}
