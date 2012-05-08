package sidebar;

import greendroid.widget.PageIndicator;
import greendroid.widget.PageIndicator.DotType;
import greendroid.widget.PagedView;
import infos.InfoTweet;

import java.util.ArrayList;

import adapters.SidebarGalleryAdapter;
import android.content.res.Resources;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cyrilmottier.android.greendroid.R;
import com.javielinux.tweettopics.TweetTopicsCore;
import com.javielinux.tweettopics.Utils;

public class SidebarGalleryLinks {
	private View mView;
	
	private Resources mResources;
	
	private TweetTopicsCore mTweetTopicsCore; 
	
	private SidebarGalleryAdapter mAdapter;
	
	private int mPositionTweet = -1;
	private PagedView mPagedView;
	

	private PageIndicator mPageIndicatorLeft, mPageIndicatorRight;
	
	public SidebarGalleryLinks(TweetTopicsCore ttc, int pos) {
		
		mPositionTweet = pos;
		
		mTweetTopicsCore = ttc;
		
		mResources = mTweetTopicsCore.getTweetTopics().getResources();
				
		ArrayList<String> links = new ArrayList<String>();
		
		InfoTweet it = mTweetTopicsCore.getCurrentInfoTweet();
		if (it!=null) {
			links = Utils.pullLinks(it.getText(), it.getContentURLs());
		}
		
		if (links.size()<=0) {
			TextView noLinks = new TextView(mTweetTopicsCore.getTweetTopics());
			noLinks.setText(mResources.getString(R.string.no_links));
			noLinks.setGravity(Gravity.CENTER);
			mView = noLinks;
		} else {
			
			LinearLayout ll = new LinearLayout(mTweetTopicsCore.getTweetTopics());
			ll.setOrientation(LinearLayout.VERTICAL);
			
			ll.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
			
			mPagedView = new PagedView(mTweetTopicsCore.getTweetTopics());
			
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, 1);
	    	lp.gravity = Gravity.CENTER;
	    	mPagedView.setLayoutParams(lp);
	    	
	    	mAdapter = new SidebarGalleryAdapter(mTweetTopicsCore, mPositionTweet, links);
	    	mPagedView.setAdapter(mAdapter);
	    	
	    	mPagedView.setClickable(true);
	    	mPagedView.setEnabled(true);
	    	/*
	    	mPagedView.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View arg0, MotionEvent me) {

					if (me.getAction()==MotionEvent.ACTION_DOWN) {
						hasMove = false;
					}
					
					if (me.getAction()==MotionEvent.ACTION_MOVE) {
						hasMove = true;
					}
					
					if (me.getAction()==MotionEvent.ACTION_UP && !hasMove) {
						if (mPagedView.getCurrentPage()>=0) {
							String link = (String) mAdapter.getItem(mPagedView.getCurrentPage());
							if (link.startsWith("#")) {
								mTweetTopicsCore.showDialogHashTag(link);
							} else if (link.startsWith("@")) {
								mTweetTopicsCore.loadSidebarUser(link.replace("@", ""));
							} else {
								InfoLink il = Utils.getInfoLinkCaches(link);
								if (il!=null) {
									mTweetTopicsCore.showSidebarLink(il, mPositionTweet);
								} else {
									mTweetTopicsCore.goToLink(link);
								}
							}
						}
					}
					
					return false;
				}
	    		
	    	});
*/
			mPagedView.setOnPageChangeListener(new PagedView.OnPagedViewChangeListener() {

				@Override
				public void onPageChanged(PagedView pagedView, int previousPage, int newPage) {
					//Utils.showMessage(mTweetTopicsCore.getTweetTopics(), "pagina: "+ newPage);
					mPageIndicatorLeft.setActiveDot(newPage);
					mPageIndicatorRight.setActiveDot(mAdapter.getCount()-1-newPage);
					/*InfoTweet it = mTweetTopicsCore.getCurrentInfoTweet();
					try {
						String search = (String)mAdapter.getItem(newPage);
						URLContent u = Utils.searchContent(it.getContentURLs(), search);
						if (u != null) {
							int type = Integer.parseInt(Utils.getPreference(mTweetTopicsCore.getTweetTopics()).getString("prf_show_links", "2"));
							if (type==1) {
								search = u.normal;
							} else if (type==2) {
								search = u.display;
							} else if (type==3) {
								search = u.expanded;
							}
						}
						
						Sidebar.text_tweet_sidebar.setText(Html.fromHtml(Utils.toHTML(mTweetTopicsCore.getTweetTopics(), it.getTextFinal(), search)));
					} catch (Exception e) {
						e.printStackTrace();
					}*/
				}

				@Override
				public void onStartTracking(PagedView pagedView) {
					
				}

				@Override
				public void onStopTracking(PagedView pagedView) {
					
				}
				
			});
			
			ll.addView(mPagedView);
		
			LinearLayout llInd = new LinearLayout(mTweetTopicsCore.getTweetTopics());
			llInd.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			llInd.setOrientation(LinearLayout.HORIZONTAL);
			
			mPageIndicatorLeft = new PageIndicator(mTweetTopicsCore.getTweetTopics());
			mPageIndicatorLeft.setGravity(Gravity.RIGHT);
			mPageIndicatorLeft.setDotCount(mAdapter.getCount());
			mPageIndicatorLeft.setDotType(DotType.MULTIPLE);
			mPageIndicatorLeft.setActiveDot(0);
			
			LinearLayout.LayoutParams lpIndLeft = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			lpIndLeft.weight = 1;
			lpIndLeft.gravity = Gravity.RIGHT;
			
			llInd.addView(mPageIndicatorLeft, lpIndLeft);
			
			
			ImageView imSelector = new ImageView(mTweetTopicsCore.getTweetTopics());
			imSelector.setImageResource(R.drawable.selected_indicator);
			
			LinearLayout.LayoutParams llIndCenter = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			llIndCenter.gravity = Gravity.TOP;
			
			llInd.addView(imSelector, llIndCenter);
			
			mPageIndicatorRight = new PageIndicator(mTweetTopicsCore.getTweetTopics());
			mPageIndicatorRight.setGravity(Gravity.LEFT);
			mPageIndicatorRight.setDotCount(mAdapter.getCount());
			mPageIndicatorRight.setDotType(DotType.MULTIPLE);
			mPageIndicatorRight.setActiveDot(mAdapter.getCount()-1);
			
			LinearLayout.LayoutParams lpIndRight = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			lpIndRight.weight = 1;
			lpIndRight.gravity = Gravity.LEFT;
			
			llInd.addView(mPageIndicatorRight, lpIndRight);
			
			
			ll.addView(llInd);
			
			//if (mAdapter.getCount()>0)Sidebar.text_tweet_sidebar.setText(Html.fromHtml(Utils.toHTML(mTweetTopicsCore.getTweetTopics(), it.getTextFinal(), (String)mAdapter.getItem(0))));
			
			mView = ll;
			
			/*
			Gallery g = new Gallery(mTweetTopicsCore.getTweetTopics());
			LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, 1);
	    	ll.gravity = Gravity.CENTER;
			g.setLayoutParams(ll);
			g.setSpacing(0);
			mAdapter = new SidebarGalleryAdapter(mTweetTopicsCore.getTweetTopics(), links);
    		g.setAdapter(mAdapter);
    		g.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					String link = mAdapter.getItem(position);
					if (link.startsWith("#")) {
						mTweetTopicsCore.showDialogHashTag(link);
					} else if (link.startsWith("@")) {
						mTweetTopicsCore.loadSidebarUser(link.replace("@", ""));
					} else {
						InfoLink il = Utils.getInfoLinkCaches(link);
						if (il!=null) {
							mTweetTopicsCore.showSidebarLink(il, mPositionTweet);
						} else {
							mTweetTopicsCore.goToLink(link);
						}
					}
					
				}
    			
    		});
    		g.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
					//Utils.showMessage(mTweetTopicsCore.getTweetTopics(), "estamos en: " + position);
					InfoTweet it = mTweetTopicsCore.getCurrentInfoTweet();
					Sidebar.text_tweet_sidebar.setText(Html.fromHtml(Utils.toHTML(mTweetTopicsCore.getTweetTopics(), it.getText(), mAdapter.getItem(position))));
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {
				}
    			
    		});
    		mView = g;
    		*/
		}
		
	}
	
	public View getView() {
		return mView;
	}
	
}
