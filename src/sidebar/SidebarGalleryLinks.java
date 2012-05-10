package sidebar;

import android.content.res.Resources;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import com.javielinux.tweettopics2.R;
import com.javielinux.tweettopics2.TweetTopicsCore;
import com.javielinux.tweettopics2.Utils;
import infos.InfoTweet;

import java.util.ArrayList;

public class SidebarGalleryLinks {
	private View mView;
	
	private Resources mResources;
	
	private TweetTopicsCore mTweetTopicsCore; 


	private int mPositionTweet = -1;
	
	public SidebarGalleryLinks(TweetTopicsCore ttc, int pos) {

		mPositionTweet = pos;
		
		mTweetTopicsCore = ttc;
		
		mResources = mTweetTopicsCore.getTweetTopics().getResources();
				
		ArrayList<String> links = new ArrayList<String>();
		
		InfoTweet it = mTweetTopicsCore.getCurrentInfoTweet();
		if (it!=null) {
			links = Utils.pullLinks(it.getText(), it.getContentURLs());
		}
		

        TextView noLinks = new TextView(mTweetTopicsCore.getTweetTopics());
        noLinks.setText(mResources.getString(R.string.no_links));
        noLinks.setGravity(Gravity.CENTER);
        mView = noLinks;

		
	}
	
	public View getView() {
		return mView;
	}
	
}
