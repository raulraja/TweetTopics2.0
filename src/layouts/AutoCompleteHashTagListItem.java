package layouts;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.javielinux.tweettopics2.R;

public class AutoCompleteHashTagListItem extends LinearLayout {

	public AutoCompleteHashTagListItem(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public void setRow(String hashtag, String searchWord) {
		TextView name1 = (TextView)findViewById(R.id.ac_hashtag1);
		name1.setText(searchWord);
		
		TextView name2 = (TextView)findViewById(R.id.ac_hashtag2);
		if (hashtag.length()>searchWord.length()) {
			name2.setText(hashtag.substring(searchWord.length()));
		} else {
			name2.setText("");
		}
	}


}
