package adapters;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.dataframework.Entity;
import com.javielinux.tweettopics.R;

public class TweetListDraftAdapter extends BaseAdapter {

    private Context mContext;
    private List<Entity> elements;
    
    public TweetListDraftAdapter(Context mContext, List<Entity> elements)
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
		
        View v = View.inflate(mContext, R.layout.draft_row_alert, null);

        TextView title = (TextView)v.findViewById(R.id.name);
       	title.setText(item.getString("text"));

        return v;
	}

}
