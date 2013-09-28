package com.adobe.server.http;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.adobe.server.core.StreamHandler;
import com.adobe.server.http.handlers.FileDownloadHandler;
import com.adobe.server.http.handlers.FileUploadHandler;
import com.adobe.server.http.handlers.HttpHandler;

public class HttpStreamHandler implements StreamHandler {

	private List<HttpHandler> handlers = new ArrayList<HttpHandler>();
	private boolean keepAliveSupport;

	public HttpStreamHandler(boolean keepAliveSupport) {
		this.keepAliveSupport = keepAliveSupport;
		/*
		 * register handlers for .. well .. handling requests/responses
		 */
		handlers.add(new FileDownloadHandler());
		handlers.add(new FileUploadHandler());
	}

	@Override
	public void handle(HttpRequestInputStream inStream, HttpResponseOutputStream outStream) throws IOException {

		HttpRequest request = HttpRequestInputStream.createRequest(inStream);
		HttpResponse response = HttpResponseOutputStream.createResponse(inStream, outStream);

		for (HttpHandler handler : handlers) {
			handler.handle(request, response);
		}

	
		if (keepAliveSupport && request.isKeepConnectionAlive()) {
			response.addKeepConnectionAliveHeader();
		} else {
			response.addCloseConnection();
		}


		response.flush();
		
		if (keepAliveSupport && request.isKeepConnectionAlive()){
			handle(inStream, outStream);
		} else {
			outStream.close();
		}
	}

}
