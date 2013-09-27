package com.adobe.server.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.junit.Test;

import com.adobe.server.IOUtil;
import com.adobe.server.file.FileManager;

public class HttpUploadTest extends RemoteHttpCallsTest {
	
	private PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();

	private CloseableHttpClient httpclient = HttpClients.custom().setConnectionManager(cm).build();

	private FileManager fileMgr = FileManager.getInstance();

	@Test
	public void test403UploadBinary() throws ClientProtocolException, IOException {
		HttpResponse response = submitUploadBinary();
		assertEquals(200, response.getStatusLine().getStatusCode());
		assertTrue(fileMgr.fileExists("upload.jpg"));

		response = submitUploadBinary();
		assertEquals(403, response.getStatusLine().getStatusCode());
		assertTrue(fileMgr.fileExists("upload.jpg"));
	}

	@Test
	public void test403UploadText() throws ClientProtocolException, IOException {
		HttpResponse response = submitUploadText();
		assertEquals(200, response.getStatusLine().getStatusCode());
		assertTrue(fileMgr.fileExists("upload.txt"));

		response = submitUploadText();
		assertEquals(403, response.getStatusLine().getStatusCode());
		assertTrue(fileMgr.fileExists("upload.txt"));
	}

	@Test
	public void multipleConcurrentUpload() throws ClientProtocolException, IOException, IllegalStateException, InterruptedException {
		PostThread[]  responses = submitMultiThreadRequest(50);
		
		for (PostThread resp : responses){
			assertEquals(200, resp.getStatusCode());
		}
		
		for (int i = 0 ; i < 50 ; i++){
			File file = new File("upload"+i+".jpg");
			assertTrue(file.exists());
			assertEquals(92176, file.length());
			file.deleteOnExit();
		}
		
	}

	private HttpResponse submitUploadBinary() throws ClientProtocolException, IOException {
		HttpClient httpclient = HttpClients.createDefault();

		HttpPost httppost = new HttpPost("http://localhost/upload.jpg");
		File file = new File("img.jpg");
		FileEntity entity = new FileEntity(file, ContentType.create("image/jpg", "UTF-8"));
		httppost.setEntity(entity);

		HttpResponse response = httpclient.execute(httppost);
		return response;
	}

	private CloseableHttpResponse submitUploadText() throws ClientProtocolException, IOException {
		CloseableHttpClient httpclient = HttpClients.createDefault();

		HttpPost httppost = new HttpPost("http://localhost/upload.txt");
		File file = new File("x.txt");
		FileEntity entity = new FileEntity(file, ContentType.create("text/plain", "UTF-8"));
		httppost.setEntity(entity);
		CloseableHttpResponse response = null;
		try {
			response = httpclient.execute(httppost);
		} finally {
			response.close();
		}

		return response;
	}
	
	private PostThread[] submitMultiThreadRequest(int threadNo) throws IllegalStateException, IOException, InterruptedException {
		// create a thread for each URI
		PostThread[] threads = new PostThread[threadNo];
		for (int i = 0; i < threads.length; i++) {
		    HttpPost httppost = new HttpPost("http://localhost/upload"+i+".jpg");
		    File file = new File("img.jpg");
			FileEntity entity = new FileEntity(file, ContentType.create("image/jpg", "UTF-8"));
			httppost.setEntity(entity);
		    threads[i] = new PostThread(httpclient, httppost);
		}

		// start the threads
		for (int j = 0; j < threads.length; j++) {
		    threads[j].start();
		}

		// join the threads
		for (int j = 0; j < threads.length; j++) {
		    threads[j].join();
		}
		
		return threads;
	}
	
	static class PostThread extends Thread {

	    private final CloseableHttpClient httpClient;
	    private final HttpContext context;
	    private final HttpPost httppost;
	    private byte[] content;
		private int statusCode;

	    public PostThread(CloseableHttpClient httpClient, HttpPost httpget) {
	        this.httpClient = httpClient;
	        this.context = HttpClientContext.create();
	        this.httppost = httpget;
	    }

	    public int getStatusCode() {
			return statusCode;
		}

		@Override
	    public void run() {
	        try {
	            CloseableHttpResponse response = httpClient.execute(httppost, context);
	            this.statusCode = response.getStatusLine().getStatusCode();
	            try {
	                HttpEntity entity = response.getEntity();
	    			content = IOUtil.toByteArray(entity.getContent());
	            } finally {
	                response.close();
	            }
	        } catch (ClientProtocolException ex) {
	            ex.printStackTrace();
	        } catch (IOException ex) {
	            ex.printStackTrace();
	        }
	    }
	    
	    public byte[] getResponseContent(){
	    	return content;
	    }

	}

}
