package task;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import com.android.dataframework.Entity;
import com.javielinux.tweettopics2.Utils;
import com.javielinux.twitter.ConnectionManager;
import twitter4j.TwitterException;
import twitter4j.User;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ProfileImageAsyncTask extends AsyncTask<ProfileImageAsyncTask.Params, Void, ProfileImageAsyncTask.Result> {
    public static final int CHANGE_AVATAR = 1;
    public static final int REFRESH_AVATAR = 2;

    public interface ProfileImageAsyncTaskResponder {
        public void profileImageLoading();
        public void profileImageLoadCancelled();
        public void profileImageLoaded(ProfileImageAsyncTask.Result result);
	}

    public class Params {
		public String url;
		public long idUser;
        public int action;

		public Params() {
			super();
		}
    }

    public class Result {
        public int action;
        public boolean ok;

		public Result() {
			super();
		}
    }

    private ProfileImageAsyncTask.Params params;
    private Result result;
    private ProfileImageAsyncTaskResponder responder;

    public ProfileImageAsyncTask(ProfileImageAsyncTaskResponder responder) {
		this.responder = responder;
	}

    private String getURLNewAvatar() {
		return Utils.appDirectory + "aux_avatar_" + params.idUser + ".jpg";
	}

    private boolean refreshAvatar() {

    	if (params.idUser > 0) {
            try {
                Entity ent = new Entity("users", params.idUser);
                User user = ConnectionManager.getInstance().getTwitter().showUser(ent.getInt("user_id"));
                Bitmap avatar = BitmapFactory.decodeStream(new Utils.FlushedInputStream(user.getProfileImageURL().openStream()));
				String file = Utils.getFileAvatar(params.idUser);

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

    	if (params.idUser > 0) {
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
                String avatar_file_path = Utils.getFileAvatar(params.idUser);

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
	protected Result doInBackground(ProfileImageAsyncTask.Params... arg) {
        params = arg[0];
        Result result = new Result();
        result.action = params.action;
		try {
            switch (params.action) {
                case CHANGE_AVATAR:
                    result.ok = changeAvatar();
                    break;
                case REFRESH_AVATAR:
                    result.ok = refreshAvatar();
                    break;
            }
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
            result.ok = false;
		} catch (Exception e) {
			e.printStackTrace();
            result.ok = false;
		}

        return result;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		responder.profileImageLoading();
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
		responder.profileImageLoadCancelled();
	}

	@Override
	protected void onPostExecute(ProfileImageAsyncTask.Result result) {
		super.onPostExecute(result);
		responder.profileImageLoaded(result);
	}
}
