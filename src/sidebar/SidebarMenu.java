package sidebar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import com.cyrilmottier.android.greendroid.R;
import com.javielinux.tweettopics.ThemeManager;
import com.javielinux.tweettopics.TweetTopicsCore;
import com.javielinux.tweettopics.Utils;
import infos.InfoTweet;
import task.CheckConversationAsyncTask;
import task.CheckConversationAsyncTask.CheckConversationAsyncTaskResponder;
import twitter4j.Status;

import java.util.ArrayList;

public class SidebarMenu implements CheckConversationAsyncTaskResponder {
	
	private Button mBtSidebar1 = null;
	private Button mBtSidebar2 = null;
	private Button mBtSidebar3 = null;
	private Button mBtSidebar4 = null;
	private Button mBtSidebar5 = null;
	private Button mBtSidebar6 = null;
	
	private View mView;
	
	protected boolean isDesactivateRetweet = false;
	protected boolean isDesactivateFavorite = false;
	protected boolean isDesactivateConversation = false;
	
	protected boolean isTweetConversation = false;
	protected boolean isLoadedConversation = false;
	
	private TweetTopicsCore mTweetTopicsCore; 
	
	public SidebarMenu(TweetTopicsCore ttc) {
		
		mTweetTopicsCore = ttc;
				
		mView = View.inflate(mTweetTopicsCore.getTweetTopics(), R.layout.sidebar_menu, null);
		
		 // botones del sidebar 
        
		mBtSidebar1 = (Button) mView.findViewById(R.id.bt_sidebar_1);
		//mBtSidebar1.setBackgroundResource(mTweetTopicsCore.getThemeManager().getResource("button_sidebar_background"));
		mBtSidebar1.setBackgroundResource(android.R.color.transparent);
		mBtSidebar1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onClickButton1();
			}
        	
        });
		mBtSidebar1.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View view, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					mBtSidebar1.setCompoundDrawablesWithIntrinsicBounds(null, mTweetTopicsCore.getThemeManager().getDrawableTweetButton(R.drawable.icon_reply, ThemeManager.TYPE_PRESS), null, null);
				}
				if (event.getAction() == MotionEvent.ACTION_UP) {
					mBtSidebar1.setCompoundDrawablesWithIntrinsicBounds(null, mTweetTopicsCore.getThemeManager().getDrawableTweetButton(R.drawable.icon_reply, ThemeManager.TYPE_NORMAL), null, null);
				}
				return false;
			}
    		
    	});
    	
    	mBtSidebar2 = (Button) mView.findViewById(R.id.bt_sidebar_2);
    	//mBtSidebar2.setBackgroundResource(mTweetTopicsCore.getThemeManager().getResource("button_sidebar_background"));
    	mBtSidebar2.setBackgroundResource(android.R.color.transparent);
    	mBtSidebar2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!isDesactivateRetweet) {
					onClickButton2();
				}
			}
        	
        });
    	mBtSidebar2.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View view, MotionEvent event) {
				if (!isDesactivateRetweet) {
					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						mBtSidebar2.setCompoundDrawablesWithIntrinsicBounds(null, mTweetTopicsCore.getThemeManager().getDrawableTweetButton(R.drawable.icon_retweet, ThemeManager.TYPE_PRESS), null, null);
					}
					if (event.getAction() == MotionEvent.ACTION_UP) {
						mBtSidebar2.setCompoundDrawablesWithIntrinsicBounds(null, mTweetTopicsCore.getThemeManager().getDrawableTweetButton(R.drawable.icon_retweet, ThemeManager.TYPE_NORMAL), null, null);
					}
				}
				return false;
			}
    		
    	});
    	
    	mBtSidebar3 = (Button) mView.findViewById(R.id.bt_sidebar_3);
    	//mBtSidebar3.setBackgroundResource(mTweetTopicsCore.getThemeManager().getResource("button_sidebar_background"));
    	mBtSidebar3.setBackgroundResource(android.R.color.transparent);
    	mBtSidebar3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!isDesactivateFavorite) {
					onClickButton3();
				}
			}
        	
        });
    	mBtSidebar3.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View view, MotionEvent event) {
				if (!isDesactivateFavorite) {
					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						mBtSidebar3.setCompoundDrawablesWithIntrinsicBounds(null, mTweetTopicsCore.getThemeManager().getDrawableTweetButton(R.drawable.icon_favorite, ThemeManager.TYPE_PRESS), null, null);
					}
					if (event.getAction() == MotionEvent.ACTION_UP) {
						mBtSidebar3.setCompoundDrawablesWithIntrinsicBounds(null, mTweetTopicsCore.getThemeManager().getDrawableTweetButton(R.drawable.icon_favorite, ThemeManager.TYPE_NORMAL), null, null);
					}
				}
				return false;
			}
    		
    	});
    	
    	mBtSidebar4 = (Button) mView.findViewById(R.id.bt_sidebar_4);
    	//mBtSidebar4.setBackgroundResource(mTweetTopicsCore.getThemeManager().getResource("button_sidebar_background"));
    	mBtSidebar4.setBackgroundResource(android.R.color.transparent);
    	mBtSidebar4.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!isDesactivateConversation) {
					onClickButton4();
				}
			}
        	
        });
    	mBtSidebar4.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View view, MotionEvent event) {
				if (!isDesactivateConversation) {
					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						mBtSidebar4.setCompoundDrawablesWithIntrinsicBounds(null, mTweetTopicsCore.getThemeManager().getDrawableTweetButton(R.drawable.icon_conversation, ThemeManager.TYPE_PRESS), null, null);
					}
					if (event.getAction() == MotionEvent.ACTION_UP) {
						mBtSidebar4.setCompoundDrawablesWithIntrinsicBounds(null, mTweetTopicsCore.getThemeManager().getDrawableTweetButton(R.drawable.icon_conversation, ThemeManager.TYPE_NORMAL), null, null);
					}
				}
				return false;
			}
    		
    	});
    	
        mBtSidebar5 = (Button) mView.findViewById(R.id.bt_sidebar_5);
        //mBtSidebar5.setBackgroundResource(mTweetTopicsCore.getThemeManager().getResource("button_sidebar_background"));
        mBtSidebar5.setBackgroundResource(android.R.color.transparent);
        mBtSidebar5.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onClickButton5();
			}
        	
        });
        mBtSidebar5.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View view, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					mBtSidebar5.setCompoundDrawablesWithIntrinsicBounds(null, mTweetTopicsCore.getThemeManager().getDrawableTweetButton(R.drawable.icon_translate, ThemeManager.TYPE_PRESS), null, null);
				}
				if (event.getAction() == MotionEvent.ACTION_UP) {
					mBtSidebar5.setCompoundDrawablesWithIntrinsicBounds(null, mTweetTopicsCore.getThemeManager().getDrawableTweetButton(R.drawable.icon_translate, ThemeManager.TYPE_NORMAL), null, null);
				}
				return false;
			}
    		
    	});
        
        mBtSidebar6 = (Button) mView.findViewById(R.id.bt_sidebar_6);
        //mBtSidebar6.setBackgroundResource(mTweetTopicsCore.getThemeManager().getResource("button_sidebar_background"));
        mBtSidebar6.setBackgroundResource(android.R.color.transparent);
        mBtSidebar6.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onClickButton6();
			}
        	
        });
        mBtSidebar6.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View view, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					mBtSidebar6.setCompoundDrawablesWithIntrinsicBounds(null, mTweetTopicsCore.getThemeManager().getDrawableTweetButton(R.drawable.icon_more, ThemeManager.TYPE_PRESS), null, null);
				}
				if (event.getAction() == MotionEvent.ACTION_UP) {
					mBtSidebar6.setCompoundDrawablesWithIntrinsicBounds(null, mTweetTopicsCore.getThemeManager().getDrawableTweetButton(R.drawable.icon_more, ThemeManager.TYPE_NORMAL), null, null);
				}
				return false;
			}
    		
    	});
        
        setColumnButtons();
		
	}
	
	private void onClickButton1() {
		InfoTweet it = mTweetTopicsCore.getCurrentInfoTweet();
		if (it!=null) {
			it.goToReply(mTweetTopicsCore);
		}
	}
	
	private void onClickButton2() {
		InfoTweet it = mTweetTopicsCore.getCurrentInfoTweet();
		if (it!=null) {
			it.goToRetweet(mTweetTopicsCore);
		}
	}
	
	private void onClickButton3() {
		InfoTweet it = mTweetTopicsCore.getCurrentInfoTweet();
		if (it!=null) {
			int out = it.goToFavorite(mTweetTopicsCore);
			if (out == InfoTweet.OUT_TRUE) {
				mBtSidebar3.setCompoundDrawablesWithIntrinsicBounds(null, mTweetTopicsCore.getThemeManager().getDrawableTweetButton(R.drawable.icon_favorite, ThemeManager.TYPE_NORMAL), null, null);
			} else if (out == InfoTweet.OUT_FALSE) {
				mBtSidebar3.setCompoundDrawablesWithIntrinsicBounds(null, mTweetTopicsCore.getThemeManager().getDrawableTweetButton(R.drawable.icon_favorite, ThemeManager.TYPE_OFF), null, null);
			}
		}
		
	}
	
	private void onClickButton4() {
		if (isLoadedConversation) {
			showConversation();
		} else {
			checkActiveConversation();
		}
	}
	
	private void onClickButton5() {
		InfoTweet it = mTweetTopicsCore.getCurrentInfoTweet();
		if (it!=null) {
			mTweetTopicsCore.loadSidebarTranslate(it.getId());
		}
	}
	
	private void onClickButton6() {
		InfoTweet it = mTweetTopicsCore.getCurrentInfoTweet();
		if (it!=null) {
			final ArrayList<String> arCode = new ArrayList<String>(); 
			ArrayList<String> ar = new ArrayList<String>(); 
			
			if (!(TweetTopicsCore.isTypeList(TweetTopicsCore.TYPE_LIST_COLUMNUSER) && TweetTopicsCore.isTypeLastColumn(TweetTopicsCore.DIRECTMESSAGES))) {
				if (TweetTopicsCore.isTypeList(TweetTopicsCore.TYPE_LIST_READAFTER))
					ar.add(mTweetTopicsCore.getTweetTopics().getString(R.string.delete_read_after));
				else
					ar.add(mTweetTopicsCore.getTweetTopics().getString(R.string.create_read_after));
				arCode.add("read_after");
				
				ar.add(mTweetTopicsCore.getTweetTopics().getString(R.string.send_direct_message));
				arCode.add("send_dm");
				
				ar.add(mTweetTopicsCore.getTweetTopics().getString(R.string.view_map));
				arCode.add("view_map");

				ar.add(mTweetTopicsCore.getTweetTopics().getString(R.string.show_retweeters));
				arCode.add("show_retweeters");
			}

			if (TweetTopicsCore.isTypeList(TweetTopicsCore.TYPE_LIST_COLUMNUSER)) {
				if (it.getUsername().equals(mTweetTopicsCore.getTweetTopics().getActiveUser().getString("name"))) {
					ar.add(mTweetTopicsCore.getTweetTopics().getString(R.string.delete_tweet));
					arCode.add("delete_tweet");
				}
			}
			
			ar.add(mTweetTopicsCore.getTweetTopics().getString(R.string.copy_to_clipboard));
			arCode.add("copy_to_clipboard");
			
			ar.add(mTweetTopicsCore.getTweetTopics().getString(R.string.share));
			arCode.add("share");
			
			CharSequence[] c = new CharSequence[ar.size()];
			for (int i=0; i<ar.size(); i++) {
				c[i] = ar.get(i);
			}
			
			AlertDialog.Builder builder = new AlertDialog.Builder(mTweetTopicsCore.getTweetTopics());
			builder.setTitle(R.string.actions);
			builder.setItems(c, new DialogInterface.OnClickListener() {
	
				@Override
				public void onClick(DialogInterface dialog, int which) {
					pushButtonMore(arCode.get(which));
				}
	
				
			});
	        builder.setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int whichButton) {
	            }
	        });
	        builder.create();
	        builder.show();	 
		}
	}
	
	private void setColumnButtons() {

		if (TweetTopicsCore.isTypeList(TweetTopicsCore.TYPE_LIST_COLUMNUSER) && TweetTopicsCore.isTypeLastColumn(TweetTopicsCore.DIRECTMESSAGES)) {
			isDesactivateRetweet = true;
			isDesactivateFavorite = true;
			isDesactivateConversation = true;
		}
		
		mBtSidebar1.setText(R.string.reply);
		mBtSidebar2.setText(R.string.retweet);
		mBtSidebar3.setText(R.string.favorite);
		mBtSidebar4.setText(R.string.conversation);
		mBtSidebar5.setText(R.string.translate);
		mBtSidebar6.setText(R.string.more);
		
		
		mBtSidebar1.setCompoundDrawablesWithIntrinsicBounds(null, mTweetTopicsCore.getThemeManager().getDrawableTweetButton(R.drawable.icon_reply, ThemeManager.TYPE_NORMAL), null, null);
		
		if (isDesactivateRetweet) {
			mBtSidebar2.setCompoundDrawablesWithIntrinsicBounds(null, mTweetTopicsCore.getThemeManager().getDrawableTweetButton(R.drawable.icon_retweet, ThemeManager.TYPE_OFF), null, null);
    		mBtSidebar2.setClickable(false);
    	} else {
			mBtSidebar2.setCompoundDrawablesWithIntrinsicBounds(null, mTweetTopicsCore.getThemeManager().getDrawableTweetButton(R.drawable.icon_retweet, ThemeManager.TYPE_NORMAL), null, null);
    		mBtSidebar2.setClickable(true);
    	}
		
		if (isDesactivateFavorite) {
			mBtSidebar3.setCompoundDrawablesWithIntrinsicBounds(null, mTweetTopicsCore.getThemeManager().getDrawableTweetButton(R.drawable.icon_favorite, ThemeManager.TYPE_OFF), null, null);
		} else {
			InfoTweet it = mTweetTopicsCore.getCurrentInfoTweet();
	        if (it!=null) {
	        	if (mTweetTopicsCore.isFavoritedSelected()) {
	        		mBtSidebar3.setCompoundDrawablesWithIntrinsicBounds(null, mTweetTopicsCore.getThemeManager().getDrawableTweetButton(R.drawable.icon_favorite, ThemeManager.TYPE_NORMAL), null, null);
	        	} else {
	        		mBtSidebar3.setCompoundDrawablesWithIntrinsicBounds(null, mTweetTopicsCore.getThemeManager().getDrawableTweetButton(R.drawable.icon_favorite, ThemeManager.TYPE_OFF), null, null);
	        	}
	        } else {
	        	mBtSidebar3.setCompoundDrawablesWithIntrinsicBounds(null, mTweetTopicsCore.getThemeManager().getDrawableTweetButton(R.drawable.icon_favorite, ThemeManager.TYPE_OFF), null, null);
	        }
		}
        
		if (isDesactivateConversation) {
			mBtSidebar4.setCompoundDrawablesWithIntrinsicBounds(null, mTweetTopicsCore.getThemeManager().getDrawableTweetButton(R.drawable.icon_conversation, ThemeManager.TYPE_OFF), null, null);
		} else {
			mBtSidebar4.setCompoundDrawablesWithIntrinsicBounds(null, mTweetTopicsCore.getThemeManager().getDrawableTweetButton(R.drawable.icon_conversation, ThemeManager.TYPE_NORMAL), null, null);
		}
       	
        mBtSidebar5.setCompoundDrawablesWithIntrinsicBounds(null, mTweetTopicsCore.getThemeManager().getDrawableTweetButton(R.drawable.icon_translate, ThemeManager.TYPE_NORMAL), null, null);
        mBtSidebar6.setCompoundDrawablesWithIntrinsicBounds(null, mTweetTopicsCore.getThemeManager().getDrawableTweetButton(R.drawable.icon_more, ThemeManager.TYPE_NORMAL), null, null);
		
        checkVerifyFastActiveConversation();
		
	}
	
	public View getView() {
		return mView;
	}
	
    private Status mCurrentStatusConversation;	
    
    public void checkVerifyFastActiveConversation() {

		mCurrentStatusConversation = null;
    	InfoTweet it = mTweetTopicsCore.getCurrentInfoTweet();
		if (it!=null) {
			if (it.getTypeFrom()==InfoTweet.FROM_STATUS) {
				if (it.getToReplyId()<=0) {
					isLoadedConversation = true;
					isDesactivateConversation = true;
					isTweetConversation = false;
					mBtSidebar4.setCompoundDrawablesWithIntrinsicBounds(null, mTweetTopicsCore.getThemeManager().getDrawableTweetButton(R.drawable.icon_conversation, ThemeManager.TYPE_OFF), null, null);
				}
			}
		}
		
     }
    
    public void checkActiveConversation() {

    	InfoTweet it = mTweetTopicsCore.getCurrentInfoTweet();
		if (it!=null) {
			if (it.getTypeFrom()==InfoTweet.FROM_STATUS) {
				if (it.getToReplyId()>0) {
					isTweetConversation = false;
					mBtSidebar4.setCompoundDrawablesWithIntrinsicBounds(null, mTweetTopicsCore.getThemeManager().getDrawableTweetButton(R.drawable.icon_conversation_loading, ThemeManager.TYPE_OFF), null, null);
			    	new CheckConversationAsyncTask(mTweetTopicsCore.getTweetTopics(), this, InfoTweet.FROM_STATUS).execute(it.getToReplyId());
				} else {
					checkConversationLoaded((Status)null);
				}
			} else {
		    	isTweetConversation = false;
		    	mBtSidebar4.setCompoundDrawablesWithIntrinsicBounds(null, mTweetTopicsCore.getThemeManager().getDrawableTweetButton(R.drawable.icon_conversation_loading, ThemeManager.TYPE_OFF), null, null);
		    	new CheckConversationAsyncTask(mTweetTopicsCore.getTweetTopics(), this, InfoTweet.FROM_TWEETS).execute(it.getId());
			}
		}

		
     }
    
	@Override
	public void checkConversationCancelled() {
	}

	@Override
	public void checkConversationLoaded(Status status) {
		if (status!=null) { 
			mCurrentStatusConversation = status; 
			isTweetConversation = true;
			mBtSidebar4.setCompoundDrawablesWithIntrinsicBounds(null, mTweetTopicsCore.getThemeManager().getDrawableTweetButton(R.drawable.icon_conversation, ThemeManager.TYPE_NORMAL), null, null);
		} else {
			isDesactivateConversation = true;
			isTweetConversation = false;
			mBtSidebar4.setCompoundDrawablesWithIntrinsicBounds(null, mTweetTopicsCore.getThemeManager().getDrawableTweetButton(R.drawable.icon_conversation, ThemeManager.TYPE_OFF), null, null);
		}
		isLoadedConversation = true;
		showConversation();
	}

	@Override
	public void checkConversationLoading() {
	}
	
	public void showConversation() {
		if (isTweetConversation) {
			InfoTweet it = mTweetTopicsCore.getCurrentInfoTweet();
			if (it!=null) {
				mTweetTopicsCore.showSidebarConversation(mCurrentStatusConversation);
			}
		} else {
			Utils.showMessage(mTweetTopicsCore.getTweetTopics(), R.string.no_conversation);
		}
	}
	
	private void pushButtonMore(String action) {
		InfoTweet it = mTweetTopicsCore.getCurrentInfoTweet();
        if (it != null) {
            if (action.equals("read_after")) {
                it.goToReadAfter(mTweetTopicsCore);
            } else if (action.equals("send_dm")) {
                it.goToSendDM(mTweetTopicsCore);
            } else if (action.equals("delete_tweet")) {
                it.goToDeleteTweet(mTweetTopicsCore);
            } else if (action.equals("show_retweeters")) {
                mTweetTopicsCore.loadSidebarRetweeters(it);
            } else if (action.equals("view_map")) {
                it.goToMap(mTweetTopicsCore);
            } else if (action.equals("copy_to_clipboard")) {
                it.goToClipboard(mTweetTopicsCore);
            } else if (action.equals("share")) {
                it.goToShare(mTweetTopicsCore);
            }
        }
	}
	
}
