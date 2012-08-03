package com.javielinux.adapters;


import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.androidquery.AQuery;
import com.javielinux.api.APIDelegate;
import com.javielinux.api.APITweetTopics;
import com.javielinux.api.request.LoadLinkRequest;
import com.javielinux.api.request.LoadUserRequest;
import com.javielinux.api.response.ErrorResponse;
import com.javielinux.api.response.LoadLinkResponse;
import com.javielinux.api.response.LoadUserResponse;
import com.javielinux.infos.InfoLink;
import com.javielinux.infos.InfoUsers;
import com.javielinux.tweettopics2.R;
import com.javielinux.utils.CacheData;
import com.javielinux.utils.Utils;

import java.util.ArrayList;

public class TweetsLinkAdapter extends ArrayAdapter<String> {

    public static class ViewHolder {

        public RelativeLayout containerLoading;
        public TextView txtLoading;

        public RelativeLayout containerImage;
        public ImageView imgImage;
        public TextView linkImage;

        public RelativeLayout containerVideo;
        public ImageView imgVideo;
        public TextView txtTitleVideo;
        public TextView txtDurationVideo;

        public RelativeLayout containerLink;
        public ImageView imgLink;
        public TextView txtLinkURL;
        public TextView txtLinkTitle;
        public TextView txtLinkDescription;

        public RelativeLayout containerUser;
        public ImageView userAvatar;
        public TextView txtUserName;
        public TextView txtUserCounters;

        public RelativeLayout containerHashTag;
        public TextView txtHashTag;

    }

    private ArrayList<String> linksWithErrors = new ArrayList<String>();

    private LoaderManager loaderManager;
    private AQuery listAQuery;

    public TweetsLinkAdapter(FragmentActivity activity, LoaderManager loaderManager, ArrayList<String> statii) {
        super(activity, android.R.layout.simple_list_item_1, statii);
        this.loaderManager = loaderManager;
        listAQuery = new AQuery(activity);
    }

