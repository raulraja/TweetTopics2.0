package com.javielinux.adapters;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.androidquery.AQuery;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.javielinux.api.APIDelegate;
import com.javielinux.api.APITweetTopics;
import com.javielinux.api.request.LoadLinkRequest;
import com.javielinux.api.response.ErrorResponse;
import com.javielinux.api.response.LoadLinkResponse;
import com.javielinux.infos.InfoLink;
import com.javielinux.infos.InfoTweet;
import com.javielinux.tweettopics2.BaseLayersActivity;
import com.javielinux.tweettopics2.R;
import com.javielinux.tweettopics2.ThemeManager;
import com.javielinux.tweettopics2.UserActivity;
import com.javielinux.utils.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.RejectedExecutionException;

public class TweetsAdapter extends ArrayAdapter<InfoTweet> {

    public static class ViewHolder {
        public ImageView avatarView;
        public ImageView tagMap;
        public ImageView tagConversation;
        public TextView screenName;
        public TextView statusText;
        public TextView sourceText;
        public TextView dateText;
        public LinearLayout tweetPhotoImgContainer;
        public ImageView tweetPhotoImg;
        public RelativeLayout lastReadLayout;
        public LinearLayout retweetLayout;
        public ImageView retweetAvatar;
        public TextView retweetUser;
        public TextView retweetText;
        public int typeBackground = -1;
    }

    private static HashMap<String,Integer> hashColorUsers = new HashMap<String,Integer>();
    private static HashMap<String,Integer> hashColorUsersPosition = new HashMap<String,Integer>();

    private static HashMap<String,Integer> hashThemeColors = new HashMap<String,Integer>();

    private boolean flinging = false;

    private ArrayList<InfoTweet> infoTweetArrayList;
    private long selected_id = -1;
    private int hide_messages = 0;
    private int last_read_position = -1;

    private ThemeManager themeManager;
    private int color_line;

    private LoaderManager loaderManager;

    private String usernameColumn;
    private int column;

    private FragmentActivity activity;

    private AQuery listAQuery;

    private PullToRefreshListView parentListView;

    public FragmentActivity getActivity() {
        return activity;
    }


    private void callLinksIfIsPossible(View view) {

        if (getActivity() instanceof PopupLinks.PopupLinksListener) {
            ((PopupLinks.PopupLinksListener)getActivity()).onShowLinks(view, (InfoTweet)view.getTag());
        }

    }

    public void setParentListView(PullToRefreshListView parentListView) {
        this.parentListView = parentListView;
    }

    public boolean isFlinging() {
        return flinging;
    }

    public void setFlinging(boolean flinging) {
        this.flinging = flinging;
        if (!flinging) {
            notifyDataSetChanged();
            launchVisibleTask();
        }
    }

    private void loadLinks(int position) {
        if (infoTweetArrayList.size()<=0) return;
        String linkForImage = "";
        try {
            linkForImage = infoTweetArrayList.get(position).getBestLink();
        } catch (IndexOutOfBoundsException e) {}

        if (!linkForImage.equals("") && !linkForImage.startsWith("@") && !linkForImage.startsWith("#")) {
            if (!CacheData.existCacheInfoLink(linkForImage)) {
                try {
                    APITweetTopics.execute(getContext(), loaderManager, new APIDelegate<LoadLinkResponse>() {

                        @Override
                        public void onResults(LoadLinkResponse result) {
                            if (result.getInfoLink() != null) {
                                if (!isFlinging()) {
                                    notifyDataSetChanged();
                                }
                            }
                        }

                        @Override
                        public void onError(ErrorResponse error) {

                        }
                    }, new LoadLinkRequest(linkForImage, null));

                } catch (RejectedExecutionException e) {}
            }
        }
    }

    public void launchVisibleTask() {

        if (parentListView!=null) {
            int firstPosition = parentListView.getRefreshableView().getFirstVisiblePosition();
            int lastPosition = parentListView.getRefreshableView().getLastVisiblePosition();

            for (int i=firstPosition; i<=lastPosition; i++) {
                loadLinks(i);
            }
        }
    }

