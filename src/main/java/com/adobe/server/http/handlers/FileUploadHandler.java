package com.adobe.server.http.handlers;

import java.io.IOException;

import com.adobe.server.Logger;
import com.adobe.server.file.FileManager;
import com.adobe.server.http.HttpRequest;
import com.adobe.server.http.HttpResponse;

public class FileUploadHandler implements HttpHandler {

	FileManager fileMgr = FileManager.getInstance();
	Logger logger = new Logger();

	@Override
	public void handle(HttpRequest request, HttpResponse response) throws IOException {

		if( !"POST".equals(request.getMethod()))
			return;
		
		String filePath = request.getPath();
		
		if (filePath.startsWith("/")) {
			filePath = filePath.replaceFirst("/", "");
		}

		synchronized (fileMgr) {
			if (fileMgr.fileExists(filePath)) {
				response.setStatus("403 Forbiden");
				response.write("File " + filePath + " already exists in configured directory " + fileMgr.getWorkingDir());
			}

			logger.logUploadFile(fileMgr.getWorkingDir(), filePath);

			fileMgr.writeFile(filePath, request.getContentAsBytes());
		}

	}

}
