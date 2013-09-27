package com.adobe.server.http;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import com.adobe.server.CollectionUtil;

public class HttpHeaders {

	private static final String CONTENT_LENGTH = "content-length";
	private static final String ACCEPT = "accept";
	private Map<String, String> headers = new HashMap<String, String>();
	private static final String CONNECTION = "connection";

	public void add(String headerLine) {
		
		Scanner scanner = new Scanner(headerLine);
		scanner.useDelimiter(":");
		if (scanner.hasNext()) {
			String key = scanner.next().toLowerCase();
			String value = scanner.next().trim();
			headers.put(key, value);
		}
	}

	public int getContentLength() {
		if (headers.containsKey(CONTENT_LENGTH)) {
			String contentLength = headers.get(CONTENT_LENGTH).trim();
			return Integer.parseInt(contentLength);
		} else {
			return -1;
		}
	}

	public List<String> getAcceptedMimeTypes() {
		if (headers.containsKey(ACCEPT)) {
			String string = headers.get(ACCEPT);
			return CollectionUtil.asList(string, ",");
		}
		return new ArrayList<String>();
	}

	public boolean isConnectionKeepAlive() {
		if (headers.containsKey(CONNECTION)) {
			if (headers.get(CONNECTION).equalsIgnoreCase("keep-alive")) {
				return true;
			}
		}
		return false;
	}

	public String getHeader(String name) {
		if ( name == null )
			return null;
		
		return headers.get(name.toLowerCase());
	}

	public Set<String> getHeaders() {
		return headers.keySet();
	}
	
	public Set<Map.Entry<String,String>> getHeadersWithValues() {
		return headers.entrySet();
	}

	public void add(String key, String value) {
		if (key == null)
			return;
		
		headers.put(key.toLowerCase(), value);
	}

}
