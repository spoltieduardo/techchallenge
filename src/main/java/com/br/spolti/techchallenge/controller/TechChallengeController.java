package com.br.spolti.techchallenge.controller;

import com.br.spolti.techchallenge.model.GitRepositoryInfo;
import com.br.spolti.techchallenge.service.TechChallengeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController(value = "/tech-challenge")
public class TechChallengeController {

	private TechChallengeService service;

	@Autowired
	public TechChallengeController(TechChallengeService service) {
		this.service = service;
	}

	@GetMapping("/scrap-repository")
	public ResponseEntity<GitRepositoryInfo> scrapRepository(@RequestParam(value = "repositoryUrl") String repositoryUrl) {
		service.isValidRepositoryUrl(repositoryUrl);
		GitRepositoryInfo result = service.getRepositoryUrlContentModel(repositoryUrl);
		return ResponseEntity.ok(result);
	}

	@PostMapping("/clearAllCache")
	public ResponseEntity<String> clearAllCache() {
		service.clearAllCache();
		return ResponseEntity.ok("All cache was destroyed successfully !");
	}

	@PostMapping("/clearCache")
	public ResponseEntity<String> clearCache(@RequestParam(value = "repositoryUrl") String repositoryUrl) {
		if (repositoryUrl == null) {
			return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
		}
		service.clearCache(repositoryUrl);
		return ResponseEntity.ok("Cache " + repositoryUrl + " was destroyed successfully !");
	}

}