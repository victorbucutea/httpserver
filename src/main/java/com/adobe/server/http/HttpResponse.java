package com.adobe.server.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HttpResponse {

	private HttpResponseOutputStream outLiveStream;
	private ByteArrayOutputStream outWriterBuffer = new ByteArrayOutputStream();
	private HttpHeaders headers = new HttpHeaders();
	private long contentLength = 0;
	private String status = "200 OK";
	private String contentType;

	public HttpResponse(HttpRequestInputStream inputStream, HttpResponseOutputStream outStream) throws IOException {
		this.outLiveStream = outStream;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void addHeader(String key, String value) {
		headers.add(key, value);
	}

	public void write(String content) throws IOException {
		if (content == null)
			return;

		byte[] bytes = content.getBytes();
		contentLength += bytes.length;
		outWriterBuffer.write(bytes);
	}

	public void write(byte[] bytes) throws IOException {
		if (bytes == null)
			return;
		contentLength += bytes.length;
		outWriterBuffer.write(bytes);
	}

	public void addCloseConnection() {
		addHeader("Connection", "close");
	}

	public void addKeepConnectionAliveHeader() {
		addHeader("Connection", "keep-alive");
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public void flush() throws IOException {
		writeStartLine();
		writeDateHeader();
		writeServerHeader();
		writeContentHeaders();
		writeCustomHeaders();
		writeContent();
		outLiveStream.flush();
	}

	private void writeContent() throws IOException {
		outLiveStream.write("\r\n".getBytes());
		outLiveStream.write(outWriterBuffer.toByteArray());

	}

	private void writeContentHeaders() throws IOException {
		writeHeader("Content-Type", contentType);// TODO detect content-type ?
		writeHeader("Content-Length", "" + contentLength);
	}

	private void writeServerHeader() throws IOException {
		writeHeader("Server", "Multi Threaded File Based Web Server");
	}

	private void writeCustomHeaders() throws IOException {
		for (String key : headers.getHeaders()) {
			writeHeader(key, headers.getHeader(key));
		}
	}

	private void writeDateHeader() throws IOException {
		// Date: Wed, 25 Sep 2013 15:52:13 GMT
		String date = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z").format(new Date());
		writeHeader("Date", date);
	}

	private void writeHeader(String key, String value) throws IOException {
		outLiveStream.write((key + ":" + value + "\r\n").getBytes());
	}

	private void writeStartLine() throws IOException {
		outLiveStream.write(("HTTP/1.1 " + status + "\r\n").getBytes());
	}

}
