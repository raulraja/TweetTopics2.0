package adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.javielinux.tweettopics2.R;
import com.javielinux.tweettopics2.TweetTopics;
import twitter4j.UserList;

import java.util.ArrayList;

public class UserListsAdapter extends ArrayAdapter<UserList> {
	
	private TweetTopics mTweetTopics;
	
	public UserListsAdapter(TweetTopics cnt, ArrayList<UserList> statii) {
		super(cnt, android.R.layout.simple_list_item_1, statii);
		mTweetTopics = cnt;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		UserList item = getItem(position);
		View v = null;
		
		if (null == convertView) {
			v = View.inflate(mTweetTopics, R.layout.row_userlists, null);
		} else {
			v = convertView;
		}
		
		TextView name = (TextView)v.findViewById(R.id.list_name);
		name.setText(item.getName());
		
		TextView text = (TextView)v.findViewById(R.id.list_fullname);
		text.setText(item.getFullName());
		/*
		ImageView btn = (ImageView)v.findViewById(R.id.btn_view);
		btn.setTag(item);
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mTweetTopics.showUserListWindow((UserList)v.getTag());
			}
			
		});
		*/
		
		return v;
	}
	

}