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
import android.widget.LinearLayout;
import android.widget.TextView;
import com.androidquery.AQuery;
import com.javielinux.infos.InfoUsers;
import com.javielinux.tweettopics2.R;

public class AutoCompleteListItem extends LinearLayout {

    private AQuery aQuery;
	
	public AutoCompleteListItem(Context context, AttributeSet attrs) {
		super(context, attrs);
        aQuery = new AQuery(context);
	}
	
	public void setRow(InfoUsers item, String searchWord) {
		TextView name1 = (TextView)findViewById(R.id.ac_username1);
		name1.setText(searchWord);
		
		TextView name2 = (TextView)findViewById(R.id.ac_username2);
		if (item.getName().length()>searchWord.length()) {
			name2.setText(item.getName().substring(searchWord.length()));
		} else {
			name2.setText("");
		}

        aQuery.id(findViewById(R.id.ac_avatar)).image(item.getUrlAvatar(), true, true, 0, R.drawable.avatar, aQuery.getCachedImage(R.drawable.avatar), 0);

	}


}
