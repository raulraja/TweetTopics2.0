package com.javielinux.api.loaders;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.android.dataframework.Entity;
import com.javielinux.api.AsynchronousLoader;
import com.javielinux.api.request.ProfileImageRequest;
import com.javielinux.api.response.BaseResponse;
import com.javielinux.api.response.ErrorResponse;
import com.javielinux.api.response.ProfileImageResponse;
import com.javielinux.twitter.ConnectionManager;
import com.javielinux.utils.ImageUtils;
import com.javielinux.utils.Utils;
import twitter4j.TwitterException;
import twitter4j.User;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

public class ProfileImageLoader extends AsynchronousLoader<BaseResponse> {

    public static final int CHANGE_AVATAR = 1;
    public static final int REFRESH_AVATAR = 2;

    private ProfileImageRequest request;

    public ProfileImageLoader(Context context, ProfileImageRequest request) {
        super(context);

        this.request = request;
    }

    private String getURLNewAvatar() {
		return Utils.appDirectory + "aux_avatar_" + request.getUserId() + ".jpg";
	}

    private boolean refreshAvatar() {

    	if (request.getUserId() > 0) {
            try {
                ConnectionManager.getInstance().open(getContext());
                Entity ent = new Entity("users", request.getUserId());
                User user = ConnectionManager.getInstance().getTwitter(request.getUserId()).showUser(ent.getInt("user_id"));
                Bitmap avatar = BitmapFactory.decodeStream(new Utils.FlushedInputStream(new URL(user.getProfileImageURL()).openStream()));
				String file = ImageUtils.getFileAvatar(request.getUserId());

				FileOutputStream out = new FileOutputStream(file);
				avatar.compress(Bitmap.CompressFormat.JPEG, 90, out);

                ent.setValue("fullname", user.getName());
                ent.save();

				avatar.recycle();
			} catch (NullPointerException e) {
				e.printStackTrace();
                return false;
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
                return false;
			} catch (TwitterException e) {
				e.printStackTrace();
                return false;
			} catch (IOException e) {
				e.printStackTrace();
                return false;
			} catch (Exception e) {
				e.printStackTrace();
                return false;
			}
    	}

        return true;
    }

    private boolean changeAvatar() {

    	if (request.getUserId() > 0) {
            try {
                String file_path = getURLNewAvatar();

			    Bitmap new_avatar = BitmapFactory.decodeFile(file_path);
                int new_avatar_width = new_avatar.getWidth();
                int new_avatar_height = new_avatar.getHeight();

                if (new_avatar_width > 500) {
                    int scaled_width = 500;
                    int scaled_height = Math.round(scaled_width * new_avatar_height / new_avatar_width);

                    Bitmap scaled_bitmap = Bitmap.createScaledBitmap(new_avatar, scaled_width, scaled_height, true);
                    FileOutputStream out = new FileOutputStream(file_path);
                    scaled_bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);

                    scaled_bitmap.recycle();
                }

                User user = ConnectionManager.getInstance().getTwitter(request.getUserId()).updateProfileImage(new FileInputStream(file_path));

                Bitmap avatar = BitmapFactory.decodeFile(file_path);
                String avatar_file_path = ImageUtils.getFileAvatar(request.getUserId());

				FileOutputStream out = new FileOutputStream(avatar_file_path);
				avatar.compress(Bitmap.CompressFormat.JPEG, 90, out);

				avatar.recycle();

                File file = new File(file_path);
                file.delete();
			} catch (NullPointerException e) {
				e.printStackTrace();
                return false;
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
                return false;
			} catch (TwitterException e) {
				e.printStackTrace();
                return false;
			} catch (IOException e) {
				e.printStackTrace();
                return false;
			} catch (Exception e) {
				e.printStackTrace();
                return false;
			}
    	}

        return true;
    }

    @Override
    public BaseResponse loadInBackground() {

		try {
            boolean result = false;

            switch (request.getAction()) {
                case CHANGE_AVATAR:
                    result = changeAvatar();
                    break;
                case REFRESH_AVATAR:
                    result = refreshAvatar();
                    break;
            }

            ProfileImageResponse response = new ProfileImageResponse();
            response.setReady(result);
            return response;
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
            ErrorResponse response = new ErrorResponse();
            response.setError(e, e.getMessage());
            return response;
		} catch (Exception e) {
			e.printStackTrace();
            ErrorResponse response = new ErrorResponse();
            response.setError(e, e.getMessage());
            return response;
		}
    }
}
