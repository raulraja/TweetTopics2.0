package adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.android.dataframework.Entity;
import com.javielinux.tweettopics2.R;
import com.javielinux.tweettopics2.Utils;

import java.util.List;

public class RowUsersNewStatusAdapter extends BaseAdapter {
	
    private List<Entity> elements;
    private Context mContext = null;
	 
    public RowUsersNewStatusAdapter(Context cnt, List<Entity> elements)
    {
        this.elements = elements;
        mContext = cnt;
    }
	
	public int getCount() {
		return elements.size();
	}

	public Entity getItem(int position) {
		return (Entity)elements.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		Entity item = elements.get(position);		
		
		View v = null;
		
		if (null == convertView) {
			v = View.inflate(mContext, R.layout.row_icon, null);
		} else {
			v = convertView;
		}
		
		ImageView img = (ImageView)v.findViewById(R.id.img_icon);
		img.setImageBitmap(Utils.getBitmapAvatar(item.getId(), Utils.AVATAR_LARGE));

		return v;
		
	}

}
