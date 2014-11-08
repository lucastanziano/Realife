package com.realapp.realife.util.client;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.util.Log;

import com.realapp.realife.util.Print;

public class WebSync {

	static public final String WEBSERVICE_URL = "http://realifeapp.altervista.org/webservice/post_date_receiver.php";
	
	static public final String CATEGORYFILE = "newcategories.xml";
	static public final String CATEGORYFILE_URL = "http://realifeapp.altervista.org/webservice/" + CATEGORYFILE;

	
	static public int uploadFile(File sourceFile) {

		String fileName = sourceFile.getName();
		int serverResponseCode = -1;
		HttpURLConnection conn = null;
		DataOutputStream dos = null;
		String lineEnd = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";
		int bytesRead, bytesAvailable, bufferSize;
		byte[] buffer;
		int maxBufferSize = 1 * 1024 * 1024;

		Print.debug("uploading file :" + fileName + " to :" + WEBSERVICE_URL);

		if (!sourceFile.isFile()) {

			Print.error(new Throwable("uploadFile: Source File not exist :"
					+ fileName));

			return 0;

		} else {
			try {

				// open a URL connection to the Servlet
				FileInputStream fileInputStream = new FileInputStream(
						sourceFile);
				URL url = new URL(WEBSERVICE_URL);

				// Open a HTTP connection to the URL
				conn = (HttpURLConnection) url.openConnection();
				conn.setDoInput(true); // Allow Inputs
				conn.setDoOutput(true); // Allow Outputs
				conn.setUseCaches(false); // Don't use a Cached Copy
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Connection", "Keep-Alive");
				conn.setRequestProperty("ENCTYPE", "multipart/form-data");
				conn.setRequestProperty("Content-Type",
						"multipart/form-data;boundary=" + boundary);
				conn.setRequestProperty("uploaded_file", fileName);

				dos = new DataOutputStream(conn.getOutputStream());

				dos.writeBytes(twoHyphens + boundary + lineEnd);
				dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
						+ fileName + "\"" + lineEnd);

				dos.writeBytes(lineEnd);

				// create a buffer of maximum size
				bytesAvailable = fileInputStream.available();

				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				buffer = new byte[bufferSize];

				// read file and write it into form...
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);

				while (bytesRead > 0) {

					dos.write(buffer, 0, bufferSize);
					bytesAvailable = fileInputStream.available();
					bufferSize = Math.min(bytesAvailable, maxBufferSize);
					bytesRead = fileInputStream.read(buffer, 0, bufferSize);

				}

				// send multipart form data necesssary after file data...
				dos.writeBytes(lineEnd);
				dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

				// Responses from the server (code and message)
				serverResponseCode = conn.getResponseCode();
				String serverResponseMessage = conn.getResponseMessage();

				Log.i("uploadFile", "HTTP Response is : "
						+ serverResponseMessage + ": " + serverResponseCode);

				if (serverResponseCode == 200) {

					Print.debug("File Upload Completed.\n\n");

				}

				// close the streams //
				fileInputStream.close();
				dos.flush();
				dos.close();

			} catch (MalformedURLException ex) {

				Print.debug("MalformedURLException Exception : check script url.");

			} catch (Exception e) {

				StringBuilder sb = new StringBuilder();
				for(StackTraceElement se : e.getStackTrace()){
					sb.append(se.toString());
					
				}
				Print.debug(
						"Upload file to server Exception: Exception : "
								+ sb.toString());
			}

			return serverResponseCode;

		} // End else block
	}

	static public void downloadFile(String sUrl, File destFile) {
		Print.debug("Downloading file");
		try {
			URL url = new URL(sUrl);
			HttpURLConnection urlConnection = (HttpURLConnection) url
					.openConnection();

			urlConnection.setRequestMethod("GET");
			urlConnection.setDoOutput(true);

			// connect
			urlConnection.connect();

			FileOutputStream fileOutput = new FileOutputStream(destFile);

			// Stream used for reading the data from the internet
			InputStream inputStream = urlConnection.getInputStream();

			// this is the total size of the file which we are downloading
//			int totalSize = urlConnection.getContentLength();

			// create a buffer...
			byte[] buffer = new byte[1024];
			int bufferLength = 0;

			while ((bufferLength = inputStream.read(buffer)) > 0) {
				fileOutput.write(buffer, 0, bufferLength);
			}
			// close the output stream when complete //
			fileOutput.close();
			
			Print.debug("Successfully downloaded category file");

		} catch (final MalformedURLException e) {
			Print.debug("MalformedURLException - Error downloading category file");
			Print.error(e);
		} catch (final IOException e) {
			Print.debug("IOException - Error downloading category file");
		} catch (final Exception e) {
			Print.debug("Exception - Error downloading category file");
			Print.error(e);
		}
	}

}
