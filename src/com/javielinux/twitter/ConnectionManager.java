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
import java.util.List;


public class ConnectionManager {

	private static final String KEY_AUTH_KEY = "auth_key";
	private static final String KEY_AUTH_SECRET_KEY = "auth_secret_key";
	
	private static Context mContext;
	
	private static ConnectionManager instance;
	private static String consumerKey; 		
	private static String consumerSecret; 
		
	private final String CALLBACKURL = "t4joauth://main";
	private Twitter twitter;
	private RequestToken requestToken;
	private AccessToken accessToken;
	
	private boolean loggedIn = false;
	
	private String oAuthAccessToken;
	private String oAuthAccessTokenSecret;
	
	private String currentNetwork;
	
	private long mIdUserDB = -1;
	
	private NetworkConfig config = null;
	
	private ConnectionManager() {};

	public static ConnectionManager getInstance() {
		if (instance == null) {
			instance = new ConnectionManager();
		}
		return instance;
	}
	
	public static void destroyInstance() {
		instance = null;
	}
	
	public void open(Context cnt) {
		mContext = cnt;
	}
	
	public Twitter getTwitter() {
		if (twitter==null) {
			loadUser(-1, false);
		}
		return twitter;
	}
	
	public Twitter getTwitter(long id, boolean saveActive) {
		loadUser(id, saveActive);
		return twitter;
	}
	
	public Twitter getTwitterForceActiveUser() {
        /*try {
            DataFramework.getInstance().open(mContext, Utils.packageName);
        } catch (Exception e) {
            e.printStackTrace();
        }
		Entity e = DataFramework.getInstance().getTopEntity("users", "active=1", "");
		if (e!=null) {
			if (mIdUserDB!=e.getId()) {
				loadUser(e.getId(), false);
			}
		}
		DataFramework.getInstance().close();*/
		loadUser(-1, false);
		return twitter;
	}
	
	public void setNetworkConfig(String networkType) {
				
		logout();
		
		NetworkConfigParser parser = new NetworkConfigParser();
		List<NetworkConfig> networkConfigs = parser.parse(mContext.getResources().getXml(R.xml.network_config));
		
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
				
		twitter = new TwitterFactory().getInstance();
		//twitter.setOAuthConsumer(config.getConsumerKey(), config.getConsumerSecret());
				
		requestToken = twitter.getOAuthRequestToken();//AuthorizationActivity.TWITTER_HOST);
		
		Log.d(Utils.TAG, "Redirigiendo a " + requestToken.getAuthorizationURL());
		
		return requestToken.getAuthorizationURL();
	}
	
	public void logout() {
		if (twitter!=null) {
			twitter.shutdown();
			System.gc();
		}
		requestToken = null;
		accessToken = null;
		twitter = null;
		mIdUserDB = -1;
		loggedIn = false;
	}
	
	public void finalizeOAuthentication(Uri uri) throws TwitterException {		
		String verifier = uri.getQueryParameter("oauth_verifier");
		accessToken = twitter.getOAuthAccessToken(requestToken,verifier);
		oAuthAccessToken = accessToken.getToken();
		oAuthAccessTokenSecret = accessToken.getTokenSecret();		
		twitter.setOAuthAccessToken(accessToken);
		
		loggedIn = true;
		
		storeAccessToken();
	}
	
	public void storeAccessToken() {
		
        try {
            DataFramework.getInstance().open(mContext, Utils.packageName);
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
	
	public void loadUser(long id, boolean saveActive) {
        
        try {
            DataFramework.getInstance().open(mContext, Utils.packageName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
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
			
	        if (saveActive) {
				DataFramework.getInstance().getDB().execSQL("UPDATE users SET active = 0");
				ent.setValue("active", 1);
				ent.save();
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
	        
	        loggedIn = true;
	        
	        Log.d(Utils.TAG, "Cargado " + ent.getString("name") + " desde " + service);
	/*
	        try {
				twitter.verifyCredentials();
				loggedIn = true;
			} catch (TwitterException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}

	        */
        }
        loggedIn = false;

		/*
		loadAccessToken(id, saveActive);
		Log.d(Utils.TAG, "Nombre " + accessToken.getScreenName());
		Log.d(Utils.TAG, "Token " + accessToken.getToken());
		Log.d(Utils.TAG, "Token Secret " + accessToken.getTokenSecret());
		twitter = new TwitterFactory().getInstance();
		twitter.setOAuthAccessToken(accessToken);
		loggedIn = true;*/
	}
	/*
	private void loadAccessToken(long id, boolean saveActive) {
        try {
            DataFramework.getInstance().open(mContext, Utils.packageName);
        } catch (Exception e) {
            e.printStackTrace();
        }
		long key_id = id;
		
		String token = "";
		String tokenSecret = "";
		
		boolean todo = false;
		
		if (id<0) {
			List<Entity> e = DataFramework.getInstance().getEntityList("users", "active=1");
			if (e.size()>0) {
				key_id = e.get(0).getId();
				todo = true;
			}
		} else {
			todo = true;
		}
		
		String name = "";
		
		if (todo) {
			Entity ent = new Entity("users", key_id);
			name = ent.getString("name");
			token = ent.getString(KEY_AUTH_KEY);
			tokenSecret = ent.getString(KEY_AUTH_SECRET_KEY);
			if (saveActive) {
				DataFramework.getInstance().getDB().execSQL("UPDATE users SET active = 0");
				ent.setValue("active", 1);
				ent.save();
			}
			setNetworkConfig(ent.getString("service"));
			mIdUserDB = key_id;
		}
		
		DataFramework.getInstance().close();
		
		if (!token.equals("") && !tokenSecret.equals("")) {
			Log.d(Utils.TAG, "Cargando usuario " + name + " en " + currentNetwork);
			Log.d(Utils.TAG, "Tokens " + token + " + " + tokenSecret);
			Log.d(Utils.TAG, "En " + System.getProperty("twitter4j.oauth.accessTokenURL"));
			accessToken = new AccessToken(token, tokenSecret);
		} else {
			accessToken = null;
		}
	}
	
	*/

	public String getConsumerkey() {
		return consumerKey;
	}

	public String getConsumersecret() {
		return consumerSecret;
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

	public boolean isLoggedIn() {
		return loggedIn;
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