package layouts;

import adapters.RowSidebarConversationAdapter;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Html;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import com.javielinux.tweettopics2.R;
import com.javielinux.tweettopics2.Utils;
import task.LoadImageWidgetAsyncTask;
import twitter4j.Status;

import java.io.File;
import java.util.HashMap;


public class TweetConversationItem extends RelativeLayout implements LoadImageWidgetAsyncTask.LoadImageWidgetAsyncTaskResponder {

    public static HashMap<String,Bitmap> cacheBitmaps = new HashMap<String,Bitmap>();

    private Context mContext;
    private LoadImageWidgetAsyncTask loadImageWidgetAsyncTask = null;
    private RowSidebarConversationAdapter.ViewHolder viewHolder;
    
    public TweetConversationItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }
    
    public void setRow(Status status_conversation) {

        viewHolder = (RowSidebarConversationAdapter.ViewHolder) this.getTag();
        
        String url = status_conversation.getUser().getProfileImageURL().toString();
        
        if (cacheBitmaps.containsKey(url)) {
            viewHolder.avatar_sidebar.setImageBitmap(cacheBitmaps.get(url));
        } else {

            File file = Utils.getFileForSaveURL(mContext, status_conversation.getUser().getProfileImageURL().toString());

            if (file.exists()) {
                viewHolder.avatar_sidebar.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
            } else {
                viewHolder.avatar_sidebar.setImageResource(R.drawable.avatar_small);
                if (loadImageWidgetAsyncTask!=null) loadImageWidgetAsyncTask.cancel(true);
                loadImageWidgetAsyncTask = new LoadImageWidgetAsyncTask(this);
                loadImageWidgetAsyncTask.execute(status_conversation.getUser().getProfileImageURL().toString());
            }

        }

        viewHolder.username_sidebar.setText(status_conversation.getUser().getScreenName() + " (" + status_conversation.getUser().getName() + ")");

        viewHolder.date_sidebar.setText(Utils.timeFromTweetExtended(mContext, status_conversation.getCreatedAt()));

        viewHolder.text_tweet_sidebar.setText(Html.fromHtml(Utils.toHTML(mContext, status_conversation.getText())));
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
