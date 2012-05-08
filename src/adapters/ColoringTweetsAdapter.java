package adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.dataframework.Entity;
import com.javielinux.tweettopics.R;
import com.javielinux.tweettopics.ThemeManager;

import java.util.ArrayList;

public class ColoringTweetsAdapter extends ArrayAdapter<Entity> {
	
	private Context context;
	ArrayList<String> mColors = new ArrayList<String>();
	
	public ColoringTweetsAdapter(Context cnt, ArrayList<Entity> statii) {
		super(cnt, android.R.layout.simple_list_item_1, statii);
		context = cnt;
		ThemeManager t = new ThemeManager(cnt);
		mColors = t.getColors();
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Entity item = getItem(position);
		View v = null;

		if (null == convertView) {
			v = View.inflate(context, R.layout.row_coloringtweets, null);
		} else {
			v = convertView;
		}
		
        try {
            Bitmap bmp = Bitmap.createBitmap(30, 30, Config.RGB_565);
            Canvas c = new Canvas(bmp);
            c.drawColor(Color.parseColor( mColors.get(item.getInt("pos")) ));
            
            ImageView im = (ImageView)v.findViewById(R.id.color_image);
            
            im.setImageBitmap(bmp);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
            
		TextView name = (TextView)v.findViewById(R.id.color_name);
		name.setText(item.getString("name"));		
		
		return v;
	}
	

}