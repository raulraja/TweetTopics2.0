package layouts;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.javielinux.tweettopics2.R;
import infos.InfoUsers;
import task.LoadImageAutoCompleteAsyncTask;
import task.LoadImageAutoCompleteAsyncTask.LoadImageAutoCompleteAsyncTaskResponder;

public class AutoCompleteListItem extends LinearLayout implements LoadImageAutoCompleteAsyncTaskResponder {

	private AsyncTask<String, Void, Bitmap> latestLoadTask;
	private ImageView mAvatar;
	
	public AutoCompleteListItem(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public void setRow(InfoUsers item, String searchWord) {
		TextView name1 = (TextView)findViewById(R.id.ac_username1);
		name1.setText(searchWord);
		
		TextView name2 = (TextView)findViewById(R.id.ac_username2);
		if (item.getName().length()>searchWord.length()) {
			name2.setText(item.getName().substring(searchWord.length()));
		} else {
			name2.setText("");
		}

		if (latestLoadTask!=null) latestLoadTask.cancel(true);
		
		mAvatar = (ImageView)findViewById(R.id.ac_avatar);
		if (item.getAvatar()==null) {
			mAvatar.setImageResource(R.drawable.avatar);
			latestLoadTask = new LoadImageAutoCompleteAsyncTask(this).execute(item.getUrlAvatar());			
		} else {
			mAvatar.setImageBitmap(item.getAvatar());
		}
	}

	@Override
	public void imageAutoCompleteLoadCancelled() {
		
	}

	@Override
	public void imageAutoCompleteLoaded(Bitmap bmp) {
		if (bmp!=null) mAvatar.setImageBitmap(bmp);
	}

	@Override
	public void imageAutoCompleteLoading() {
		
	}

}
