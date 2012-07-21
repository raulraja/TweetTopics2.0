package com.javielinux.infos;

import com.javielinux.utils.GuessEncodingInputStream;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;

public class InfoWeb {

	private String web = "";
	private String title = "";
	private String image = "";
	private String description = "";
	
	public InfoWeb(String web) {
		this.web = web;
		
		if ( (web.endsWith(".pdf") || (web.endsWith(".jpg")) || (web.endsWith(".png")) || (web.endsWith(".gif")) ) ) {
    	} else {
		
			try {
				HtmlCleaner cleaner = new HtmlCleaner();
		        CleanerProperties props = cleaner.getProperties();
		        props.setAllowHtmlInsideAttributes(true);
		        props.setAllowMultiWordAttributes(true);
		        props.setRecognizeUnicodeChars(true);
		        props.setOmitComments(true);
			
				URL url = new URL(web);
				
				InputStream istream = url.openStream();
				
				// ver el encoding
				InputStream istreamEncoding = new GuessEncodingInputStream(istream);
				String encoding = ((GuessEncodingInputStream)istreamEncoding).guess();

				URLConnection conn;
				conn = url.openConnection();
				
				InputStreamReader isr;
				if (encoding!=null) {
					isr = new InputStreamReader(conn.getInputStream(), encoding);
				} else {
					isr = new InputStreamReader(conn.getInputStream());
				}
				TagNode node = cleaner.clean(isr);
	
		        Object[] objTitle = node.evaluateXPath("//title");
		        if (objTitle.length > 0) {
		            TagNode info_node = (TagNode) objTitle[0];
		            title = URLDecoder.decode(info_node.getChildren().iterator().next().toString().trim());
		        }
	            Object[] desc = node.evaluateXPath("//meta[@name='description']");
	            if (desc.length > 0) {
	                TagNode info_node = (TagNode) desc[0];
	                description = URLDecoder.decode(info_node.getAttributeByName("content").toString().trim());
	            }
	            Object[] icon = node.evaluateXPath("//link[@rel='image_src']");
	            if (icon.length > 0) {
	                TagNode info_node = (TagNode) icon[0];
	                String w = info_node.getAttributeByName("href").toString().trim();
	                if (!w.startsWith("http")) {
	                	if (!w.startsWith("www")) {
	                		w = web.substring(0, web.indexOf("/", 9)+1) + w;
	                	} else {
	                		w = "http://" + w;
	                	}
	                }
	                image = w;
	            }
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
			} catch (StackOverflowError e) {
				e.printStackTrace();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (XPatherException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
    	}
		
	}

	public String getWeb() {
		return web;
	}

	public String getImage() {
		return image;
	}

	public String getDescription() {
		return description;
	}

	public String getTitle() {
		return title;
	}
	
}
