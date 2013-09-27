package com.adobe.server.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.adobe.server.IOUtil;
import com.adobe.server.core.Server;


@Ignore
public class KeepAliveTests {
	
	private Server server;

	private PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();

	private CloseableHttpClient httpclient = HttpClients.custom().setConnectionManager(cm).build();

	private CloseableHttpResponse response;

	@Before
	public void setUp() {
		server = new Server(80);
		server.start();
	}

	@After
	public void tearDown() throws IOException {
		server.stop();
	}

	@Test
	public void keepConnectionAliveAndCloseAfter3Requests() throws IllegalStateException, IOException {
		submitRequestKeepAlive();
	}

	@Test
	public void closeConnectionOnFirstRequest() throws IOException {
	}
	
	@SuppressWarnings("unused")
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
			IOUtil.printStream(instream);
		}
		response.close();
	}


}
