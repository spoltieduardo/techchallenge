package com.br.spolti.techchallenge.core;

import java.util.List;

public interface HtmlStringParserContentStrategy {

	String getSourceCodeRepositoryUrl();
	
	List<HtmlLineContent> getContentList(String repositoryUrl);

}