package com.adobe.server.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.adobe.server.IOUtil;
import com.adobe.server.core.Server;

//@Ignore
public class KeepAliveTest {

	private Server server;

	private PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();

	private CloseableHttpClient httpclient = HttpClients.custom().setConnectionManager(cm).build();

	private CloseableHttpResponse response;

	@Before
	public void setUp() throws IOException {
		server = new Server(80, true);
		server.start();
		FileUtils.copyFile(new File("src/test/resources/x.txt"), new File("x.txt"));
	}

	@After
	public void tearDown() throws IOException {
		server.stop();
		new File("x.txt").delete();
		new File("upload.txt").delete();
	}

	@Test
	public void keepConnectionAliveAndCloseAfter3Requests() throws IllegalStateException, IOException {
		submitRequestKeepAlive();
		assertConnectionOpen();
		submitRequestKeepAlive();
		assertConnectionOpen();
		submitRequestKeepAlive();
		assertConnectionOpen();

		submitRequestCloseConnection();
		assertConnectionClosed();
	}

	private void assertConnectionClosed() {
		Header[] headers = response.getHeaders("Connection");
		assertNotNull(headers);
		String value = headers[0].getValue();
		assertEquals("close", value);
	}

	private void submitRequestCloseConnection() throws IllegalStateException, IOException {
		submitRequest(false);
	}

	private void assertConnectionOpen() {
		Header[] headers = response.getHeaders("Connection");
		assertNotNull(headers);
		String value = headers[0].getValue();
		assertEquals("keep-alive", value);
	}

	private void submitRequestKeepAlive() throws IllegalStateException, IOException {
		submitRequest(true);
	}

	private void submitRequest(boolean keepalive) throws IllegalStateException, IOException {
		HttpGet get = new HttpGet("http://localhost/x.txt");
		if (!keepalive) {
			get.addHeader("Connection", "close");
		} else {
			get.addHeader("Connection", "Keep-Alive");
		}

		response = httpclient.execute(get);
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			InputStream instream = entity.getContent();
			/*
			 * read request but just swalow it in a byte array 
			 */
			IOUtil.copy(instream, new ByteArrayOutputStream());
		}
		// we're not closing the request, we're keeping the connection alive response.close();
	}

}
