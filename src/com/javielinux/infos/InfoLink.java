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

package com.javielinux.infos;

public class InfoLink {

    public static final int IMAGE = 0;
    public static final int VIDEO = 1;
    public static final int GENERAL = 2;
    public static final int TWEETOPICS_QR = 3;
    public static final int TWEETOPICS_THEME = 4;
    public static final int TWEET = 5;

	private String title = "";
	private String description = "";
	private int durationVideo = 0;
	private String originalLink = "";
	private String link = "";
	private String service = "";
	private int type = 0; // 0 -> imagen 1 -> video 2 -> Enlace  3 -> QR TweetTopics 4 -> Theme TweetTopics 5 -> Es un tweet
	private String linkImageThumb = "";
	private String linkImageLarge = "";
	private boolean extensiveInfo = false;
	private long tweetId = 0; // solo si es un tweet
	private String tweetUser = ""; // solo si es un tweet
	
	public InfoLink() {
		
	}
	/*
	public InfoLink copy() {
		InfoLink another = new InfoLink();
		another.setTitle(title);
		another.setDescription(description);
		another.setDurationVideo(durationVideo);
		another.setOriginalLink(originalLink);
		another.setLink(link);
		another.setBitmapThumb(bmpThumb);
		another.setBitmapLarge(bmpLarge);
		another.setService(service);
		another.setType(type);
		another.setLinkImageThumb(linkImageThumb);
		another.setLinkImageLarge(linkImageLarge);
		another.setExtensiveInfo(extensiveInfo);
		return another;
	}
*/
	public void setLink(String link) {
		this.link = link;
	}

	public String getLink() {
		return link;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getService() {
		return service;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getType() {
		return type;
	}
	
	public void setLinkImageThumb(String linkImageThumb) {
		this.linkImageThumb = linkImageThumb;
	}

	public String getLinkImageThumb() {
		return linkImageThumb;
	}

	public void setLinkImageLarge(String linkImageLarge) {
		this.linkImageLarge = linkImageLarge;
	}

	public String getLinkImageLarge() {
		return linkImageLarge;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public void setDurationVideo(int durationVideo) {
		this.durationVideo = durationVideo;
	}

	public int getDurationVideo() {
		return durationVideo;
	}

	public void setOriginalLink(String originalLink) {
		this.originalLink = originalLink;
	}

	public String getOriginalLink() {
		return originalLink;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public void setExtensiveInfo(boolean extensiveInfo) {
		this.extensiveInfo = extensiveInfo;
	}

	public boolean isExtensiveInfo() {
		return extensiveInfo;
	}
	public void setTweetId(long tweetId) {
		this.tweetId = tweetId;
	}
	public long getTweetId() {
		return tweetId;
	}
	public void setTweetUser(String tweetUser) {
		this.tweetUser = tweetUser;
	}
	public String getTweetUser() {
		return tweetUser;
	}
	
}
