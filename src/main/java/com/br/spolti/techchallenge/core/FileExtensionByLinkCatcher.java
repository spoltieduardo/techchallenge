package com.br.spolti.techchallenge.core;

/**
 * Catch the file extension from repository url.
 */
public class FileExtensionByLinkCatcher {

	public static String getFileExtension(String link) {
		String[] dataSplited = link.split("\\.");
		if (dataSplited.length == 0 || link.lastIndexOf("/") > link.lastIndexOf(".")) {
			return "";
		}
		return dataSplited[dataSplited.length - 1];
	}

}