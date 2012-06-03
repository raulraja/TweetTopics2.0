/**
 * 
 */
package adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.dataframework.Entity;
import com.javielinux.tweettopics2.R;
import com.javielinux.utils.Utils;

import java.util.ArrayList;

public class UsersAdapter extends ArrayAdapter<Entity> {

	private Context context;

	public UsersAdapter(Context context, ArrayList<Entity> statii) {
		super(context, android.R.layout.simple_list_item_1, statii);
		this.context = context;
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Entity user = getItem(position);
		View v = null;
		if (null == convertView) {
			v = View.inflate(context, R.layout.row_users_twitter, null);
		} else {
			v = convertView;
		}
		
		ImageView icon = (ImageView)v.findViewById(R.id.icon);
		icon.setImageBitmap(Utils.getBitmapAvatar(user.getId(), Utils.AVATAR_LARGE));
		
		TextView name = (TextView)v.findViewById(R.id.name);
		
		name.setText(user.getString("name"));
		
		return v;
	}


}