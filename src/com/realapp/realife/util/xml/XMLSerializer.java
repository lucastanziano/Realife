package com.realapp.realife.util.xml;

import java.io.File;
import java.io.FileOutputStream;
import java.io.StringWriter;
import java.util.List;

import org.xmlpull.v1.XmlSerializer;

import android.util.Xml;

import com.realapp.realife.models.apps.AppID;

public class XMLSerializer {


	
	/**
	 * Write to file XML file the app list containing categories information
	 * @param apps
	 * @param path NB you can get it with context.getFilesDir()
	 * @throws Exception
	 */
	public static void serializeCategoriesToXML(List<AppID> apps, File path, String filename) throws Exception {
		
	    String text = serializeCategoriesToXML(apps);
		File file = new File(path, filename);
		FileOutputStream fos = new FileOutputStream(file);
		fos.write(text.getBytes());
		fos.close();		
	}
	

	private static String serializeCategoriesToXML(List<AppID> apps) throws Exception {
		XmlSerializer xmlSerializer = Xml.newSerializer();
		StringWriter writer = new StringWriter();

		xmlSerializer.setOutput(writer);
		// start DOCUMENT
		xmlSerializer.startDocument("UTF-8", true);
		xmlSerializer.startTag("", TAG.DATA);
		
		for (AppID app : apps) {
			xmlSerializer.startTag("", TAG.APP);

			xmlSerializer.startTag("", TAG.APP_NAME);
			xmlSerializer.text(app.getAppName());
			xmlSerializer.endTag("", TAG.APP_NAME);

			xmlSerializer.startTag("", TAG.PACK_NAME);
			xmlSerializer.text(app.getPackageName());
			xmlSerializer.endTag("", TAG.PACK_NAME);

			xmlSerializer.startTag("", TAG.CAT_ID);
			xmlSerializer.text(Integer.toString(app.getCategoryID()));
			xmlSerializer.endTag("", TAG.CAT_ID);

			xmlSerializer.endTag("", TAG.APP);
		}
		xmlSerializer.endTag("", TAG.DATA);

		// end DOCUMENT
		xmlSerializer.endDocument();

		return writer.toString();
	}
}
