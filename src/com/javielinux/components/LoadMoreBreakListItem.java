package com.javielinux.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.javielinux.tweettopics2.R;

public class LoadMoreBreakListItem extends RelativeLayout {

	private TextView loadMoreText;
	private ProgressBar progressIndicator;

	public LoadMoreBreakListItem(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	public void onFinishInflate() {
		findViews();
	}
	
	public void showProgress() {
		progressIndicator.setVisibility(View.VISIBLE);
		showLoadingTextText();
	}

	public void hideProgress() {
		progressIndicator.setVisibility(View.GONE);
		showText();
	}

	public void showLoadingTextText() {
		String headerText = getResources().getString(R.string.loading);
		loadMoreText.setText(headerText);
	}

	public void showText() {
		String headerText = getResources().getString(R.string.load_more_footer);
		loadMoreText.setText(headerText);
	}

	public void showText(String date) {
		String headerText = getResources().getString(R.string.time_without_timeline, date);
		loadMoreText.setText(headerText);
	}
	
	private void findViews() {
		loadMoreText = (TextView)findViewById(R.id.load_more_text);
		progressIndicator = (ProgressBar)findViewById(R.id.load_more_progress);
	}

}
