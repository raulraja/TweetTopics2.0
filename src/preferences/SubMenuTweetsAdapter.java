package preferences;

import infos.InfoSubMenuTweet;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.javielinux.tweettopics.R;
import com.javielinux.tweettopics.Utils;

public class SubMenuTweetsAdapter extends ArrayAdapter<InfoSubMenuTweet> {
	
	private Context context;
	
	public SubMenuTweetsAdapter(Context cnt, ArrayList<InfoSubMenuTweet> statii) {
		super(cnt, android.R.layout.simple_list_item_1, statii);
		context = cnt;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		InfoSubMenuTweet item = getItem(position);
		View v = null;
		v = View.inflate(context, R.layout.row_submenu_tweet, null);
				
		ImageView im = (ImageView)v.findViewById(R.id.image_submenu);
		im.setImageResource(item.getResDrawable());
		
		TextView name = (TextView)v.findViewById(R.id.name_submenu);
		name.setText(item.getResName());
		
		CheckBox cb = (CheckBox)v.findViewById(R.id.cb_submenu);
		cb.setChecked(item.isValue());
		cb.setTag(item.getCode());
		cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,	boolean isChecked) {
				Utils.setSubMenuTweet(buttonView.getContext(), buttonView.getTag().toString(), isChecked);
			}
			
		});
		
		return v;
	}
	

}
