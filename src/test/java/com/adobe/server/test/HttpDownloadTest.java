package com.adobe.server.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.junit.Test;

import com.adobe.server.IOUtil;
import com.adobe.server.file.FileManager;

public class HttpDownloadTest extends RemoteHttpCallsTest {

	private PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();

	private CloseableHttpClient httpclient = HttpClients.custom().setConnectionManager(cm).build();

	@Test
	public void concurrentDownloadRequests() throws IllegalStateException, IOException, InterruptedException {
		GetThread[] responses = submitRequest(2, "http://localhost/x.txt");
		assertFileProperlyTx(responses, "x.txt");
	}
	
	@Test
	public void manyConcurrentDownloadRequests() throws IllegalStateException, IOException, InterruptedException {
		GetThread[] responses = submitRequest(4000, "http://localhost/x.txt");
		assertFileProperlyTx(responses, "x.txt");
	}
	
	@Test
	public void downloadBinaryFile() throws IllegalStateException, IOException, InterruptedException {
		GetThread[] responses = submitRequest(40, "http://localhost/img.jpg");
		assertFileProperlyTx(responses, "img.jpg");
	}

	private void assertFileProperlyTx(GetThread[] responses, String fileName) throws IOException {
		byte[] readFile = FileManager.getInstance().readFileWithBuffer(fileName);
		for(GetThread response : responses ){
			byte[] responseBytes = response.getResponseContent();
			for (int i = 0 ; i < responseBytes.length ; i++ ){
				assertEquals(responseBytes[i], readFile[i]);
			}
		}
	}


	private GetThread[] submitRequest(int threadNo, String uri) throws IllegalStateException, IOException, InterruptedException {
		// create a thread for each URI
		GetThread[] threads = new GetThread[threadNo];
		for (int i = 0; i < threads.length; i++) {
		    HttpGet httpget = new HttpGet(uri);
		    threads[i] = new GetThread(httpclient, httpget);
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
	
	static class GetThread extends Thread {

	    private final CloseableHttpClient httpClient;
	    private final HttpContext context;
	    private final HttpGet httpget;
	    private byte[] content;

	    public GetThread(CloseableHttpClient httpClient, HttpGet httpget) {
	        this.httpClient = httpClient;
	        this.context = HttpClientContext.create();
	        this.httpget = httpget;
	    }

	    @Override
	    public void run() {
	        try {
	            CloseableHttpResponse response = httpClient.execute(httpget, context);
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
