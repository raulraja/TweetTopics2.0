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
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.CompoundButton.OnCheckedChangeListener;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.javielinux.tweetprogrammed.OnAlarmReceiverTweetProgrammed;
import com.javielinux.tweettopics2.BaseActivity;
import com.javielinux.tweettopics2.R;
import com.javielinux.utils.ImageUtils;
import com.javielinux.utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class NewEditTweetProgrammed extends BaseActivity {
	
    static final int TIME_DIALOG_ID = 0;
    static final int DATE_DIALOG_ID = 1;
	
	private long mCurrentId = -1;
	
	private EditText mETText;
	private EditText mETUserDirect;
	private CheckBox mCBDirect;
	private Button mBTSave;
	private Button mBTCancel;
	private Button mBTDate;
	private Button mBTTime;
	private Button mBTAddUser;
	
	private long mDate;
	
	private LinearLayout mLayoutUsers;
	
	private long[] mUsersId;
	private CharSequence[] mUserNames;
	private boolean[] mUserChecks;

    private int mYear;
    private int mMonth;
    private int mDay;
    private int mHour;
    private int mMinute;
    
    private String mText = "";
	
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case TIME_DIALOG_ID:
                return new TimePickerDialog(this,
                        mTimeSetListener, mHour, mMinute, false);
            case DATE_DIALOG_ID:
                return new DatePickerDialog(this,
                            mDateSetListener,
                            mYear, mMonth, mDay);
        }
        return null;
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        switch (id) {
            case TIME_DIALOG_ID:
                ((TimePickerDialog) dialog).updateTime(mHour, mMinute);
                break;
            case DATE_DIALOG_ID:
                ((DatePickerDialog) dialog).updateDate(mYear, mMonth, mDay);
                break;
        }
    }  
	
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
        	if (savedInstanceState.containsKey("text")) mText = savedInstanceState.getString("text");
       	} else {
       		Bundle extras = getIntent().getExtras();  
       		if (extras != null) {
       			if (extras.containsKey(DataFramework.KEY_ID)) mCurrentId = extras.getLong(DataFramework.KEY_ID);
       			if (extras.containsKey("text")) mText = extras.getString("text");
       		}
       	}
        
        setContentView(R.layout.new_tweet_programmed);
        
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 60);
        mDate = calendar.getTimeInMillis();
        
        mLayoutUsers = (LinearLayout) findViewById(R.id.users);
        
    	mETText = (EditText) findViewById(R.id.prog_text);
    	mETText.setText(mText);
    	
    	mETUserDirect = (EditText) findViewById(R.id.prog_direct);

    	
    	mCBDirect = (CheckBox) findViewById(R.id.cb_direct);
    	mCBDirect.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
				mETUserDirect.setEnabled(isChecked);
			}
    		
    	});
    	
    	mBTDate = (Button) findViewById(R.id.bt_programmed_date);
    	mBTDate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				showDialog(DATE_DIALOG_ID);	
			}
    		
    	});
    	
    	mBTTime = (Button) findViewById(R.id.bt_programmed_time);
    	mBTTime.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				showDialog(TIME_DIALOG_ID);
			}
    		
    	});
    	
    	mBTAddUser = (Button) findViewById(R.id.bt_add_user);
    	mBTAddUser.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				selectUsers();
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
    
    private void selectUsers() {

    	AlertDialog builder = new AlertDialog.Builder(this)
    		.setCancelable(false)
            .setTitle(R.string.users)
            .setMultiChoiceItems(mUserNames, mUserChecks,
                    new DialogInterface.OnMultiChoiceClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton, boolean isChecked) {
                            
                        }
                    })
            .setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    	refreshUsers();
                    }
                })
           .create();  
    	builder.show();
        
    }

    private void refreshUsers() {

    	mLayoutUsers.removeAllViews();
    	int count = 0;
    	
    	for (int i=0; i<mUsersId.length; i++) {
    		if (mUserChecks[i]) {
    			count++;

    			ImageView im = new ImageView(this);
    			im.setPadding(10, 0, 0, 0);
    			im.setLayoutParams(new LinearLayout.LayoutParams(40,40));
    			im.setAdjustViewBounds(true);
    			try {
    				im.setImageBitmap(ImageUtils.getBitmapAvatar(mUsersId[i], Utils.AVATAR_LARGE));
    			} catch (Exception ex) {
					ex.printStackTrace();
					im.setImageResource(R.drawable.avatar);
				}
    			mLayoutUsers.addView(im);
    			TextView txt = new TextView(this);
    			txt.setPadding(5, 0, 0, 0);
    			txt.setText(mUserNames[i]);
    			mLayoutUsers.addView(txt);
	    		
    		}
    	}
    	
    }
    
    private void populateFields() {
        List<Entity> users = DataFramework.getInstance().getEntityList("users");
        mUsersId = new long[users.size()];
        mUserNames = new CharSequence[users.size()];
    	mUserChecks = new boolean[users.size()];   	
    	
    	if (mCurrentId != -1) {
    		Entity ent = new Entity("tweets_programmed", mCurrentId);
    		
    		String txt_users = ent.getString("users");
    		ArrayList<Long> usersId = new ArrayList<Long>();
    		for (String user : txt_users.split(",")) {
    			usersId.add(Long.parseLong(user));
    		}
    		
    		mETText.setText(ent.getString("text"));
    		
    		int count = 0;
	    	for (Entity user : users) {
	    		mUsersId[count] = user.getId();
	    		mUserNames[count] = user.getString("name");
	    		if (usersId.contains(user.getId())) {
	    			mUserChecks[count] = true;	
	    		} else {
	    			mUserChecks[count] = false;
	    		}
	    		count++;  		           
	    	}
    		
    		
    		//mSpUsers.setSelection(mAdapterUsers.getPosition(ent.getEntity("user_tt_id")));
    		mDate = Long.parseLong(ent.getString("date"));
    		if (ent.getInt("type_id")==2) {
    			mCBDirect.setChecked(true);
    			mETUserDirect.setEnabled(true);
    			mETUserDirect.setText(ent.getString("username_direct"));
    		}
    	} else {
	    	int count = 0;
	    	for (Entity user : users) {
	    		mUsersId[count] = user.getId();
	    		mUserNames[count] = user.getString("name");
	    		if (user.getInt("active")==1) {
	    			mUserChecks[count] = true;	
	    		} else {
	    			mUserChecks[count] = false;
	    		}
	    		count++;  		           
	    	}
    	}
    	refreshUsers();
    	writeDate();
    }
    
    
    private void writeDate() {
    	//Date date = new Date(mDate);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(mDate);
        //calendar.setTime(date);
    	    	    	
    	mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DATE);
        mHour = calendar.get(Calendar.HOUR_OF_DAY);
        mMinute = calendar.get(Calendar.MINUTE);
        
        mBTDate.setText(mYear+"-"+(mMonth+1)+"-"+mDay);
        
        String hour = mHour+"";
        if (mHour<10) hour = "0"+mHour;
        
        if (mMinute<10) {
        	hour += ":0"+mMinute;
        } else {
        	hour += ":"+mMinute;
        }
        
    	mBTTime.setText(hour);
        
        //printDate();
    }
    /*
    private void printDate() {
    	Log.d(Utils.TAG, "=================>>>> "+mYear+"-"+(mMonth+1)+"-"+mDay+" "+mHour+":"+mMinute);
    }
    */
    private void save() {

    	if (mETText.getText().toString().equals("")) {
    		Utils.showMessage(this, this.getString(R.string.need_text));
    	} else {

    		String users = "";
	    	for (int i=0; i<mUsersId.length; i++) {
	    		if (mUserChecks[i]) users += mUsersId[i] + ",";
	    	}
    		
			Entity ent = new Entity("tweets_programmed", mCurrentId);
			ent.setValue("users", users);
			ent.setValue("text", mETText.getText().toString());
			ent.setValue("date", mDate);
			if (mCBDirect.isChecked()) {
				ent.setValue("type_id", 2);
				mCBDirect.setChecked(true);
				ent.setValue("username_direct", mETUserDirect.getText().toString());
			} else {
				ent.setValue("type_id", 1);
			}
			ent.setValue("is_sent", 0);
			ent.save();
						
			OnAlarmReceiverTweetProgrammed.callNextAlarm(this);
			
			Utils.showMessage(this, this.getString(R.string.programmed_save));
			setResult(RESULT_OK);
			finish();
    	}


    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        DataFramework.getInstance().close();
    }
    
    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                mYear = year;
                mMonth = monthOfYear;
                mDay = dayOfMonth;
                /*Date d = new Date(mDate);
                
                Date date = new Date(mYear-1900, mMonth, mDay, d.getHours(), d.getMinutes(), 0) ;*/
                
                Calendar calendar = Calendar.getInstance();
                //calendar.setTime(date);
                calendar.set(mYear, mMonth, mDay, mHour, mMinute);

                mDate = calendar.getTimeInMillis();
                writeDate();
            }
        };

	private TimePickerDialog.OnTimeSetListener mTimeSetListener =
	        new TimePickerDialog.OnTimeSetListener() {
	
	            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
	                mHour = hourOfDay;
	                mMinute = minute;
	                /*Date d = new Date(mDate);
	                
	                Date date = new Date(d.getYear(), d.getMonth(), d.getDate(), mHour, mMinute, 0) ;
*/
	                Calendar calendar = Calendar.getInstance();
	                //calendar.setTime(date);
	                calendar.set(mYear, mMonth, mDay, mHour, mMinute);

	                mDate = calendar.getTimeInMillis();
	                
	                writeDate();
	            }
	        };

    
}
