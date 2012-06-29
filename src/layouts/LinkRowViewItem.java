package layouts;

import android.content.Context;
import android.support.v4.app.LoaderManager;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import com.javielinux.adapters.LinksAdapter;
import com.javielinux.api.APIDelegate;
import com.javielinux.api.APITweetTopics;
import com.javielinux.api.request.LoadLinkRequest;
import com.javielinux.api.request.LoadUserRequest;
import com.javielinux.api.response.ErrorResponse;
import com.javielinux.api.response.LoadLinkResponse;
import com.javielinux.api.response.LoadUserResponse;
import com.javielinux.tweettopics2.R;
import com.javielinux.utils.Utils;
import infos.CacheData;
import infos.InfoLink;
import infos.InfoUsers;

public class LinkRowViewItem extends LinearLayout {

    private Context context;
    private LinksAdapter.ViewHolder viewHolder;

    public LinkRowViewItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LinkRowViewItem(Context context) {
        super(context);
    }

    private String writeTitle(String title) {
        if (title.startsWith("http://")) {
            title = title.substring(7);
        }
        if (title.length()>14) {
            title = title.substring(0,12)+"...";
        }
        return title;
    }

    public void setRow(String link, final Context cnt, LoaderManager loaderManager) {

        context = cnt;

        viewHolder = (LinksAdapter.ViewHolder) this.getTag();


        boolean hasImage = false;

        if (link.startsWith("@")) {
            InfoUsers infoUser = CacheData.getCacheUser(link.substring(1));
            if (infoUser!=null) {
                if (infoUser.getAvatar()!=null) viewHolder.image.setImageBitmap(infoUser.getAvatar());
                viewHolder.title.setText(writeTitle(link));
                hasImage = true;
            } else {

                APITweetTopics.execute(context, loaderManager, new APIDelegate<LoadUserResponse>() {

                    @Override
                    public void onResults(LoadUserResponse result) {
                        viewHolder.image.setImageBitmap(result.getInfoUsers().getAvatar());
                    }

                    @Override
                    public void onError(ErrorResponse error) {
                    }
                }, new LoadUserRequest(link.substring(1)));

            }
        } else if (!link.startsWith("#")) {

            if (CacheData.getCacheImages().containsKey(link)) {
                InfoLink item = CacheData.getCacheImages().get(link);

                try {
                    if (item.getBitmapThumb()!=null) {
                        viewHolder.image.setImageBitmap(item.getBitmapThumb());
                    } else {
                        if (Utils.isLinkImage(link)) {
                            if (Utils.isLinkVideo(link)) {
                                viewHolder.image.setImageResource(R.drawable.icon_tweet_video);
                            } else {
                                viewHolder.image.setImageResource(R.drawable.icon_tweet_image);
                            }
                        } else {
                            viewHolder.image.setImageResource(R.drawable.icon_tweet_link);
                        }
                    }
                    hasImage = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }

                viewHolder.title.setText(writeTitle(item.getLink()));
            } else {
                APITweetTopics.execute(context, loaderManager, new APIDelegate<LoadLinkResponse>() {

                    @Override
                    public void onResults(LoadLinkResponse result) {

                        try {
                            if (result.getInfoLink().getBitmapThumb()!=null) viewHolder.image.setImageBitmap(result.getInfoLink().getBitmapThumb());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        viewHolder.title.setText(writeTitle(result.getInfoLink().getLink()));
                    }

                    @Override
                    public void onError(ErrorResponse error) {
                    }
                }, new LoadLinkRequest(link, null));
            }

        }

        if (!hasImage) {
            viewHolder.title.setText(writeTitle(link));
            if (Utils.isLinkImage(link)) {
                if (Utils.isLinkVideo(link)) {
                    viewHolder.image.setImageResource(R.drawable.icon_tweet_video);
                } else {
                    viewHolder.image.setImageResource(R.drawable.icon_tweet_image);
                }
            } else {
                if (link.startsWith("@")) {
                    viewHolder.image.setImageResource(R.drawable.icon_tweet_user);
                } else if (link.startsWith("#")) {
                    viewHolder.image.setImageResource(R.drawable.icon_tweet_hashtag);
                } else {
                    viewHolder.image.setImageResource(R.drawable.icon_tweet_link);
                }
            }
        }
        
    }
}
