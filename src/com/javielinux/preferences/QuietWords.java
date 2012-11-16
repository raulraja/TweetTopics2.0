package com.javielinux.preferences;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.javielinux.adapters.QuietWordsAdapter;
import com.javielinux.tweettopics2.BaseActivity;
import com.javielinux.tweettopics2.R;
import com.javielinux.utils.CacheData;
import com.javielinux.utils.Utils;

public class QuietWords extends BaseActivity {
	
	private static final int ADD_ID = Menu.FIRST;
	private static final int BACK_ID = Menu.FIRST+1;
	
	private static final int DIALOG_ITEM = 0;
		
	private ListView mListView;
	private TextView mNoQuietWords;
	
	private long mCurrentId = 0;
	
	private QuietWordsAdapter mAdapter;
	
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
    	
    	View v = View.inflate(this, R.layout.new_word, null);
    	
    	final EditText et = (EditText)v.findViewById(R.id.word);
    	//final RadioButton rbUser = (RadioButton)v.findViewById(R.id.rb_user);
    	final RadioButton rbWord = (RadioButton)v.findViewById(R.id.rb_word);
    	final RadioButton rbUser = (RadioButton)v.findViewById(R.id.rb_user);
    	final RadioButton rbSource = (RadioButton)v.findViewById(R.id.rb_source);
    	
    	
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.title_quiet_words);
		builder.setMessage(R.string.desc_quiet_words);
		builder.setView(v);
		builder.setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Entity ent = new Entity("quiet");
				ent.setValue("word", et.getText().toString());
				if (rbWord.isChecked()) {
					ent.setValue("type_id", 1);	
				}
				if (rbUser.isChecked()) {
					ent.setValue("type_id", 2);	
				}
				if (rbSource.isChecked()) {
					ent.setValue("type_id", 3);	
				}
		    	ent.save();
                CacheData.getInstance().fillHide();
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
    	Entity ent = new Entity("quiet", mCurrentId);
    	ent.delete();
        CacheData.getInstance().fillHide();
    	refresh ();
    }
    
    
    private void refresh () {

    	mAdapter = new QuietWordsAdapter(this, DataFramework.getInstance().getEntityList("quiet"));
    	
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