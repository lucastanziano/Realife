package com.realapp.realife.util.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.content.Context;

import com.realapp.realife.R;
import com.realapp.realife.models.apps.AppID;
import com.realapp.realife.util.Print;

public class XMLParser {

	
	
	static public List<AppID> parseCategoryXMLfile(Context context, File fCategories) {
		List<AppID> apps = new ArrayList<AppID>();

		Document doc = null;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {

			DocumentBuilder db = dbf.newDocumentBuilder();
            
			InputStream is;
			if(fCategories == null || !fCategories.exists()){
				is = context.getResources().openRawResource(R.raw.categories);
			}
			else{
				is = new FileInputStream(fCategories);
			}
			doc = db.parse(is);
			
			is.close();

		} catch (ParserConfigurationException e) {
			Print.error(new Throwable("Error: " + e.getMessage(), e));
			return null;
		} catch (SAXException e) {
			Print.error(new Throwable("Error: " + e.getMessage(), e));
			return null;
		} catch (IOException e) {
			Print.error(new Throwable("Error: " + e.getMessage(), e));
			return null;
		}

		NodeList nl = doc.getElementsByTagName(TAG.APP);
        // looping through all item nodes <item>
        for (int i = 0; i < nl.getLength(); i++) {

            Element e = (Element) nl.item(i);
            String appName = getValue(e, TAG.APP_NAME);
            String packageName = getValue(e, TAG.PACK_NAME);
            int categoryID = Integer.parseInt(getValue(e, TAG.CAT_ID));
            apps.add(new AppID(appName, packageName, categoryID));
   
        }
        
		return apps;
	}

	/**
	 * Getting XML from URL making HTTP request
	 * 
	 * @param url
	 *            string
	 * */
	public String getXmlFromUrl(String url) {
		String xml = null;

		try {
			// defaultHttpClient
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);

			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();
			xml = EntityUtils.toString(httpEntity);

		} catch (UnsupportedEncodingException e) {
			Print.error(new Throwable("Error: " + e.getMessage(), e));
		} catch (ClientProtocolException e) {
			Print.error(new Throwable("Error: " + e.getMessage(), e));
		} catch (IOException e) {
			Print.error(new Throwable("Error: " + e.getMessage(), e));
		}
		// return XML
		return xml;
	}



	/**
	 * Getting node value
	 * 
	 * @param elem
	 *            element
	 */
	static private final String getElementValue(Node elem) {
		Node child;
		if (elem != null) {
			if (elem.hasChildNodes()) {
				for (child = elem.getFirstChild(); child != null; child = child
						.getNextSibling()) {
					if (child.getNodeType() == Node.TEXT_NODE) {
						return child.getNodeValue();
					}
				}
			}
		}
		return "";
	}

	/**
	 * Getting node value
	 * 
	 * @param Element
	 *            node
	 * @param key
	 *            string
	 * */
	static private String getValue(Element item, String str) {
		NodeList n = item.getElementsByTagName(str);
		return getElementValue(n.item(0));
	}
}
