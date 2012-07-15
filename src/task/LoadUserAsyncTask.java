package task;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.javielinux.infos.InfoUsers;
import com.javielinux.twitter.ConnectionManager;
import com.javielinux.utils.Utils;
import twitter4j.TwitterException;
import twitter4j.User;

import java.io.File;

public class LoadUserAsyncTask extends AsyncTask<String, Void, InfoUsers> {

	public interface LoadUserAsyncAsyncTaskResponder {
		public void userLoading();
		public void userCancelled();
		public void userLoaded(InfoUsers iu);
	}

	private LoadUserAsyncAsyncTaskResponder responder;
	private Context mContext;

	public LoadUserAsyncTask(Context cnt, LoadUserAsyncAsyncTaskResponder responder) {
		mContext = cnt;
		this.responder = responder;
	}

	@Override
	protected InfoUsers doInBackground(String... args) {
		InfoUsers iu = new InfoUsers();
		try {
			
			try {
	            DataFramework.getInstance().open(mContext, Utils.packageName);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        
	        Entity ent = DataFramework.getInstance().getTopEntity("users", "active=1", "");
	        String screenName = "";
	        if (ent!=null) {
	        	screenName = ent.getString("name");
	        }
	        
	        DataFramework.getInstance().close();
			
			ConnectionManager.getInstance().open(mContext);
			
			User u = ConnectionManager.getInstance().getTwitter(ent.getId()).showUser(args[0]);
			
			iu.setFollower(ConnectionManager.getInstance().getTwitter(ent.getId()).existsFriendship(args[0], screenName));
			iu.setFriend(ConnectionManager.getInstance().getTwitter(ent.getId()).existsFriendship(screenName, args[0]));
			
			/*IDs ids = TweetTopicsCore.twitter.getFriendsIDs(-1);
			iu.setFriend(false);
			for (long i : ids.getIDs()) {
				if (u.getId()==i) {
					iu.setFriend(true);
					break;
				}
			}
			
			ids = TweetTopicsCore.twitter.getFollowersIDs(-1);
			iu.setFollower(false);
			for (long i : ids.getIDs()) {
				if (u.getId()==i) {
					iu.setFollower(true);
					break;
				}
			}*/
			
			iu.setName(u.getScreenName());
			iu.setFullname(u.getName());
			iu.setCreated(u.getCreatedAt());
			iu.setLocation(u.getLocation());
			if (u.getURL()!=null) iu.setUrl(u.getURL().toString());
			iu.setFollowers(u.getFollowersCount());
			iu.setFollowing(u.getFriendsCount());
			iu.setTweets(u.getStatusesCount());
			iu.setBio(u.getDescription());
			if (u.getStatus()!=null) iu.setTextTweet(u.getStatus().getText());
			try {
				Bitmap bmp = null;
				String urlAvatar = u.getProfileImageURL().toString();
				iu.setUrlAvatar(urlAvatar);
				File file = Utils.getFileForSaveURL(mContext, urlAvatar);
				if (!file.exists()) {
                    bmp = Utils.saveAvatar(urlAvatar, file);
					/*URL url = new URL(urlAvatar);
					bmp = BitmapFactory.decodeStream(new Utils.FlushedInputStream(url.openStream()));	
					FileOutputStream out = new FileOutputStream(file);
					bmp.compress(Bitmap.CompressFormat.PNG, 90, out);   */
				} else {
					bmp = BitmapFactory.decodeFile(file.getAbsolutePath());
				}
				//Bitmap bmp = BitmapFactory.decodeStream(u.getProfileImageURL().openStream());
				iu.setAvatar(Bitmap.createScaledBitmap(bmp, 64, 64, true));
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return iu;
		} catch (TwitterException e1) {
			e1.printStackTrace();
			iu = null;
		} catch (NullPointerException e1) {
			e1.printStackTrace();
			iu = null;
		} catch (Exception e1) {
			e1.printStackTrace();
			iu = null;
		}

		return iu;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		responder.userLoading();
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
		responder.userCancelled();
	}

	@Override
	protected void onPostExecute(InfoUsers iu) {
		super.onPostExecute(iu);
		responder.userLoaded(iu);
	}

}
