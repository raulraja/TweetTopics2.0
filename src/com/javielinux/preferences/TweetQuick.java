package com.javielinux.preferences;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.javielinux.adapters.TweetQuickAdapter;
import com.javielinux.tweettopics2.BaseActivity;
import com.javielinux.tweettopics2.R;
import com.javielinux.utils.DialogUtils.PersonalDialogBuilder;
import com.javielinux.utils.Utils;

import java.util.Locale;

public class TweetQuick extends BaseActivity {
	
	private static final int ADD_ID = Menu.FIRST;
	private static final int HOWTO_ID = Menu.FIRST+1;
	private static final int BACK_ID = Menu.FIRST+2;
	
	private static final int DIALOG_ITEM = 0;
	
	public static final int ACTIVITY_NEWEDITTWEETQUICK = 0;
	
	private ListView mListView;
	private TextView mNoTweetQuick;
	
	private long mCurrentId = 0;
	
	private TweetQuickAdapter mAdapter;
	
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case DIALOG_ITEM:
            return new AlertDialog.Builder(this)
            .setTitle(R.string.actions)
            .setItems(R.array.items_tweetquick, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if (which==0) {
                    	editItem();
                    } else if (which==1) {
                    	resetCountItem();
                    } else if (which==2) {
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
        
        setTitle("TweetQuick");
        
        setContentView(R.layout.tweetquick_list);
        
        mListView = (ListView) this.findViewById(R.id.list_tweetquick);
        
        mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				mCurrentId = mAdapter.getItem(position).getId();
				showDialog(DIALOG_ITEM);				
			}
        });
        
        mNoTweetQuick = (TextView) this.findViewById(R.id.empty);
        
        refresh();
        
    }
    
    private void newItem() {
    	Intent newquick = new Intent(TweetQuick.this, NewEditTweetQuick.class);
		startActivityForResult(newquick, ACTIVITY_NEWEDITTWEETQUICK);
    }
    
    private void editItem() {
    	Intent newquick = new Intent(TweetQuick.this, NewEditTweetQuick.class);
    	newquick.putExtra(DataFramework.KEY_ID, mCurrentId);
		startActivityForResult(newquick, ACTIVITY_NEWEDITTWEETQUICK);
    }
    
    private void deleteItem() {
    	Entity ent = new Entity("tweet_quick", mCurrentId);
    	ent.delete();
    	refresh();
    }
    
    private void resetCountItem() {
    	Entity ent = new Entity("tweet_quick", mCurrentId);
    	ent.setValue("count", 1);
    	ent.save();
    	refresh();
    }
    
    private void refresh () {

    	mAdapter = new TweetQuickAdapter(this, DataFramework.getInstance().getEntityList("tweet_quick"));
    	
    	if (mAdapter.getCount()<=0) {
    		mNoTweetQuick.setVisibility(View.VISIBLE);
    	} else {
    		mNoTweetQuick.setVisibility(View.GONE);
    		mListView.setAdapter(mAdapter);
    	}

    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, ADD_ID, 0,  R.string.add)
			.setIcon(android.R.drawable.ic_menu_add);
        menu.add(0, HOWTO_ID, 0,  R.string.howto_use)
			.setIcon(android.R.drawable.ic_menu_view);
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
        case HOWTO_ID:
			String file = "howto_tweetquick.txt"; 
			if (Locale.getDefault().getLanguage().equals("es")) {
				file = "howto_tweetquick_es.txt";
			}
			
			try {
				AlertDialog builder = PersonalDialogBuilder.create(this, this.getString(R.string.howto_use), file);
				builder.show();
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
            return true;
        case BACK_ID:
        	setResult(RESULT_OK);
			finish();
            return true;
        }
       
        return super.onOptionsItemSelected(item);
	}
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, 
                                    Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
               
        switch (requestCode){
        	case ACTIVITY_NEWEDITTWEETQUICK:
        		if( resultCode != 0 ) {
        			refresh();
        		}
        	break;
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        DataFramework.getInstance().close();
    }
    
}