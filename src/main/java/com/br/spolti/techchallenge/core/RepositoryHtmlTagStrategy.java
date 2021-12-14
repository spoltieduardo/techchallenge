package com.br.spolti.techchallenge.core;

/**
 * String Constants to use in GitHub html repository fetch data string.
 */
public interface RepositoryHtmlTagStrategy {

	/**
	 * Returns repository url.
	 */
	String getSourceCodeRepositoryUrl();

	/**
	 * Returns tag that represents the link to subdirectory in repository html.
	 */
	String getTagSearchLinkSubdirectory();

	/**
	 * Returns tag that represents the number of lines of file by link in repository html.
	 */
	String getTagSearchLines();

}