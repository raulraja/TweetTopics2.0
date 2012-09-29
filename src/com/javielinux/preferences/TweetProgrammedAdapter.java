package com.javielinux.preferences;

import android.database.CursorIndexOutOfBoundsException;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.dataframework.Entity;
import com.javielinux.tweettopics2.R;
import com.javielinux.utils.ImageUtils;
import com.javielinux.utils.Utils;

import java.util.ArrayList;
import java.util.Date;

public class TweetProgrammedAdapter extends ArrayAdapter<Entity> {
	
	private TweetProgrammed mTweetProgrammed;
	
	public TweetProgrammedAdapter(TweetProgrammed cnt, ArrayList<Entity> statii) {
		super(cnt, android.R.layout.simple_list_item_1, statii);
		mTweetProgrammed = cnt;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Entity item = getItem(position);
		View v = null;
		
		if (null == convertView) {
			v = View.inflate(mTweetProgrammed, R.layout.row_tweetprogrammed, null);
		} else {
			v = convertView;
		}
		
		String txt_users = item.getString("users");
		ArrayList<Long> usersId = new ArrayList<Long>();
		String users = "";
		for (String user : txt_users.split(",")) {
			usersId.add(Long.parseLong(user));
            // TODO guardar los nombres de usuario en lugar de los IDs. Los nombres de usuario son únicos
            try {
                Entity ent = new Entity("users", Long.parseLong(user));
                if (!users.equals("")) users += ", ";
                users += ent.getString("name");
            } catch (CursorIndexOutOfBoundsException e){}
		}
		
		ImageView tag_more = (ImageView)v.findViewById(R.id.tag_more);
		tag_more.setVisibility(View.GONE);
		
		if (usersId.size()>0) {
			ImageView avatar = (ImageView)v.findViewById(R.id.user_avatar);
			avatar.setImageBitmap(ImageUtils.getBitmapAvatar(usersId.get(0), Utils.AVATAR_LARGE));
			if (usersId.size()>1) {
				tag_more.setVisibility(View.VISIBLE);				
				tag_more.setImageBitmap(ImageUtils.getBitmapNumber(mTweetProgrammed, usersId.size(), Color.RED, Utils.TYPE_BUBBLE, 12));
			}
		}
		
		TextView user = (TextView)v.findViewById(R.id.prog_username);
		user.setText(users);
				
		TextView text = (TextView)v.findViewById(R.id.prog_text);
		text.setText(item.getString("text"));
		
		TextView date = (TextView)v.findViewById(R.id.prog_date);
		Date d = new Date(item.getLong("date"));
		String dateText = d.toLocaleString();
		if (item.getInt("is_sent")==1) {
			dateText += " (" + mTweetProgrammed.getString(R.string.sent) + ")";
		} else if (item.getInt("is_sent")==0) {
			dateText += " (" + mTweetProgrammed.getString(R.string.to_send) + ")";
		} else {
			dateText += " (" + mTweetProgrammed.getString(R.string.error_sending) + ")";
		}
		date.setText(dateText);
		
		
		return v;
	}
	

}