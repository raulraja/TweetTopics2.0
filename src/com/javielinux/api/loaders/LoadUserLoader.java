package com.javielinux.api.loaders;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.javielinux.api.AsynchronousLoader;
import com.javielinux.api.request.LoadUserRequest;
import com.javielinux.api.response.BaseResponse;
import com.javielinux.api.response.ErrorResponse;
import com.javielinux.api.response.LoadUserResponse;
import com.javielinux.twitter.ConnectionManager;
import com.javielinux.utils.Utils;
import infos.CacheData;
import infos.InfoUsers;
import twitter4j.TwitterException;
import twitter4j.User;

import java.io.File;

public class LoadUserLoader extends AsynchronousLoader<BaseResponse> {

    private LoadUserRequest request;

    public LoadUserLoader(Context context, LoadUserRequest request) {
        super(context);

        this.request = request;
    }

    @Override
    public BaseResponse loadInBackground() {

		try {
            LoadUserResponse response = new LoadUserResponse();
            InfoUsers infoUsers = new InfoUsers();

			ConnectionManager.getInstance().open(getContext());

			User user_data = ConnectionManager.getInstance().getAnonymousTwitter().showUser(request.getUser());

			//infoUsers.setFollower(ConnectionManager.getInstance().getTwitter(request.getUserId()).existsFriendship(request.getUser(), screenName));
			//infoUsers.setFriend(ConnectionManager.getInstance().getTwitter(request.getUserId()).existsFriendship(screenName, request.getUser()));

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

            CacheData.addCacheUsers(infoUsers);

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
