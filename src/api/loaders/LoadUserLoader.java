package api.loaders;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import api.AsynchronousLoader;
import api.request.LoadUserRequest;
import api.response.BaseResponse;
import api.response.ErrorResponse;
import api.response.LoadUserResponse;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.javielinux.tweettopics2.Utils;
import com.javielinux.twitter.ConnectionManager;
import infos.InfoUsers;
import twitter4j.TwitterException;
import twitter4j.User;

import java.io.File;

public class LoadUserLoader extends AsynchronousLoader<BaseResponse> {

    private String user = "";

    public LoadUserLoader(Context context, LoadUserRequest request) {
        super(context);

        this.user = request.getUser();
    }

    @Override
    public BaseResponse loadInBackground() {

		try {
            LoadUserResponse response = new LoadUserResponse();
            InfoUsers infoUsers = new InfoUsers();

            DataFramework.getInstance().open(getContext(), Utils.packageName);

	        Entity user_entity = DataFramework.getInstance().getTopEntity("users", "active=1", "");
            String screenName = "";

	        if (user_entity!=null) {
	        	screenName = user_entity.getString("name");
	        }

	        DataFramework.getInstance().close();

			ConnectionManager.getInstance().open(getContext());

			User user_data = ConnectionManager.getInstance().getTwitter().showUser(user);

			infoUsers.setFollower(ConnectionManager.getInstance().getTwitter().existsFriendship(user, screenName));
			infoUsers.setFriend(ConnectionManager.getInstance().getTwitter().existsFriendship(screenName, user));

			infoUsers.setName(user_data.getScreenName());
			infoUsers.setFullname(user_data.getName());
			infoUsers.setCreated(user_data.getCreatedAt());
			infoUsers.setLocation(user_data.getLocation());
			if (user_data.getURL()!=null) infoUsers.setUrl(user_data.getURL().toString());
			infoUsers.setFollowers(user_data.getFollowersCount());
			infoUsers.setFollowing(user_data.getFriendsCount());
			infoUsers.setTweets(user_data.getStatusesCount());
			infoUsers.setBio(user_data.getDescription());
			if (user_data.getStatus()!=null) infoUsers.setTextTweet(user_data.getStatus().getText());

			try {
				Bitmap bmp = null;
				String urlAvatar = user_data.getProfileImageURL().toString();
				infoUsers.setUrlAvatar(urlAvatar);
				File file = Utils.getFileForSaveURL(getContext(), urlAvatar);
				if (!file.exists()) {
                    bmp = Utils.saveAvatar(urlAvatar, file);
				} else {
					bmp = BitmapFactory.decodeFile(file.getAbsolutePath());
				}

				infoUsers.setAvatar(Bitmap.createScaledBitmap(bmp, 64, 64, true));
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}

            response.setInfoUsers(infoUsers);
            return response;
		} catch (TwitterException e) {
			e.printStackTrace();
            ErrorResponse response = new ErrorResponse();
			response.setError(e, e.getMessage());
            return response;
		} catch (NullPointerException e) {
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
