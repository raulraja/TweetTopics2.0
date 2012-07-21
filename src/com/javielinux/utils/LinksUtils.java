package com.javielinux.utils;

import android.util.Log;
import com.javielinux.infos.InfoLink;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LinksUtils {

    static public ArrayList<String> pullLinksHTTP(String text) {
    	return pullLinksHTTP(text, null);
    }

    static public ArrayList<String> pullLinksHTTP(String text, ArrayList<Utils.URLContent> urls) {
    	ArrayList<String> links = new ArrayList<String>();

    	// enlaces

    	String regex = "\\(?\\b(http://|https://|www[.])[-A-Za-z0-9+&@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&@#/%=~_()|]";
    	Pattern p = Pattern.compile(regex);
    	Matcher m = p.matcher(text);
    	while(m.find()) {
    		String urlStr = m.group();
    		if (urlStr.startsWith("(") && urlStr.endsWith(")")) {
    			urlStr = urlStr.substring(1, urlStr.length() - 1);
    		}
    		if (urls==null) {
    			links.add(urlStr);
    		} else {
    			Utils.URLContent u = Utils.searchContent(urls, urlStr);
    			if (u!=null) {
    				links.add(u.expanded);
    			} else {
    				links.add(urlStr);
    			}
    		}
    	}

    	return links;
    }

    static public ArrayList<String> pullLinksUsers(String text) {
    	ArrayList<String> links = new ArrayList<String>();

    	// usuarios twitter

    	String regex = "(@[\\w-]+)";
    	Pattern p = Pattern.compile(regex);
    	Matcher m = p.matcher(text);
    	while(m.find()) {
    		String urlStr = m.group();
    		if (urlStr.startsWith("(") && urlStr.endsWith(")")) {
    			urlStr = urlStr.substring(1, urlStr.length() - 1);
    		}
    		links.add(urlStr);
    	}

    	return links;
    }

    static public ArrayList<String> pullLinksHashTags(String text) {
    	ArrayList<String> links = new ArrayList<String>();

    	// hashtags

    	String regex = "(#[\\w-]+)";
    	Pattern p = Pattern.compile(regex);
    	Matcher m = p.matcher(text);
    	while(m.find()) {
    		String urlStr = m.group();
    		if (urlStr.startsWith("(") && urlStr.endsWith(")")) {
    			urlStr = urlStr.substring(1, urlStr.length() - 1);
    		}
    		links.add(urlStr);
    	}

    	return links;
    }

    static public ArrayList<String> pullLinks(String text) {
    	return pullLinks(text, null);
    }

    static public ArrayList<String> pullLinks(String text, ArrayList<Utils.URLContent> urls) {
    	ArrayList<String> links = pullLinksHTTP(text, urls);//= new ArrayList<String>();

    	// enlaces
    	/*
    	String regex = "\\(?\\b(http://|https://|www[.])[-A-Za-z0-9+&@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&@#/%=~_()|]";
    	Pattern p = Pattern.compile(regex);
    	Matcher m = p.matcher(text);
    	while(m.find()) {
    		String urlStr = m.group();
    		if (urlStr.startsWith("(") && urlStr.endsWith(")")) {
    			urlStr = urlStr.substring(1, urlStr.length() - 1);
    		}
    		links.add(urlStr);
    	}
    	*/
    	// hashtag

    	String regex = "(#[\\w-]+)";
    	Pattern p = Pattern.compile(regex);
    	Matcher m = p.matcher(text);
    	while(m.find()) {
    		String urlStr = m.group();
    		if (urlStr.startsWith("(") && urlStr.endsWith(")")) {
    			urlStr = urlStr.substring(1, urlStr.length() - 1);
    		}
    		links.add(urlStr);
    	}

    	// usuarios twitter

    	regex = "(@[\\w-]+)";
    	p = Pattern.compile(regex);
    	m = p.matcher(text);
    	while(m.find()) {
    		String urlStr = m.group();
    		if (urlStr.startsWith("(") && urlStr.endsWith(")")) {
    			urlStr = urlStr.substring(1, urlStr.length() - 1);
    		}
    		links.add(urlStr);
    	}

    	return links;
    }

    static public CharSequence[] splitLinks(String text) {
    	ArrayList<String> links = pullLinks(text);
    	CharSequence[] out = new CharSequence[links.size()];
    	for (int i=0; i<links.size(); i++) {
    		out[i] = links.get(i);
    	}
    	return out;

    }

    static public String shortURL(String link) {

    	link = largeLink(link);

    	int s = (Integer.parseInt(Utils.preference.getString("prf_service_shorter", "1")));
    	if ( s == 1 ) { // bit.ly
    		String user = "tweettopics";
    		String key = "R_ba0652e93e7c9c527c016447d2e29091";
    		if (!PreferenceUtils.getUsernameBitly(Utils.context).equals("") && !PreferenceUtils.getKeyBitly(Utils.context).equals("")) {
    			user = PreferenceUtils.getUsernameBitly(Utils.context);
        		key = PreferenceUtils.getKeyBitly(Utils.context);
    		}
    		String url = "http://com.javielinux.api.bit.ly/v3/shorten?login="+user+"&apiKey="+key+"&format=json&longUrl=" + URLEncoder.encode(link);

    		HttpGet request = new HttpGet(url);
    		HttpClient client = new DefaultHttpClient();
    		HttpResponse httpResponse;
    		try {
    			httpResponse = client.execute(request);
    			String xml = EntityUtils.toString(httpResponse.getEntity());
    			JSONObject jsonObject = new JSONObject(xml);
    			if (jsonObject.getString("status_txt").equals("OK")) {
    				return jsonObject.getJSONObject("data").getString("url");
    			}
    		} catch (Exception e) {
    			e.printStackTrace();
    		}

    	} else { // karmacracy
    		// http://kcy.me/com.javielinux.api/?u=javielinux&key=nyk1tjr20x&format=json&url=http://www.javielinux.com
    		String user = PreferenceUtils.getUsernameKarmacracy(Utils.context);
    		String key = PreferenceUtils.getKeyKarmacracy(Utils.context);

    		String url = "http://kcy.me/com.javielinux.api/?u="+user+"&key="+key+"&format=json&url=" + URLEncoder.encode(link);

    		HttpGet request = new HttpGet(url);
    		HttpClient client = new DefaultHttpClient();
    		HttpResponse httpResponse;
    		try {
    			httpResponse = client.execute(request);
    			String xml = EntityUtils.toString(httpResponse.getEntity());
    			JSONObject jsonObject = new JSONObject(xml);
    			if (jsonObject.getString("status_txt").equals("OK")) {
    				return jsonObject.getJSONObject("data").getString("url");
    			}
    		} catch (Exception e) {
    			e.printStackTrace();
    		}

    	}


		return null;
    }

    static public String shortLinks(String text, ArrayList<String> noImages) {
    	ArrayList<String> links = pullLinksHTTP(text);
    	String out = text;
    	for (int i=0; i<links.size(); i++) {
    		if ( (!links.get(i).contains("bit.ly")) && (!links.get(i).contains("goo.gl"))
    				&& (!links.get(i).contains("twitpic.com")) && (!links.get(i).contains("yfrog.com"))
    				&& (!links.get(i).contains("lockerz.com")) && (!links.get(i).contains("kcy.me"))
    				&& (!links.get(i).contains("t.co")) && (!links.get(i).contains("tinyurl")) ) {
    			String link = links.get(i);
    			String newUrl = shortURL(links.get(i));
    			if (newUrl!=null) {
    				if (!newUrl.equals("")) out = out.replace(link, newUrl);
    			}
    		}
    	}

    	return out;

    }

    static public String largeLink(String link) {
    	/*
		if ( (link.contains("bit.ly")) || (link.contains("short.ie")) || (link.contains("tinyurl.com"))
				|| (link.contains("ow.ly")) || (link.contains("ff.im")) || (link.contains("post.ly"))
				|| (link.contains("j.mp")) || (link.contains("t.co")) ) {

			String url = "http://www.longurlplease.com/com.javielinux.api/v1.1?q=" + link;

			HttpGet request = new HttpGet(url);
			HttpClient client = new DefaultHttpClient();
			HttpResponse httpResponse;
			try {
				httpResponse = client.execute(request);
				String xml = EntityUtils.toString(httpResponse.getEntity());
				JSONObject jsonObject = new JSONObject(xml);
			    String t = jsonObject.getString(link);
			    if ( (t!=null) && t!="") link = t;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}*/

    	boolean done = false;

		if (link.contains("goo.gl")) {
			try {
				String url = "https://www.googleapis.com/urlshortener/v1/url?shortUrl=" + link;

				HttpGet request = new HttpGet(url);
				HttpClient client = new DefaultHttpClient();
				HttpResponse httpResponse;

				httpResponse = client.execute(request);
				String xml = EntityUtils.toString(httpResponse.getEntity());
				JSONObject jsonObject = new JSONObject(xml);
			    String t = jsonObject.getString("longUrl");
			    if ( (t!=null) && t!="") {
			    	link = t;
			    	done = true;
			    }
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		if (link.contains("kcy.me")) {
			//http://karmacracy.com/com.javielinux.api/v1/kcy/2e10?appkey=tweet!t0pic
			try {
				String id = link.substring(link.lastIndexOf("/")+1);
				String url = "http://karmacracy.com/com.javielinux.api/v1/kcy/"+id+"?appkey=tweet!t0pic";

				HttpGet request = new HttpGet(url);
				HttpClient client = new DefaultHttpClient();
				HttpResponse httpResponse;

				httpResponse = client.execute(request);
				String xml = EntityUtils.toString(httpResponse.getEntity());
				JSONObject jsonObject = new JSONObject(xml);
			    String t = jsonObject.getJSONObject("data").getJSONObject("kcy").getString("url");
			    //Log.d(Utils.TAG, "URL: " +t);
			    if ( (t!=null) && t!="") {
			    	link = t;
			    	done = true;
			    }
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (!done) {

			try {
				String url = "http://www.longurlplease.com/com.javielinux.api/v1.1?q=" + link;
				HttpGet request = new HttpGet(url);
				HttpClient client = new DefaultHttpClient();
				HttpResponse httpResponse;
				httpResponse = client.execute(request);

				String xml = EntityUtils.toString(httpResponse.getEntity());
				JSONObject jsonObject = new JSONObject(xml);
			    String t = jsonObject.getString(link);
			    if ( (t!=null) && t!="" && !t.equals("null")) link = t;
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		return link;
    }

    static public boolean hasLinksTweet(String text) {
    	ArrayList<String> links = pullLinks(text);
    	for (int i=0; i<links.size(); i++) {
    		String link = links.get(i);
    		if ( (!link.startsWith("#")) && (!link.startsWith("@")) ) {
    			return true;
    		}
    	}
    	return false;
    }

    static public boolean isLinkImage(String link) {
        // mytubo.net
        if (link.contains("mytubo.net")) {
            return true;
        }

        // imgur.com
        if (link.contains("imgur.com")) {
            return true;
        }

        // instagr.am
        if (link.contains("instagr.am")) {
            return true;
        }

        // lightbox
        if (link.contains("lightbox")) {
            return true;
        }

        // vvcap
        if (link.contains("vvcap")) {
            return true;
        }

        // twitpic
        if (link.contains("twitpic")) {
            return true;
        }

        // picplz.com
        if (link.contains("picplz")) {
            return true;
        }

        // plixi
        if (link.contains("plixi")) {
            return true;
        }

        // yfrog

        if (link.contains("yfrog")) {
            return true;
        }

        // vimeo
        if (link.contains("vimeo")) {
            return true;
        }

        // twitgoo
        if (link.contains("twitgoo")) {
            return true;
        }

        // twitvid

        if (link.contains("twitvid")) {
            return true;
        }

        // youtube

        if (link.contains("youtube")) {
            return true;
        }

        if (link.contains("youtu.be")) {
            return true;
        }

        return false;
    }

    static public boolean isLinkVideo(String link) {

        // vimeo
        if (link.contains("vimeo")) {
            return true;
        }

        // youtube

        if (link.contains("youtube")) {
            return true;
        }

        if (link.contains("youtu.be")) {
            return true;
        }

        return false;
    }

    static public boolean hasImagesTweet(String text) {
    	ArrayList<String> links = pullLinks(text);
    	for (int i=0; i<links.size(); i++) {
    		String link = links.get(i);
    		if ( (!link.startsWith("#")) && (!link.startsWith("@")) ) {
                if (isLinkImage(link)) return true;
    		}

    	}
    	return false;
    }

    static public InfoLink getInfoTweet(String link) {

    	String originalLink = link;

		// acortadores

    	link = largeLink(link);

        // si es un url media
        if (CacheData.existURLMedia(link)) {
            Utils.URLContent content = CacheData.getURLMedia(link);
            InfoLink il = new InfoLink();
            il.setService("Twitter Pic");
            il.setExtensiveInfo(true);
            il.setType(0);
            il.setLink(link);
            il.setOriginalLink(originalLink);
            il.setLinkImageThumb(content.linkMediaThumb);
            il.setLinkImageLarge(content.linkMediaLarge);
            return il;

        }

		// es una busqueda
		if (link.startsWith(Utils.URL_QR)) {
			InfoLink il = new InfoLink();
			il.setService("tweettopics-qr");
			il.setType(Utils.TYPE_LINK_TWEETOPICS_QR);
			il.setLink(link);
			il.setOriginalLink(originalLink);
			return il;
		}

		// es un tema

		if (link.startsWith(Utils.URL_SHARE_THEME_QR)) {
			InfoLink il = new InfoLink();
			il.setService("tweettopics-theme");
			il.setType(Utils.TYPE_LINK_TWEETOPICS_THEME);
			il.setLink(link);
			il.setOriginalLink(originalLink);
			return il;
		}

		if ( (link.endsWith(".jpg")) || (link.endsWith(".png"))	|| (link.endsWith(".gif")) || (link.endsWith(".bmp")) ) {
            InfoLink il = new InfoLink();
            il.setExtensiveInfo(true);
            il.setService("Web");
            il.setType(0);
            il.setLink(link);
            il.setOriginalLink(originalLink);
            il.setLinkImageThumb(link);
            il.setLinkImageLarge(link);
            return il;
    	}


		if (link.contains("imgur.com")) {
			String id = link.substring(link.lastIndexOf("/")+1);
			String imgThumb = "http://i.imgur.com/"+id+"b.jpg";
			String imgLarge = "http://i.imgur.com/"+id+".jpg";

            InfoLink il = new InfoLink();
            il.setService("Imgur");
            il.setExtensiveInfo(true);
            il.setType(0);
            il.setLink(link);
            il.setOriginalLink(originalLink);
            il.setLinkImageThumb(imgThumb);
            il.setLinkImageLarge(imgLarge);
            return il;

		}

		// lightbox
		if (link.contains("lightbox")) {
			String id = link.substring(link.lastIndexOf("/")+1);
            InfoLink il = new InfoLink();
            il.setService("Lightbox");
            il.setExtensiveInfo(true);
            il.setType(0);
            il.setLink(link);
            il.setOriginalLink(originalLink);
            il.setLinkImageThumb("http://lightbox.com/show/thumb/"+id);
            il.setLinkImageLarge("http://lightbox.com/show/large/"+id);
            return il;

		}

		// twitpic
		if (link.contains("twitpic")) {
			String id = link.substring(link.lastIndexOf("/")+1);

            InfoLink il = new InfoLink();
            il.setService("Twitpic");
            il.setExtensiveInfo(true);
            il.setType(0);
            il.setLink(link);
            il.setOriginalLink(originalLink);
            il.setLinkImageThumb("http://twitpic.com/show/mini/"+id);
            il.setLinkImageLarge("http://twitpic.com/show/large/"+id);
            return il;
		}

		// picplz
		if (link.contains("picplz")) {
			String id = link.substring(link.lastIndexOf("/")+1);
            InfoLink il = new InfoLink();
            il.setService("Picplz");
            il.setExtensiveInfo(true);
            il.setType(0);
            il.setLink(link);
            il.setOriginalLink(originalLink);
            il.setLinkImageThumb("http://picplz.com/"+id+"/thumb/200");
            il.setLinkImageLarge("http://picplz.com/"+id+"/thumb/400");
            return il;
		}

		// img.ly
		if (link.contains("img.ly")) {
			String id = link.substring(link.lastIndexOf("/")+1);
            InfoLink il = new InfoLink();
            il.setService("Img.ly");
            il.setExtensiveInfo(true);
            il.setType(0);
            il.setLink(link);
            il.setOriginalLink(originalLink);
            il.setLinkImageThumb("http://img.ly/show/thumb/"+id);
            il.setLinkImageLarge("http://img.ly/show/medium/"+id);
            return il;
		}

        // vvcap

        if (link.contains("vvcap")) {
            String image = link.replace(".htp", ".png");
            InfoLink il = new InfoLink();
            il.setExtensiveInfo(true);
            il.setService("Vvcap.net");
            il.setType(0);
            il.setLink(link);
            il.setOriginalLink(originalLink);
            il.setLinkImageThumb(image);
            il.setLinkImageLarge(image);
            return il;

        }

		// yfrog

		if (link.contains("yfrog")) {
            InfoLink il = new InfoLink();
            il.setService("Yfrog");
            il.setExtensiveInfo(true);
            il.setType(0);
            il.setLink(link);
            il.setOriginalLink(originalLink);
            il.setLinkImageThumb(link+".th.jpg");
            il.setLinkImageLarge(link+":android");
            Log.d(Utils.TAG, "yfrog (\"+link+\"): " + link+".th.jpg" + " -- " +link+":android");
            return il;

		}

		// twitvid
		if (link.contains("twitvid")) {
			String id = link.substring(link.lastIndexOf("/")+1);
            InfoLink il = new InfoLink();
            il.setService("twitvid");
            il.setExtensiveInfo(true);
            il.setType(1);
            il.setLink(link);
            il.setOriginalLink(originalLink);
            il.setTitle("Twitvid");
            il.setDurationVideo(0);
            il.setLinkImageThumb("http://images2.twitvid.com/"+id+".jpg");
            il.setLinkImageLarge("http://images2.twitvid.com/"+id+".jpg");
            return il;
		}

        /*
        if (link.contains("flic.kr")) {
            String idbase58 = link.substring(link.lastIndexOf("/")+1);
            String id = String.valueOf(alphaToNumber(idbase58));

            String urlApi = "http://com.javielinux.api.flickr.com/services/rest/?method=flickr.photos.getInfo&api_key=6ce2af123df7dd2a7dab086f086e9824&photo_id="+id+"&format=json&nojsoncallback=1";

            Log.d(Utils.TAG, "urlApi: (" + link + ") " + urlApi);

            String farmId="";
            String serverId="";
            String secret="";

            HttpGet request = new HttpGet(urlApi);
            HttpClient client = new DefaultHttpClient();
            HttpResponse httpResponse;
            try {
                httpResponse = client.execute(request);
                String xml = EntityUtils.toString(httpResponse.getEntity());
                JSONObject jsonObject = new JSONObject(xml);
                if (jsonObject!=null) {
                    if (jsonObject.getJSONObject("photo")!=null) {
                        farmId = jsonObject.getJSONObject("photo").getString("farm");
                        serverId = jsonObject.getJSONObject("photo").getString("server");
                        secret = jsonObject.getJSONObject("photo").getString("secret");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (farmId!="") {
                String imgThumb = "http://farm"+farmId+".static.flickr.com/"+serverId+"/"+id+"_"+secret+"_s.jpg";
                String imgLarge = "http://farm"+farmId+".static.flickr.com/"+serverId+"/"+id+"_"+secret+".jpg";
                Bitmap bmp = getBitmap(imgThumb, HEIGHT_THUMB);
                if (bmp!=null) {
                    InfoLink il = new InfoLink();
                    il.setBitmapThumb(bmp);
                    il.setService("Flickr");
                    il.setType(0);
                    il.setLink(link);
                    il.setOriginalLink(originalLink);
                    il.setLinkImageThumb(imgThumb);
                    il.setLinkImageLarge(imgLarge);
                    return il;
                }
            }

        }
        */
        if (link.contains("mytubo.net")) {
            String image = "";

            try {
                HtmlCleaner cleaner = new HtmlCleaner();
                CleanerProperties props = cleaner.getProperties();
                props.setAllowHtmlInsideAttributes(true);
                props.setAllowMultiWordAttributes(true);
                props.setRecognizeUnicodeChars(true);
                props.setOmitComments(true);

                URL url = new URL(link);
                URLConnection conn;
                conn = url.openConnection();
                InputStreamReader isr = new InputStreamReader(conn.getInputStream());
                TagNode node = cleaner.clean(isr);

                Object[] objMeta = node.evaluateXPath("//img[@id='originPic']");
                if (objMeta.length > 0) {
                    TagNode info_node = (TagNode) objMeta[0];
                    image = URLDecoder.decode(info_node.getAttributeByName("src").toString().trim());
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XPatherException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            InfoLink il = new InfoLink();
            il.setExtensiveInfo(true);
            il.setService("Mytubo.net");
            il.setType(0);
            il.setLink(link);
            il.setOriginalLink(originalLink);
            il.setLinkImageThumb(image);
            il.setLinkImageLarge(image);
            return il;

        }

        // instagr.am
        if (link.contains("instagr.am")) {

            String image = "";

            try {
                HtmlCleaner cleaner = new HtmlCleaner();
                CleanerProperties props = cleaner.getProperties();
                props.setAllowHtmlInsideAttributes(true);
                props.setAllowMultiWordAttributes(true);
                props.setRecognizeUnicodeChars(true);
                props.setOmitComments(true);
                props.setUseEmptyElementTags(true);
                /*
                URL url = new URL(link);
                URLConnection conn;
                conn = url.openConnection();
                InputStreamReader isr = new InputStreamReader(conn.getInputStream());    */
                TagNode node = cleaner.clean(getURIContent(link));

                Object[] objMeta = node.evaluateXPath("//meta[@property='og:image']");
                if (objMeta.length > 0) {
                    TagNode info_node = (TagNode) objMeta[0];
                    image = URLDecoder.decode(info_node.getAttributeByName("content").toString().trim());
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XPatherException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            InfoLink il = new InfoLink();
            il.setExtensiveInfo(true);
            il.setService("Instagr.am");
            il.setType(0);
            il.setLink(link);
            il.setOriginalLink(originalLink);
            il.setLinkImageThumb(image);
            il.setLinkImageLarge(image);
            Log.d(Utils.TAG, "Instagr.am ("+link+"): " + image);
            return il;

        }

        // plixi o lockerz

        if (link.contains("plixi") || link.contains("lockerz")) {

            String strURL = "http://com.javielinux.api.plixi.com/com.javielinux.api/tpapi.svc/metadatafromurl?url=" + link;
            try {
                Document doc = null;
                try {
                    URL url;
                    URLConnection urlConn = null;
                    url = new URL(strURL);
                    urlConn = url.openConnection();
                    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                    DocumentBuilder db = dbf.newDocumentBuilder();
                    doc = db.parse(urlConn.getInputStream());
                } catch (IOException ioe) {
                } catch (ParserConfigurationException pce) {
                } catch (SAXException se) {
                }

                if (doc!=null) {
                    try {
                        String imgThumb = doc.getElementsByTagName("ThumbnailUrl").item(0).getFirstChild().getNodeValue();
                        String imgLarge = doc.getElementsByTagName("MediumImageUrl").item(0).getFirstChild().getNodeValue();

                        if (!imgThumb.equals("")) {
                            InfoLink il = new InfoLink();
                            il.setService("Lockerz");
                            il.setType(0);
                            il.setLink(link);
                            il.setOriginalLink(originalLink);
                            il.setLinkImageThumb(imgThumb);
                            il.setLinkImageLarge(imgLarge);
                            return il;
                        }
                    } catch (Exception e) {
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        // twitgoo

        if (link.contains("twitgoo")) {
            String id = link.substring(link.lastIndexOf("/")+1);
            String strURL = "http://twitgoo.com/com.javielinux.api/message/info/" + id;
            Document doc = null;
            try {
                URL url;
                URLConnection urlConn = null;
                url = new URL(strURL);
                urlConn = url.openConnection();
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                doc = db.parse(urlConn.getInputStream());
            } catch (IOException ioe) {
            } catch (ParserConfigurationException pce) {
            } catch (SAXException se) {
            }

            if (doc!=null) {
                try {
                    String imgThumb = doc.getElementsByTagName("thumburl").item(0).getFirstChild().getNodeValue();
                    String imgLarge = doc.getElementsByTagName("imageurl").item(0).getFirstChild().getNodeValue();
                    if (!imgThumb.equals("")) {
                        InfoLink il = new InfoLink();
                        il.setService("Twitgoo");
                        il.setExtensiveInfo(true);
                        il.setType(0);
                        il.setLink(link);
                        il.setOriginalLink(originalLink);
                        il.setLinkImageThumb(imgThumb);
                        il.setLinkImageLarge(imgLarge);
                        return il;
                    }
                } catch (Exception e) {
                }
            }

        }

        // vimeo

        if (link.contains("vimeo")) {
            String id = link.substring(link.lastIndexOf("/")+1);
            String strURL = "http://vimeo.com/com.javielinux.api/v2/video/"+id+".xml";

            Document doc = null;
            try {
                URL url;
                URLConnection urlConn = null;
                url = new URL(strURL);
                urlConn = url.openConnection();
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                doc = db.parse(urlConn.getInputStream());
            } catch (IOException ioe) {
            } catch (ParserConfigurationException pce) {
            } catch (SAXException se) {
            }

            if (doc!=null) {
                try {
                    String imgThumb = doc.getElementsByTagName("thumbnail_small").item(0).getFirstChild().getNodeValue();
                    String imgLarge = doc.getElementsByTagName("thumbnail_large").item(0).getFirstChild().getNodeValue();
                    String title = doc.getElementsByTagName("title").item(0).getFirstChild().getNodeValue();
                    int duration = Integer.parseInt(doc.getElementsByTagName("duration").item(0).getFirstChild().getNodeValue());
                    if (!imgThumb.equals("")) {
                        InfoLink il = new InfoLink();
                        il.setService("Vimeo");
                        il.setExtensiveInfo(true);
                        il.setType(1);
                        il.setLink(link);
                        il.setOriginalLink(originalLink);
                        il.setTitle(title);
                        il.setDurationVideo(duration);
                        il.setLinkImageThumb(imgThumb);
                        il.setLinkImageLarge(imgLarge);
                        return il;
                    }
                } catch (Exception e) {
                }
            }

        }

        // youtube

        if ( (link.contains("youtube")) || (link.contains("youtu.be")) ) {
            String id = "";
            if (link.contains("youtube")) {
                id = link.substring(link.lastIndexOf("v=")+2);
                if (id.contains("&")) {
                    id = id.substring(0,id.indexOf("&"));
                }
            }
            if (link.contains("youtu.be")) {
                id = link.substring(link.lastIndexOf("/")+1);
                if (id.contains("?")) {
                    id = id.substring(0,id.indexOf("?"));
                }
            }
            String imgThumb = "http://img.youtube.com/vi/"+id+"/2.jpg";
            String imgLarge = "http://img.youtube.com/vi/"+id+"/0.jpg";

            String strURL = "http://gdata.youtube.com/feeds/com.javielinux.api/videos/"+id;

            Document doc = null;
            try {
                URL url;
                URLConnection urlConn = null;
                url = new URL(strURL);
                urlConn = url.openConnection();
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                doc = db.parse(urlConn.getInputStream());
            } catch (IOException ioe) {
            } catch (ParserConfigurationException pce) {
            } catch (SAXException se) {
            }

            String title = "Youtube";
            int duration = 0;

            try {
                if (doc!=null) {
                    title = doc.getElementsByTagName("title").item(0).getFirstChild().getNodeValue();
                    duration = Integer.parseInt(doc.getElementsByTagName("yt:duration").item(0).getAttributes().getNamedItem("seconds").getNodeValue());
                }
            } catch (Exception e) {

            }

            InfoLink il = new InfoLink();

            il.setService("Youtube");
            il.setExtensiveInfo(true);
            il.setType(1);
            il.setLink(link);
            il.setOriginalLink(originalLink);
            il.setTitle(title);
            il.setDurationVideo(duration);
            il.setLinkImageThumb(imgThumb);
            il.setLinkImageLarge(imgLarge);
            return il;

        }

        // si no es una imagen, es un enlace web

        InfoLink il = new InfoLink();
        il.setService("web");
        il.setType(2);
        il.setLink(link);
        il.setOriginalLink(originalLink);
        il.setTitle(originalLink);
        return il;

    }


    public static String getURIContent(String url) {
        String result = null;
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet();
        StringBuffer sb = null;
        Boolean flag = false;
        try {
            request.setURI(new URI(url));
            request.setHeader("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.1; ru; rv:1.9.2.10) Gecko/20100914 Firefox/3.6.10");
            HttpResponse response = client.execute(request);
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    response.getEntity().getContent(), "CP-1251"));
            sb = new StringBuffer("");
            String line = "";
            String NL = System.getProperty("line.separator");
            while ((line = in.readLine()) != null) {
                sb.append(line + NL);
            }
            in.close();
            flag = true;
        } catch (ClientProtocolException e) {
        } catch (IOException e) {
        } catch (URISyntaxException e) {
        }
        if (flag) {
            result = sb.toString();
            if (result.equals("")) {
                result = null;
            }
        }
        return result;
    }

}
