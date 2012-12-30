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

package com.javielinux.utils;

import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.javielinux.infos.InfoLink;
import com.javielinux.infos.InfoUsers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CacheData {

    static private CacheData INSTANCE;

    private CacheData() {

    }

    static public CacheData getInstance() {
        if (INSTANCE==null) {
            INSTANCE = new CacheData();
        }
        return INSTANCE;
    }

	/*
	 * TRABAJO CON USUARIOS 
	 */
	
	private HashMap<String,InfoUsers> cacheUsers = new HashMap<String,InfoUsers>();
	
	public void addCacheUsers(InfoUsers user) {
		if (cacheUsers !=null) {
			if (!existCacheUser(user.getName())) {
				cacheUsers.put(user.getName(), user);
			}
		}
	}
	
	public InfoUsers getCacheUser(String name) {
        name = name.replace("@", "");
		if (cacheUsers !=null && cacheUsers.containsKey(name)) {
			return cacheUsers.get(name);
		}
		return null;
	}
	
	public boolean existCacheUser(String name) {
		if (cacheUsers !=null && cacheUsers.containsKey(name)) {
			return true;
		}
		return false;
	}
	
	/*
	 * TRABAJO CON IMAGENES Y AVATARS
	 */
	
	private HashMap<String,InfoLink> cacheInfoLinks = new HashMap<String,InfoLink>();
	

	public void putCacheInfoLinks(String image, InfoLink il) {
		cacheInfoLinks.put(image, il);
	}

	public InfoLink getCacheInfoLink(String link) {
		if (cacheInfoLinks.containsKey(link)) {
			return cacheInfoLinks.get(link);
		}
		return null;
	}

    public boolean existCacheInfoLink(String name) {
        if (cacheInfoLinks!=null && cacheInfoLinks.containsKey(name)) {
            return true;
        }
        return false;
    }

    /*
      * TRABAJO CON MEDIAS URLs
      */

    private HashMap<String,Utils.URLContent> cacheURLsMedia = new HashMap<String,Utils.URLContent>();

    public void putURLMedia(String url, Utils.URLContent content) {
        // TODO Hack-brutal... a quitar cuando se arregle el tema en twitter4j
        content.linkMediaLarge = content.linkMediaLarge.replace(":medium","");
        cacheURLsMedia.put(url, content);
    }

    public Utils.URLContent getURLMedia(String name) {
        if (cacheURLsMedia !=null && cacheURLsMedia.containsKey(name)) {
            return cacheURLsMedia.get(name);
        }
        return null;
    }

    public boolean existURLMedia(String name) {
        if (cacheURLsMedia !=null && cacheURLsMedia.containsKey(name)) {
            return true;
        }
        return false;
    }

    // Cosas ocultas

    private List<String> hideUser = new ArrayList<String>();
    private List<String> hideWord = new ArrayList<String>();
    private List<String> hideSource = new ArrayList<String>();



    public void fillHide() {
        hideWord.clear();
        hideUser.clear();
        hideSource.clear();
        ArrayList<Entity> words = DataFramework.getInstance().getEntityList("quiet");
        for (Entity word : words) {
            if (word.getInt("type_id") == 1) { // palabra
                hideWord.add(word.getString("word").toLowerCase());
            }
            if (word.getInt("type_id") == 2) { // usuario
                hideUser.add(word.getString("word").toLowerCase());
            }
            if (word.getInt("type_id") == 3) { // fuente
                hideSource.add(word.getString("word").toLowerCase());
            }
        }
    }

    public boolean isHideUserInText(String text) {
        return hideUser.contains(text);
    }

    public boolean isHideWordInText(String text) {
        for (String word : hideWord) {
            if (text.toLowerCase().contains(word.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    public boolean isHideSourceInText(String text) {
        for (String word : hideSource) {
            if (text.toLowerCase().contains(word.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

}
