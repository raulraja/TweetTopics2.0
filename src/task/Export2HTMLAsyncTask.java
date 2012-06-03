package task;

import adapters.ResponseListAdapter;
import adapters.RowResponseList;
import adapters.StatusListAdapter;
import android.os.AsyncTask;
import com.javielinux.tweettopics2.R;
import com.javielinux.tweettopics2.TweetTopicsCore;
import com.javielinux.utils.Utils;
import twitter4j.User;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;


public class Export2HTMLAsyncTask extends AsyncTask<Long, Void, String> {

	public interface Export2HTMLAsyncTaskResponder {
		public void export2HTMLLoading();
		public void export2HTMLCancelled();
		public void export2HTMLLoaded(String file);
	}

	private Export2HTMLAsyncTaskResponder responder;
	private TweetTopicsCore mTweetTopicsCore;
	private ResponseListAdapter adapterResponseList = null;
	private StatusListAdapter adapterStatusList = null;

	public Export2HTMLAsyncTask(TweetTopicsCore responder, ResponseListAdapter adapterResponseList, StatusListAdapter adapterStatusList) {
		mTweetTopicsCore = responder;
		this.adapterResponseList = adapterResponseList;
		this.adapterStatusList = adapterStatusList;
		this.responder = (Export2HTMLAsyncTaskResponder)responder;
	}

	@Override
	protected String doInBackground(Long... args) {

		try {
			
			String f = Utils.appDirectory+"export_html_"+ System.currentTimeMillis() +".html";
			
			File file = new File(f);
    		if (file.exists()) file.delete();
    		FileOutputStream fOut = new FileOutputStream(f); 
    		OutputStreamWriter osw = new OutputStreamWriter(fOut, "ISO-8859-1"); 
			
    		osw.write("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\"\n"
				+ "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n"
				+ "<html>\n"
				+ "<head>\n"
				+ "<title>Listado de tweets</title>\n"
				+ "<style type=\"text/css\">\n"
				+ "body {\n"
				+ "padding: 10px;\n"
				+ "background-color: #89ace5;\n"
				+ "}\n"
				+ "div.tweet {\n"
				+ "font-family: Helvetica, Geneva, Arial, sans-serif;\n"
				+ "border: 2px solid #286cd9;\n"
				+ "background-color: #ffffff;\n"
				+ "-webkit-border-radius: 10px;\n"
				+ "moz-border-radius: 10px;\n"
				+ "border-radius: 10px;\n"
				+ "padding: 5px;\n"
				+ "margin-bottom: 10px;\n"
				+ "}\n"
				+ "div.username {\n"
				+ "font-size: 13px;\n"
				+ "font-weight: bolder;\n"
				+ "}\n" 
				+ "div.date {\n"
				+ "font-size: 11px;\n"
				+ "color: #999999;\n"
				+ "}\n"
				+ ".image img {\n"
				+ "width: 50px;\n"
				+ "height: 50px;\n"
				+ "margin: 5px;\n"
				+ "float: left;\n"
				+ "}\n"
				+ "p {\n"
				+ "margin-left: 10px;\n"
				+ "font-size: 14px;\n"
				+ "}\n"
				+ "</style>\n"
				+ "</head>\n"
				+ "<body>\n");


    		if (adapterResponseList!=null) {
	    		for (int i=0; i<adapterResponseList.getCount(); i++) {
	    			RowResponseList r = adapterResponseList.getItem(i);
	    			String t = "<div class=\"tweet\">\n"
	    			+ "<div class=\"image\">\n";
	    			if (r.isRetweet()) {
	    				t += "<img src=\""+r.getRetweetUrlAvatar()+"\" />\n";
	    			} else {
	    				t += "<img src=\""+r.getUrlAvatar()+"\" />\n";
	    			}
	    			t += "</div>\n";
	    			if (r.isRetweet()) {
	    				t +="<div class=\"username\">"+r.getRetweetUsername()+ " - " + mTweetTopicsCore.getTweetTopics().getString(R.string.retweet_by) + " " + r.getUsername()+ "</div>\n";
	    			} else {
	    				t +="<div class=\"username\">"+r.getUsername()+"</div>\n";
	    			}
	    			t += "<p>"+Utils.toExportHTML(mTweetTopicsCore.getTweetTopics(), r.getText())+"</p>\n"
	    			+ "<div class=\"date\">"+r.getDate().toLocaleString()+" - (<a href=\"" + r.getTwitterURL() + "\">twitter</a>) " + "</div>\n"
	    			+ "</div>\n";
	    			osw.append(t);
	    		}
    		} else {
				for (int i=0; i<adapterStatusList.getCount(); i++) {
					twitter4j.Status r = adapterStatusList.getItem(i);
					User user = r.getUser();
					osw.append("<div class=\"tweet\">\n"
        			+ "<div class=\"image\">\n"
        			+ "<img src=\""+user.getProfileImageURL().toString()+"\" />\n"
        			+ "</div>\n"
        			+ "<div class=\"username\">"+user.getScreenName()+"</div>\n"
        			+ "<p>"+Utils.toExportHTML(mTweetTopicsCore.getTweetTopics(), r.getText())+"</p>\n"
        			+ "<div class=\"date\">"+r.getCreatedAt().toLocaleString()+"</div>\n"
        			+ "</div>\n");
        		}
    		}
        	
			
			osw.append("</body>\n"
				+ "</html>");
			
            osw.flush();
            osw.close();
            
            return f;
						
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return "";
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		responder.export2HTMLLoading();
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
		responder.export2HTMLCancelled();
	}

	@Override
	protected void onPostExecute(String file) {
		super.onPostExecute(file);
		responder.export2HTMLLoaded(file);
	}

}
