package com.adobe.server.core;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.adobe.server.Logger;
import com.adobe.server.http.HttpRequestInputStream;
import com.adobe.server.http.HttpResponseOutputStream;
import com.adobe.server.http.HttpStreamHandler;

/**
 * Main class responsible for listening on sockets and delegating to processing
 * threads. The processing Threads are managed by a {@link ThreadManager} and
 * the request stream is processed by a {@link HttpStreamHandler}.
 * 
 * @author VictorBucutea
 * 
 */
public class Server {

	private int port;
	private boolean keepAliveSupport;
	private ThreadManager manager = new ThreadManager();
	private ServerSocket ss;
	private Logger logger = new Logger();
	private boolean serverStopped;

	public Server(int port) {
		this.port = port;
	}
	
	public Server(int port, boolean keepAliveSupport){
		this.port = port;
		this.keepAliveSupport = keepAliveSupport;
	}

	public void start() {
		logger.logStartingServer(port);
		startSocketListenerThread();
	}

	public void stop() throws IOException {
		closeServerSocket();
		closeActiveThreads();
		logger.logServerStopped();
	}

	private void closeActiveThreads() {
		manager.stopExecuting();
	}

	private void closeServerSocket() throws IOException {
		/*
		 * close() is the best option to stop the socket. If a thread is blocked
		 * in accept() it will throw an exception and close.
		 */
		ss.close();
		this.serverStopped = true;
	}

	private void startSocketListenerThread() {
		SocketListenerThread socketListener = new SocketListenerThread(port);
		socketListener.start();
	}

	private class SocketListenerThread extends Thread {

		private Logger logger = new Logger();
		private int port;

		public SocketListenerThread(int port) {
			this.port = port;
			setName("Socket listener thread");
		}

		public void run() {
			try {
				ss = new ServerSocket(port);
				logger.logListenToConnection(port);
				acceptData();
			} catch (IOException e) {
				// this can only occur when binding to specified port is not
				// possible
				logger.logCannotBindToPort(port);
				logger.error(e);
			}

		}

		private void acceptData() {
			while (true) {
				try {

					if (serverStopped)
						break;

					final Socket s = ss.accept();
					Runnable streamHandler = createHttpStreamHandlerRunnable(s);
					manager.execute(streamHandler);
				} catch (IOException e) {
					// I/O error in reading/writing data, or server closed while
					// accepting data
					if (!serverStopped)
						logger.error(e);
				}
			}
		}

		private Runnable createHttpStreamHandlerRunnable(final Socket s) {
			Runnable processRequest = new Runnable() {
				public void run() {
					try {
						HttpRequestInputStream inputStream = new HttpRequestInputStream(s.getInputStream());
						HttpResponseOutputStream outputStream = new HttpResponseOutputStream(s.getOutputStream());
						new HttpStreamHandler(keepAliveSupport).handle(inputStream, outputStream);
					} catch (IOException e) {
						logger.error(e);
					}
				};
			};
			return processRequest;
		}

	}

}
