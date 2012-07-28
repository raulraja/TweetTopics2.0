package preferences;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.CompoundButton.OnCheckedChangeListener;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.javielinux.tweettopics2.BaseActivity;
import com.javielinux.tweettopics2.R;
import com.javielinux.utils.Utils;

public class NewEditTweetQuick extends BaseActivity {
	
	private long mCurrentId = -1;
	private EditText mETName;
	private EditText mETText;
	private EditText mETUserDirect;
	private CheckBox mCBDirect;
	private Button mBTCounter;
	private Button mBTAddress;
	private Button mBTPhoto;
	private Button mBTSave;
	private Button mBTCancel;
	private TextView mCountChars;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try {
        	DataFramework.getInstance().open(this, Utils.packageName);
        } catch (Exception e) {
        	e.printStackTrace();
        }
        
        if (savedInstanceState != null) {
        	if (savedInstanceState.containsKey(DataFramework.KEY_ID)) mCurrentId = savedInstanceState.getLong(DataFramework.KEY_ID);
       	} else {
       		Bundle extras = getIntent().getExtras();  
       		if (extras != null) {
       			if (extras.containsKey(DataFramework.KEY_ID)) mCurrentId = extras.getLong(DataFramework.KEY_ID);
       		}
       	}
        
        setTitle("TweetQuick");
        
        setContentView(R.layout.new_tweet_quick);
        
        mCountChars = (TextView) findViewById(R.id.count_chars);
        
    	mETName = (EditText) findViewById(R.id.quick_name);
    	mETText = (EditText) findViewById(R.id.quick_text);
    	
    	mETText.addTextChangedListener(new TextWatcher() {
				public void afterTextChanged (Editable s) {
					countChars();
				}
				public void beforeTextChanged (CharSequence s, int start, int count, int after) {
				}
				public void onTextChanged (CharSequence s, int start, int before, int count) {
				}
			}
		);
	    	
    	mETUserDirect = (EditText) findViewById(R.id.quick_direct);
    	
    	mCBDirect = (CheckBox) findViewById(R.id.cb_direct);
    	mCBDirect.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
				mETUserDirect.setEnabled(isChecked);
			}
    		
    	});
    	
    	mBTCounter = (Button) findViewById(R.id.bt_quick_count);
    	mBTCounter.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mETText.append("%COUNTER%");			
			}
    		
    	});
    	
    	mBTAddress = (Button) findViewById(R.id.bt_quick_address);
    	mBTAddress.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mETText.append("%ADDRESS%");			
			}
    		
    	});
    	
    	mBTPhoto = (Button) findViewById(R.id.bt_quick_photo);
    	mBTPhoto.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mETText.append("%PHOTO%");			
			}
    		
    	});
    	
    	mBTSave = (Button) findViewById(R.id.bt_save);
    	mBTSave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				save();				
			}
    		
    	});
    	
    	mBTCancel = (Button) findViewById(R.id.bt_cancel);
    	mBTCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				setResult(RESULT_OK);
				finish();				
			}
    		
    	});
    	
    	
    	populateFields();
        
    }
    
    private void populateFields() {
    	if (mCurrentId != -1) {
    		Entity ent = new Entity("tweet_quick", mCurrentId);
    		mETName.setText(ent.getString("name"));
    		mETText.setText(ent.getString("text"));
    		if (ent.getInt("type_id")==2) {
    			mCBDirect.setChecked(true);
    			mETUserDirect.setEnabled(true);
    			mETUserDirect.setText(ent.getString("username_direct"));
    		}
    	}
    }
    
    private void save() {
    	
    	if (!mETText.getText().toString().contains("%PHOTO%")) {
    		
    		Utils.showMessage(this, "El texto debe contener el parametro %PHOTO%");
    		
    	} else {

			Entity ent = new Entity("tweet_quick", mCurrentId);
			if (mCurrentId<=0) {
				ent.setValue("count", 1);
			}
			ent.setValue("name", mETName.getText().toString());
			ent.setValue("text", mETText.getText().toString());
			if (mCBDirect.isChecked()) {
				ent.setValue("type_id", 2);
				mCBDirect.setChecked(true);
				ent.setValue("username_direct", mETUserDirect.getText().toString());
			} else {
				ent.setValue("type_id", 1);
			}
			ent.save();
			setResult(RESULT_OK);
			finish();
			
    	}

    }
    
	private void countChars() {
		String[] ar = mETText.getText().toString().split("%");
		int count = 0;
		for (String token : ar) {
			if (token.equals("ADDRESS")) {
				count += 30;
			} else if (token.equals("PHOTO")) {
				count += 25;
			} else if (token.equals("COUNTER")) {
				count += 2;
			} else {
				count += token.length();
			}
		}
		
		mCountChars.setText(count + "/140");
	}
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        DataFramework.getInstance().close();
    }
}
