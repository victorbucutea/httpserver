Multi Thread Http File Server
==========


The server is a bare minimum http based file server. It supports download and upload (not yet multiplart/form-data) of files from the disk.

To start the server simply build via <b>mvn clean install</b> and then just run <b> java.exe -jar target/http.server-1.0.jar </b>

The start command syntax is: 
			
			<b> java.exe -jar target/http.server-1.0.jar </b> [port] ['keep-alive']

			* [port]         is ... well ... the port on which the server will start
			* ['keep-alive'] is the http keep-alive option in Http 1.1 . Please consider
							 this functionality as beta. It is not tested well enough.
							 
							 
The architecture of the server is simple:

 *  Server        --> Will create the socket listener thread and it will delegate processing to a HttpStreamHandler
                      via a ThreadManager
 *  ThreadManager





