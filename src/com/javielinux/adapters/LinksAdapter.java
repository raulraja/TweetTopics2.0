/*
 * Copyright 2012 Javier Pérez Pacheco and Francisco Díaz Rodriguez
 * TweetTopics 2.0
 * javielinux@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.javielinux.adapters;

import android.graphics.Bitmap;
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
import com.javielinux.infos.InfoLink;
import com.javielinux.infos.InfoUsers;
import com.javielinux.tweettopics2.R;
import com.javielinux.utils.CacheData;
import com.javielinux.utils.LinksUtils;

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
            convertView.setTag(generateViewHolder(convertView));
        }

        ViewHolder viewHolder = (ViewHolder) convertView.getTag();

        AQuery aQuery = listAQuery.recycle(convertView);
        int typeResource = getTypeResource(link);

        boolean hasImage = false;

        if (link.startsWith("@")) {
            InfoUsers infoUser = CacheData.getInstance().getCacheUser(link);
            if (infoUser!=null) {

                Bitmap avatar = aQuery.getCachedImage(infoUser.getUrlAvatar());
                if (avatar!=null) {
                    aQuery.id(viewHolder.image).image(avatar);
                } else {
                    aQuery.id(viewHolder.image).image(infoUser.getUrlAvatar(), true, true, 0, typeResource, aQuery.getCachedImage(typeResource), 0);
                }

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

            if (CacheData.getInstance().existCacheInfoLink(link)) {
                InfoLink item = CacheData.getInstance().getCacheInfoLink(link);
                String thumb = item.getLinkImageThumb();
                if (thumb.equals("")) {
                    aQuery.id(viewHolder.image).image(typeResource);
                } else {
                    Bitmap image = aQuery.getCachedImage(thumb);
                    if (image!=null) {
                        aQuery.id(viewHolder.image).image(image);
                    } else {
                        aQuery.id(viewHolder.image).image(thumb, true, true, 0, typeResource, aQuery.getCachedImage(typeResource), 0);
                    }
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
        if (title.startsWith("www.")) {
            title = title.substring(4);
        }
        if (title.length()>14) {
            title = title.substring(0,12)+"...";
        }
        return title;
    }

}
