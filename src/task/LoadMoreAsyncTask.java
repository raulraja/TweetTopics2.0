package task;

import adapters.RowResponseList;
import android.content.Context;
import android.os.AsyncTask;
import com.javielinux.tweettopics2.TweetTopicsCore;
import com.javielinux.twitter.ConnectionManager;
import twitter4j.DirectMessage;
import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.TwitterException;

import java.util.ArrayList;


public class LoadMoreAsyncTask extends AsyncTask<Void, Void, ArrayList<RowResponseList>> {
  
	private long targetId;
	private int mTypeList;
	private int mTypeLastColumn;
	private LoadMoreResponder responder;
	private Context mContext;
	  
	public interface LoadMoreResponder {
		public void loadingMoreStatuses();
		public void statusesLoaded(ArrayList<RowResponseList> result);
	}
  
  
	public LoadMoreAsyncTask(Context cnt, LoadMoreResponder responder, long targetId, int mTypeList, int mTypeLastColumn) {
		super();
		mContext = cnt;
		this.responder = responder;
		this.mTypeList = mTypeList;
		this.mTypeLastColumn = mTypeLastColumn;
		this.targetId = targetId;
	}

	@Override
	protected ArrayList<RowResponseList> doInBackground(Void...params) {
		
		ArrayList<RowResponseList> result = new ArrayList<RowResponseList>();

		try {
			ConnectionManager.getInstance().open(mContext);
			if (targetId>0) {
				if (mTypeList==TweetTopicsCore.TYPE_LIST_COLUMNUSER) {
					if (mTypeLastColumn==TweetTopicsCore.TIMELINE) {
						ResponseList<twitter4j.Status> statii = ConnectionManager.getInstance().getTwitter().getHomeTimeline(new Paging(1).maxId(targetId));
						if (statii.size()>1) {
							for (int i=1; i<statii.size(); i++) {
								result.add(new RowResponseList(statii.get(i)));
							}
						}
					} else if (mTypeLastColumn==TweetTopicsCore.MENTIONS) {
						ResponseList<twitter4j.Status> statii = ConnectionManager.getInstance().getTwitter().getMentions(new Paging(1).maxId(targetId));
						if (statii.size()>1) {
							for (int i=1; i<statii.size(); i++) {
								result.add(new RowResponseList(statii.get(i)));
							}
						}
					} else if (mTypeLastColumn==TweetTopicsCore.DIRECTMESSAGES) {
						ResponseList<DirectMessage> dms = ConnectionManager.getInstance().getTwitter().getDirectMessages(new Paging(1).maxId(targetId));
						if (dms.size()>1) {
							for (int i=1; i<dms.size(); i++) {
								result.add(new RowResponseList(dms.get(i)));
							}
						}
					}
				}
				if (mTypeList==TweetTopicsCore.TYPE_LIST_LISTUSERS) {
					ResponseList<twitter4j.Status> statii = ConnectionManager.getInstance().getTwitter().getUserListStatuses(
							TweetTopicsCore.mCurrentList.getId(), new Paging(1).maxId(targetId));
					if (statii.size()>1) {
						for (int i=1; i<statii.size(); i++) {
							result.add(new RowResponseList(statii.get(i)));
						}
					}
				}
			}
		} catch (TwitterException e) {
			//throw new RuntimeException("Unable to load timeline", e);
			e.printStackTrace();
		}
    
		return result;
	}

	@Override
	public void onPreExecute() {
		responder.loadingMoreStatuses();
	}
  
	@Override
	public void onPostExecute(ArrayList<RowResponseList> result) {
		responder.statusesLoaded(result);
	}


}
