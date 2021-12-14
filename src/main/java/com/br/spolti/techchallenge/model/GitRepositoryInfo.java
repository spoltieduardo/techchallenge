package com.br.spolti.techchallenge.model;

import com.google.gson.Gson;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Component
public class GitRepositoryInfo implements Serializable {

	private static final long serialVersionUID = -3410669692176028544L;

	private String repositoryUrl;

	private List<FileInfo> repositoryFiles;

	public GitRepositoryInfo() {
		repositoryFiles = new ArrayList<>();
	}

	public void addFileNumberOfLines(FileInfo fileInfo) {
		int index = repositoryFiles.indexOf(fileInfo);

		if (index > -1) {
			repositoryFiles.get(index).addFileNumberOfLines(fileInfo.getLines());
		} else {
			repositoryFiles.add(fileInfo);
		}
	}

	public String toJson() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}

}