    public TweetsAdapter(FragmentActivity activity, LoaderManager loaderManager, ArrayList<InfoTweet> infoTweetArrayList, String usernameColumn, int column) {
        super(activity, android.R.layout.simple_list_item_1, infoTweetArrayList);
        init(activity, loaderManager, infoTweetArrayList, usernameColumn, column);
    }

    public TweetsAdapter(FragmentActivity activity, LoaderManager loaderManager, ArrayList<InfoTweet> infoTweetArrayList) {
        super(activity, android.R.layout.simple_list_item_1, infoTweetArrayList);
        init(activity, loaderManager, infoTweetArrayList, "", -1);
    }

    private void init(FragmentActivity activity, LoaderManager loaderManager, ArrayList<InfoTweet> infoTweetArrayList, String usernameColumn, int column) {
        this.activity = activity;
        listAQuery = new AQuery(activity);

        Log.d(Utils.TAG, "Numero de elementos: " + infoTweetArrayList.size());
        this.infoTweetArrayList = infoTweetArrayList;
        this.usernameColumn = usernameColumn;
        this.column = column;

        this.loaderManager = loaderManager;

        themeManager = new ThemeManager(activity);
        color_line = themeManager.getColor("color_tweet_no_read");

        ArrayList<Entity> colors = DataFramework.getInstance().getEntityList("colors", "type_id=2", "");
        int count = 0;
        for (Entity color : colors) {
            hashColorUsers.put(color.getString("word"), Color.parseColor(themeManager.getColors().get(color.getEntity("type_color_id").getInt("pos"))));
            hashColorUsersPosition.put(color.getString("word"), count);
            count++;
        }

        hashThemeColors.put("list_background_row_color", themeManager.getColor("list_background_row_color"));
        hashThemeColors.put("color_tweet_text", themeManager.getColor("color_tweet_text"));
        hashThemeColors.put("color_tweet_source", themeManager.getColor("color_tweet_source"));
        hashThemeColors.put("color_tweet_retweet", themeManager.getColor("color_tweet_retweet"));
        hashThemeColors.put("color_tweet_usename", themeManager.getColor("color_tweet_usename"));
        hashThemeColors.put("color_tweet_date", themeManager.getColor("color_tweet_date"));

    }

