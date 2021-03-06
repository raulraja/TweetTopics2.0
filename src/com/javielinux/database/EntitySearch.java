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

package com.javielinux.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Location;
import android.preference.PreferenceManager;
import android.util.Log;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.javielinux.infos.InfoSaveTweets;
import com.javielinux.tweettopics2.R;
import com.javielinux.twitter.ConnectionManager;
import com.javielinux.utils.LocationUtils;
import com.javielinux.utils.Utils;
import twitter4j.*;

import java.util.ArrayList;

public class EntitySearch extends Entity {
	/*
	public static int TYPE_SICELASTID_NOUSE = 0;
	public static int TYPE_SICELASTID_NORMAL = 1;
	public static int TYPE_SICELASTID_NOTIFICATIONS = 2;
	*/
	private String mErrorLastQuery = "";

    public EntitySearch() {
        super("search");
    }

	public EntitySearch(Long id) {
		super("search", id);
	}
	
	public boolean isUser() {
		if (!this.getString("words_and").equals("")) {
			return false;
		}
		
		if (!this.getString("words_or").equals("")) {
			return false;
		}
		
		if (!this.getString("words_not").equals("")) {
			return false;
		}
		
		if (!this.getString("lang").equals("")) {
			return false;
		}
		
		if (!this.getString("source").equals("")) {
			return false;
		}
		
		if (!this.getString("to_user").equals("")) {
			return false;
		}
		
		if (this.getInt("attitude")!=0) {
			return false;
		}
		
		if (this.getInt("filter")!=0) {
			return false;
		}
		
		if (this.getInt("use_geo")!=0) {
			return false;
		}
		
		if (!this.getString("from_user").equals("")) {
			return true;
		}
		
		return false;
	}
	
	public String getErrorLastQuery() {
		return mErrorLastQuery;
	}
	
	public int getValueNewCount() {
		if (getInt("notifications")==1) {
			return DataFramework.getInstance().getEntityListCount("tweets", "search_id = " + this.getId() 
   				+ " AND favorite = 0 AND tweet_id >'" + Utils.fillZeros(""+getString("last_tweet_id"))+"'");
		} else {
			return 0;
		}
	}
	
	public void setValueLastId(String id) {
		setValue("last_tweet_id", id);
	}
	
	public void setValueLastIdNotification(String id) {
		setValue("last_tweet_id_notifications", id);
	}
	
	public long getValueLastId() {
		return getLong("last_tweet_id");
	}
	
	public long getValueLastIdNotification() {
		return getLong("last_tweet_id_notifications");
	}
	
	public InfoSaveTweets saveTweets(Context cnt, boolean saveNotifications, long since_id) {
        ConnectionManager.getInstance().open(cnt);
        Twitter twitter = ConnectionManager.getInstance().getUserForSearchesTwitter();
		InfoSaveTweets out = new InfoSaveTweets();
		try {
			int nResult = DataFramework.getInstance().getEntityListCount("tweets", "favorite=0 and search_id="+getId());

            Query query = getQuery(cnt);
            if (since_id != -1)
                query.setSinceId(since_id);

			QueryResult result = twitter.search(query);
			ArrayList<Status> tweets = (ArrayList<Status>)result.getTweets();
			
			if (tweets.size()>0) {
				
				out.setNewMessages(tweets.size());
				out.setNewerId(tweets.get(0).getId());
				out.setOlderId(tweets.get(tweets.size()-1).getId());
				if (saveNotifications) {
					setValue("new_tweets_count",getInt("new_tweets_count")+tweets.size());
					save();
				}
			
				Log.d(Utils.TAG,tweets.size()+" mensajes nuevos en "+getString("name"));
			
				long fisrtId = 1;
				Cursor c = DataFramework.getInstance().getCursor("tweets", new String[]{DataFramework.KEY_ID}, 
						null, null, null, null, DataFramework.KEY_ID + " desc", "1");
				if (!c.moveToFirst()) {
					c.close();
					fisrtId = 1;
				} else {
					long Id = c.getInt(0) + 1;
					c.close();
					fisrtId = Id;
				}
							
				for (int i=tweets.size()-1; i>=0; i--) {
					
					/*String sql = "INSERT INTO 'tweets' ("  + DataFramework.KEY_ID + ", search_id, url_avatar, username, user_id, tweet_id,"
							+ "text, source, to_username, to_user_id, date, favorite) VALUES (" + fisrtId + "," + getId()
							+ ",'" +tweets.get(i).getProfileImageUrl() + "','"+tweets.get(i).getFromUser()+"','"
							+ tweets.get(i).getFromUserId() + "','" + tweets.get(i).getId() + "','" + tweets.get(i).getText() 
							+ "','" + tweets.get(i).getSource()	+ "','"+tweets.get(i).getToUser()
							+"','"+tweets.get(i).getToUserId()+"','"+String.valueOf(tweets.get(i).getCreatedAt().getTime())
							+ "',0);\n";*/
                    User u = tweets.get(i).getUser();
					ContentValues args = new ContentValues();
					args.put(DataFramework.KEY_ID, "" + fisrtId);
					args.put("search_id", "" + getId());
                    if (u.getProfileImageURL()!=null) {
                        args.put("url_avatar", u.getProfileImageURL().toString());
                    } else {
                        args.put("url_avatar", "");
                    }
                    args.put("username", u.getScreenName());
                    args.put("fullname", u.getName());
                    args.put("user_id", "" + u.getId());
					args.put("tweet_id", Utils.fillZeros("" + tweets.get(i).getId()));
					args.put("text", tweets.get(i).getText());
					args.put("source", tweets.get(i).getSource());
                    args.put("to_username", tweets.get(i).getInReplyToScreenName());
                    args.put("to_user_id", "" + tweets.get(i).getInReplyToUserId());
					args.put("date", String.valueOf(tweets.get(i).getCreatedAt().getTime()));
                    if (tweets.get(i).getGeoLocation()!=null) {
                        args.put("latitude", tweets.get(i).getGeoLocation().getLatitude());
                        args.put("longitude", tweets.get(i).getGeoLocation().getLongitude());
                    }
					args.put("favorite", "0");
					
					DataFramework.getInstance().getDB().insert("tweets", null, args);
					
					fisrtId++;
	
				}
				
				if (saveNotifications) {
					setValue("last_tweet_id_notifications",tweets.get(0).getId()+"");
					save();
				}
				
				int total = nResult+tweets.size();
				if (total>Utils.MAX_ROW_BYSEARCH) {
					Log.d(Utils.TAG,"Limpiando base de datos");
					String date = DataFramework.getInstance().getEntityList("tweets", "favorite=0 and search_id="+getId(), "date desc").get(Utils.MAX_ROW_BYSEARCH).getString("date");
					String sqldelete = "DELETE FROM tweets WHERE favorite=0 AND search_id="+getId() + " AND date  < '" + date + "'";
					DataFramework.getInstance().getDB().execSQL(sqldelete);
				}

			}
			
		} catch (TwitterException e) {
			e.printStackTrace();
    		RateLimitStatus rate = e.getRateLimitStatus();
    		if (rate!=null) {
    			out.setError(Utils.LIMIT_ERROR);
    			out.setRate(rate);
    		} else {
    			out.setError(Utils.UNKNOWN_ERROR);
    		}
		} catch (Exception e) {
			e.printStackTrace();
			out.setError(Utils.UNKNOWN_ERROR);
		}
		return out;
	}
	
