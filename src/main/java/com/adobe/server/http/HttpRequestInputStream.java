package com.adobe.server.http;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;

import com.adobe.server.IOUtil;

public class HttpRequestInputStream extends DataInputStream {

	/**
	 * Protocol specific header encoding. This is not the charset for content
	 * encoding as that is specified in Content-Type headers
	 */
	public static final String HEADER_ENCODING = "UTF-8";

	HttpRequest request = new HttpRequest();

	public HttpRequestInputStream(InputStream in) {
		super(in);
	}


	private HttpRequest readRequest() throws IOException {
		LinkedList<String> headers = IOUtil.readHeaderAreaAsString(in);
		initStartLine(headers);
		initHeaders(headers);
		request.initHeadersMetaInfo();
		initContent();
		return request;
	}

	private void initContent() throws IOException {
		if (request.hasContent()) {
			byte[] content = IOUtil.read(this, request.getContentLength());
			request.setContent(content);
		}
	}

	private void initHeaders(LinkedList<String> headers) {
		// Http 1.1 can have 0 or more headers.

		for (String header : headers) {
			request.addHeader(header);
		}

	}

	private void initStartLine(LinkedList<String> headers) {
		String rawFirstLine = headers.removeFirst();
		String[] methodAndPath = rawFirstLine.split(" ");
		request.setMethod(methodAndPath[0]);
		request.setPath(methodAndPath[1]);

	}

	public void closeIfAplicable() throws IOException {
		if (!request.isKeepConnectionAlive()) {
			close();
		}
	}


	public static HttpRequest createRequest(HttpRequestInputStream inStream) throws IOException {
		return inStream.readRequest();
	}

}