    public static ViewHolder generateViewHolder(View v) {

        ViewHolder viewHolder = new ViewHolder();

        viewHolder.avatarView = (ImageView)v.findViewById(R.id.user_avatar);
        viewHolder.tagMap = (ImageView)v.findViewById(R.id.tag_map);
        viewHolder.tagConversation = (ImageView)v.findViewById(R.id.tag_conversation);
        viewHolder.screenName = (TextView)v.findViewById(R.id.tweet_user_name_text);
        viewHolder.statusText = (TextView)v.findViewById(R.id.tweet_text);
        viewHolder.dateText = (TextView)v.findViewById(R.id.tweet_date);
        viewHolder.sourceText = (TextView)v.findViewById(R.id.tweet_source);
        viewHolder.tweetPhotoImg = (ImageView)v.findViewById(R.id.tweet_photo_img);
        viewHolder.tweetPhotoImgContainer = (LinearLayout)v.findViewById(R.id.tweet_photo_img_container);
        viewHolder.lastReadLayout = (RelativeLayout)v.findViewById(R.id.lastread_layout);
        viewHolder.retweetLayout = (LinearLayout)v.findViewById(R.id.retweet_layout);
        viewHolder.retweetAvatar = (ImageView)v.findViewById(R.id.retweet_avatar);
        viewHolder.retweetUser = (TextView)v.findViewById(R.id.retweet_user);
        viewHolder.retweetText = (TextView)v.findViewById(R.id.retweet_text);

        return viewHolder;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        InfoTweet infoTweet = getItem(position) ;

        if (null == convertView) {
            convertView = activity.getLayoutInflater().inflate(R.layout.tweet_list_view_item, parent, false);
            //view = (RelativeLayout) View.inflate(getContext(), R.layout.tweet_list_view_item, null);
            convertView.setTag(generateViewHolder(convertView));
            loadLinks(position);
        }

        ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        int typeBackground = -1;

        if (selected_id == position) {
            typeBackground = 2;
            if (infoTweet.isRead()) typeBackground++;
            if (viewHolder.typeBackground!=typeBackground) {
                convertView.setBackgroundDrawable(ImageUtils.createGradientDrawableSelected(getContext(), infoTweet.isRead() ? 0 : color_line));
            }
        } else if (column == TweetTopicsUtils.COLUMN_TIMELINE && infoTweet.getText().toLowerCase().contains("@"+usernameColumn.toLowerCase())) {
            typeBackground = 4;
            if (infoTweet.isRead()) typeBackground++;
            if (viewHolder.typeBackground!=typeBackground) {
                convertView.setBackgroundDrawable(ImageUtils.createGradientDrawableMention(getContext(), infoTweet.isRead() ? 0 : color_line));
            }
        } else if ((column == TweetTopicsUtils.COLUMN_MENTIONS || column == TweetTopicsUtils.COLUMN_TIMELINE) &&infoTweet.isFavorited()) {
            typeBackground = 6;
            if (infoTweet.isRead()) typeBackground++;
            if (viewHolder.typeBackground!=typeBackground) {
                convertView.setBackgroundDrawable(ImageUtils.createGradientDrawableFavorite(getContext(), infoTweet.isRead() ? 0 : color_line));
            }
        } else {

            if (hashColorUsers.containsKey(infoTweet.getUsername())) {
                typeBackground = 8 + (hashColorUsersPosition.get(infoTweet.getUsername())*2);
                if (infoTweet.isRead()) typeBackground++;
                if (viewHolder.typeBackground!=typeBackground) {
                    convertView.setBackgroundDrawable(ImageUtils.createStateListDrawable(getContext(), hashColorUsers.get(infoTweet.getUsername()), infoTweet.isRead() ? 0 : color_line));
                }
            } else {
                typeBackground = 0;
                if (infoTweet.isRead()) typeBackground++;
                if (viewHolder.typeBackground!=typeBackground) {
                    convertView.setBackgroundDrawable(ImageUtils.createStateListDrawable(getContext(), hashThemeColors.get("list_background_row_color"), infoTweet.isRead() ? 0 : color_line));
                }
            }
        }

        viewHolder.typeBackground = typeBackground;


        AQuery aQuery = listAQuery.recycle(convertView);

        String html = "";

        if (infoTweet.getTextHTMLFinal().equals("")) {
            String[] in = Utils.toHTMLTyped(activity, infoTweet.getText(), infoTweet.getTextURLs());
            html = in[1];
            infoTweet.setTextFinal(in[0]);
            infoTweet.setTextHTMLFinal(in[1]);
        } else {
            html = infoTweet.getTextHTMLFinal();
        }

        viewHolder.statusText.setTextColor(hashThemeColors.get("color_tweet_text"));
        viewHolder.sourceText.setTextColor(hashThemeColors.get("color_tweet_source"));
        viewHolder.retweetUser.setTextColor(hashThemeColors.get("color_tweet_retweet"));
        viewHolder.screenName.setTextColor(hashThemeColors.get("color_tweet_usename"));
        viewHolder.dateText.setTextColor(hashThemeColors.get("color_tweet_date"));


        if (infoTweet.isLastRead()) {
            BitmapDrawable bmp = (BitmapDrawable)getContext().getResources().getDrawable(R.drawable.readafter_tile);
            bmp.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
            viewHolder.lastReadLayout.setBackgroundDrawable(bmp);
        } else {
            viewHolder.lastReadLayout.setBackgroundColor(Color.TRANSPARENT);
        }

        // TODO mostrar el numero de retweets
        /*
          if (TweetTopicsCore.isTypeList(TweetTopicsCore.TYPE_LIST_RETWEETS)) {
              if (infoTweet.getRetweetCount()>0) {
                  viewHolder.tagAvatar.setImageBitmap(Utils.getBitmapNumber(cnt, (int)infoTweet.getRetweetCount(), Color.RED, Utils.TYPE_BUBBLE, 12));
              } else {
                  viewHolder.tagAvatar.setImageBitmap(null);
              }
          }
           */
        if (infoTweet.hasGeoLocation()) {
            viewHolder.tagMap.setVisibility(View.VISIBLE);
        } else {
            viewHolder.tagMap.setVisibility(View.GONE);
        }

        if (infoTweet.hasConversation()) {
            viewHolder.tagConversation.setVisibility(View.VISIBLE);
        } else {
            viewHolder.tagConversation.setVisibility(View.GONE);
        }

        aQuery.id(viewHolder.statusText).text(Html.fromHtml(html));
        viewHolder.statusText.setTextSize(PreferenceUtils.getSizeText(getContext()));

        int typeInfo = Integer.parseInt(Utils.preference.getString("prf_username_right", "2"));
        String data = "";
        if (typeInfo == 2) {
            if (infoTweet.isRetweet()) {
                data = Html.fromHtml(infoTweet.getSourceRetweet()).toString();
            } else {
                data = Html.fromHtml(infoTweet.getSource()).toString();
            }
        } else if (typeInfo == 3) {
            data = infoTweet.getFullname();
        }
        aQuery.id(viewHolder.sourceText).text(data);
        viewHolder.sourceText.setTextSize(PreferenceUtils.getSizeTitles(getContext())-1);

        if (infoTweet.isRetweet()) {
            viewHolder.retweetLayout.setVisibility(View.VISIBLE);
            aQuery.id(viewHolder.screenName).text(infoTweet.getUsernameRetweet());
            aQuery.id(viewHolder.retweetUser).text(infoTweet.getUsername());
            aQuery.id(viewHolder.retweetText).text(R.string.retweet_by);
            viewHolder.retweetAvatar.setVisibility(View.VISIBLE);
        } else {
            aQuery.id(viewHolder.screenName).text(infoTweet.getUsername());
            viewHolder.retweetLayout.setVisibility(View.GONE);
        }

        // si es un DM usamos el layout de retweet para poner el nombre al que le escribimos
        if (infoTweet.isDm()) {
            if (!infoTweet.getToUsername().equals("") && !infoTweet.getToUsername().equals(usernameColumn)) {
                viewHolder.retweetLayout.setVisibility(View.VISIBLE);
                aQuery.id(viewHolder.retweetUser).text(infoTweet.getUsername());
                aQuery.id(viewHolder.retweetText).text(R.string.sent_to);
                viewHolder.retweetAvatar.setVisibility(View.GONE);
            }
        }

        viewHolder.screenName.setTextSize(PreferenceUtils.getSizeTitles(getContext()));

        aQuery.id(viewHolder.dateText).text(infoTweet.getTime(getContext()));
        viewHolder.dateText.setTextSize(PreferenceUtils.getSizeTitles(getContext())-4);

        String mUrlAvatar = infoTweet.getUrlAvatar();
        String mRetweetUrlAvatar = infoTweet.getUrlAvatarRetweet();

        boolean isRetweet = infoTweet.isRetweet();

        if (isRetweet) {

            Bitmap retweetAvatar = aQuery.getCachedImage(mUrlAvatar);
            if (retweetAvatar!=null) {
                aQuery.id(viewHolder.retweetAvatar).image(retweetAvatar);
            } else {
                aQuery.id(viewHolder.retweetAvatar).image(mUrlAvatar, true, true, 0, R.drawable.avatar_small);
            }

            Bitmap avatar = aQuery.getCachedImage(mRetweetUrlAvatar);
            if (avatar!=null) {
                aQuery.id(viewHolder.avatarView).image(avatar);
            } else {
                aQuery.id(viewHolder.avatarView).image(mRetweetUrlAvatar, true, true, 0, R.drawable.avatar, aQuery.getCachedImage(R.drawable.avatar), 0);
            }

            viewHolder.avatarView.setTag(infoTweet.getUsernameRetweet());

        } else {

            Bitmap avatar = aQuery.getCachedImage(mUrlAvatar);
            if (avatar!=null) {
                aQuery.id(viewHolder.avatarView).image(avatar);
            } else {
                aQuery.id(viewHolder.avatarView).image(mUrlAvatar, true, true, 0, R.drawable.avatar, aQuery.getCachedImage(R.drawable.avatar), 0);
            }

            viewHolder.avatarView.setTag(infoTweet.getUsername());
        }


        viewHolder.avatarView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (getActivity() instanceof BaseLayersActivity) {
                    Bundle bundle = new Bundle();
                    bundle.putString(UserActivity.KEY_EXTRAS_USER, v.getTag().toString());
                    ((BaseLayersActivity)getActivity()).startAnimationActivity(UserActivity.class, bundle);
                }
            }

        });

        viewHolder.statusText.setTag(infoTweet);
        // buscar imagenes de los tweets

        //ArrayList<String> links = LinksUtils.pullLinks(infoTweet.getText(), infoTweet.getContentURLs());

        String linkForImage = infoTweet.getBestLink();

        if (linkForImage.equals("")) {
            viewHolder.tweetPhotoImgContainer.setVisibility(View.GONE);
        } else {
            viewHolder.tweetPhotoImgContainer.setTag(infoTweet);
            viewHolder.tweetPhotoImgContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callLinksIfIsPossible(view);
                }
            });

            viewHolder.tweetPhotoImgContainer.setVisibility(View.VISIBLE);

            if (infoTweet.getLinksCount()>1) {
                viewHolder.tweetPhotoImgContainer.setBackgroundResource(R.drawable.container_image_multiple);
            } else {
                viewHolder.tweetPhotoImgContainer.setBackgroundResource(R.drawable.container_image_simple);
            }

            InfoLink infoLink = null;

            if (CacheData.existCacheInfoLink(linkForImage)) {
                infoLink = CacheData.getCacheInfoLink(linkForImage);
            }

            int typeResource = getTypeResource(linkForImage);

            if (infoLink!=null) {

                String thumb = infoLink.getLinkImageThumb();

                if (thumb.equals("")) {
                    aQuery.id(viewHolder.tweetPhotoImg).image(typeResource);
                } else {
                    Bitmap image = aQuery.getCachedImage(infoLink.getLinkImageThumb());
                    if (image!=null) {
                        aQuery.id(viewHolder.tweetPhotoImg).image(image);
                    } else {
                        aQuery.id(viewHolder.tweetPhotoImg).image(infoLink.getLinkImageThumb(), true, true, 0, typeResource, aQuery.getCachedImage(typeResource), 0);
                    }
                }

            } else { // si no tenemos InfoLink en cache

                aQuery.id(viewHolder.tweetPhotoImg).image(typeResource);
            }
        }

        return convertView;

    }

    private int getTypeResource(String linkForImage) {
        int res = 0;
        if (LinksUtils.isLinkImage(linkForImage)) {
            if (LinksUtils.isLinkVideo(linkForImage)) {
                res = R.drawable.icon_tweet_video;
            } else {
                res = R.drawable.icon_tweet_image;
            }
        } else {
            if (linkForImage.startsWith("@")) {
                res = R.drawable.icon_tweet_user;
            } else if (linkForImage.startsWith("#")) {
                res = R.drawable.icon_tweet_hashtag ;
            } else {
                res = R.drawable.icon_tweet_link;
            }
        }
        return res;
    }

    public void addHideMessages(int hide_messages) {
        this.hide_messages += hide_messages;
    }

    public int getHideMessages() {
        return this.hide_messages;
    }

    public void setHideMessages(int hide_messages) {
        this.hide_messages = hide_messages;
    }

    public int getLastReadPosition() {
        return this.last_read_position;
    }

    public void setLastReadPosition(int last_read_position) {
        this.last_read_position = last_read_position;
    }

}
