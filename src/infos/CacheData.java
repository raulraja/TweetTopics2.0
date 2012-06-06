package infos;

import android.graphics.Bitmap;
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
	
	
	public static void clearChace_Users() {
		mCacheAvatars_Users.clear();
		mCacheImages_Users.clear();
	}
	
	public static void putCacheAvatars(String avatar, Bitmap bmp) {
		mCacheAvatars_Users.put(avatar, bmp);
	}
	
	public static void putCacheImages(String image, InfoLink il) {
		mCacheImages_Users.put(image, il);
	}
	
	public static HashMap<String,Bitmap> getCacheAvatars() {
		return mCacheAvatars_Users;
	}
	
	public static HashMap<String,InfoLink> getCacheImages() {
		return mCacheImages_Users;
	}
	
	public static InfoLink getInfoLinkCaches(String link) {
		if (CacheData.mCacheImages_Users.containsKey(link)) {
			return CacheData.mCacheImages_Users.get(link);		
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
