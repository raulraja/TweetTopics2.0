package infos;

import android.graphics.Bitmap;
import com.javielinux.tweettopics2.TweetTopicsCore;
import com.javielinux.utils.Utils;

import java.util.HashMap;

public class CacheData {
	
	/*
	 * TRABAJO CON USUARIOS 
	 */
	
	public static HashMap<String,InfoUsers> CacheUsers = new HashMap<String,InfoUsers>();
	
	public static void addCacheUsers(InfoUsers user) {
		if (CacheUsers!=null) {
			if (!existCacheUser(user.getName())) {
				CacheUsers.put(user.getName(), user);
			}
		}
	}
	
	public static InfoUsers getCacheUser(String name) {
		if (CacheUsers!=null && CacheUsers.containsKey(name)) {
			return CacheUsers.get(name);
		}
		return null;
	}
	
	public static boolean existCacheUser(String name) {
		if (CacheUsers!=null && CacheUsers.containsKey(name)) {
			return true;
		}
		return false;
	}
	
	/*
	 * TRABAJO CON IMAGENES Y AVATARS
	 */
	
	public static HashMap<String,Bitmap> mCacheAvatars_Users = new HashMap<String,Bitmap>();
	public static HashMap<String,InfoLink> mCacheImages_Users = new HashMap<String,InfoLink>();
	
	public static HashMap<String,Bitmap> mCacheAvatars_Other = new HashMap<String,Bitmap>();
	public static HashMap<String,InfoLink> mCacheImages_Other = new HashMap<String,InfoLink>();
	
	
	public static void clearChace_Users() {
		mCacheAvatars_Users.clear();
		mCacheImages_Users.clear();
	}
	
	public static void clearChace_Others() {
		mCacheAvatars_Other.clear();
		mCacheImages_Other.clear();
	}
	
	public static void putCacheAvatars(String avatar, Bitmap bmp) {
		if (TweetTopicsCore.isTypeList(TweetTopicsCore.TYPE_LIST_COLUMNUSER)) {
			mCacheAvatars_Users.put(avatar, bmp);
		} else {
			mCacheAvatars_Other.put(avatar, bmp);
		}
	}
	
	public static void putCacheImages(String image, InfoLink il) {
		if (TweetTopicsCore.isTypeList(TweetTopicsCore.TYPE_LIST_COLUMNUSER)) {
			mCacheImages_Users.put(image, il);
		} else {
			mCacheImages_Other.put(image, il);
		}
	}
	
	public static HashMap<String,Bitmap> getCacheAvatars() {
		if (TweetTopicsCore.isTypeList(TweetTopicsCore.TYPE_LIST_COLUMNUSER)) {
			return mCacheAvatars_Users;
		} else {
			return mCacheAvatars_Other;
		}
	}
	
	public static HashMap<String,InfoLink> getCacheImages() {
		if (TweetTopicsCore.isTypeList(TweetTopicsCore.TYPE_LIST_COLUMNUSER)) {
			return mCacheImages_Users;
		} else {
			return mCacheImages_Other;
		}
	}
	
	public static InfoLink getInfoLinkCaches(String link) {
		if (CacheData.mCacheImages_Users.containsKey(link)) {
			return CacheData.mCacheImages_Users.get(link);		
		}
		if (CacheData.mCacheImages_Other.containsKey(link)) {
			return CacheData.mCacheImages_Other.get(link);			
		}
		return null;
	}

    /*
      * TRABAJO CON MEDIAS URLs
      */

    public static HashMap<String,Utils.URLContent> CacheURLsMedia = new HashMap<String,Utils.URLContent>();

    public static void putURLMedia(String url, Utils.URLContent content) {
        CacheURLsMedia.put(url, content);
    }

    public static Utils.URLContent getURLMedia(String name) {
        if (CacheURLsMedia!=null && CacheURLsMedia.containsKey(name)) {
            return CacheURLsMedia.get(name);
        }
        return null;
    }

    public static boolean existURLMedia(String name) {
        if (CacheURLsMedia!=null && CacheURLsMedia.containsKey(name)) {
            return true;
        }
        return false;
    }

}
