package layouts;

import adapters.TweetsAdapter;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.text.Html;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.javielinux.tweettopics2.R;
import com.javielinux.tweettopics2.ThemeManager;
import com.javielinux.tweettopics2.TweetTopicsCore;
import com.javielinux.utils.PreferenceUtils;
import com.javielinux.utils.Utils;
import infos.CacheData;
import infos.InfoLink;
import infos.InfoTweet;
import task.LoadImageAsyncTask;
import task.LoadImageAsyncTask.LoadImageAsyncTaskResponder;
import twitter4j.Status;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class TweetListViewItem extends RelativeLayout implements LoadImageAsyncTaskResponder {

    private Context context;
	private AsyncTask<String, Void, Void> latestLoadTask;
	
	private String mUrlAvatar = "";
	private String mRetweetUrlAvatar = "";
	
	private boolean isRetweet = false;
	private boolean hasAvatar = false;
	private boolean hasRetweetAvatar = false;
	
	private int position = -1;
	
	private HashMap<String,Integer> mPosImages = new HashMap<String,Integer>();
	
	private TweetsAdapter.ViewHolder viewHolder;

	
	public TweetListViewItem(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
	}
	
	private boolean searchAvatar(String url, ImageView holder) {
		if (CacheData.getCacheAvatars().containsKey(url)) {
			holder.setImageBitmap(CacheData.getCacheAvatars().get(url));
			return true;
		} else {
			File file = Utils.getFileForSaveURL(getContext(), url);
			if (file.exists()) {
                try {
                    Bitmap bmp = BitmapFactory.decodeFile(file.getAbsolutePath());
                    if (bmp != null) {
                        CacheData.putCacheAvatars(url, bmp);
                        holder.setImageBitmap(bmp);
                        return true;
                    }
                } catch (OutOfMemoryError e) {
                    e.printStackTrace();
                }
			}
            try {
			    holder.setImageResource(R.drawable.avatar);
            } catch (OutOfMemoryError e) {}
			return false;
		}
	}
	
	private boolean searchImage(String url) {
		if (CacheData.getCacheImages().containsKey(url)) {
			addImage(CacheData.getCacheImages().get(url));
			return true;
		} else {
			addImageWait(url);
			return false;
		}
	}
	
	private void addImageWait(String url) {
		
		int n = viewHolder.tweetPhotoLayout.getChildCount();
		mPosImages.put(url, n);
		
		ImageView img = new ImageView(viewHolder.tweetPhotoLayout.getContext());
		
		int px = Utils.dip2px(context, 40);
        
        LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, px);
        ll.gravity = Gravity.CENTER;
        ll.setMargins(5, 2, 5, 2);
        img.setLayoutParams(ll);
        
        img.setAdjustViewBounds(true);
		
		int type = Integer.parseInt(Utils.preference.getString("prf_links", "3"));
		if (type==3) img.setImageResource(R.drawable.icon_general_preview);
		viewHolder.tweetPhotoLayout.addView(img);
	}
	
	private void modImage(int pos, InfoLink info) {
		
		if (viewHolder.tweetPhotoLayout.getVisibility()==View.GONE) viewHolder.tweetPhotoLayout.setVisibility(View.VISIBLE);
		
		ImageView imageView = (ImageView)viewHolder.tweetPhotoLayout.getChildAt(pos);
		if (imageView != null) {
            imageView.setBackgroundResource(R.drawable.image_link_background);
            imageView.setPadding(0, 2, 0, 2);
			if (info.getType()==2) { // es un enlace
				int resID = getResources().getIdentifier(Utils.packageName+":drawable/icon_general_link_"+(pos+1), null, null);
                imageView.setImageResource(resID);
			} else if (info.getType()==3) { // es una busqueda QR
                imageView.setImageResource(R.drawable.icon_tweettopics_qr);
			} else if (info.getType()==4) { // es un tema
                imageView.setImageResource(R.drawable.icon_tweettopics_theme);
			} else {
                imageView.setImageBitmap(info.getBitmapThumb());
			}
            imageView.setTag(info);
            imageView.setOnClickListener(new OnClickListener() {
	
				@Override
				public void onClick(View img) {
                    Log.d("TweetTopics", "ImageView.onClick");
					//InfoLink il = (InfoLink)img.getTag();
					//mTweetTopicsCore.showSidebarLink(il, position);
				}
				
			});

            imageView.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View img) {
                    Log.d("TweetTopics", "ImageView.onLongClick");
					//InfoLink il = (InfoLink)img.getTag();
		    		//mTweetTopicsCore.goToLink(il);
					return true;
				}
				
			});
		}
	}
	
	private void addImage(InfoLink info) {
				
		int n = viewHolder.tweetPhotoLayout.getChildCount()+1;
		
		ImageView img = new ImageView(viewHolder.tweetPhotoLayout.getContext());
		
		int px = Utils.dip2px(context, 40);
        
        LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, px);
        ll.gravity = Gravity.CENTER;
        ll.setMargins(5, 2, 5, 2);
        img.setLayoutParams(ll);
        
        img.setAdjustViewBounds(true);
		
		img.setBackgroundResource(R.drawable.image_link_background);
		img.setPadding(0, 2, 0, 2);
		if (info.getType()==2) { // es un enlace
			int resID = getResources().getIdentifier(Utils.packageName+":drawable/icon_general_link_"+n, null, null);
			img.setImageResource(resID);
		} else if (info.getType()==3) { // es una busqueda QR
			img.setImageResource(R.drawable.icon_tweettopics_qr);
		} else if (info.getType()==4) { // es un tema
			img.setImageResource(R.drawable.icon_tweettopics_theme);
		} else {
			img.setImageBitmap(info.getBitmapThumb());
		}
		img.setTag(info);
		img.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View img) {
                Log.d("TweetTopics", "ImageView.onClick");
				//InfoLink il = (InfoLink)img.getTag();
				//mTweetTopicsCore.showSidebarLink(il, position);
			}
			
		});
		img.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View img) {
                Log.d("TweetTopics", "ImageView.onLongClick");
				//InfoLink il = (InfoLink)img.getTag();
	    		//mTweetTopicsCore.goToLink(il);
				return true;
			}
			
		});
		viewHolder.tweetPhotoLayout.addView(img);
	}
	
	public void setRow(final InfoTweet infoTweet, long lastTweetId, Context cnt, int pos) {

        context = cnt;

        ThemeManager themeManager = new ThemeManager(context);

		String html = "";
		
		if (infoTweet.getTextHTMLFinal().equals("")) {
			String[] in = Utils.toHTMLTyped(context, infoTweet.getText(), infoTweet.getTextURLs());
			html = in[1];
            infoTweet.setTextFinal(in[0]);
            infoTweet.setTextHTMLFinal(in[1]);
		} else {
			html = infoTweet.getTextHTMLFinal();
		}
		
		position = pos;
		viewHolder = (TweetsAdapter.ViewHolder) this.getTag();
		
		viewHolder.statusText.setTextColor(Color.parseColor("#"+themeManager.getStringColor("color_tweet_text")));
		viewHolder.sourceText.setTextColor(Color.parseColor("#"+themeManager.getStringColor("color_tweet_source")));
		viewHolder.retweetUser.setTextColor(Color.parseColor("#"+themeManager.getStringColor("color_tweet_retweet")));
		viewHolder.screenName.setTextColor(Color.parseColor("#"+themeManager.getStringColor("color_tweet_usename")));
		viewHolder.dateText.setTextColor(Color.parseColor("#"+themeManager.getStringColor("color_tweet_date")));
		
		
		if (infoTweet.isLastRead()) {
			BitmapDrawable bmp = (BitmapDrawable)cnt.getResources().getDrawable(R.drawable.readafter_tile);
			bmp.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
			viewHolder.lastReadLayout.setBackgroundDrawable(bmp);
		} else {
			viewHolder.lastReadLayout.setBackgroundColor(Color.TRANSPARENT);
		}
		
		if (TweetTopicsCore.isTypeList(TweetTopicsCore.TYPE_LIST_RETWEETS)) {
			if (infoTweet.getRetweetCount()>0) {
				viewHolder.tagAvatar.setImageBitmap(Utils.getBitmapNumber(cnt, (int)infoTweet.getRetweetCount(), Color.RED, Utils.TYPE_BUBBLE, 12));
			} else {
				viewHolder.tagAvatar.setImageBitmap(null);
			}
		}
		
		if (infoTweet.hasGeoLocation()) {
			viewHolder.tagMap.setVisibility(View.VISIBLE);
		} else {
			viewHolder.tagMap.setVisibility(View.GONE);
		}
		
		if (infoTweet.hasConversation()) {
			viewHolder.tagConversation.setVisibility(View.VISIBLE);
		} else {
			viewHolder.tagConversation.setVisibility(View.GONE);
		}
		
		viewHolder.statusText.setText(Html.fromHtml(html));
		viewHolder.statusText.setTextSize(PreferenceUtils.getSizeText(cnt));
		
		int typeInfo = Integer.parseInt(Utils.preference.getString("prf_username_right", "2"));
		String data = "";
		if (typeInfo == 2) {
			if (infoTweet.isRetweet()) {
				data = Html.fromHtml(infoTweet.getSourceRetweet()).toString();
			} else {
				data = Html.fromHtml(infoTweet.getSource()).toString();
			}
		} else if (typeInfo == 3) {
			data = infoTweet.getFullname();
		}
		viewHolder.sourceText.setText(data);
		viewHolder.sourceText.setTextSize(PreferenceUtils.getSizeTitles(cnt)-1);
		
		if (infoTweet.isRetweet()) {
			viewHolder.retweetLayout.setVisibility(View.VISIBLE);
			viewHolder.screenName.setText(infoTweet.getUsernameRetweet());
			viewHolder.retweetUser.setText(infoTweet.getUsername());
			viewHolder.retweetText.setText(R.string.retweet_by);
			viewHolder.retweetAvatar.setVisibility(View.VISIBLE);
		} else {
			viewHolder.screenName.setText(infoTweet.getUsername());
			viewHolder.retweetLayout.setVisibility(View.GONE);
		}
		/*
		if (TweetTopicsCore.mTypeLastColumn == TweetTopicsCore.DIRECTMESSAGES) {
			if (!infoTweet.getToUsername().equals("") && !infoTweet.getToUsername().equals(currentUser)) {
				viewHolder.retweetLayout.setVisibility(View.VISIBLE);
				viewHolder.retweetUser.setText(infoTweet.getToUsername());
				viewHolder.retweetText.setText(R.string.sent_to);
				viewHolder.retweetAvatar.setVisibility(View.GONE);
			}			
		}
		  */
		viewHolder.screenName.setTextSize(PreferenceUtils.getSizeTitles(cnt));
				
		viewHolder.dateText.setText(infoTweet.getTime(context));		
		viewHolder.dateText.setTextSize(PreferenceUtils.getSizeTitles(cnt)-4);

		if (null != latestLoadTask) {
			if (latestLoadTasks.contains(latestLoadTask)) latestLoadTasks.remove(latestLoadTask);
			latestLoadTask.cancel(true);
		}
		
		
		// buscar avatar en la cache de memoria del programa
		
		mUrlAvatar = infoTweet.getUrlAvatar();
		mRetweetUrlAvatar = infoTweet.getUrlAvatarRetweet();
		
		ArrayList<String> searchAvatars = new ArrayList<String>();
		ArrayList<String> searchImages = new ArrayList<String>();
		
		isRetweet = infoTweet.isRetweet(); 
		
		if (infoTweet.isRetweet()) {
			hasAvatar = this.searchAvatar(mUrlAvatar, viewHolder.retweetAvatar);
			if (!hasAvatar) {
				searchAvatars.add(mUrlAvatar);
			}
			hasRetweetAvatar = this.searchAvatar(mRetweetUrlAvatar, viewHolder.avatarView);
			if (!hasRetweetAvatar) {
				searchAvatars.add(mRetweetUrlAvatar);
			}
			viewHolder.avatarView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
                    Log.d("TweetTopics", "AvatarView.onClick");
					//mTweetTopicsCore.loadSidebarUser(infoTweet.getUsernameRetweet());
				}
				
			});
		} else {
			hasAvatar = this.searchAvatar(mUrlAvatar, viewHolder.avatarView);
			if (!hasAvatar) {
				searchAvatars.add(mUrlAvatar);
			}
			viewHolder.avatarView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
                    Log.d("TweetTopics", "AvatarView.onClick");
					//mTweetTopicsCore.loadSidebarUser(infoTweet.getUsername());
				}
				
			});
		}
		
		// buscar imagenes de los tweets
		
		mPosImages.clear();
		
		if (viewHolder.tweetPhotoLayout.getChildCount()>0)	viewHolder.tweetPhotoLayout.removeAllViews();
		viewHolder.tweetPhotoLayout.setVisibility(View.GONE);
		
		boolean hasLink = false;
		boolean hasCacheLink = false;
		int typeLinks = Integer.parseInt(Utils.preference.getString("prf_links", "3"));
		
		//if ( typeLinks != 1 ) {
	
			ArrayList<String> links = Utils.pullLinksHTTP(infoTweet.getText(), infoTweet.getContentURLs());
			
			for (String link : links) {
				hasLink = true;
							
				if (!this.searchImage(link)) {
					searchImages.add(link);
				} else {
					hasCacheLink = true;
				}
			}
		//}
			

		if ( typeLinks == 3 ) {
			if (hasLink) {
				viewHolder.tweetPhotoLayout.setVisibility(View.VISIBLE);
			} else {
				viewHolder.tweetPhotoLayout.setVisibility(View.GONE);
			}
		} else if ( typeLinks == 2 ) {
			if (hasCacheLink) {
				viewHolder.tweetPhotoLayout.setVisibility(View.VISIBLE);
			} else {
				viewHolder.tweetPhotoLayout.setVisibility(View.GONE);
			}
		} else {
			viewHolder.tweetPhotoLayout.setVisibility(View.GONE);
		}
		
		if (searchImages.size()+searchAvatars.size()>0) {
            //TODO: Revisar este código
			/*if (!tt.isFlinging()) {
				latestLoadTask = new LoadImageAsyncTask(getContext(), this, searchAvatars, searchImages).execute();
			} else {
				latestLoadTask = new LoadImageAsyncTask(getContext(), this, searchAvatars, searchImages);
				latestLoadTasks.add(latestLoadTask);
			}*/
		}

	}
	
	static ArrayList<AsyncTask<String, Void, Void>> latestLoadTasks = new ArrayList<AsyncTask<String, Void, Void>>();
	
	static public void executeLoadTasks() {
		//Log.d(Utils.TAG, "latestLoadTasks: " + latestLoadTasks.size());
		for (AsyncTask<String, Void, Void> task : latestLoadTasks) {
			try {
				task.execute();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		latestLoadTasks.clear();
	}
	
	public void setStatus(final Status status, int pos) {
		
		String text = status.getText();
        ThemeManager themeManager = new ThemeManager(context);
		
		position = pos;
		
		viewHolder = (TweetsAdapter.ViewHolder) this.getTag();
        
        viewHolder.statusText.setTextColor(Color.parseColor("#"+themeManager.getStringColor("color_tweet_text")));
        viewHolder.sourceText.setTextColor(Color.parseColor("#"+themeManager.getStringColor("color_tweet_source")));
        viewHolder.retweetUser.setTextColor(Color.parseColor("#"+themeManager.getStringColor("color_tweet_retweet")));
        viewHolder.screenName.setTextColor(Color.parseColor("#"+themeManager.getStringColor("color_tweet_usename")));
        viewHolder.dateText.setTextColor(Color.parseColor("#"+themeManager.getStringColor("color_tweet_date")));
		
		viewHolder.screenName.setText(status.getUser().getScreenName());
        viewHolder.screenName.setTextSize(PreferenceUtils.getSizeTitles(context));

		viewHolder.statusText.setText(Html.fromHtml(Utils.toHTML(context, text)));
        viewHolder.statusText.setTextSize(PreferenceUtils.getSizeText(context));
		
		if (status.getGeoLocation()!=null) {
			viewHolder.tagMap.setVisibility(View.VISIBLE);
		} else {
			viewHolder.tagMap.setVisibility(View.GONE);
		}
		
		int typeInfo = Integer.parseInt(Utils.preference.getString("prf_username_right", "2"));
		String data = "";
		if (typeInfo == 2) {
			data = Html.fromHtml(status.getSource()).toString();
		} else if (typeInfo == 3) {
			data = status.getUser().getName();
		}

		viewHolder.sourceText.setText(data);
        viewHolder.sourceText.setTextSize(PreferenceUtils.getSizeTitles(context)-1);
		
		viewHolder.avatarView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
                Log.d("TweetTopics", "AvatarView.onClick");
				//mTweetTopicsCore.loadSidebarUser(status.getUser().getScreenName());
			}
			
		});
				
		viewHolder.dateText.setText(Utils.timeFromTweet(context, status.getCreatedAt()));
        viewHolder.dateText.setTextSize(PreferenceUtils.getSizeTitles(context)-4);
		
		viewHolder.retweetLayout.setVisibility(View.GONE);
		
		if (viewHolder.tweetPhotoLayout.getChildCount()>0)	viewHolder.tweetPhotoLayout.removeAllViews();
		

		if (null != latestLoadTask) {
			latestLoadTask.cancel(true);
		}
		
		// buscar avatar en la cache de memoria del programa
				
		mUrlAvatar = status.getUser().getProfileImageURL().toString();
		
		ArrayList<String> searchAvatars = new ArrayList<String>();
		ArrayList<String> searchImages = new ArrayList<String>();

		if (!this.searchAvatar(mUrlAvatar, viewHolder.avatarView)) {
			searchAvatars.add(mUrlAvatar);
		}
		
		// buscar imagenes de los tweets
		
		mPosImages.clear();
		
		if (viewHolder.tweetPhotoLayout.getChildCount()>0)	viewHolder.tweetPhotoLayout.removeAllViews();
		
		boolean hasLink = false;
		boolean hasCacheLink = false;
		int typeLinks = Integer.parseInt(Utils.preference.getString("prf_links", "3"));
		
		//if ( typeLinks != 1 ) {
	
			ArrayList<String> links = Utils.pullLinksHTTP(text);
			
			for (String link : links) {
				hasLink = true;
				if (!this.searchImage(link)) {
					searchImages.add(link);
				} else {
					hasCacheLink = true;
				}
			}
		//}
			
		
		if ( typeLinks == 3 ) {
			if (hasLink) {
				viewHolder.tweetPhotoLayout.setVisibility(View.VISIBLE);
			} else {
				viewHolder.tweetPhotoLayout.setVisibility(View.GONE);
			}
		} else if ( typeLinks == 2 ) {
			if (hasCacheLink) {
				viewHolder.tweetPhotoLayout.setVisibility(View.VISIBLE);
			} else {
				viewHolder.tweetPhotoLayout.setVisibility(View.GONE);
			}
		} else {
			viewHolder.tweetPhotoLayout.setVisibility(View.GONE);
		}
		
		if (searchImages.size()+searchAvatars.size()>0) {
			latestLoadTask = new LoadImageAsyncTask(getContext(), this, searchAvatars, searchImages).execute();
		}
		
	}

	public void imageLoading() {}
	public void imageLoadCancelled() {}
	public void imageLoaded(Void v) {

		if (isRetweet) {
			if (!hasAvatar) {
				this.searchAvatar(mUrlAvatar, viewHolder.retweetAvatar);
			}
			if (!hasRetweetAvatar) {
				this.searchAvatar(mRetweetUrlAvatar, viewHolder.avatarView);
			}
		} else {
			if (!hasAvatar) {
				this.searchAvatar(mUrlAvatar, viewHolder.avatarView);
			}
		}
		
		int typeLinks = Integer.parseInt(Utils.preference.getString("prf_links", "3"));
		
		if (typeLinks!=1) {
		
			Iterator it = mPosImages.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry e = (Map.Entry)it.next();
				String url = e.getKey().toString();
				int pos = Integer.parseInt(e.getValue().toString());
				if (CacheData.getCacheImages().containsKey(url)) {
					modImage(pos, CacheData.getCacheImages().get(url));
				}			
			}
			
		}
	}
}
