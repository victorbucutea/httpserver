/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.adobe.server;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * Utility class, copied from Apache commons Io package
 * 
 * @author VictorBucutea
 * 
 */
public class IOUtil {

	private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

	/**
	 * COPY FROM Apache Commons Io.
	 * 
	 * @param input
	 * @param output
	 * @return
	 * @throws IOException
	 */
	public static long copy(InputStream input, OutputStream output) throws IOException {
		byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
		long count = 0;
		int n = 0;
		while (-1 != (n = input.read(buffer))) {
			output.write(buffer, 0, n);
			count += n;
		}
		return count;
	}

	/**
	 * COPY FROM Apache Commons Io.
	 * 
	 * @param input
	 * @return
	 * @throws IOException
	 */
	public static byte[] toByteArray(InputStream input) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		copy(input, output);
		return output.toByteArray();
	}


	public static String toString(InputStream instream) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(instream));
		ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
		int input = 0;
		while ((input = in.read()) != -1) {
			outBytes.write(input);
		}

		return new String(outBytes.toByteArray());
	}
	
	public static byte[] read(InputStream in, long length ) throws IOException {
		int input = 0;
		long bytesRead = 0;
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		while ((input = in.read()) != -1) {
			buffer.write((byte) input);
			bytesRead++;
			if (bytesRead >= length) {
				break;
			}
		}
		return buffer.toByteArray();
	}
	
	public static LinkedList<String> readHeaderAreaAsString(InputStream in) throws IOException { 
		String headerArea = new String(readHeaderArea(in));
		headerArea = headerArea.replaceAll("[\\r\\n]*$", "");
		LinkedList<String> lines = new LinkedList<String>(); 
		Scanner scanner = new Scanner(headerArea);
		scanner.useDelimiter("\\r?\\n");
		while (scanner.hasNext()){
			lines.add(scanner.next());
		}
		return lines;
	}

	public static byte[] readHeaderArea(InputStream in) throws IOException {
		int input = 0;
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		ByteBuffer last4Chars = ByteBuffer.wrap(new byte[4]);
		
		while ((input = in.read()) != -1) {
			byte inputByte = (byte) input;
			buffer.write(inputByte);
			last4Chars.put(inputByte);
			
			if (emptyLine(last4Chars)) {
				break;
			}

		}
		byte[] byteArray = buffer.toByteArray();
		return byteArray;

	}

	private static boolean emptyLine(ByteBuffer buffer) {

		boolean foundEmptyLine = false;
		if (!buffer.hasRemaining()) {
			/*
			 * empty line is either \n\n or \r\n \r\n
			 */
			int noOfLf = 0;
			int noOfCr = 0;
			for (byte b : buffer.array()) {
				char ch = (char) b; // a CR or LF is exactly one byte long
				if ('\n' == ch)
					noOfLf++;
				if ('\r' == ch)
					noOfCr++;
			}
			
			if ( ((char) buffer.array()[3]) == '\r'){
				buffer.position(3);
				buffer.compact();
				return false;
			}
			
			if (noOfLf == 2 || noOfCr == 2) {
				foundEmptyLine = true;
			}

			buffer.position(2);
			buffer.compact();
		}

		return foundEmptyLine;

	}

}
