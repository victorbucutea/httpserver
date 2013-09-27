package com.adobe.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.adobe.server.core.Server;


public class Main {

	static Logger logger = new Logger();
	
	public static void main(String[] args) throws IOException {
		
		int port = 80;
		
		if (args.length > 0 ){
			port = Integer.valueOf(args[0]);
		}
		
		Server server = new Server(port);
		server.start();
		
		String command = "start";
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in) );
		logger.log("--- To stop the server just type 'stop' followed by enter ---");
		while (true){
			command = br.readLine();
			if (!"stop".equals(command)){
				logger.log("The only command is 'stop'. ");
			} else {
				server.stop();
				break;
			}
		}
	}
}
