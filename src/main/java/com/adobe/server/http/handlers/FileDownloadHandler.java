package com.adobe.server.http.handlers;

import java.io.IOException;

import com.adobe.server.file.FileManager;
import com.adobe.server.http.HttpRequest;
import com.adobe.server.http.HttpResponse;

public class FileDownloadHandler implements HttpHandler {

	private FileManager fileMgr = FileManager.getInstance();

	@Override
	public void handle(HttpRequest request, HttpResponse response) throws IOException {

		if (!"GET".equals(request.getMethod())) {
			return;
		}

		String filePath = request.getPath();

		if (filePath.startsWith("/")) {
			filePath = filePath.replaceFirst("/", "");
		}

		synchronized (fileMgr) {

			if (fileMgr.fileNotAvailable(filePath)) {
				response.setStatus("404 Not Found");
				response.write("Cannot find file :" + filePath + " in configured directory " + fileMgr.getWorkingDir());
				response.flush();
				return;
			}

			response.setContentType(fileMgr.evaluateContentType(filePath));

			response.write(fileMgr.readFileWithBuffer(filePath));
		}

	}

}
