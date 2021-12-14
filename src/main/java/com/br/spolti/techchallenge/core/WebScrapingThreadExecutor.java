package com.br.spolti.techchallenge.core;

import com.br.spolti.techchallenge.model.FileInfo;
import com.br.spolti.techchallenge.model.GitRepositoryInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Each request is processed in separate thread to parsing the url repository and return json.
 */
public class WebScrapingThreadExecutor extends Thread {

	private static Logger logger = LoggerFactory.getLogger(WebScrapingThreadExecutor.class);
	
	private HtmlStringParserContentStrategy htmlGitHubStringContent;
	
	private GitRepositoryInfoCache gitRepositoryInfoCache;

	private String repositoryUrl;
	
	private GitRepositoryInfo gitRepositoryInfo;
	
	public WebScrapingThreadExecutor(String repositoryUrl) {
		this.repositoryUrl = repositoryUrl;
	}

	public void setHtmlGitHubStringContent(HtmlStringParserContentStrategy htmlGitHubStringContent) {
		this.htmlGitHubStringContent = htmlGitHubStringContent;
	}

	public void setGroupDataByFileExtensionModelCache(GitRepositoryInfoCache gitRepositoryInfoCache) {
		this.gitRepositoryInfoCache = gitRepositoryInfoCache;
	}

	public void setRepositoryUrl(String repositoryUrl) {
		this.repositoryUrl = repositoryUrl;
	}

	public GitRepositoryInfo getGroupDataByFileExtensionModel() {
		return gitRepositoryInfo;
	}

	@Override
	public void run() {
		synchronized(this) {
		boolean isFetchedFromCache = false;

		try {
			gitRepositoryInfo = gitRepositoryInfoCache.getItem(repositoryUrl);
			isFetchedFromCache = gitRepositoryInfo != null;

			if(!isFetchedFromCache) {
				gitRepositoryInfo = getRepositoryUrlContentModel(repositoryUrl, new GitRepositoryInfo());
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			 if(!isFetchedFromCache) {
				gitRepositoryInfoCache.addItem(repositoryUrl, gitRepositoryInfo);
			}
			 notifyAll();
		}
			logger.info(gitRepositoryInfo.toJson());
		}
	}
    
	/**
	 * Object model to represent data of file number of lines and file count by each file extension.
	 */
	private GitRepositoryInfo getRepositoryUrlContentModel(final String repositoryUrl, GitRepositoryInfo gitRepositoryInfo) {
		try {
			List<HtmlLineContent> lineHtmlContentList = htmlGitHubStringContent.getContentList(repositoryUrl);
			int fileNumberLines;
			String fileExtension = FileExtensionByLinkCatcher.getFileExtension(repositoryUrl);
			
			for (HtmlLineContent htmlLineContent : lineHtmlContentList) {
				if(htmlLineContent.isDirectory()) {
					String href = "href=";
					int posHref = htmlLineContent.getContent().indexOf(href);
					int endPosHref = htmlLineContent.getContent().indexOf("</a>");
					String macrolink = htmlLineContent.getContent().substring(posHref + 7, endPosHref - 1);
					String linkNotTreatable = "";
					
					for(String s :  macrolink.split("/")) {
						linkNotTreatable += "/" + s;
					}
					
					String linkTreatable = linkNotTreatable.substring(1, linkNotTreatable.indexOf("\">"));			
					
					gitRepositoryInfo = getRepositoryUrlContentModel(
							htmlGitHubStringContent.getSourceCodeRepositoryUrl() + linkTreatable,
                            gitRepositoryInfo);
				}
				else {
					if(htmlLineContent.isHasTagSearchLines()) {

						fileNumberLines = FileLinesCatcher.getNumberLines(htmlLineContent.getContent());

						FileInfo fileInfo = new FileInfo(fileExtension);
						fileInfo.addFileNumberOfLines(fileNumberLines);

						gitRepositoryInfo.addFileNumberOfLines(fileInfo);
					}
				}
			}
		}
		catch (RuntimeException e) {
			logger.error(e.getMessage());
		}
		finally {
			gitRepositoryInfo.setRepositoryUrl(repositoryUrl);
		}
		return gitRepositoryInfo;
	}

}