package com.br.spolti.techchallenge.core;

import lombok.Data;

/**
 * Represents each html string line from repository url.
 */
@Data
public class HtmlLineContent {

	private String content;
	private boolean isDirectory;
	boolean hasTagSearchLines;
	boolean hasTagSearchBytes;

	public HtmlLineContent(String content, boolean isDirectory) {
		this.content = content;
		this.isDirectory = isDirectory;
	}

	public HtmlLineContent(String content, boolean hasTagSearchLines, boolean hasTagSearchBytes) {
		this.content = content;
		this.hasTagSearchLines = hasTagSearchLines;
		this.hasTagSearchBytes = hasTagSearchBytes;
	}

	@Override
	public String toString() {
		return content + " - isDirectory = " + isDirectory +
				" - hasTagSearchLines = " + hasTagSearchLines +
				" - hasTagSearchBytes = " + hasTagSearchBytes;
	}

}