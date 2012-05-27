package com.javielinux.tweettopics2;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;


public class TweetTopicsListView {

	private PullToRefreshListView mPullToRefreshListView;
	private ListView mListView;
	private boolean pullToRefresh;
	
	public TweetTopicsListView(TweetTopics cnt) {
		pullToRefresh = cnt.getBooleanPreference("prf_use_pulltorefresh", false);
		if (pullToRefresh) {
			mPullToRefreshListView = new PullToRefreshListView(cnt);
		} else {
			mListView = new ListView(cnt);
		}
	}
	 /*
	public boolean isPullToRefresh() {
		return pullToRefresh;
	}  */
	
	public void setOnRefreshListener(PullToRefreshBase.OnRefreshListener onRefreshListener) {
		if (pullToRefresh) {
			mPullToRefreshListView.setOnRefreshListener(onRefreshListener);
		}
	}
	
	public void onRefreshComplete() {
		if (pullToRefresh) {
			mPullToRefreshListView.onRefreshComplete();
		}
	}
	
	public void setOnItemClickListener(OnItemClickListener listener) {
		if (pullToRefresh) {
			mPullToRefreshListView.getRefreshableView().setOnItemClickListener(listener);
		} else {
			mListView.setOnItemClickListener(listener);
		}
	}
	
	public void setOnItemLongClickListener(OnItemLongClickListener listener) {
		if (pullToRefresh) {
			mPullToRefreshListView.getRefreshableView().setOnItemLongClickListener(listener);
		} else {
			mListView.setOnItemLongClickListener(listener);
		}
	}
	public void setOnScrollListener(AbsListView.OnScrollListener listener) {
		if (pullToRefresh) {
			mPullToRefreshListView.setOnScrollListener(listener);
		} else {
			mListView.setOnScrollListener(listener);
		}
	}
	
	
	public void setDivider(Drawable d) {
		if (pullToRefresh) {
			mPullToRefreshListView.getRefreshableView().setDivider(d);
		} else {
			mListView.setDivider(d);
		}
	}
	
	public void setDividerHeight(int h) {
		if (pullToRefresh) {
			mPullToRefreshListView.getRefreshableView().setDividerHeight(h);
		} else {
			mListView.setDividerHeight(h);
		}
	}
	
	public void setFadingEdgeLength(int l) {
		if (pullToRefresh) {
			mPullToRefreshListView.setFadingEdgeLength(l);
		} else {
			mListView.setFadingEdgeLength(l);
		}
	}
	
	public void setCacheColorHint(int c) {
		if (pullToRefresh) {
			mPullToRefreshListView.getRefreshableView().setCacheColorHint(c);
		} else {
			mListView.setCacheColorHint(c);
		}
	}
	
	public void addFooterView(View v) {
		try {
			if (pullToRefresh) {
				mPullToRefreshListView.getRefreshableView().addFooterView(v);
			} else {
				mListView.addFooterView(v);
			}
		} catch (ClassCastException e) {
			e.printStackTrace();
		}
	}
	
	public void removeFooterView(View v) {
		try {
			if (pullToRefresh) {
				mPullToRefreshListView.getRefreshableView().removeFooterView(v);
			} else {
				mListView.removeFooterView(v);
			}
		} catch (ClassCastException e) {
			e.printStackTrace();
		}
	}
	
	public int getFirstVisiblePosition() {
		if (pullToRefresh) {
			return mPullToRefreshListView.getRefreshableView().getFirstVisiblePosition();
		} else {
			return mListView.getFirstVisiblePosition();
		}
	}
	
	public void setAdapter(ListAdapter adapter) {
		if (pullToRefresh) {
			mPullToRefreshListView.getRefreshableView().setAdapter(adapter);
		} else {
			mListView.setAdapter(adapter);
		}
	}
	
	public void setSelection(int pos) {
		if (pullToRefresh) {
			mPullToRefreshListView.getRefreshableView().setSelection(pos);
		} else {
			mListView.setSelection(pos);
		}
	}
	/*
	public void setSelectionExact(int pos) {
		if (pullToRefresh) {
			mPullToRefreshListView.getAdapterView().setSelection(pos);
		} else {
			mListView.setSelection(pos);
		}
	}
	 */
	public View get() {
		if (pullToRefresh) {
			return mPullToRefreshListView;
		} else {
			return mListView;
		}
	}
	
}
