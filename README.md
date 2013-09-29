Multi Thread Http File Server
==========


The server is a bare minimum http based file server. It supports download and upload (not yet multiplart/form-data) of files from the disk.

To start the server simply build via <b>mvn clean install</b> and then just run <b> java.exe -jar target/http.server-1.0.jar </b>

The start command syntax is: 
			
			java.exe -jar target/http.server-1.0.jar [port] ['keep-alive']

			* [port]         is ... well ... the port on which the server will start
			* ['keep-alive'] is the http keep-alive option in Http 1.1 . Please consider
							 this functionality as beta. It is not tested well enough.

The working directory of the server will be the directory from which it was launched. 

To <b>access</b> files from the working directory simply refer them in the path of the URL (e.g <b>http://localhost/somefile.txt</b> will try to download <b>somefile.txt</b> from the directory the JVM was launched).

To <b>upload</b> files is a bit more complicated, you would need a <a href="http://hc.apache.org/httpclient-3.x/">client</a>. Simply attach them to the request and set the proper Content-Type. An example to upload file <b>'upload.jpg'</b> would be this:
```java
	private HttpResponse submitUploadBinary() throws ClientProtocolException, IOException {
		HttpClient httpclient = HttpClients.createDefault();

		HttpPost httppost = new HttpPost("http://localhost/upload.jpg");
		File file = new File("img.jpg");
		FileEntity entity = new FileEntity(file, ContentType.create("image/jpg", "UTF-8"));
		httppost.setEntity(entity);

		HttpResponse response = httpclient.execute(httppost);
		return response;
	}
```
							 
The components main of the server are these:

*  Server        	    --> Will create the socket listener thread and it will delegate processing to a HttpStreamHandler
                          via a ThreadManager
*  ThreadManager 	    --> Handles Thread pool management, increase/decrease number of threads, manage thread saturation limits ( 
					      how many concurrent executing jobs we can have).
*  HttpStreamHandler   --> Will create the HttpRequest from the HttpRequestInputStream and the HttpResponse object and it will 
                          delegate processing to 'handlers'
*  HttpDownloadHandler --> HttpHandler responsible for streaming files into the HttpResponse
*  HttpUploadHandler   --> HttpHandler responsible for writing files read from the HttpRequest content files
							resources ( locking , proper stream closing, etc. )





