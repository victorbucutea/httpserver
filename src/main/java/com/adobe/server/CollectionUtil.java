package com.adobe.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CollectionUtil {

	public static List<String> asList(String source, String separator) {
		Scanner scanner = new Scanner(source);
		scanner.useDelimiter(separator);
		List<String> list = new ArrayList<String>();
		while (scanner.hasNext()) {
			list.add(scanner.next());
		}
		return list;
	}

}
