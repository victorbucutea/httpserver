package com.adobe.server.core;

import java.io.IOException;

import com.adobe.server.http.HttpRequestInputStream;
import com.adobe.server.http.HttpResponseOutputStream;



/**
 * 
 * Takes in InputStream and OutputStreams, and interprets the content and transforms it 
 * into an HttpRequest and HttpResponse.
 * 
 * @author VictorBucutea
 *
 */
public interface StreamHandler {
	
	/**
	 * 
	 * @param stream
	 * @throws IOException 
	 */
	public void handle(HttpRequestInputStream inStream, HttpResponseOutputStream outStream) throws IOException;

}
