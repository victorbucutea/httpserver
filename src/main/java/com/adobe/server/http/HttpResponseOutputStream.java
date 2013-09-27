package com.adobe.server.http;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class HttpResponseOutputStream extends DataOutputStream {


	public HttpResponseOutputStream(OutputStream outputStream) {
		super(outputStream);
	}

	public static HttpResponse createResponse(HttpRequestInputStream inStream, HttpResponseOutputStream outStream) throws IOException {
		HttpResponse response = new HttpResponse(inStream,outStream);
		return response;
	}


}
