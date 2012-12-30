/*
 * Copyright 2012 Javier Pérez Pacheco and Francisco Díaz Rodriguez
 * TweetTopics 2.0
 * javielinux@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.javielinux.twitter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.javielinux.tweettopics2.R;
import com.javielinux.utils.ImageUtils;
import com.javielinux.utils.Utils;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ConnectionManager {

    interface TwitterAuthentication {
        void onFinalizeOAuthentication();
    }

	private static final String KEY_AUTH_KEY = "auth_key";
	private static final String KEY_AUTH_SECRET_KEY = "auth_secret_key";

	private static Context context;

	private static ConnectionManager instance;

	private final String CALLBACKURL = "t4joauth://main";

    private Twitter userForSearchesTwitter;

	private HashMap<Long, Twitter> twitters = new HashMap<Long, Twitter>();

    private Twitter twitterOAuth;
	private RequestToken requestTokenOAuth;
	private AccessToken accessTokenOAuth;

	private String oAuthAccessToken;
	private String oAuthAccessTokenSecret;

    private List<Entity> users = new ArrayList<Entity>();

	private ConnectionManager() {};

	public static ConnectionManager getInstance() {
		if (instance == null) {
			instance = new ConnectionManager();
		}
		return instance;
	}

    public void loadUsers() {
        users = DataFramework.getInstance().getEntityList("users");
    }

    public Entity getUserById(long id) {
        if (users.size() == 0) {
            loadUsers();
        }
        for (Entity user : users) {
            if (user.getId() == id) {
                return user;
            }
        }
        return null;
    }
	
	public static void destroyInstance() {
		instance = null;
	}
	
	public void open(Context cnt) {
		context = cnt;
       // loadFromDB();
	}
	
	public Twitter getTwitter(long id) {
        if (!twitters.containsKey(id)) {
            twitters.put(id, loadUser(id));
        }
		return twitters.get(id);
	}

    public Twitter getUserForSearchesTwitter() {
        if (userForSearchesTwitter ==null) {
            loadUserForSearches();
        }
        return userForSearchesTwitter;
    }

    public void loadFromDB() {

        for (Entity user: users) {
            if (!twitters.containsKey(user.getId())) {
                twitters.put(user.getId(), loadUser(user.getId()));
            }
        }

    }

    public void forceLoadFromDB() {
        twitters.clear();
        loadFromDB();
    }

    private void loadUserForSearches() {

        try {
            DataFramework.getInstance().open(context, Utils.packageName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Entity user = DataFramework.getInstance().getTopEntity("users", "use_for_searches = 1", "");

        if (user == null) {
            user = DataFramework.getInstance().getTopEntity("users", "service is null or service = \"twitter.com\"", "");
            user.setValue("use_for_searches", 1);
            user.save();
        }

        userForSearchesTwitter = loadUser(user.getId());

        DataFramework.getInstance().close();
    }

    /*
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
    */

    public NetworkConfig getNetworkConfig() {
        NetworkConfig config = null;
        NetworkConfigParser parser = new NetworkConfigParser();
        List<NetworkConfig> networkConfigs = parser.parse(context.getResources().getXml(R.xml.network_config));

        for (NetworkConfig c : networkConfigs) {
            if(c.getName().equals("twitter.com")) {
                config = c;
                break;
            }
        }
        return config;
    }

    public Configuration getConfiguration() {
        NetworkConfig config = getNetworkConfig();

        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        configurationBuilder.setOAuthConsumerKey(config.getConsumerKey());
        configurationBuilder.setOAuthConsumerSecret(config.getConsumerSecret());
        configurationBuilder.setUseSSL(true);
        return configurationBuilder.build();
    }

	public String getAuthenticationURL() throws TwitterException {

        NetworkConfig config = getNetworkConfig();

        twitterOAuth = new TwitterFactory().getInstance();
        twitterOAuth.setOAuthConsumer(config.getConsumerKey(), config.getConsumerSecret());

		requestTokenOAuth = twitterOAuth.getOAuthRequestToken();//AuthorizationActivity.TWITTER_HOST);

		Log.d(Utils.TAG, "Redirigiendo a " + requestTokenOAuth.getAuthorizationURL());

		return requestTokenOAuth.getAuthorizationURL();
	}

	public void finalizeOAuthentication(final Uri uri, final TwitterAuthentication twitterAuthentication) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String verifier = uri.getQueryParameter("oauth_verifier");
                    accessTokenOAuth = twitterOAuth.getOAuthAccessToken(requestTokenOAuth,verifier);
                    oAuthAccessToken = accessTokenOAuth.getToken();
                    oAuthAccessTokenSecret = accessTokenOAuth.getTokenSecret();
                    twitterOAuth.setOAuthAccessToken(accessTokenOAuth);
                    storeAccessToken(twitterOAuth);
                    twitterAuthentication.onFinalizeOAuthentication();
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
            }
        }).start();

	}

	private void storeAccessToken(Twitter twitter) {

        try {
            DataFramework.getInstance().open(context, Utils.packageName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String where = KEY_AUTH_KEY + " = '" + accessTokenOAuth.getToken() + "' AND " + KEY_AUTH_SECRET_KEY + " = '" + accessTokenOAuth.getTokenSecret() + "'";

        List<Entity> e = DataFramework.getInstance().getEntityList("users", where);

        if (e.size()==0) {
        	DataFramework.getInstance().getDB().execSQL("UPDATE users SET active = 0");

	        Entity ent = new Entity("users");
	        ent.setValue("service", "twitter.com");
	        try {
				ent.setValue("name", twitter.getScreenName());
                ent.setValue("fullname", twitter.showUser(twitter.getId()).getName());
				ent.setValue("user_id", twitter.getId());

				ent.setValue(KEY_AUTH_KEY, accessTokenOAuth.getToken());
		        ent.setValue(KEY_AUTH_SECRET_KEY, accessTokenOAuth.getTokenSecret());

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

            loadUsers();

			try {
				User user = twitter.showUser(ent.getInt("user_id"));

				Bitmap avatar = BitmapFactory.decodeStream(new Utils.FlushedInputStream(new URL(user.getProfileImageURL()).openStream()));

				if (avatar!=null) {
					String file = ImageUtils.getFileAvatar(ent.getId());
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


        Entity ent = getUserById(id);
        
        if (ent!=null) {
        	
            // comprobamos si no es facebook
            /*
        	if (ent.getString("service")!=null) {
	            if (ent.getString("service").equals("facebook")) {
	            	ent = DataFramework.getInstance().getTopEntity("users", "service is null or service = \"twitter.com\"", "");
	            	ent.setValue("active", 1);
	    			ent.save();
	            }
        	}
	        */
	        String service = ent.getString("service");
	        if (service.equals("")) service = "twitter.com";
	        
			//setNetworkConfig(service);
			String accessToken = ent.getString(KEY_AUTH_KEY);
	        String accessTokenSecret = ent.getString(KEY_AUTH_SECRET_KEY);


//            ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
//            configurationBuilder.setOAuthConsumerKey(accessToken);
//            configurationBuilder.setOAuthConsumerSecret(accessTokenSecret);
//            configurationBuilder.setUseSSL(true);
//            Configuration configuration = configurationBuilder.build();
//            twitter = new TwitterFactory(configuration).getInstance();


            AccessToken at = new AccessToken(accessToken, accessTokenSecret);
	        twitter = new TwitterFactory(getConfiguration()).getInstance(at);
	        
	        Log.d(Utils.TAG, "Cargado " + ent.getString("name") + " desde twitter");

        }

		return twitter;
	}


	public String getCALLBACKURL() {
		return CALLBACKURL;
	}

	public RequestToken getRequestTokenOAuth() {
		return requestTokenOAuth;
	}

	public AccessToken getAccessTokenOAuth() {
		return accessTokenOAuth;
	}

	public String getOAuthToken() {
		return oAuthAccessToken;
	}

	public String getOAuthTokenSecret() {
		return oAuthAccessTokenSecret;
	}

}