package com.br.spolti.techchallenge.core;

import com.br.spolti.techchallenge.model.GitRepositoryInfo;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class GitRepositoryInfoCache {

	private Map<String, GitRepositoryInfo> gitRepositoryInfoCache;
	
	public GitRepositoryInfoCache() {
		gitRepositoryInfoCache = new HashMap<>();
	}
	
	public void addItem(String repositoryUrl, GitRepositoryInfo gitRepositoryInfo) {
		gitRepositoryInfoCache.put(repositoryUrl, gitRepositoryInfo);
	}

	public GitRepositoryInfo getItem(String repositoryUrl) {
		return gitRepositoryInfoCache.get(repositoryUrl);
	}

	public void clearCache(String repositoryUrl) {
		gitRepositoryInfoCache.remove(repositoryUrl);
	}
	
	public void clearAllCache() {
		gitRepositoryInfoCache.clear();
	}

}