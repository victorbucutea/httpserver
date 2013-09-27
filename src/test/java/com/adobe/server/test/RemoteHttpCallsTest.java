package com.adobe.server.test;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;

import com.adobe.server.core.Server;

public class RemoteHttpCallsTest {

	private Server server;

	@Before
	public void setUp() throws IOException {
		server = new Server(80);
		server.start();
		FileUtils.copyFile(new File("src/test/resources/img.jpg"), new File("img.jpg"));
		FileUtils.copyFile(new File("src/test/resources/x.txt"),new File("x.txt"));
	}

	@After
	public void tearDown() throws IOException {
		server.stop();
		new File("upload.jpg").delete();
		new File("upload.txt").delete();
		new File("img.jpg").delete();
		new File("x.txt").delete();
	}
	
}