    public static ViewHolder generateViewHolder(View v) {

        ViewHolder viewHolder = new ViewHolder();

        viewHolder.containerLoading = (RelativeLayout) v.findViewById(R.id.tweet_links_row_container_loading);
        viewHolder.txtLoading = (TextView) v.findViewById(R.id.tweet_links_row_loading_text);

        viewHolder.containerImage = (RelativeLayout) v.findViewById(R.id.tweet_links_row_container_image);
        viewHolder.imgImage = (ImageView) v.findViewById(R.id.tweet_links_row_image);
        viewHolder.linkImage = (TextView) v.findViewById(R.id.tweet_links_row_image_link);

        viewHolder.containerVideo = (RelativeLayout) v.findViewById(R.id.tweet_links_row_container_video);
        viewHolder.imgVideo = (ImageView) v.findViewById(R.id.tweet_links_row_video);
        viewHolder.txtTitleVideo = (TextView) v.findViewById(R.id.tweet_links_row_video_title);
        viewHolder.txtDurationVideo = (TextView) v.findViewById(R.id.tweet_links_row_video_duration);

        viewHolder.containerLink = (RelativeLayout) v.findViewById(R.id.tweet_links_row_container_link);
        viewHolder.imgLink = (ImageView) v.findViewById(R.id.tweet_links_row_link_image);
        viewHolder.txtLinkURL = (TextView) v.findViewById(R.id.tweet_links_row_link_url);
        viewHolder.txtLinkTitle = (TextView) v.findViewById(R.id.tweet_links_row_link_title);
        viewHolder.txtLinkDescription = (TextView) v.findViewById(R.id.tweet_links_row_link_description);

        viewHolder.containerUser = (RelativeLayout) v.findViewById(R.id.tweet_links_row_container_user);
        viewHolder.userAvatar = (ImageView) v.findViewById(R.id.tweet_links_row_user_avatar);
        viewHolder.txtUserName = (TextView) v.findViewById(R.id.tweet_links_row_user_name);
        viewHolder.txtUserCounters = (TextView) v.findViewById(R.id.tweet_links_row_user_counters);

        viewHolder.containerHashTag = (RelativeLayout) v.findViewById(R.id.tweet_links_row_container_hashtag);
        viewHolder.txtHashTag = (TextView) v.findViewById(R.id.tweet_links_row_hashtag_name);

        return viewHolder;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String link = getItem(position);
        View v = null;

        ViewHolder viewHolder;

        if (null == convertView) {
            v = View.inflate(getContext(), R.layout.tweet_links_row, null);
            viewHolder = generateViewHolder(v);
            v.setTag(generateViewHolder(v));
        } else {
            v = convertView;
            viewHolder = (ViewHolder) v.getTag();
        }

        if (linksWithErrors.contains(link)) {
            viewHolder.containerLoading.setVisibility(View.GONE);
            viewHolder.containerImage.setVisibility(View.GONE);
            viewHolder.containerVideo.setVisibility(View.GONE);
            viewHolder.containerLink.setVisibility(View.GONE);
            viewHolder.containerUser.setVisibility(View.GONE);
            viewHolder.containerHashTag.setVisibility(View.VISIBLE);

            viewHolder.txtHashTag.setText(link);
        } else {

            AQuery aQuery = listAQuery.recycle(convertView);

            if (link.startsWith("@")) {  // es un usuario
                InfoUsers user = CacheData.getCacheUser(link.replace("@", ""));

                if (user!=null) {
                    viewHolder.containerLoading.setVisibility(View.GONE);
                    viewHolder.containerImage.setVisibility(View.GONE);
                    viewHolder.containerVideo.setVisibility(View.GONE);
                    viewHolder.containerLink.setVisibility(View.GONE);
                    viewHolder.containerUser.setVisibility(View.VISIBLE);
                    viewHolder.containerHashTag.setVisibility(View.GONE);

                    aQuery.id(viewHolder.txtUserName).text("@"+user.getName());
                    aQuery.id(viewHolder.txtUserCounters).text(getContext().getString(R.string.info_user_counters, user.getTweets(), user.getFollowers(), user.getFollowing()));

                    aQuery.id(viewHolder.userAvatar).image(user.getUrlAvatar(), true, true, 0, R.drawable.avatar, aQuery.getCachedImage(R.drawable.avatar), 0);

                } else {
                    viewHolder.containerLoading.setVisibility(View.VISIBLE);
                    aQuery.id(viewHolder.txtLoading).text(link);
                    viewHolder.containerImage.setVisibility(View.GONE);
                    viewHolder.containerVideo.setVisibility(View.GONE);
                    viewHolder.containerLink.setVisibility(View.GONE);
                    viewHolder.containerUser.setVisibility(View.GONE);
                    viewHolder.containerHashTag.setVisibility(View.GONE);
                    loadUser(link);
                }

            } else if (link.startsWith("#")) {  // es un hashtag
                viewHolder.containerLoading.setVisibility(View.GONE);
                viewHolder.containerImage.setVisibility(View.GONE);
                viewHolder.containerVideo.setVisibility(View.GONE);
                viewHolder.containerLink.setVisibility(View.GONE);
                viewHolder.containerUser.setVisibility(View.GONE);
                viewHolder.containerHashTag.setVisibility(View.VISIBLE);

                viewHolder.txtHashTag.setText(link);

            } else {

                InfoLink il = CacheData.getCacheInfoLink(link);

                if (il == null) {
                    viewHolder.containerLoading.setVisibility(View.VISIBLE);
                    aQuery.id(viewHolder.txtLoading).text(link);
                    viewHolder.containerImage.setVisibility(View.GONE);
                    viewHolder.containerVideo.setVisibility(View.GONE);
                    viewHolder.containerLink.setVisibility(View.GONE);
                    viewHolder.containerUser.setVisibility(View.GONE);
                    viewHolder.containerHashTag.setVisibility(View.GONE);
                    loadLink(link, null);
                } else if (!il.isExtensiveInfo()) {
                    viewHolder.containerLoading.setVisibility(View.VISIBLE);
                    aQuery.id(viewHolder.txtLoading).text(link);
                    viewHolder.containerImage.setVisibility(View.GONE);
                    viewHolder.containerVideo.setVisibility(View.GONE);
                    viewHolder.containerLink.setVisibility(View.GONE);
                    viewHolder.containerUser.setVisibility(View.GONE);
                    viewHolder.containerHashTag.setVisibility(View.GONE);
                    loadLink(link, il);
                } else {

                    switch (il.getType()) {
                        case InfoLink.IMAGE:
                            viewHolder.containerLoading.setVisibility(View.GONE);
                            viewHolder.containerImage.setVisibility(View.VISIBLE);
                            viewHolder.containerVideo.setVisibility(View.GONE);
                            viewHolder.containerLink.setVisibility(View.GONE);
                            viewHolder.containerUser.setVisibility(View.GONE);
                            viewHolder.containerHashTag.setVisibility(View.GONE);
                            aQuery.id(viewHolder.imgImage).image(il.getLinkImageLarge(), true, true, 0, R.drawable.icon_tweet_image_large, aQuery.getCachedImage(R.drawable.icon_tweet_image_large), 0);
                            aQuery.id(viewHolder.linkImage).text(il.getService());
                            break;
                        case InfoLink.VIDEO:
                            viewHolder.containerLoading.setVisibility(View.GONE);
                            viewHolder.containerImage.setVisibility(View.GONE);
                            viewHolder.containerVideo.setVisibility(View.VISIBLE);
                            viewHolder.containerLink.setVisibility(View.GONE);
                            viewHolder.containerUser.setVisibility(View.GONE);
                            viewHolder.containerHashTag.setVisibility(View.GONE);
                            aQuery.id(viewHolder.imgVideo).image(il.getLinkImageLarge(), true, true, 0, R.drawable.icon_tweet_video_large, aQuery.getCachedImage(R.drawable.icon_tweet_video_large), 0);
                            aQuery.id(viewHolder.txtTitleVideo).text(il.getTitle());
                            if (il.getDurationVideo()==0) {
                                viewHolder.txtDurationVideo.setVisibility(View.GONE);
                            } else {
                                viewHolder.txtDurationVideo.setVisibility(View.VISIBLE);
                                aQuery.id(viewHolder.txtDurationVideo).text(getContext().getString(R.string.duration) + ": " + Utils.seconds2Time(il.getDurationVideo(), false));
                            }
                            break;
                        default:
                            viewHolder.containerLoading.setVisibility(View.GONE);
                            viewHolder.containerImage.setVisibility(View.GONE);
                            viewHolder.containerVideo.setVisibility(View.GONE);
                            viewHolder.containerLink.setVisibility(View.VISIBLE);
                            viewHolder.containerUser.setVisibility(View.GONE);
                            viewHolder.containerHashTag.setVisibility(View.GONE);

                            viewHolder.txtLinkURL.setText(il.getLink());
                            viewHolder.txtLinkTitle.setText(il.getTitle());
                            if ("".equals(il.getDescription())) {
                                viewHolder.txtLinkDescription.setVisibility(View.GONE);
                            } else {
                                viewHolder.txtLinkDescription.setVisibility(View.VISIBLE);
                                viewHolder.txtLinkDescription.setText(il.getDescription());
                            }

                            if ("".equals(il.getLinkImageThumb())) {
                                viewHolder.imgLink.setVisibility(View.GONE);
                            } else {
                                viewHolder.imgLink.setVisibility(View.VISIBLE);
                                aQuery.id(viewHolder.imgLink).image(il.getLinkImageThumb(), true, true, 0, R.drawable.icon_tweet_link, aQuery.getCachedImage(R.drawable.icon_tweet_link), 0);
                            }

                            break;
                    }


                }
            }
        }

        return v;
    }

    void loadLink(final String link, InfoLink infoLink) {
        APITweetTopics.execute(getContext(), loaderManager, new APIDelegate<LoadLinkResponse>() {
            @Override
            public void onResults(LoadLinkResponse result) {
                notifyDataSetChanged();
            }

            @Override
            public void onError(ErrorResponse error) {
                linksWithErrors.add(link);
                notifyDataSetChanged();
            }
        }, new LoadLinkRequest(link, infoLink));
    }

    void loadUser(final String user) {
        APITweetTopics.execute(getContext(), loaderManager, new APIDelegate<LoadUserResponse>() {
            @Override
            public void onResults(LoadUserResponse result) {
                notifyDataSetChanged();
            }

            @Override
            public void onError(ErrorResponse error) {
                linksWithErrors.add(user);
                notifyDataSetChanged();
            }
        }, new LoadUserRequest(user.replace("@", "")));
    }


}