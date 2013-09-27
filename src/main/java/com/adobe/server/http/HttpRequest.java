package com.adobe.server.http;

import java.util.Map;
import java.util.Set;

/**
 * An {@link HttpRequest} object will contain all headers and content
 * 
 * @author VictorBucutea
 * 
 */
public class HttpRequest {

	private HttpHeaders headers = new HttpHeaders();
	private int contentLength = -1;
	private String method;
	private String path;
	private byte[] content;
	private boolean keepConnectionAlive;

	public void setMethod(String method) {
		this.method = method;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void addHeader(String rawHeaderString) {
		headers.add(rawHeaderString);
	}

	public void initHeadersMetaInfo() {
		contentLength = headers.getContentLength();
		keepConnectionAlive = headers.isConnectionKeepAlive();
	}

	public boolean hasContent() {
		if (contentLength == -1)
			return false;
		else
			return true;
	}

	public int getContentLength() {
		return contentLength;
	}

	public boolean isKeepConnectionAlive() {
		return keepConnectionAlive;
	}

	public void setContent(byte[] content2) {
		this.content = content2;
	}

	public String getMethod() {
		return method;
	}

	public String getPath() {
		return path;
	}

	public String getContent() {
		if (content == null)
			return null;

		return new String(content);
	}
	
	public byte[] getContentAsBytes() {
		return content;
	}

	public String getHeader(String string) {
		return headers.getHeader(string);
	}

	public Set<Map.Entry<String, String>> getHeaders() {
		return headers.getHeadersWithValues();
	}

	public String getContentType() {
		return headers.getHeader("Content-Type");
	}

}
