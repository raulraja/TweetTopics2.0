package com.javielinux.tweettopics2;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.javielinux.adapters.UserTwitterListAdapter;
import com.javielinux.twitter.ConnectionManager;
import com.javielinux.utils.Utils;
import task.ListUserTwitterAsyncTask;
import task.ListUserTwitterAsyncTask.UserTwitterStatusAsyncTaskResponder;
import twitter4j.Twitter;

import java.util.Date;

public class TabGeneral extends Activity implements UserTwitterStatusAsyncTaskResponder {
	private long mCurrentId = -1;
	
	private EditText mName;
	private EditText mSearchOR;
	private EditText mSearchAND;
	private EditText mSearchNOT;
	private EditText mSearchFromUser;
	private EditText mSearchToUser;
	private ImageButton mBtIcons;
	private EditText mIconId;
	private EditText mIconFile;

	public static Twitter twitter;
	
	private AsyncTask<String, Void, UserTwitterListAdapter> userTask;
	
	protected ProgressDialog progressDialog;
	private UserTwitterListAdapter mAdapter;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
                
        try {
            DataFramework.getInstance().open(this, Utils.packageName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        ConnectionManager.getInstance().open(this);

        twitter = ConnectionManager.getInstance().getAnonymousTwitter();
        
        String name = "";
        String search = "";
        String searchOR = "";
        String user = "";
        
        if (savedInstanceState != null) {
        	if (savedInstanceState.containsKey(DataFramework.KEY_ID)) mCurrentId = savedInstanceState.getLong(DataFramework.KEY_ID);
        	if (savedInstanceState.containsKey("name")) name = savedInstanceState.getString("name");
        	if (savedInstanceState.containsKey("search")) search = savedInstanceState.getString("search");
        	if (savedInstanceState.containsKey("search_or")) searchOR = savedInstanceState.getString("search_or");
        	if (savedInstanceState.containsKey("user")) user = savedInstanceState.getString("user");
       	} else {
       		Bundle extras = getIntent().getExtras();  
       		if (extras != null) {
       			if (extras.containsKey(DataFramework.KEY_ID)) mCurrentId = extras.getLong(DataFramework.KEY_ID);
       			if (extras.containsKey("name")) name = extras.getString("name");
       			if (extras.containsKey("search")) search = extras.getString("search");
       			if (extras.containsKey("search_or")) searchOR = extras.getString("search_or");
       			if (extras.containsKey("user")) user = extras.getString("user");
       		}
       	}
        
        ThemeManager mThemeManager = new ThemeManager(this);
        mThemeManager.setTheme();
        
        setContentView(R.layout.tab_newedit_search);
        
        mBtIcons = (ImageButton) findViewById(R.id.bt_icon);
        
        mBtIcons.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

			}
        	
        });
        
        mIconId = (EditText) findViewById(R.id.icon_id);
        mIconId.setText("1");
        
        mIconFile = (EditText) findViewById(R.id.icon_file);
        
        mName = (EditText) findViewById(R.id.et_name);
        
        mName.setText(name);
        
        mSearchAND = (EditText) findViewById(R.id.et_words_and);
        
        mSearchAND.setText(search);
        
        mSearchOR = (EditText) findViewById(R.id.et_words_or);
        
        mSearchOR.setText(searchOR);
        
        mSearchNOT = (EditText) findViewById(R.id.et_words_not);
        
        mSearchFromUser = (EditText) findViewById(R.id.et_from_user);
        
        mSearchFromUser.setText(user);
        
        mSearchToUser = (EditText) findViewById(R.id.et_to_user);
        
        populateFields();
        
    }
    
    public void selectIcon(long id) {
    	mIconId.setText(id+"");
    	mIconFile.setText("");
    	Entity icon = new Entity("icons", id);
    	mBtIcons.setImageDrawable(icon.getDrawable("icon"));
    }
    
    public void searchIcon() {
    	final EditText et = new EditText(this);
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(this.getString(R.string.search_avatar));
		builder.setMessage(this.getString(R.string.search_avatar_msg));
		builder.setView(et);
		builder.setPositiveButton(R.string.search, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				searchAvatarInTwitter(et.getText().toString());		
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
    
    public void searchAvatarInTwitter(String text) {
    		
		progressDialog = ProgressDialog.show(
				this,
				getResources().getString(R.string.loading_users),
				getResources().getString(R.string.loading_description)
		);
		
		progressDialog.setCancelable(true);
		progressDialog.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface arg0) {
				if (userTask!=null) userTask.cancel(true);
			}
			
		});
		
		userTask = new ListUserTwitterAsyncTask(this).execute(text);	

    }
    
    private void populateFields() {
    	if (mCurrentId != -1) {
    		Entity ent = new Entity("search", mCurrentId);
    		mName.setText(ent.getString("name"));
    		mSearchOR.setText(ent.getString("words_or"));
    		mSearchAND.setText(ent.getString("words_and"));
    		mSearchNOT.setText(ent.getString("words_not"));
    		mSearchFromUser.setText(ent.getString("from_user"));
    		mSearchToUser.setText(ent.getString("to_user"));
    		mBtIcons.setImageDrawable(Utils.getDrawable(this, ent.getString("icon_big")));
    		mIconId.setText(ent.getString("icon_id"));
   			mIconFile.setText(ent.getString("icon_token_file"));
    	}
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        DataFramework.getInstance().close();
    }

	@Override
	public void userTwitterStatusCancelled() {
		
	}

	@Override
	public void userTwitterStatusLoaded(UserTwitterListAdapter adapter) {
		
		mAdapter = adapter;
		
		progressDialog.dismiss();
		
		if (mAdapter.getError()==Utils.UNKNOWN_ERROR) {
			Utils.showMessage(this, this.getString(R.string.no_server));
		} else if (mAdapter.getError()==Utils.LIMIT_ERROR) {
    		Date date = mAdapter.getRate().getResetTime();
    		Utils.showMessage(this, this.getString(R.string.limit_server) + " " + date.toLocaleString());
    	} else {
		
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(this.getString(R.string.users));
			builder.setAdapter(mAdapter, new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog, int which) {
					
					try {					
						String tokenFile = Utils.createIconForSearch(mAdapter.getItem(which).getAvatar());
						
						mIconId.setText("-1");
				    	mIconFile.setText(tokenFile);
				    	
				    	mBtIcons.setImageBitmap(mAdapter.getItem(which).getAvatar());
						
					} catch (Exception e) {
						e.printStackTrace();
					}
					
			    }
			});
			AlertDialog alert = builder.create();
			alert.show();
			
    	}
		
	}

	@Override
	public void userTwitterStatusLoading() {
		
	}
	
}