	public Query getQuery(Context cnt) {
		String q = this.getString("words_and");
		
		if (!this.getString("words_or").equals("")) {
			q += Utils.getQuotedText(this.getString("words_or"), "OR ", false);	
		}
		
		if (!this.getString("words_not").equals("")) {
			q += Utils.getQuotedText(this.getString("words_not"), "-", true);	
		}
		
		if (!this.getString("from_user").equals("")) {
			q+=" from:"+this.getString("from_user");
		}
		
		if (!this.getString("to_user").equals("")) {
			q+=" to:"+this.getString("to_user");
		}
		
		if (!this.getString("source").equals("")) {
			q+=" source:"+this.getString("source");
		}

		if (this.getInt("attitude")==1) q+=" :)";
		if (this.getInt("attitude")==2) q+=" :(";
		
		String modLinks = "filter:links";
		String websVideos = "twitvid OR youtube OR vimeo OR youtu.be";
		String webPhotos = "lightbox.com OR mytubo.net OR imgur.com OR instagr.am OR twitpic OR yfrog OR plixi OR twitgoo OR img.ly OR picplz OR lockerz";
		
		if (this.getInt("filter")==1) q+=" " + modLinks;
		if (this.getInt("filter")==2) q+=" " + webPhotos + " " + modLinks;
		if (this.getInt("filter")==3) q+=" " + websVideos + " " + modLinks;
		if (this.getInt("filter")==4) q+=" " + websVideos + " OR " + webPhotos + " " + modLinks;
		if (this.getInt("filter")==5) q+=" source:twitterfeed " + modLinks;
		if (this.getInt("filter")==6) q+=" ?";
		if (this.getInt("filter")==7) q+=" market.android.com OR androidzoom.com OR androlib.com OR appbrain.com OR bubiloop.com OR yaam.mobi OR slideme.org " + modLinks;
				
		Log.d(Utils.TAG, "Buscando: "+q);
		
		Query query = new Query(q);
								
		if (this.getInt("use_geo")==1) {
			if (this.getInt("type_geo")==0) { // coordenadas del mapa
				GeoLocation gl = new GeoLocation(this.getDouble("latitude"), this.getDouble("longitude"));
				String unit = Query.KILOMETERS;
				if (this.getInt("type_distance")==0) unit = Query.MILES;
				query.setGeoCode(gl, this.getDouble("distance"), unit);
			}
			
			if (this.getInt("type_geo")==1) { // coordenadas del gps
				Location loc = LocationUtils.getLastLocation(cnt);
				if (loc!=null) {
					GeoLocation gl = new GeoLocation(loc.getLatitude(), loc.getLongitude());
					String unit = Query.KILOMETERS;
					if (this.getInt("type_distance")==0) unit = Query.MILES;
					query.setGeoCode(gl, this.getDouble("distance"), unit);
				} else {
					mErrorLastQuery = cnt.getString(R.string.no_location);
				}
			}
		}
		
		PreferenceManager.setDefaultValues(cnt, R.xml.preferences, false);
		SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(cnt);

        int count = Integer.parseInt(preference.getString("prf_n_max_download", "60"));
        if (count <= 0) count = 60;

		query.setCount(count);

        String lang = "";
        if (!this.getString("lang").equals("")) lang = this.getString("lang");
        if (!lang.equals("all")) query.setLang(lang);

		// obtener desde donde quiero hacer la consulta
		
		if (getInt("notifications")==1) {
			String where = "search_id = " + this.getId() + " AND favorite = 0";
			int nResult = DataFramework.getInstance().getEntityListCount("tweets", where);			
			if (nResult>0) {
				long mLastIdNotification = DataFramework.getInstance().getTopEntity("tweets", where, "date desc").getLong("tweet_id");
				query.setSinceId(mLastIdNotification);
			}
		}
		
		//query.setResultType(Query.POPULAR);
		
		return query;
	}

}
