package com.javielinux.adapters;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.androidquery.AQuery;
import com.javielinux.api.APIDelegate;
import com.javielinux.api.APITweetTopics;
import com.javielinux.api.request.LoadLinkRequest;
import com.javielinux.api.request.LoadUserRequest;
import com.javielinux.api.response.ErrorResponse;
import com.javielinux.api.response.LoadLinkResponse;
import com.javielinux.api.response.LoadUserResponse;
import com.javielinux.tweettopics2.R;
import com.javielinux.utils.LinksUtils;
import infos.CacheData;
import infos.InfoLink;
import infos.InfoUsers;

import java.util.List;

public class LinksAdapter extends BaseAdapter {

    private FragmentActivity activity;
    private List<String> links;

    private LoaderManager loaderManager;
    private AQuery listAQuery;

    public static class ViewHolder {
        public ImageView image;
        public TextView title;
    }

    public LinksAdapter(FragmentActivity activity, LoaderManager loaderManager, List<String> links)
    {
        this.activity = activity;
        this.loaderManager = loaderManager;
        this.links = links;
        listAQuery = new AQuery(activity);
    }

    public static ViewHolder generateViewHolder(View v) {

        ViewHolder viewHolder = new ViewHolder();

        viewHolder.image = (ImageView)v.findViewById(R.id.row_links_icon);
        viewHolder.title = (TextView)v.findViewById(R.id.row_links_title);

        return viewHolder;
    }
    
	@Override
	public int getCount() {
		return links.size();
	}
	

	@Override
	public Object getItem(int position) {
        return links.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        String link = links.get(position);

        if (null == convertView) {
            convertView = activity.getLayoutInflater().inflate(R.layout.row_links, parent, false);
            //convertView = View.inflate(activity, R.layout.row_links, null);
            convertView.setTag(generateViewHolder(convertView));
        }

        ViewHolder viewHolder = (ViewHolder) convertView.getTag();

        AQuery aQuery = listAQuery.recycle(convertView);
        int typeResource = getTypeResource(link);

        boolean hasImage = false;

        if (link.startsWith("@")) {
            InfoUsers infoUser = CacheData.getCacheUser(link.substring(1));
            if (infoUser!=null) {
                if (infoUser.getAvatar()!=null) viewHolder.image.setImageBitmap(infoUser.getAvatar());
                viewHolder.title.setText(writeTitle(link));
                hasImage = true;
            } else {

                aQuery.id(viewHolder.image).image(typeResource);

                APITweetTopics.execute(activity, loaderManager, new APIDelegate<LoadUserResponse>() {

                    @Override
                    public void onResults(LoadUserResponse result) {
                        //viewHolder.image.setImageBitmap(result.getInfoUsers().getAvatar());
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onError(ErrorResponse error) {
                    }
                }, new LoadUserRequest(link.substring(1)));

            }
        } else if (!link.startsWith("#")) {

            if (CacheData.getCacheImages().containsKey(link)) {
                InfoLink item = CacheData.getCacheImages().get(link);
                String thumb = item.getLinkImageThumb();
                if (thumb.equals("")) {
                    aQuery.id(viewHolder.image).image(typeResource);
                } else {
                    aQuery.id(viewHolder.image).image(thumb, true, true, 0, typeResource, aQuery.getCachedImage(typeResource), AQuery.FADE_IN_NETWORK);
                }
                hasImage = true;
                viewHolder.title.setText(writeTitle(item.getLink()));
            } else {


                APITweetTopics.execute(activity, loaderManager, new APIDelegate<LoadLinkResponse>() {

                    @Override
                    public void onResults(LoadLinkResponse result) {
                        if (result.getInfoLink()!=null) {
                            notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onError(ErrorResponse error) {

                    }
                }, new LoadLinkRequest(link, null));

            }

        }

        if (!hasImage) {
            viewHolder.title.setText(writeTitle(link));
            if (LinksUtils.isLinkImage(link)) {
                if (LinksUtils.isLinkVideo(link)) {
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


        return convertView;
	}

    private int getTypeResource(String link) {
        int res = R.drawable.icon_tweet_link;
        if (LinksUtils.isLinkImage(link)) {
            if (LinksUtils.isLinkVideo(link)) {
                res = R.drawable.icon_tweet_video;
            } else {
                res = R.drawable.icon_tweet_image;
            }
        }
        return res;
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

}
