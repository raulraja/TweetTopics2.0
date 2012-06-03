package adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.dataframework.Entity;
import com.javielinux.tweettopics2.R;
import com.javielinux.utils.Utils;

import java.util.List;

public class RowSearchWidgetAdapter extends BaseAdapter {
	
	private Context mContext;
    private List<Entity> elements;
	 
    public RowSearchWidgetAdapter(Context mContext, List<Entity> elements)
    {
        this.mContext = mContext;
        this.elements = elements;
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
			v = View.inflate(mContext, R.layout.row_search_widget, null);
		} else {
			v = convertView;
		}

		ImageView img = (ImageView)v.findViewById(R.id.img_search);
		
		try {
			Drawable d = Utils.getDrawable(v.getContext(), item.getString("icon_big"));
			if (d==null) {
				img.setImageResource(R.drawable.letter_az);	
			} else {
				img.setImageDrawable( d );
			}
		} catch (Exception e) {
			img.setImageResource(R.drawable.letter_az);
			e.printStackTrace();
		}		
		
		String name = item.getString("name");	

		TextView lTitle = (TextView)v.findViewById(R.id.title);
		lTitle.setText(name);
			
		return v;
	}

}
