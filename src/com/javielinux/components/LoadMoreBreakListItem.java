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
