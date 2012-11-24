package com.javielinux.utils;

import android.content.Context;
import android.database.CursorIndexOutOfBoundsException;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import com.android.dataframework.Entity;
import com.javielinux.tweettopics2.R;

public class ColumnsUtils {

    public static String getTitleColumn(Context context, Entity entity) {
        try {
            if (entity.getInt("user_desc_as_title") == 1) {
                return entity.getString("description");
            }
            int type_column = entity.getInt("type_id");
            switch (type_column) {
                case TweetTopicsUtils.COLUMN_SEARCH:
                    Entity ent = new Entity("search", entity.getLong("search_id"));
                    return ent.getString("name");
                case TweetTopicsUtils.COLUMN_LIST_USER:
                    return entity.getString("description");
                case TweetTopicsUtils.COLUMN_TRENDING_TOPIC:
                    return entity.getEntity("type_id").getString("title") + " " + entity.getString("description");
                default:
                    return entity.getEntity("type_id").getString("title");
            }
        } catch (CursorIndexOutOfBoundsException e) {
        } catch (Exception e) {
        }

        return context.getString(R.string.app_name);
    }

    public static Bitmap getButtonWithTitle(Context context, Entity entity, boolean showCounter) {
        return getButtonWithTitle(context, entity, showCounter, Color.BLACK);
    }

    public static Bitmap getButtonWithTitle(Context context, Entity entity, boolean showCounter, int bgColorTitle) {
        Bitmap bitmap = getButtonBigActionBar(context, entity, showCounter);
        String title = getTitleColumn(context, entity);
        if (title.length() > 8) title = title.substring(0, 8);
        Paint paint = new Paint();
        paint.setAntiAlias(true);

        int padding = (int) context.getResources().getDimension(R.dimen.default_separation);

        int sizeText = (int) context.getResources().getDimension(R.dimen.text_size_text_columns);
        int width = bitmap.getWidth() + (padding * 2);
        int height = bitmap.getHeight() + 22;

        Bitmap text = ImageUtils.getBitmapInBubble(context, title, bgColorTitle, Utils.TYPE_RECTANGLE, sizeText, -1);
        Bitmap newBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawBitmap(bitmap, padding, 0, paint);
        canvas.drawBitmap(text, (width / 2) - (text.getWidth() / 2), height - text.getHeight(), paint);
        bitmap = newBitmap;
        return bitmap;
    }

    public static Bitmap getButtonBigActionBar(Context context, Entity entity, boolean showCounter) {
        if (entity != null) {
            int column_type = entity.getInt("type_id");
            int tweets_count = 0;
            Bitmap bitmap = null;
            int size = (int) context.getResources().getDimension(R.dimen.size_avatar_xlarge);
            int sizeNumber = (int) context.getResources().getDimension(R.dimen.size_number_circle_horizontal_buttons);
            switch (column_type) {
                case TweetTopicsUtils.COLUMN_TIMELINE:
                case TweetTopicsUtils.COLUMN_MENTIONS:
                case TweetTopicsUtils.COLUMN_DIRECT_MESSAGES:
                    if (showCounter) {
                        tweets_count = DBUtils.getUnreadTweetsUser(column_type, entity.getEntity("user_id").getId());
                    }
                    bitmap = ImageUtils.getBitmapAvatar(entity.getEntity("user_id").getId(), size);
                    if (tweets_count > 0) {
                        Paint paint = new Paint();
                        paint.setAntiAlias(true);
                        Bitmap number = ImageUtils.getBitmapNumber(context, tweets_count, Color.RED, Utils.TYPE_RECTANGLE, sizeNumber, size / 2);
                        Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
                        Canvas canvas = new Canvas(newBitmap);
                        canvas.drawBitmap(bitmap, 0, 0, paint);
                        canvas.drawBitmap(number, bitmap.getWidth() - number.getWidth(), 0, paint);
                        bitmap = newBitmap;
                    }
                    break;
                case TweetTopicsUtils.COLUMN_SENT_DIRECT_MESSAGES:
                case TweetTopicsUtils.COLUMN_RETWEETS_BY_OTHERS:
                case TweetTopicsUtils.COLUMN_RETWEETS_BY_YOU:
                case TweetTopicsUtils.COLUMN_FOLLOWERS:
                case TweetTopicsUtils.COLUMN_FOLLOWINGS:
                case TweetTopicsUtils.COLUMN_FAVORITES:
                    return ImageUtils.getBitmapAvatar(entity.getEntity("user_id").getId(), size);
                case TweetTopicsUtils.COLUMN_SEARCH:
                    Entity searchEntity = new Entity("search", entity.getLong("search_id"));
                    if (showCounter) {
                        tweets_count = DBUtils.getUnreadTweetsSearch(searchEntity.getId());
                    }
                    Drawable drawable = Utils.getDrawable(context, searchEntity.getString("icon_big"));
                    if (drawable == null) drawable = context.getResources().getDrawable(R.drawable.letter_az);
                    bitmap = ((BitmapDrawable) drawable).getBitmap();
                    bitmap = Bitmap.createScaledBitmap(bitmap, size, size, true);
                    if (tweets_count > 0) {
                        Paint paint = new Paint();
                        paint.setAntiAlias(true);
                        Bitmap number = ImageUtils.getBitmapNumber(context, tweets_count, Color.RED, Utils.TYPE_RECTANGLE, sizeNumber, size / 2);
                        Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
                        Canvas canvas = new Canvas(newBitmap);
                        canvas.drawBitmap(bitmap, 0, 0, paint);
                        canvas.drawBitmap(number, bitmap.getWidth() - number.getWidth(), 0, paint);
                        bitmap = newBitmap;
                    }

                    break;
                default:
                    Drawable drawableIcon = context.getResources().getDrawable(R.drawable.icon);
                    bitmap = ((BitmapDrawable) drawableIcon).getBitmap();
                    bitmap = Bitmap.createScaledBitmap(bitmap, size, size, true);
                    break;
            }

            return bitmap;
        }
        return null;
    }

    public static Bitmap getIconItem(Context context, Entity entity) {
        if (entity != null) {
            int column_type = entity.getInt("type_id");
            Bitmap bitmap = null;
            switch (column_type) {
                case TweetTopicsUtils.COLUMN_TIMELINE:
                case TweetTopicsUtils.COLUMN_MENTIONS:
                case TweetTopicsUtils.COLUMN_DIRECT_MESSAGES:
                case TweetTopicsUtils.COLUMN_SENT_DIRECT_MESSAGES:
                case TweetTopicsUtils.COLUMN_RETWEETS_BY_OTHERS:
                case TweetTopicsUtils.COLUMN_RETWEETS_BY_YOU:
                case TweetTopicsUtils.COLUMN_FOLLOWERS:
                case TweetTopicsUtils.COLUMN_FOLLOWINGS:
                case TweetTopicsUtils.COLUMN_FAVORITES:
                    bitmap = ImageUtils.getBitmapAvatar(entity.getEntity("user_id").getId(), Utils.AVATAR_LARGE);
                    break;
                case TweetTopicsUtils.COLUMN_SEARCH:
                    Entity search_entity = new Entity("search", entity.getLong("search_id"));
                    Drawable drawable = Utils.getDrawable(context, search_entity.getString("icon_big"));
                    if (drawable == null) drawable = context.getResources().getDrawable(R.drawable.letter_az);
                    bitmap = ((BitmapDrawable) drawable).getBitmap();
                    bitmap = Bitmap.createScaledBitmap(bitmap, Utils.AVATAR_LARGE, Utils.AVATAR_LARGE, true);
                    break;
            }

            return bitmap;
        }

        return null;
    }

}
