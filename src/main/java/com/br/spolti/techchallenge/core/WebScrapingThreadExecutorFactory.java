package com.br.spolti.techchallenge.core;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class WebScrapingThreadExecutorFactory {

	private Map<String, WebScrapingThreadExecutor> mapWebScrapingServiceThreadExecutor;
	
	private WebScrapingThreadExecutorFactory() {
		mapWebScrapingServiceThreadExecutor = new HashMap<>();
	}
	
	public WebScrapingThreadExecutor createNewInstanceOrReturnExistent(String repositoryUrl) {
	
		WebScrapingThreadExecutor instance = mapWebScrapingServiceThreadExecutor.get(repositoryUrl);
		
		if(instance == null) {
			
			instance =  new WebScrapingThreadExecutor(repositoryUrl);
		}
		
		return instance;
	}
}
