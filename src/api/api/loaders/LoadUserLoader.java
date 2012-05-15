package api.api.loaders;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import api.APIResult;
import api.AsynchronousLoader;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.javielinux.tweettopics2.Utils;
import com.javielinux.twitter.ConnectionManager;
import infos.InfoUsers;
import twitter4j.TwitterException;
import twitter4j.User;

import java.io.File;

public class LoadUserLoader extends AsynchronousLoader<APIResult> {

    private String user = "";

    public LoadUserLoader(Context context, Bundle bundle) {
        super(context);

        this.user = bundle.getString("user");
    }

    @Override
    public APIResult loadInBackground() {

        APIResult out = new APIResult();

		try {
            InfoUsers infoUsers = new InfoUsers();

			try {
                DataFramework.getInstance().open(getContext(), Utils.packageName);
            } catch (Exception e) {
	            e.printStackTrace();
                out.setError(e, e.getMessage());
                return out;
	        }

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

            out.addParameter("infoUsers", infoUsers);
            return out;
		} catch (TwitterException e) {
			e.printStackTrace();
			out.setError(e, e.getMessage());
            return out;
		} catch (NullPointerException e) {
			e.printStackTrace();
			out.setError(e, e.getMessage());
            return out;
		} catch (Exception e) {
			e.printStackTrace();
			out.setError(e, e.getMessage());
            return out;
		}
    }
}
