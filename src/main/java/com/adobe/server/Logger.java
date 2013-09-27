package com.adobe.server;

import java.io.IOException;

/**
 * Simple logger implementation. Will format text and will delegate to other
 * logging mechanisms.
 * 
 * @author VictorBucutea
 * 
 */
public class Logger {

	public void log(String text) {
		System.out.println(text);
	}

	public void logStartingServer(int port) {
		log("--- Starting server  ---");
	}
	
	public void logListenToConnection(int port ){
		log("--- Server started. Listening to connections on port "+port+" --- ");
	}

	public void logServerStopped() {
		log("--- Server stopped ---");
	}

	public void logCannotBindToPort(int port) {
		log("Cannot bind to port "+port +".");
	}

	public void error(IOException e) {
		e.printStackTrace();
	}

	public void logUploadFile(String workingDir, String filePath) {
		log("--- Uploading file "+filePath+" in directory "+workingDir);
	}
}
