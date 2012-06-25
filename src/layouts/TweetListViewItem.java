package layouts;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.LoaderManager;
import android.text.Html;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.javielinux.adapters.TweetsAdapter;
import com.javielinux.api.APIDelegate;
import com.javielinux.api.APITweetTopics;
import com.javielinux.api.request.LoadImageRequest;
import com.javielinux.api.response.BaseResponse;
import com.javielinux.api.response.ErrorResponse;
import com.javielinux.tweettopics2.R;
import com.javielinux.tweettopics2.ThemeManager;
import com.javielinux.tweettopics2.TweetTopicsActivity;
import com.javielinux.tweettopics2.UserActivity;
import com.javielinux.utils.PreferenceUtils;
import com.javielinux.utils.Utils;
import infos.CacheData;
import infos.InfoLink;
import infos.InfoTweet;

import java.io.File;
import java.util.ArrayList;

public class TweetListViewItem extends RelativeLayout {

    private Context context;
	
	private String mUrlAvatar = "";
	private String mRetweetUrlAvatar = "";
	
	private boolean isRetweet = false;
	private boolean hasAvatar = false;
	private boolean hasRetweetAvatar = false;

	private TweetsAdapter.ViewHolder viewHolder;


    private String linkForImage;
	
	public TweetListViewItem(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
	}
	
	private boolean searchAvatar(String url, ImageView holder) {
		if (CacheData.getCacheAvatars().containsKey(url)) {
			holder.setImageBitmap(CacheData.getCacheAvatars().get(url));
			return true;
		} else {
			File file = Utils.getFileForSaveURL(getContext(), url);
			if (file.exists()) {
                try {
                    Bitmap bmp = BitmapFactory.decodeFile(file.getAbsolutePath());
                    if (bmp != null) {
                        CacheData.putCacheAvatars(url, bmp);
                        holder.setImageBitmap(bmp);
                        return true;
                    }
                } catch (OutOfMemoryError e) {
                    e.printStackTrace();
                }
			}
            try {
			    holder.setImageResource(R.drawable.avatar);
            } catch (OutOfMemoryError e) {}
			return false;
		}
	}
	
	private InfoLink searchImage(String url) {
		if (CacheData.getCacheImages().containsKey(url)) {
			return CacheData.getCacheImages().get(url);
		} else {
			return null;
		}
	}

