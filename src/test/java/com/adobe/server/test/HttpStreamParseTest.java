package com.adobe.server.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import com.adobe.server.IOUtil;
import com.adobe.server.http.HttpRequest;
import com.adobe.server.http.HttpRequestInputStream;

public class HttpStreamParseTest {

	//@f_off
	private String header = "GET /rfc/rfc3261.txt HTTP/1.1\r\n" +
			"Accept:text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8\r\n"+
			"accept-Encoding:gzip,deflate,sdch\r\n"+
			"Connection:keep-alive\r\n"+
			"Host:www.ietf.org\r\n"+
			"User-Agent:Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/29.0.1547.76 Safari/537.36\r\n";
	
	private String lf_header = "GET /rfc/rfc3261.txt HTTP/1.1\n" +
				"Accept:text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8\n"+
				"accept-Encoding:gzip,deflate,sdch\n"+
				"Connection:keep-alive\n"+
				"Host:www.ietf.org\n"+
				"User-Agent:Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/29.0.1547.76 Safari/537.36\n";
	
	private String newHeader = "POST /upload.img HTTP/1.1\r\n"+
							   "Content-Length: 92176\r\n"+
							   "Content-Type: image/jpg; charset=UTF-8\r\n"+
							   "Host: localhost\r\n"+
							   "Connection: Keep-Alive\r\n"+
							   "User-Agent: Apache-HttpClient/4.3 (java 1.5)\r\n"+
							   "Accept-Encoding: gzip,deflate\r\n\r\n" +
							   "CONTENT !!!!";	
	//@f_on
	ByteArrayInputStream stream = new ByteArrayInputStream(header.getBytes());

	@Test
	public void parseTest() throws IOException {
		List<String> readLines = IOUtil.readHeaderLines(new BufferedReader(new InputStreamReader(stream)));

		assertEquals(6, readLines.size());
		assertEquals("GET /rfc/rfc3261.txt HTTP/1.1", readLines.get(0));
		assertEquals("Accept:text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8", readLines.get(1));
		assertEquals("accept-Encoding:gzip,deflate,sdch", readLines.get(2));
		assertEquals("Connection:keep-alive", readLines.get(3));
		assertEquals("Host:www.ietf.org", readLines.get(4));
		assertEquals("User-Agent:Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/29.0.1547.76 Safari/537.36",
				readLines.get(5));
	}
	
	@Test
	public void parseHeaderTest2() throws IOException {
		ByteArrayInputStream stream = new ByteArrayInputStream(newHeader.getBytes());
		HttpRequestInputStream httpStream = new HttpRequestInputStream(stream);
		HttpRequest request = HttpRequestInputStream.createRequest(httpStream);

		assertEquals("POST", request.getMethod());
		assertEquals("/upload.img", request.getPath());
		assertEquals("localhost", request.getHeader("Host"));
		assertEquals(92176, request.getContentLength());
		assertEquals("gzip,deflate",request.getHeader("Accept-Encoding"));
		assertEquals("CONTENT !!!!",request.getContent());
		httpStream.close();
	}

	@Test
	public void parseHeaderTest() throws IOException {

		HttpRequestInputStream httpStream = new HttpRequestInputStream(stream);
		HttpRequest request = HttpRequestInputStream.createRequest(httpStream);

		assertEquals("GET", request.getMethod());
		assertEquals("/rfc/rfc3261.txt", request.getPath());
		assertHeadersRead(request);
		httpStream.close();
	}

	private void assertHeadersRead(HttpRequest request) {
		assertEquals("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8", request.getHeader("Accept"));
		assertEquals("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8", request.getHeader("accept"));
		assertEquals("gzip,deflate,sdch", request.getHeader("Accept-Encoding"));
		assertEquals("www.ietf.org", request.getHeader("Host"));
		assertEquals("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/29.0.1547.76 Safari/537.36",
				request.getHeader("user-agent"));
	}

	@Test
	public void parseDoubleCRLFContent() throws IOException {
		header += "\r\n";
		stream = new ByteArrayInputStream(header.getBytes());

		HttpRequestInputStream httpStream = new HttpRequestInputStream(stream);
		HttpRequest request = HttpRequestInputStream.createRequest(httpStream);
		assertEquals("GET", request.getMethod());
		assertEquals("/rfc/rfc3261.txt", request.getPath());
		assertHeadersRead(request);
		httpStream.close();
	}

	@Test
	public void parseContentRequestWithNoContentLength() throws IOException {
		header += "\r\ncontent !!! hoooray ";
		stream = new ByteArrayInputStream(header.getBytes());

		HttpRequestInputStream httpStream = new HttpRequestInputStream(stream);
		HttpRequest request = HttpRequestInputStream.createRequest(httpStream);

		assertEquals("GET", request.getMethod());
		assertEquals("/rfc/rfc3261.txt", request.getPath());
		assertHeadersRead(request);
		assertNull(request.getContent());

		httpStream.close();
	}

	@Test
	public void parseContentRequest() throws IOException {
		header += "Content-Length: 21\r\n\r\ncontent !!! hoooray ";
		stream = new ByteArrayInputStream(header.getBytes());

		HttpRequestInputStream httpStream = new HttpRequestInputStream(stream);
		HttpRequest request = HttpRequestInputStream.createRequest(httpStream);

		assertEquals("GET", request.getMethod());
		assertEquals("/rfc/rfc3261.txt", request.getPath());
		assertHeadersRead(request);
		assertEquals("content !!! hoooray ", request.getContent());

		httpStream.close();
	}
	
	@Test
	@Ignore// should pass but LF endings are not mandated by standard
	public void parseContentRequestWithLf() throws IOException { 
		stream = new ByteArrayInputStream(lf_header.getBytes());

		HttpRequestInputStream httpStream = new HttpRequestInputStream(stream);
		HttpRequest request = HttpRequestInputStream.createRequest(httpStream);

		assertEquals("GET", request.getMethod());
		assertEquals("/rfc/rfc3261.txt", request.getPath());
		assertHeadersRead(request);
		assertEquals("content !!! hoooray ", request.getContent());

		httpStream.close();
	}

}
