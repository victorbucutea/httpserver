package com.adobe.server.http.handlers;

import com.adobe.server.http.HttpRequest;
import com.adobe.server.http.HttpResponse;

public class PrintHandler implements HttpHandler {

	@Override
	public void handle(HttpRequest request, HttpResponse response) {
		System.out.println("Incoming " + request.getMethod() + " request for path " + request.getPath());
		System.out.println("Headers :" + request.getHeaders());
		System.out.println("Processing Thread :" + Thread.currentThread());
		System.out.println("Keep alive:" + request.isKeepConnectionAlive());
		System.out.print("Content :");
		if (request.hasContent())
			if ("text/plain".equals(request.getContentType())) {
				System.out.println(new String(request.getContent()));
			} else
				System.out.println("None.");

	}
}
