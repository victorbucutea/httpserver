package com.adobe.server.file;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.adobe.server.IOUtil;

/**
 * File manipulator class.
 * 
 * 1. Configure folder/storage location
 * 
 * 2. read/write files
 * 
 * 3. decide to keep file buffer (cache) or stream directly
 * from disk.
 * 
 * 4. Manage streams and resources ( locking , proper stream closing, etc. )
 * 
 * @author VictorBucutea
 * 
 */
public class FileManager {
	
	private FileManager(){
		
	}
	
	private static FileManager instance;
	
	public static FileManager getInstance() {
		if (instance == null ){
			instance = new FileManager();
		}
		return instance;
	}
	
	private File workingDir = new File(".");

	public boolean fileNotAvailable(String filePath) {
		File f = new File(workingDir,filePath);
		if (!f.canRead())
			return true;
		else
			return false;
	}
	
	public boolean fileExists(String filePath) {
		File f = new File(workingDir,filePath);
		
		if(f.exists())
			return true;
		else
			return false;
	}

	public InputStream readFile(String filePath) throws FileNotFoundException {
		FileInputStream stream = new FileInputStream(new File(workingDir,filePath));
		return stream;
	}

	public byte[] readFileWithBuffer(String filePath) throws IOException {
		ByteArrayOutputStream outputStr = new ByteArrayOutputStream();
		InputStream readFile = readFile(filePath);
		IOUtil.copy(readFile, outputStr);
		readFile.close();
		return outputStr.toByteArray();
	}
	

	public void writeFile(String filePath, byte[] content) throws IOException {
		FileOutputStream fileStream = new FileOutputStream(new File(workingDir,filePath));
		fileStream.write(content);
		fileStream.close();
	}

	public String getWorkingDir() {
		return workingDir.getAbsolutePath();
	}
	
	public void setWorkingDir(File dir){
		this.workingDir = dir;
	}

	public String evaluateContentType(String fileName) {
		String contentType;
		if ((fileName.toLowerCase().endsWith(".jpg")) || (fileName.toLowerCase().endsWith(".jpeg")) || (fileName.toLowerCase().endsWith(".jpe"))) {
			contentType = "image/jpg";
		} else if ((fileName.toLowerCase().endsWith(".gif"))) {
			contentType = "image/gif";
		} else if ((fileName.toLowerCase().endsWith(".htm")) || (fileName.toLowerCase().endsWith(".html"))) {
			contentType = "text/html";
		} else if ((fileName.toLowerCase().endsWith(".qt")) || (fileName.toLowerCase().endsWith(".mov"))) {
			contentType = "video/quicktime";
		} else if ((fileName.toLowerCase().endsWith(".class"))) {
			contentType = "application/octet-stream";
		} else if ((fileName.toLowerCase().endsWith(".mpg")) || (fileName.toLowerCase().endsWith(".mpeg"))
				|| (fileName.toLowerCase().endsWith(".mpe"))) {
			contentType = "video/mpeg";
		} else if ((fileName.toLowerCase().endsWith(".au")) || (fileName.toLowerCase().endsWith(".snd"))) {
			contentType = "audio/basic";
		} else if ((fileName.toLowerCase().endsWith(".wav"))) {
			contentType = "audio/x-wave";
		} else {
			contentType = "text/plain";
		} // default
		return contentType;
	}


}
