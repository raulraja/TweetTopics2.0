package com.javielinux.utils;

import com.javielinux.infos.InfoLink;
import com.javielinux.infos.InfoUsers;

import java.util.HashMap;

public class CacheData {
	
	/*
	 * TRABAJO CON USUARIOS 
	 */
	
	public static HashMap<String,InfoUsers> cacheUsers = new HashMap<String,InfoUsers>();
	
	public static void addCacheUsers(InfoUsers user) {
		if (cacheUsers !=null) {
			if (!existCacheUser(user.getName())) {
				cacheUsers.put(user.getName(), user);
			}
		}
	}
	
	public static InfoUsers getCacheUser(String name) {
        name = name.replace("@", "");
		if (cacheUsers !=null && cacheUsers.containsKey(name)) {
			return cacheUsers.get(name);
		}
		return null;
	}
	
	public static boolean existCacheUser(String name) {
		if (cacheUsers !=null && cacheUsers.containsKey(name)) {
			return true;
		}
		return false;
	}
	
	/*
	 * TRABAJO CON IMAGENES Y AVATARS
	 */
	
	public static HashMap<String,InfoLink> cacheInfoLinks = new HashMap<String,InfoLink>();
	

	public static void putCacheInfoLinks(String image, InfoLink il) {
		cacheInfoLinks.put(image, il);
	}

	public static InfoLink getCacheInfoLink(String link) {
		if (CacheData.cacheInfoLinks.containsKey(link)) {
			return CacheData.cacheInfoLinks.get(link);
		}
		return null;
	}

    public static boolean existCacheInfoLink(String name) {
        if (cacheInfoLinks!=null && cacheInfoLinks.containsKey(name)) {
            return true;
        }
        return false;
    }

    /*
      * TRABAJO CON MEDIAS URLs
      */

    public static HashMap<String,Utils.URLContent> cacheURLsMedia = new HashMap<String,Utils.URLContent>();

    public static void putURLMedia(String url, Utils.URLContent content) {
        // TODO Hack-brutal... a quitar cuando se arregle el tema en twitter4j
        content.linkMediaLarge = content.linkMediaLarge.replace(":medium","");
        cacheURLsMedia.put(url, content);
    }

    public static Utils.URLContent getURLMedia(String name) {
        if (cacheURLsMedia !=null && cacheURLsMedia.containsKey(name)) {
            return cacheURLsMedia.get(name);
        }
        return null;
    }

    public static boolean existURLMedia(String name) {
        if (cacheURLsMedia !=null && cacheURLsMedia.containsKey(name)) {
            return true;
        }
        return false;
    }

}
