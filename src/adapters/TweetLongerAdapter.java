package adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.javielinux.tweettopics2.R;

import java.util.List;

public class TweetLongerAdapter extends BaseAdapter {

    private Context mContext;
    private List<TypeTweetLonger> elements;

    static public class TypeTweetLonger {
        public int mode;
        public String title;
        public String description;
        public TypeTweetLonger() {

        }
    }

    public TweetLongerAdapter(Context mContext, List<TypeTweetLonger> elements)
    {
        this.mContext = mContext;
        this.elements = elements;
    }
    
	@Override
	public int getCount() {
		return elements.size();
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
        TypeTweetLonger item = elements.get(position);

		View v = null;
		
		if (null == convertView) {
			v = View.inflate(mContext, R.layout.row_tweetlonger, null);
		} else {
			v = convertView;
		}

        ((TextView)v.findViewById(R.id.tweetlonger_title)).setText(item.title);

        ((TextView)v.findViewById(R.id.tweetlonger_description)).setText(item.description);

        return v;
	}

}
