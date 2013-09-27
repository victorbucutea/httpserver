package com.adobe.server.http.handlers;

import java.io.IOException;

import com.adobe.server.http.HttpRequest;
import com.adobe.server.http.HttpResponse;

public interface HttpHandler {
	
	void handle(HttpRequest request, HttpResponse response) throws IOException;
}
