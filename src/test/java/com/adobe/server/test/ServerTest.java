package com.adobe.server.test;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.adobe.server.core.Server;
import com.adobe.server.file.FileManager;

public class ServerTest {

	private Server server;

	@Before
	public void startServer() throws IOException {
		server = new Server(80);
		server.start();
		FileUtils.copyFile(new File("src/test/resources/x.txt"),new File("x.txt"));
	}

	@After
	public void stopServer() throws IOException {
		new File("x.txt").delete();
		new File("upload.txt").delete();
	}

	@Test
	public void stopWhileWaitingOnReadFromSocket() throws Exception {
		URL url = new URL("http://localhost");
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setDoOutput(true);
		connection.setRequestMethod("GET");

		OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
		writer.write("GET /rfc/rfc3261.txt HTTP/1.1\r\n");
		writer.flush();
		// leave the stream open and stop server
		// writer.close();

		server.stop();
	}

	// @Test
	public void stopWhileThreadsAreProcessing() {

	}

	@Test
	public void stopWhileProcessingIsBlocked() throws ClientProtocolException, IOException {
		/*
		 * we synchronize on File manager, this way we make sure the
		 * executor Thread will wait for us to release the lock, which will
		 * never happen btw ;)
		 */

		FileManager fm = FileManager.getInstance();

		synchronized (fm) {

			/*
			 * run in new thread so we don't block ourselves whilst waiting for
			 * an answer
			 */
			new Thread(new Runnable() {
				public void run() {
					submitUploadText();
				}
			}).start();
			
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			server.stop();
		}
	}

	// @Test
	public void stopWhileProcessingIsSleeping() {

	}

	// @Test
	public void mixedStateStop() {

	}

	private CloseableHttpResponse submitUploadText() {
		CloseableHttpClient httpclient = HttpClients.createDefault();

		HttpPost httppost = new HttpPost("http://localhost/upload.txt");
		File file = new File("x.txt");
		FileEntity entity = new FileEntity(file, ContentType.create("text/plain", "UTF-8"));
		httppost.setEntity(entity);
		CloseableHttpResponse response = null;
		try {
			response = httpclient.execute(httppost);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				response.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return response;
	}

}
