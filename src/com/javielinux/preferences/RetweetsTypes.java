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

package com.javielinux.preferences;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.javielinux.adapters.RetweetsTypesAdapter;
import com.javielinux.tweettopics2.BaseActivity;
import com.javielinux.tweettopics2.R;
import com.javielinux.utils.Utils;

public class RetweetsTypes extends BaseActivity {
	
	private static final int ADD_ID = Menu.FIRST;
	private static final int BACK_ID = Menu.FIRST+1;
	
	private static final int DIALOG_ITEM = 0;
		
	private ListView mListView;
	private TextView mNoQuietWords;
	
	private long mCurrentId = 0;
	
	private RetweetsTypesAdapter mAdapter;
	
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case DIALOG_ITEM:
            return new AlertDialog.Builder(this)
            .setTitle(R.string.actions)
            .setItems(R.array.items_quietwords, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if (which==0) {
                    	deleteItem();
                    }
                }
            })
            .create();     
        }
        return null;
    }
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try {
        	DataFramework.getInstance().open(this, Utils.packageName);
        } catch (Exception e) {
        	e.printStackTrace();
        }
        
        setContentView(R.layout.tweetquick_list);
        
        mListView = (ListView) this.findViewById(R.id.list_tweetquick);
        
        mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				mCurrentId = mAdapter.getItem(position).getId();
				showDialog(DIALOG_ITEM);				
			}
        });
        
        mNoQuietWords = (TextView) this.findViewById(R.id.empty);
        
        refresh();
        
    }
    
    private void newItem() {
    	    	
    	final EditText et = new EditText(this);
    	
    	
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.title_new_retweet);
		builder.setMessage(R.string.desc_new_retweet);
		builder.setView(et);
		builder.setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Entity ent = new Entity("types_retweets");
				ent.setValue("phrase", et.getText().toString());
		    	ent.save();
		    	refresh();
	
			}
			
		});
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {	
			}
			
		});
		AlertDialog alert = builder.create();
		alert.show();
    }
    

    private void deleteItem() {
    	Entity ent = new Entity("types_retweets", mCurrentId);
    	ent.delete();
    	refresh ();
    }
    
    
    private void refresh () {

    	mAdapter = new RetweetsTypesAdapter(this, DataFramework.getInstance().getEntityList("types_retweets"));
    	
    	if (mAdapter.getCount()<=0) {
    		mNoQuietWords.setVisibility(View.VISIBLE);
    	} else {
    		mNoQuietWords.setVisibility(View.GONE);
    		mListView.setAdapter(mAdapter);
    	}

    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, ADD_ID, 0,  R.string.add)
			.setIcon(android.R.drawable.ic_menu_add);
        menu.add(0, BACK_ID, 0,  R.string.back)
			.setIcon(android.R.drawable.ic_menu_directions);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
        case ADD_ID:
        	newItem();
            return true;
        case BACK_ID:
        	setResult(RESULT_OK);
			finish();
            return true;
        }
       
        return super.onOptionsItemSelected(item);
	}
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        DataFramework.getInstance().close();
    }
    
}