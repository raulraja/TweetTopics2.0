package com.javielinux.twitter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.javielinux.tweettopics2.R;
import com.javielinux.tweettopics2.Utils;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ConnectionManager2 {

	private static final String KEY_AUTH_KEY = "auth_key";
	private static final String KEY_AUTH_SECRET_KEY = "auth_secret_key";

	private static Context context;

	private static ConnectionManager2 instance;

	private final String CALLBACKURL = "t4joauth://main";

    private Twitter anonymousTwitter;

	private HashMap<Long, Twitter> twitters = new HashMap<Long, Twitter>();
	private RequestToken requestToken;
	private AccessToken accessToken;

	private String oAuthAccessToken;
	private String oAuthAccessTokenSecret;

	private String currentNetwork;

	private long mIdUserDB = -1;

	private NetworkConfig config = null;

	private ConnectionManager2() {};

	public static ConnectionManager2 getInstance() {
		if (instance == null) {
			instance = new ConnectionManager2();
		}
		return instance;
	}
	
	public static void destroyInstance() {
		instance = null;
	}
	
	public void open(Context cnt) {
		context = cnt;
        loadFromDB();
	}
	
	public Twitter getTwitter(long id) {
		return twitters.get(id);
	}

    public Twitter getAnonymousTwitter() {
        if (anonymousTwitter==null) {
            anonymousTwitter = new TwitterFactory().getInstance();
        }
        return anonymousTwitter;
    }

    public void loadFromDB() {
        try {
            DataFramework.getInstance().open(context, Utils.packageName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ArrayList<Entity> users = DataFramework.getInstance().getEntityList("users");
        for (Entity user: users) {
            if (!twitters.containsKey(user.getId())) {
                loadUser(user.getId());
            }
        }

        DataFramework.getInstance().close();
    }

    public void forceLoadFromDB() {
        twitters.clear();
        loadFromDB();
    }

	
	public void setNetworkConfig(String networkType) {
		
		NetworkConfigParser parser = new NetworkConfigParser();
		List<NetworkConfig> networkConfigs = parser.parse(context.getResources().getXml(R.xml.network_config));
		
		for (NetworkConfig c : networkConfigs) {
			if(c.getName().equals(networkType)) {
				config = c;
				break;
			}
		}
		currentNetwork = networkType;
		try {
			System.setProperty("twitter4j.oauth.consumerKey", config.getConsumerKey()); 
	        System.setProperty("twitter4j.oauth.consumerSecret", config.getConsumerSecret()); 
	        System.setProperty("twitter4j.oauth.accessTokenURL", config.getAccessTokenURL()); 
	        System.setProperty("twitter4j.oauth.authorizationURL", config.getAuthorizationURL()); 
	        System.setProperty("twitter4j.oauth.requestTokenURL", config.getRequestTokenURL()); 
	        System.setProperty("twitter4j.restBaseURL", config.getRestBaseURL()); 
	        System.setProperty("twitter4j.searchBaseURL", config.getSearchBaseURL()); 
	        System.setProperty("twitter4j.oauth.authenticationURL", config.getAuthenticationURL()); 
		} catch (NullPointerException e) {
			e.printStackTrace();
		}

		
	}
	
	public String getCurrentNetwork() {
		return currentNetwork;
	}
	
	public String getAuthenticationURL() throws TwitterException {
				
		Twitter twitter = new TwitterFactory().getInstance();
		//twitter.setOAuthConsumer(config.getConsumerKey(), config.getConsumerSecret());
				
		requestToken = twitter.getOAuthRequestToken();//AuthorizationActivity.TWITTER_HOST);
		
		Log.d(Utils.TAG, "Redirigiendo a " + requestToken.getAuthorizationURL());
		
		return requestToken.getAuthorizationURL();
	}

	public void finalizeOAuthentication(Uri uri, Twitter twitter) throws TwitterException {
		String verifier = uri.getQueryParameter("oauth_verifier");
		accessToken = twitter.getOAuthAccessToken(requestToken,verifier);
		oAuthAccessToken = accessToken.getToken();
		oAuthAccessTokenSecret = accessToken.getTokenSecret();		
		twitter.setOAuthAccessToken(accessToken);
		
		storeAccessToken(twitter);
	}
	
	public void storeAccessToken(Twitter twitter) {
		
        try {
            DataFramework.getInstance().open(context, Utils.packageName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        String where = KEY_AUTH_KEY + " = '" + accessToken.getToken() + "' AND " + KEY_AUTH_SECRET_KEY + " = '" + accessToken.getTokenSecret() + "'";
        
        List<Entity> e = DataFramework.getInstance().getEntityList("users", where);
                
        if (e.size()==0) {
        	DataFramework.getInstance().getDB().execSQL("UPDATE users SET active = 0");
        	
	        Entity ent = new Entity("users");
	        ent.setValue("service", currentNetwork);
	        try {
				ent.setValue("name", twitter.getScreenName());
				ent.setValue("user_id", twitter.getId());
				
				ent.setValue(KEY_AUTH_KEY, accessToken.getToken());
		        ent.setValue(KEY_AUTH_SECRET_KEY, accessToken.getTokenSecret());
	        	
			} catch (IllegalStateException e1) {
				e1.printStackTrace();
			} catch (TwitterException e1) {
				e1.printStackTrace();
			}
	        

        	ent.setValue("active", 1);
        	
        	ent.setValue("last_timeline_id", 0);
        	ent.setValue("last_mention_id", 0);
        	ent.setValue("last_direct_id", 0);
        	
			ent.save();
			
			mIdUserDB = ent.getId();
			
			try {
				User user = twitter.showUser(ent.getInt("user_id"));
				
				Bitmap avatar = BitmapFactory.decodeStream(new Utils.FlushedInputStream(user.getProfileImageURL().openStream()));
				
				if (avatar!=null) {
					String file = Utils.getFileAvatar(ent.getId());
					FileOutputStream out = new FileOutputStream(file);
					avatar.compress(Bitmap.CompressFormat.JPEG, 90, out);
					avatar.recycle();
				}
				
			} catch (IllegalStateException ex) {
				ex.printStackTrace();
			} catch (TwitterException ex) {
				ex.printStackTrace();
			} catch (IOException ex) {
				ex.printStackTrace();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			
        }
		
		DataFramework.getInstance().close();
	}
	
	public Twitter loadUser(long id) {

        Twitter twitter = null;

        Entity ent = null;
        
        if (id<0) {
        	ent = DataFramework.getInstance().getTopEntity("users", "active=1", "");
		} else {
			ent = new Entity("users", id);
		}
        
        if (ent!=null) {
        	
            // comprobamos si no es facebook
            
        	if (ent.getString("service")!=null) {
	            if (ent.getString("service").equals("facebook")) {
	            	ent = DataFramework.getInstance().getTopEntity("users", "service is null or service = \"twitter.com\"", "");
	            	ent.setValue("active", 1);
	    			ent.save();
	            }
        	}
	        
	        String service = ent.getString("service");
	        if (service.equals("")) service = "twitter.com";
	        
			setNetworkConfig(service);
			String accessToken = ent.getString(KEY_AUTH_KEY);
	        String accessTokenSecret = ent.getString(KEY_AUTH_SECRET_KEY);
	        //long userId = ent.getLong("user_id");
	
	        DataFramework.getInstance().close();
	        
	        //AccessToken at = new AccessToken(accessToken, accessTokenSecret, userId);
            AccessToken at = new AccessToken(accessToken, accessTokenSecret);
	        
	        twitter = new TwitterFactory().getInstance(at);
	        
	        mIdUserDB = ent.getId();

	        
	        Log.d(Utils.TAG, "Cargado " + ent.getString("name") + " desde " + service);

        }

		return twitter;
	}


	public String getCALLBACKURL() {
		return CALLBACKURL;
	}

	public RequestToken getRequestToken() {
		return requestToken;
	}

	public AccessToken getAccessToken() {
		return accessToken;
	}

	public String getOAuthToken() {
		return oAuthAccessToken;
	}

	public String getOAuthTokenSecret() {
		return oAuthAccessTokenSecret;
	}

	public long getIdUserDB() {
		return mIdUserDB;
	}

}