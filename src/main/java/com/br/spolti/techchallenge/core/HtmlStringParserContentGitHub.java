package com.br.spolti.techchallenge.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Parsing the repository content to return only html lines interested.
 */
@Component
public class HtmlStringParserContentGitHub implements HtmlStringParserContentStrategy {

	private static Logger logger = LoggerFactory.getLogger(HtmlStringParserContentGitHub.class);

	private final int NEXT_REQUEST_DELAY = 50000;  // 50s

	private RepositoryHtmlTagStrategy repositoryHtmlTagStrategy;

	@Autowired
	public HtmlStringParserContentGitHub(RepositoryHtmlTagStrategy repositoryHtmlTagStrategy) {
		this.repositoryHtmlTagStrategy = repositoryHtmlTagStrategy;
	}

	public String getSourceCodeRepositoryUrl() {
		return repositoryHtmlTagStrategy.getSourceCodeRepositoryUrl();
	}

	private boolean isSucceedConnection(HttpURLConnection urlConnection) throws IOException {
		int attemptsToConnectIfUrlConnectionReturnDifferentHttpOk = 1;
		int responseCode = HttpURLConnection.HTTP_OK;

		do {
			urlConnection.connect();
			responseCode = urlConnection.getResponseCode();

			if (responseCode != HttpURLConnection.HTTP_OK) {
				try {
					if (attemptsToConnectIfUrlConnectionReturnDifferentHttpOk == 3) {
						break;
					}
					attemptsToConnectIfUrlConnectionReturnDifferentHttpOk++;
					Thread.sleep(NEXT_REQUEST_DELAY);
				} catch (InterruptedException e) {
					logger.error(e.getMessage());
					throw new RuntimeException(e.getMessage());
				}
			}
		} while (responseCode != HttpURLConnection.HTTP_OK);

		return responseCode == HttpURLConnection.HTTP_OK;
	}

	public List<HtmlLineContent> getContentList(String repositoryUrl) {
		List<HtmlLineContent> htmlLineContentList = new ArrayList<HtmlLineContent>();
		HttpURLConnection urlConnection = null;

		try {
			URL url = new URL(repositoryUrl);
			urlConnection = (HttpURLConnection) url.openConnection();

			if (!isSucceedConnection(urlConnection)) {
				throw new RuntimeException(urlConnection.getResponseMessage());
			}

			int attemptsToConnectIfThrowsTooManyRequests = 1;

			do {
				try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"))) {
					String line;
					boolean isCatchNumberFileLines = false;

					while ((line = bufferedReader.readLine()) != null) {
						if (line.equals("")) {
							continue;
						}

						boolean isLinkToSubDirectory = line.indexOf(repositoryHtmlTagStrategy.getTagSearchLinkSubdirectory()) > -1;

						if (isLinkToSubDirectory) {

							htmlLineContentList.add(new HtmlLineContent(line, isLinkToSubDirectory));
						}

						boolean hasTagSearchLines = line.indexOf(repositoryHtmlTagStrategy.getTagSearchLines()) > -1;

						if (isCatchNumberFileLines && !line.trim().equals("")) {
							if (line.contains("lines")) {
								HtmlLineContent lineContent = new HtmlLineContent(line, true, false);
								htmlLineContentList.add(lineContent);
							} else {
								HtmlLineContent lineContent = new HtmlLineContent(line, false, true);
								htmlLineContentList.add(lineContent);
							}
							isCatchNumberFileLines = false;
						}

						if (hasTagSearchLines) {
							isCatchNumberFileLines = true;
						}

					}
					attemptsToConnectIfThrowsTooManyRequests = 4;
				} catch (Exception e) {
					if (e.getMessage().toLowerCase().contains("too many requests")) {
						try {
							attemptsToConnectIfThrowsTooManyRequests++;
							Thread.sleep(NEXT_REQUEST_DELAY);
						} catch (InterruptedException e1) {
							logger.error("e1: " + e1.getMessage());
							throw new RuntimeException(e1.getMessage());
						}
					} else {
						logger.error("e2: " + e.getMessage());
						throw new RuntimeException(e.getMessage());
					}
				}

			} while (attemptsToConnectIfThrowsTooManyRequests <= 3);

		} catch (IOException | RuntimeException e) {
			if (e instanceof IOException) {
				logger.error("Could not connect to " + repositoryUrl, e);
			} else {
				logger.error(e.getMessage());
			}
			throw new RuntimeException(e.getMessage());
		} finally {
			if (urlConnection != null) {
				urlConnection.disconnect();
			}
		}
		return htmlLineContentList;
	}

}