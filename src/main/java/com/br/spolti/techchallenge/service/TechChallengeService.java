package com.br.spolti.techchallenge.service;

import com.br.spolti.techchallenge.core.GitRepositoryInfoCache;
import com.br.spolti.techchallenge.core.HtmlStringParserContentStrategy;
import com.br.spolti.techchallenge.core.WebScrapingThreadExecutor;
import com.br.spolti.techchallenge.core.WebScrapingThreadExecutorFactory;
import com.br.spolti.techchallenge.model.GitRepositoryInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.commons.validator.routines.UrlValidator;

/**
 * Reading each requisition concurrently, one by one, using cache, to avoid message: "HTTP 429: Too Many Requests".
 */
@Service
public class TechChallengeService {

	private static Logger logger = LoggerFactory.getLogger(TechChallengeService.class);

	private HtmlStringParserContentStrategy htmlGitHubStringContent;

	private GitRepositoryInfoCache gitRepositoryInfoCache;

	private WebScrapingThreadExecutorFactory webScrapingThreadExecutorFactory;

	@Autowired
	public TechChallengeService(HtmlStringParserContentStrategy htmlGitHubStringContent,
															GitRepositoryInfoCache gitRepositoryInfoCache,
															WebScrapingThreadExecutorFactory webScrapingThreadExecutorFactory) {
		this.htmlGitHubStringContent = htmlGitHubStringContent;
		this.gitRepositoryInfoCache = gitRepositoryInfoCache;
		this.webScrapingThreadExecutorFactory = webScrapingThreadExecutorFactory;
	}

	public GitRepositoryInfo getRepositoryUrlContentModel(final String repositoryUrl) throws RuntimeException {
		synchronized (this) {
			GitRepositoryInfo gitRepositoryInfo = gitRepositoryInfoCache.getItem(repositoryUrl);

			if (gitRepositoryInfo != null) {
				return gitRepositoryInfo;
			}

			WebScrapingThreadExecutor webScrapingThreadExecutor = webScrapingThreadExecutorFactory.createNewInstanceOrReturnExistent(repositoryUrl);
			webScrapingThreadExecutor.setHtmlGitHubStringContent(htmlGitHubStringContent);
			webScrapingThreadExecutor.setGroupDataByFileExtensionModelCache(gitRepositoryInfoCache);

			webScrapingThreadExecutor.start();

			synchronized (webScrapingThreadExecutor) {
				try {
					while (gitRepositoryInfoCache.getItem(repositoryUrl) == null) {
						webScrapingThreadExecutor.wait();
					}
				} catch (InterruptedException e) {
					logger.info(e.getMessage());
					throw new RuntimeException(e.getMessage());
				}
			}
			return webScrapingThreadExecutor.getGroupDataByFileExtensionModel();
		}
	}

	public void clearCache(String repositoryUrl) {
		gitRepositoryInfoCache.clearCache(repositoryUrl);
	}

	public void clearAllCache() {
		gitRepositoryInfoCache.clearAllCache();
	}

	public void isValidRepositoryUrl(String repositoryUrl) {
		if (repositoryUrl == null) {
			throw new RuntimeException("Repository URL is cannot be null.");
		}

		String[] schemes = {"http", "https"};
		UrlValidator urlValidator = new UrlValidator(schemes);

		if (!urlValidator.isValid(repositoryUrl)) {
			throw new RuntimeException("Repository URL is not valid.");
		}
	}

}