package layouts;

import adapters.RowSidebarRetweetersAdapter;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import com.javielinux.tweettopics.R;
import com.javielinux.tweettopics.Utils;
import task.LoadImageWidgetAsyncTask;
import twitter4j.User;

import java.io.File;
import java.util.HashMap;


public class RetweeterItem extends RelativeLayout implements LoadImageWidgetAsyncTask.LoadImageWidgetAsyncTaskResponder {

    public static HashMap<String,Bitmap> cacheBitmaps = new HashMap<String,Bitmap>();

    private Context mContext;
    private LoadImageWidgetAsyncTask loadImageWidgetAsyncTask = null;
    private RowSidebarRetweetersAdapter.ViewHolder viewHolder;

    public RetweeterItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }
    
    public void setRow(User retweeter_user) {

        viewHolder = (RowSidebarRetweetersAdapter.ViewHolder) this.getTag();
        
        String url = retweeter_user.getProfileImageURL().toString();
        
        if (cacheBitmaps.containsKey(url)) {
            viewHolder.avatar_sidebar.setImageBitmap(cacheBitmaps.get(url));
        } else {
            File file = Utils.getFileForSaveURL(mContext, retweeter_user.getProfileImageURL().toString());

            if (file.exists()) {
                viewHolder.avatar_sidebar.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
            } else {
                viewHolder.avatar_sidebar.setImageResource(R.drawable.avatar_small);
                if (loadImageWidgetAsyncTask!=null) loadImageWidgetAsyncTask.cancel(true);
                loadImageWidgetAsyncTask = new LoadImageWidgetAsyncTask(this);
                loadImageWidgetAsyncTask.execute(retweeter_user.getProfileImageURL().toString());
            }

        }

        viewHolder.username_sidebar.setText(retweeter_user.getScreenName() + " (" + retweeter_user.getName() + ")");
    }

    @Override
    public void imageWidgetLoading() {
    }

    @Override
    public void imageWidgetLoadCancelled() {
    }

    @Override
    public void imageWidgetLoaded(LoadImageWidgetAsyncTask.ImageData data) {
        cacheBitmaps.put(data.url, data.bitmap);
        viewHolder.avatar_sidebar.setImageBitmap(data.bitmap);
    }
}
