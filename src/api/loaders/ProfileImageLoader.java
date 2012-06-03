package api.loaders;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import api.AsynchronousLoader;
import api.request.ProfileImageRequest;
import api.response.BaseResponse;
import api.response.ErrorResponse;
import api.response.ProfileImageResponse;
import com.android.dataframework.Entity;
import com.javielinux.twitter.ConnectionManager;
import com.javielinux.utils.Utils;
import twitter4j.TwitterException;
import twitter4j.User;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ProfileImageLoader extends AsynchronousLoader<BaseResponse> {

    public static final int CHANGE_AVATAR = 1;
    public static final int REFRESH_AVATAR = 2;

    private int action = 0;
    private long user_id = 0;

    public ProfileImageLoader(Context context, ProfileImageRequest request) {
        super(context);

        this.action = request.getAction();
        this.user_id = request.getUserId();
    }

    private String getURLNewAvatar() {
		return Utils.appDirectory + "aux_avatar_" + user_id + ".jpg";
	}

    private boolean refreshAvatar() {

    	if (user_id > 0) {
            try {
                Entity ent = new Entity("users", user_id);
                User user = ConnectionManager.getInstance().getTwitter().showUser(ent.getInt("user_id"));
                Bitmap avatar = BitmapFactory.decodeStream(new Utils.FlushedInputStream(user.getProfileImageURL().openStream()));
				String file = Utils.getFileAvatar(user_id);

				FileOutputStream out = new FileOutputStream(file);
				avatar.compress(Bitmap.CompressFormat.JPEG, 90, out);

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

    	if (user_id > 0) {
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

                User user = ConnectionManager.getInstance().getTwitter().updateProfileImage(new FileInputStream(file_path));

                Bitmap avatar = BitmapFactory.decodeFile(file_path);
                String avatar_file_path = Utils.getFileAvatar(user_id);

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

            switch (action) {
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