    private String getBestLink(ArrayList<String> links) {
        if (links.size()>0) {
            // primero buscamos un link con imagen

            for (String link : links) {
                if (!link.startsWith("@") || !link.startsWith("#")) {
                    if (Utils.isLinkImage(link)){
                        return link;
                    }
                }
            }

            // si no un link normal

            for (String link : links) {
                if (!link.startsWith("@") || !link.startsWith("#")) {
                    return link;
                }
            }


            // si no un usuario

            for (String link : links) {
                if (link.startsWith("@")) {
                    return link;
                }
            }

            // si no un hashtag

            for (String link : links) {
                if (link.startsWith("#")) {
                    return link;
                }
            }

        }
        return "";
    }

	
	public void setRow(final InfoTweet infoTweet, final Context cnt, LoaderManager loaderManager, final TweetsAdapter tweetsAdapter, String usernameColumn) {

        context = cnt;

        ThemeManager themeManager = new ThemeManager(context);

		String html = "";
		
		if (infoTweet.getTextHTMLFinal().equals("")) {
			String[] in = Utils.toHTMLTyped(context, infoTweet.getText(), infoTweet.getTextURLs());
			html = in[1];
            infoTweet.setTextFinal(in[0]);
            infoTweet.setTextHTMLFinal(in[1]);
		} else {
			html = infoTweet.getTextHTMLFinal();
		}

		viewHolder = (TweetsAdapter.ViewHolder) this.getTag();
		
		viewHolder.statusText.setTextColor(Color.parseColor("#"+themeManager.getStringColor("color_tweet_text")));
		viewHolder.sourceText.setTextColor(Color.parseColor("#"+themeManager.getStringColor("color_tweet_source")));
		viewHolder.retweetUser.setTextColor(Color.parseColor("#"+themeManager.getStringColor("color_tweet_retweet")));
		viewHolder.screenName.setTextColor(Color.parseColor("#"+themeManager.getStringColor("color_tweet_usename")));
		viewHolder.dateText.setTextColor(Color.parseColor("#"+themeManager.getStringColor("color_tweet_date")));
		
		
		if (infoTweet.isLastRead()) {
			BitmapDrawable bmp = (BitmapDrawable)cnt.getResources().getDrawable(R.drawable.readafter_tile);
			bmp.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
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
		
		viewHolder.statusText.setText(Html.fromHtml(html));
		viewHolder.statusText.setTextSize(PreferenceUtils.getSizeText(cnt));
		
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
		viewHolder.sourceText.setText(data);
		viewHolder.sourceText.setTextSize(PreferenceUtils.getSizeTitles(cnt)-1);
		
		if (infoTweet.isRetweet()) {
			viewHolder.retweetLayout.setVisibility(View.VISIBLE);
			viewHolder.screenName.setText(infoTweet.getUsernameRetweet());
			viewHolder.retweetUser.setText(infoTweet.getUsername());
			viewHolder.retweetText.setText(R.string.retweet_by);
			viewHolder.retweetAvatar.setVisibility(View.VISIBLE);
		} else {
			viewHolder.screenName.setText(infoTweet.getUsername());
			viewHolder.retweetLayout.setVisibility(View.GONE);
		}

		// si es un DM usamos el layout de retweet para poner el nombre al que le escribimos
		if (infoTweet.isDm()) {
			if (!infoTweet.getToUsername().equals("") && !infoTweet.getToUsername().equals(usernameColumn)) {
				viewHolder.retweetLayout.setVisibility(View.VISIBLE);
				viewHolder.retweetUser.setText(infoTweet.getToUsername());
				viewHolder.retweetText.setText(R.string.sent_to);
				viewHolder.retweetAvatar.setVisibility(View.GONE);
			}			
		}

		viewHolder.screenName.setTextSize(PreferenceUtils.getSizeTitles(cnt));
				
		viewHolder.dateText.setText(infoTweet.getTime(context));		
		viewHolder.dateText.setTextSize(PreferenceUtils.getSizeTitles(cnt)-4);
		
		
		// buscar avatar en la cache de memoria del programa
		
		mUrlAvatar = infoTweet.getUrlAvatar();
		mRetweetUrlAvatar = infoTweet.getUrlAvatarRetweet();

		ArrayList<String> searchAvatars = new ArrayList<String>();
		ArrayList<String> searchImages = new ArrayList<String>();
		
		isRetweet = infoTweet.isRetweet(); 
		
		if (infoTweet.isRetweet()) {
			hasAvatar = searchAvatar(mUrlAvatar, viewHolder.retweetAvatar);
			if (!hasAvatar) {
				searchAvatars.add(mUrlAvatar);
			}
			hasRetweetAvatar = this.searchAvatar(mRetweetUrlAvatar, viewHolder.avatarView);
			if (!hasRetweetAvatar) {
				searchAvatars.add(mRetweetUrlAvatar);
			}
			viewHolder.avatarView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
                    Intent intent = new Intent(cnt, UserActivity.class);
                    intent.putExtra(UserActivity.KEY_EXTRAS_USER, infoTweet.getUsernameRetweet());
                    cnt.startActivity(intent);
				}
				
			});
		} else {
			hasAvatar = searchAvatar(mUrlAvatar, viewHolder.avatarView);
			if (!hasAvatar) {
				searchAvatars.add(mUrlAvatar);
			}
			viewHolder.avatarView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
                    Intent intent = new Intent(cnt, UserActivity.class);
                    intent.putExtra(UserActivity.KEY_EXTRAS_USER, infoTweet.getUsername());
                    cnt.startActivity(intent);
				}
				
			});
		}
		
		// buscar imagenes de los tweets
	
        ArrayList<String> links = Utils.pullLinks(infoTweet.getText(), infoTweet.getContentURLs());

        linkForImage = getBestLink(links);

        if (linkForImage.equals("")) {
            viewHolder.tweetPhotoImgContainer.setVisibility(GONE);
        } else {

            viewHolder.tweetPhotoImgContainer.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (tweetsAdapter.getActivity() instanceof TweetTopicsActivity) {
                        ((TweetTopicsActivity)tweetsAdapter.getActivity()).showLinks(viewHolder.tweetPhotoImgContainer, infoTweet.getText());
                    }
                }
            });

            viewHolder.tweetPhotoImgContainer.setVisibility(VISIBLE);

            if (links.size()>1) {
                viewHolder.tweetPhotoImgContainer.setBackgroundResource(R.drawable.container_image_multiple);
            } else {
                viewHolder.tweetPhotoImgContainer.setBackgroundResource(R.drawable.container_image_simple);
            }

            InfoLink infoLink = searchImage(linkForImage);

            if (infoLink!=null && infoLink.getBitmapThumb()!=null) {
                viewHolder.tweetPhotoImg.setImageBitmap(infoLink.getBitmapThumb());
            } else {
                if (Utils.isLinkImage(linkForImage)) {
                    if (Utils.isLinkVideo(linkForImage)) {
                        viewHolder.tweetPhotoImg.setImageResource(R.drawable.icon_tweet_video);
                    } else {
                        viewHolder.tweetPhotoImg.setImageResource(R.drawable.icon_tweet_image);
                    }
                    searchImages.add(linkForImage);
                } else {
                    if (linkForImage.startsWith("@")) {
                        viewHolder.tweetPhotoImg.setImageResource(R.drawable.icon_tweet_user);
                    } else if (linkForImage.startsWith("#")) {
                        viewHolder.tweetPhotoImg.setImageResource(R.drawable.icon_tweet_hashtag);
                    } else {
                        viewHolder.tweetPhotoImg.setImageResource(R.drawable.icon_tweet_link);
                    }
                }
            }
        }

		if (searchImages.size()+searchAvatars.size()>0) {
            Log.d(Utils.TAG,"Execute Loader IMAGE");

            APITweetTopics.execute(getContext(), loaderManager, new APIDelegate<BaseResponse>() {

                @Override
                public void onResults(BaseResponse result) {

                    if (isRetweet) {
                        if (!hasAvatar) {
                            searchAvatar(mUrlAvatar, viewHolder.retweetAvatar);
                        }
                        if (!hasRetweetAvatar) {
                            searchAvatar(mRetweetUrlAvatar, viewHolder.avatarView);
                        }
                    } else {
                        if (!hasAvatar) {
                            searchAvatar(mUrlAvatar, viewHolder.avatarView);
                        }
                    }

                    InfoLink infoLink = CacheData.getCacheImages().get(linkForImage);

                    if (infoLink != null) {
                        if (infoLink.getBitmapThumb() != null)
                            viewHolder.tweetPhotoImg.setImageBitmap(infoLink.getBitmapThumb());
                    }

                    //if (!tweetsAdapter.isFlinging()) tweetsAdapter.notifyDataSetChanged();

                }

                @Override
                public void onError(ErrorResponse error) {
                }
            }, new LoadImageRequest(searchAvatars, searchImages));

		}

	}



}
