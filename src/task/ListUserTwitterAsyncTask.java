package task;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import com.javielinux.adapters.UserTwitterListAdapter;
import com.javielinux.tweettopics2.TabGeneral;
import com.javielinux.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ListUserTwitterAsyncTask extends AsyncTask<String, Void, UserTwitterListAdapter> {

	public interface UserTwitterStatusAsyncTaskResponder {
		public void userTwitterStatusLoading();
		public void userTwitterStatusCancelled();
		public void userTwitterStatusLoaded(UserTwitterListAdapter adapter);
	}

	private UserTwitterStatusAsyncTaskResponder responder;
	private TabGeneral mTabGeneral;

	public ListUserTwitterAsyncTask(TabGeneral mTabGeneral) {
		this.responder = mTabGeneral;
		this.mTabGeneral = mTabGeneral;
	}

	@Override
	protected UserTwitterListAdapter doInBackground(String... args) {
		/*ArrayList<InfoUsers> ar = new ArrayList<InfoUsers>();
		try {
			ResponseList<User> users = TabGeneral.twitter.searchUsers(args[0], 0);
			for (int i=0; i<users.size(); i++) {
				InfoUsers u = new InfoUsers();
				u.setName(users.get(i).getScreenName());
				ar.add(u);
			}
			return new UserTwitterListAdapter(mTabGeneral, ar);
		} catch (TwitterException e) {
			e.printStackTrace();
			UserTwitterListAdapter adapter = new UserTwitterListAdapter(mTabGeneral, ar);
    		RateLimitStatus rate = e.getRateLimitStatus();
    		if (rate!=null) {
    			adapter.setError(Utils.LIMIT_ERROR);
    			adapter.setRate(rate);
    		} else {
    			adapter.setError(Utils.UNKNOWN_ERROR);
    		}
    		return adapter;
		}   */
        return null;
	}
	
	public static Bitmap getBitmapFromURL(String src) {
	    try {
	        URL url = new URL(src);
	        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	        connection.setDoInput(true);
	        connection.connect();
	        InputStream input = connection.getInputStream();
	        Bitmap myBitmap = BitmapFactory.decodeStream(new Utils.FlushedInputStream(input));
	        return myBitmap;
	    } catch (OutOfMemoryError e) {
	    	e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();	        
	    } catch (Exception e) {
	        e.printStackTrace();	        
	    }
	    return null;
	}


	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		responder.userTwitterStatusLoading();
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
		responder.userTwitterStatusCancelled();
	}

	@Override
	protected void onPostExecute(UserTwitterListAdapter adapter) {
		super.onPostExecute(adapter);
		responder.userTwitterStatusLoaded(adapter);
	}

}
