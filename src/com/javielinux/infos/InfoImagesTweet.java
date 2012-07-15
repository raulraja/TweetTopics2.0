package com.javielinux.infos;


import android.graphics.Bitmap;

import java.util.ArrayList;

public class InfoImagesTweet {

	private int position = 0;
	//private Drawable avatar = null;
	private Bitmap bmpAvatar = null;
	private Bitmap bmpAvatarRetweet = null;
	private ArrayList<InfoLink> images;
	
	private boolean retweet = false; 
	
	public InfoImagesTweet(int pos) {
		position = pos;
		images = new ArrayList<InfoLink>();
	}
	
	public void addPosition(int p) {
		position += p;
	}

	public int getPosition() {
		return position;
	}
	/*
	public void setAvatar(Drawable avatar) {
		this.avatar = avatar;
	}

	public Drawable getAvatar() {
		return avatar;
	}
*/
	public void addImage(InfoLink image) {
		images.add(image);
	}
	
	public void setImages(ArrayList<InfoLink> imgs) {
		images = imgs;
	}

	public ArrayList<InfoLink> getImages() {
		return images;
	}
	
	public int getImagesCount() {
		return images.size();
	}

	public void setBmpAvatar(Bitmap bmp) {
		this.bmpAvatar = bmp;
	}

	public Bitmap getBmpAvatar() {
		return bmpAvatar;
	}
	
	public void setBmpAvatarRetweet(Bitmap bmp) {
		this.bmpAvatarRetweet = bmp;
	}

	public Bitmap getBmpAvatarRetweet() {
		return bmpAvatarRetweet;
	}

	public void setRetweet(boolean retweet) {
		this.retweet = retweet;
	}

	public boolean isRetweet() {
		return retweet;
	}
}
