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

package com.javielinux.task;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import com.javielinux.utils.Utils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class LoadImageAutoCompleteAsyncTask extends AsyncTask<String, Void, Bitmap> {

	public interface LoadImageAutoCompleteAsyncTaskResponder {
		public void imageAutoCompleteLoading();
		public void imageAutoCompleteLoadCancelled();
		public void imageAutoCompleteLoaded(Bitmap bmp);
	}

	private LoadImageAutoCompleteAsyncTaskResponder responder;
		
	public LoadImageAutoCompleteAsyncTask(LoadImageAutoCompleteAsyncTaskResponder responder) {
		this.responder = responder;
	}
	

	@Override
	protected Bitmap doInBackground(String... arg) {
		Bitmap bmp = null;
		try {
			URL url = new URL(arg[0]);
			bmp = BitmapFactory.decodeStream(new Utils.FlushedInputStream(url.openStream()));	
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return bmp;

	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		responder.imageAutoCompleteLoading();
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
		responder.imageAutoCompleteLoadCancelled();
	}

	@Override
	protected void onPostExecute(Bitmap bmp) {
		super.onPostExecute(bmp);
		responder.imageAutoCompleteLoaded(bmp);
	}

}
