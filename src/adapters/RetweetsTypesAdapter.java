package adapters;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.android.dataframework.Entity;
import com.javielinux.tweettopics2.R;

import java.util.ArrayList;

public class RetweetsTypesAdapter extends ArrayAdapter<Entity> {
	
	private Context mContext;
	
	public RetweetsTypesAdapter(Context cnt, ArrayList<Entity> statii) {
		super(cnt, android.R.layout.simple_list_item_1, statii);
		mContext = cnt;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Entity item = getItem(position);
		View v = null;
		
		if (null == convertView) {
			v = View.inflate(mContext, R.layout.row_quiet, null);
		} else {
			v = convertView;
		}
		
		TextView word = (TextView)v.findViewById(R.id.quick_name);
		word.setText(item.getString("phrase"));
		
		TextView type = (TextView)v.findViewById(R.id.quick_type);
		type.setText("");
		
		
		return v;
	}
	

}