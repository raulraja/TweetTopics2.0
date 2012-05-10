package preferences;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.dataframework.Entity;
import com.javielinux.tweettopics2.R;
import com.javielinux.tweettopics2.ThemeManager;

import java.util.ArrayList;

public class ColorsAdapter extends ArrayAdapter<Entity> {	
	
	private Colors mColor;
	ArrayList<String> mColors = new ArrayList<String>();
	
	public ColorsAdapter(Colors cnt, ArrayList<Entity> statii) {
		super(cnt, android.R.layout.simple_list_item_1, statii);
		mColor = cnt;
		ThemeManager t = new ThemeManager(cnt);
		mColors = t.getColors();
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Entity item = getItem(position);
		View v = null;
		
		if (null == convertView) {
			v = View.inflate(mColor, R.layout.row_color, null);
		} else {
			v = convertView;
		}
		
		TextView word = (TextView)v.findViewById(R.id.color_name);
		word.setText(item.getString("name"));
		
		Bitmap bmp =  Bitmap.createBitmap(30, 30, Bitmap.Config.RGB_565);
		
		if (item.getInt("pos")<0) {
			item.setValue("pos", 0);
			item.save();
		}
		
		Canvas cv = new Canvas(bmp);
		cv.drawColor(Color.parseColor( mColors.get(item.getInt("pos") ) ));
		
		ImageView img = (ImageView)v.findViewById(R.id.color_img);
		img.setImageBitmap(bmp);
		
		return v;
	}
	

}