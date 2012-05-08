package adapters;

import java.util.List;

import widget.ServiceWidgetTweets4x2;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.javielinux.tweettopics.R;

public class RowUserWidgetAdapter extends BaseAdapter {
	
	private Context mContext;
    private List<Integer> elements;
	 
    public RowUserWidgetAdapter(Context mContext, List<Integer> elements)
    {
        this.mContext = mContext;
        this.elements = elements;
    }
	
	public int getCount() {
		return elements.size();
	}

	public Integer getItem(int position) {
		return (Integer)elements.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		int item = elements.get(position);
		
		View v = null;
		
		if (null == convertView) {
			v = View.inflate(mContext, R.layout.row_search_widget, null);
		} else {
			v = convertView;
		}
		
		ImageView img = (ImageView)v.findViewById(R.id.img_search);
		TextView lTitle = (TextView)v.findViewById(R.id.title);
		
		ImageView tagNew = (ImageView)v.findViewById(R.id.tag_new);
		tagNew.setVisibility(View.GONE);
		ImageView tagLang = (ImageView)v.findViewById(R.id.tag_lang);
		tagLang.setVisibility(View.GONE);
		
		if (item==ServiceWidgetTweets4x2.TIMELINE) {
			lTitle.setText(R.string.timeline);
			img.setImageResource(R.drawable.action_bar_timeline);
		} else {
			lTitle.setText(R.string.mentions);
			img.setImageResource(R.drawable.action_bar_mentions);
		}
			
		return v;
	}

}
