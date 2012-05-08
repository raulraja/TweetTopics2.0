package widget;

import java.util.ArrayList;

import adapters.RowSearchWidgetAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.appwidget.AppWidgetManager;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;

import com.android.dataframework.DataFramework;
import com.javielinux.tweettopics.R;
import com.javielinux.tweettopics.Utils;

public class WidgetTweetsConf4x2 extends Activity {
		
	int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setResult(RESULT_CANCELED);
        
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }
                
    	try {
        	DataFramework.getInstance().open(this, Utils.packageName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		showDialogType();
		
		/*
		this.setTitle(R.string.configure_widget);
		
		setContentView(R.layout.widget_configure);
		
		GridView mGridUser = (GridView) findViewById(R.id.w_grid_user);
		
		ArrayList<Integer> ar = new ArrayList<Integer>();
		ar.add(ServiceWidgetTweets4x2.TIMELINE);
		ar.add(ServiceWidgetTweets4x2.MENTIONS);
		
		mAdapterUser = new RowUserWidgetAdapter(this, ar);
		
		mGridUser.setAdapter(mAdapterUser);
		mGridUser.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> av, View v, int pos, long id) {
				
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
				i.putExtra("id_user", mAdapterUser.getItem(pos));
				WidgetTweetsConf4x2.this.sendOrderedBroadcast(i, null);
				finish();
				
			}
        });
		
		GridView mGridSearch = (GridView) findViewById(R.id.w_grid_search);
		
		mAdapterSearch = new RowSearchWidgetAdapter(this, DataFramework.getInstance().getEntityList("search", "is_temp=0"));
		
    	mGridSearch.setAdapter(mAdapterSearch);
    	mGridSearch.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> av, View v, int pos, long id) {
				
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
				i.putExtra("id_search", mAdapterSearch.getItem(pos).getId());
				WidgetTweetsConf4x2.this.sendOrderedBroadcast(i, null);
				finish();
			
				
			}
        });
		*/
    }
    
    public void showDialogType() {
    	
    	CharSequence[] cs = new CharSequence[3];
    	cs [0] = this.getText(R.string.timeline);
    	cs [1] = this.getText(R.string.mentions);
    	cs [2] = Utils.toCapitalize(this.getText(R.string.searchs).toString());
    	
		final ArrayList<Integer> arType = new ArrayList<Integer>();
		arType.add(ServiceWidgetTweets4x2.TIMELINE);
		arType.add(ServiceWidgetTweets4x2.MENTIONS);    	
		arType.add(ServiceWidgetTweets4x2.SEARCH);
    	
    	AlertDialog builder = new AlertDialog.Builder(this)
    		.setCancelable(true)
            .setTitle(R.string.options)
            .setItems(cs, new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					int type = arType.get(which);
					if (type == ServiceWidgetTweets4x2.TIMELINE) {
						Intent i = new Intent(Intent.ACTION_VIEW);
						i.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
						i.putExtra("id_user", ServiceWidgetTweets4x2.TIMELINE);
						WidgetTweetsConf4x2.this.sendOrderedBroadcast(i, null);
						finish();
					} else if (type == ServiceWidgetTweets4x2.MENTIONS) {
						Intent i = new Intent(Intent.ACTION_VIEW);
						i.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
						i.putExtra("id_user", ServiceWidgetTweets4x2.MENTIONS);
						WidgetTweetsConf4x2.this.sendOrderedBroadcast(i, null);
						finish();
					} else if (type == ServiceWidgetTweets4x2.SEARCH) {
						showDialogSearch();
					}
				}
            	
            })
            .setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface arg0) {
					finish();
				}
            })
           .create();  
    	builder.show();
        
    }
    
    public void showDialogSearch() {
    	
    	final RowSearchWidgetAdapter adapter = new RowSearchWidgetAdapter(this, DataFramework.getInstance().getEntityList("search", "is_temp=0"));

    	AlertDialog builder = new AlertDialog.Builder(this)
    		.setCancelable(true)
            .setTitle(Utils.toCapitalize(this.getText(R.string.searchs).toString()))
            .setAdapter(adapter, new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent i = new Intent(Intent.ACTION_VIEW);
					i.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
					i.putExtra("id_search", adapter.getItem(which).getId());
					WidgetTweetsConf4x2.this.sendOrderedBroadcast(i, null);
					finish();
				}
            	
            })
            .setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface arg0) {
					finish();
				}
            })
           .create();  
    	builder.show();
        
    }
    
	@Override
	protected void onDestroy() {
		super.onDestroy();
		DataFramework.getInstance().close();
	}
    
}
