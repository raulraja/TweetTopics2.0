package preferences;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.javielinux.tweettopics2.BaseActivity;
import com.javielinux.tweettopics2.R;
import com.javielinux.utils.Utils;

public class TweetProgrammed extends BaseActivity {
	
	private static final int ADD_ID = Menu.FIRST;
	private static final int BACK_ID = Menu.FIRST+1;
	
	private static final int DIALOG_ITEM = 0;
	
	public static final int ACTIVITY_NEWEDITTWEETPROGRAMMED = 0;
	
	private ListView mListView;
	private TextView mNoTweetProgrammed;
	
	private long mCurrentId = 0;
	
	private TweetProgrammedAdapter mAdapter;
	
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case DIALOG_ITEM:
            return new AlertDialog.Builder(this)
            .setTitle(R.string.actions)
            .setItems(R.array.items_tweetprogrammed, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if (which==0) {
                    	editItem();
                    } else if (which==1) {
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
        
        setContentView(R.layout.tweetprogrammed_list);
        
        mListView = (ListView) this.findViewById(R.id.list_prog);
        
        mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				mCurrentId = mAdapter.getItem(position).getId();
				showDialog(DIALOG_ITEM);				
			}
        });
        
        mNoTweetProgrammed = (TextView) this.findViewById(R.id.empty);
        
        refresh();
        
    }
    
    private void newItem() {
    	Intent newquick = new Intent(TweetProgrammed.this, NewEditTweetProgrammed.class);
		startActivityForResult(newquick, ACTIVITY_NEWEDITTWEETPROGRAMMED);
    }
    
    private void editItem() {
    	Intent newquick = new Intent(TweetProgrammed.this, NewEditTweetProgrammed.class);
    	newquick.putExtra(DataFramework.KEY_ID, mCurrentId);
		startActivityForResult(newquick, ACTIVITY_NEWEDITTWEETPROGRAMMED);
    }
    
    private void deleteItem() {
    	Entity ent = new Entity("tweets_programmed", mCurrentId);
    	ent.delete();
    	refresh();
    }
    
    private void refresh () {

    	mAdapter = new TweetProgrammedAdapter(this, DataFramework.getInstance().getEntityList("tweets_programmed", "", "date asc"));
    	
    	if (mAdapter.getCount()<=0) {
    		mNoTweetProgrammed.setVisibility(View.VISIBLE);
    	} else {
    		mNoTweetProgrammed.setVisibility(View.GONE);
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
    protected void onActivityResult(int requestCode, int resultCode, 
                                    Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
               
        switch (requestCode){
        	case ACTIVITY_NEWEDITTWEETPROGRAMMED:
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