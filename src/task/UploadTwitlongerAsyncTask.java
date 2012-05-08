package task;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import twitter4j.GeoLocation;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.javielinux.tweettopics.Utils;

public class UploadTwitlongerAsyncTask extends AsyncTask<String, Void, Boolean> {

	public interface UploadTwitlongerAsyncTaskResponder {
		public void uploadTwitlongerLoading();
		public void uploadTwitlongerCancelled();
		public void uploadTwitlongerLoaded(boolean error);
	}
	
	private Context mContext;
	private Twitter twitter;

	private UploadTwitlongerAsyncTaskResponder responder;

	public UploadTwitlongerAsyncTask(Context context, UploadTwitlongerAsyncTaskResponder responder, Twitter twitter) {
		this.mContext = context;
		this.responder = responder;
		this.twitter = twitter;
	}

	@Override
	protected Boolean doInBackground(String... args) {
		try {
			String text = args[0];
			Log.d(Utils.TAG, "Enviando a twitlonger: " + text);
			long tweet_id = Long.parseLong(args[1]);
			boolean useGeo = args[2].equals("1")?true:false;
			
			String textTwitLonger = "";
			
			HttpClient httpclient = new DefaultHttpClient();  
            HttpPost httppost = new HttpPost("http://www.twitlonger.com/api_post");  
            try {  
            	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);  
				nameValuePairs.add(new BasicNameValuePair("application", "tweettopics"));
				nameValuePairs.add(new BasicNameValuePair("api_key", "f7y8lgz31srR46sr"));
				nameValuePairs.add(new BasicNameValuePair("username", twitter.getScreenName()));
				
				//byte[] utf8Bytes = text.getBytes("UTF8");
				//String textutf8 = new String(utf8Bytes, "UTF8");
				nameValuePairs.add(new BasicNameValuePair("message", text));

                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));  
                HttpResponse httpResponse = httpclient.execute(httppost);  
                
				String xml = EntityUtils.toString(httpResponse.getEntity());
				try {

					XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
					factory.setNamespaceAware(true);
					XmlPullParser x = factory.newPullParser();

					x.setInput(new StringReader(xml));
					
					String error = "";
					
					int eventType = x.getEventType();
					while (eventType != XmlPullParser.END_DOCUMENT) {
						if (eventType == XmlPullParser.START_TAG) {
							if (x.getName().equals("error")) {
								error = x.nextText();
							}
							if (x.getName().equals("content")) {
								textTwitLonger = x.nextText();
								Log.d(Utils.TAG, "Enviando a twitter: " + textTwitLonger);
							}
						}
						eventType = x.next();
					}
					
					if (!error.equals("")) {
						Log.d(Utils.TAG, "Error: " + error);
						return true;
					}				

				} catch (Exception e) {
					e.printStackTrace();
					return true;
				}
				
            } catch (Exception e) {
            	e.printStackTrace();
            	return true;
            } 
			
			if (!textTwitLonger.equals("")) {
				StatusUpdate su = new StatusUpdate(textTwitLonger);
				if (useGeo) {
					Location loc = Utils.getLastLocation(mContext);
					GeoLocation gl = new GeoLocation(loc.getLatitude(), loc.getLongitude());
					su.setLocation(gl);
				}
				if (tweet_id>0) su.inReplyToStatusId(tweet_id);
				twitter.updateStatus(su);
			} else {
				return true;
			}

		//} catch (TwitterException e) {
		//	e.printStackTrace();
		//	return true;
		} catch (Exception e) {
			e.printStackTrace();
			return true;
		}
		return false;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		responder.uploadTwitlongerLoading();
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
		responder.uploadTwitlongerCancelled();
	}

	@Override
	protected void onPostExecute(Boolean error) {
		super.onPostExecute(error);
		responder.uploadTwitlongerLoaded(error);
	}

}
