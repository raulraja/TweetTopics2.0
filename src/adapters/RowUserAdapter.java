package adapters;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.dataframework.Entity;
import com.javielinux.tweettopics.R;
import com.javielinux.tweettopics.ThemeManager;
import com.javielinux.tweettopics.Utils;

public class RowUserAdapter extends BaseAdapter {

    private Context mContext;
    private List<Entity> elements;
    
    public RowUserAdapter(Context mContext, List<Entity> elements)
    {
        this.mContext = mContext;
        this.elements = elements;
    }
    
	@Override
	public int getCount() {
		return elements.size();
	}
	
	public int getPositionById(long id) {
        for (int i=0; i<getCount(); i++) {
        	if ( ((Entity)getItem(i)).getId() == id ) {
        		return i;
        	}
        }
        return -1;
	}
	

	@Override
	public Object getItem(int position) {
        return elements.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Entity item = elements.get(position);
		
		long id = item.getId();
		
		View v = null;
		
		if (null == convertView) {
			v = View.inflate(mContext, R.layout.users_row, null);
		} else {
			v = convertView;
		}
		
		v.setBackgroundDrawable(Utils.createStateListDrawable(mContext, new ThemeManager(mContext).getColor("list_background_row_color")));
        
        ImageView img = (ImageView)v.findViewById(R.id.icon);
        try {
        	img.setImageBitmap(Utils.getBitmapAvatar(id, Utils.AVATAR_LARGE));	
        } catch (Exception e) {
        	e.printStackTrace();
        	img.setImageResource(R.drawable.avatar);
        }
        
        ImageView tag_network = (ImageView)v.findViewById(R.id.tag_network);
        TextView saveTimeline = (TextView)v.findViewById(R.id.save_timeline);
        
        if (item.getString("service").equals("facebook")) {
        	tag_network.setImageResource(R.drawable.icon_facebook);
        	saveTimeline.setText(R.string.facebook_network);
        } else {
        	tag_network.setImageResource(R.drawable.icon_twitter);
        	if (item.getInt("no_save_timeline")==1) {
            	saveTimeline.setText(R.string.no_save_timeline);
            } else {
            	saveTimeline.setText(R.string.save_timeline);
            }
        }
        
        TextView title = (TextView)v.findViewById(R.id.name);
        title.setText(item.getString("name"));
        
        ImageView imgSel = (ImageView)v.findViewById(R.id.icon_sel);
        
        if (item.getInt("active")==1) {
        	imgSel.setImageResource(R.drawable.list_item_selected);
        } else {
        	imgSel.setImageResource(R.drawable.list_item_unselected);
        }
        
        
        /*
        Button refreshAvatar = (Button)v.findViewById(R.id.bt_refresh_avatar); 
        refreshAvatar.setTag(item.getId());
        
        refreshAvatar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mUsersActivity.refreshAvatar(Long.parseLong(v.getTag().toString()));
			}
        	
        });
        
        Button delete = (Button)v.findViewById(R.id.bt_delete); 
        delete.setTag(item.getId());
        
        delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mUsersActivity.showDeleteDialogUser(Long.parseLong(v.getTag().toString()));
			}
        	
        });
*/
        return v;
	}

}
