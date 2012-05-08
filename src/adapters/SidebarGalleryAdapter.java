package adapters;

import greendroid.widget.PagedAdapter;
import infos.CacheData;
import infos.InfoLink;
import infos.InfoUsers;

import java.util.ArrayList;

import task.LoadLinkAsyncTask;
import task.LoadLinkAsyncTask.LoadLinkAsyncAsyncTaskResponder;
import task.LoadUserAsyncTask;
import task.LoadUserAsyncTask.LoadUserAsyncAsyncTaskResponder;
import android.graphics.Bitmap;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cyrilmottier.android.greendroid.R;
import com.javielinux.tweettopics.ThemeManager;
import com.javielinux.tweettopics.TweetTopics;
import com.javielinux.tweettopics.TweetTopicsCore;
import com.javielinux.tweettopics.Utils;

public class SidebarGalleryAdapter extends PagedAdapter {
	
	private ArrayList<String> mItems;
	private TweetTopics mTweetTopics;
	private TweetTopicsCore mTweetTopicsCore; 
	private int mPositionTweet = -1;
	
	//private ArrayList<LoadUserAsyncTask> mTasksImages = new ArrayList<LoadUserAsyncTask>();
	
	public SidebarGalleryAdapter(TweetTopicsCore mTweetTopicsCore, int mPositionTweet, ArrayList<String> statii) {
		//super(mTweetTopics, android.R.layout.simple_list_item_1, statii);
		this.mTweetTopics = mTweetTopicsCore.getTweetTopics();
		this.mTweetTopicsCore = mTweetTopicsCore;
		this.mPositionTweet = mPositionTweet;
		mItems = statii;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final String link = (String)getItem(position);
		
		if (link.startsWith("#")) {
			View v = View.inflate(mTweetTopics, R.layout.sidebar_hashtag_gallery, null);
			TextView text = (TextView)v.findViewById(R.id.text_gal);
			text.setText(link);
			ImageView im = (ImageView)v.findViewById(R.id.image_gal);
			im.setImageDrawable(new ThemeManager(mTweetTopics).getDrawable("icon_hash"));
			v.setLayoutParams(new Gallery.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
			v.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					mTweetTopicsCore.showDialogHashTag(link);
				}
			});
			return v;
		} else if (link.startsWith("@")) {
			View v = View.inflate(mTweetTopics, R.layout.sidebar_user_gallery, null);
			TextView text = (TextView)v.findViewById(R.id.text_gal);
			text.setText(link);
			ImageView im = (ImageView)v.findViewById(R.id.image_gal);
			ProgressBar loadProgress = (ProgressBar)v.findViewById(R.id.load_progress);
			LinearLayout ll_data = (LinearLayout)v.findViewById(R.id.data_gal);
			TextView tv_tweets = (TextView)v.findViewById(R.id.text_n_tweets);
			TextView tv_followers = (TextView)v.findViewById(R.id.text_followers);
			TextView tv_following = (TextView)v.findViewById(R.id.text_following);
			InfoUsers user = CacheData.getCacheUser(link.replace("@", ""));
			if (user!=null) {
				loadProgress.setVisibility(View.GONE);
				im.setVisibility(View.VISIBLE);
				ll_data.setVisibility(View.VISIBLE);
				tv_tweets.setText(user.getTweets()+"");
				tv_followers.setText(user.getFollowers()+"");
				tv_following.setText(user.getFollowing()+"");
				if (user.getAvatar()!=null) {
					im.setImageBitmap(Bitmap.createScaledBitmap(user.getAvatar(), Utils.AVATAR_LARGE, Utils.AVATAR_LARGE, true));
				}
			} else {
				loadProgress.setVisibility(View.VISIBLE);
				im.setVisibility(View.GONE);
				ll_data.setVisibility(View.GONE);
				im.setImageResource(R.drawable.avatar);
				new LoadUserAsyncTask(mTweetTopics, new LoadUserAsyncAsyncTaskResponder() {
	
					@Override
					public void userLoading() {					
					}
	
					@Override
					public void userCancelled() {
					}
	
					@Override
					public void userLoaded(InfoUsers iu) {
						try {
							CacheData.addCacheUsers(iu);
							notifyDataSetChanged();
						} catch (Exception e) {
						}
					}
					
				}).execute(link.replace("@", ""));		
			}
			
			v.setLayoutParams(new Gallery.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
			
			v.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					mTweetTopicsCore.loadSidebarUser(link.replace("@", ""));
				}
			});
			return v;
		} else {
			InfoLink il = CacheData.getInfoLinkCaches(link);
			View v = null;
			if (il!=null) {
				if ( (il.getType() == Utils.TYPE_LINK_IMAGE) || (il.getType() == Utils.TYPE_LINK_VIDEO) ) {
					if (il.isExtensiveInfo()) {
						v = View.inflate(mTweetTopics, R.layout.sidebar_image_gallery, null);
						ImageView im = (ImageView)v.findViewById(R.id.image_gal);
						im.setImageBitmap(il.getBitmapLarge());
						v.setLayoutParams(new Gallery.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
					} else {
						new LoadLinkAsyncTask(mTweetTopics, new LoadLinkAsyncAsyncTaskResponder() {
							@Override
							public void linkLoading() {
							}

							@Override
							public void linkCancelled() {	
							}

							@Override
							public void linkLoaded(InfoLink newIl) {
								notifyDataSetChanged();
							}
						}).execute(il);
						v = getLoadingView();
					}
				} else if (il.getType() == Utils.TYPE_LINK_GENERAL) {
					if (il.isExtensiveInfo()) {
						v = View.inflate(mTweetTopics, R.layout.sidebar_link_gallery, null);
						TextView title = (TextView)v.findViewById(R.id.text_gal_title);
						title.setText(il.getTitle());
						TextView tlink = (TextView)v.findViewById(R.id.text_gal_link);
						tlink.setText(il.getOriginalLink());
						v.setLayoutParams(new Gallery.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
					} else {
						new LoadLinkAsyncTask(mTweetTopics, new LoadLinkAsyncAsyncTaskResponder() {
							@Override
							public void linkLoading() {
							}

							@Override
							public void linkCancelled() {	
							}

							@Override
							public void linkLoaded(InfoLink newIl) {
								notifyDataSetChanged();
							}
						}).execute(il);
						v = getLoadingView();
					}
				}
			}
			
			if (v==null) {
				TextView t = new TextView(mTweetTopics);
				t.setGravity(Gravity.CENTER);
				t.setText(link);
				t.setLayoutParams(new Gallery.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
				v = t;
			}
			
			if (v!=null) {
				v.setClickable(true);
				v.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						InfoLink il = CacheData.getInfoLinkCaches(link);
						if (il!=null) {
							mTweetTopicsCore.showSidebarLink(il, mPositionTweet);
						} else {
							mTweetTopicsCore.goToLink(link);
						}
					}
				});
			}
			return v;
		}

	}
	
	public View getLoadingView() {
		View v = mTweetTopics.getLayoutInflater().inflate(R.layout.loading, null);
		v.setLayoutParams(new Gallery.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		return v;
	}

	@Override
	public int getCount() {
		return mItems.size();
	}

	@Override
	public Object getItem(int position) {
		return mItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}
	

}