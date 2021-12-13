package com.br.spolti.techchallenge.core;

/**
 *  Catch file number lines from html line string from repository.
 */
public class FileLinesCatcher {

	public static int getNumberLines(String valueOrigin) {
		if(valueOrigin == null || valueOrigin.trim().equals("")) {
			return 0;
		}
		String[] dataSplited = valueOrigin.trim().split(" ");
		return Integer.parseInt(dataSplited[0].trim());
	}

}