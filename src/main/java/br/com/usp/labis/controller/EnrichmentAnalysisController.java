package br.com.usp.labis.controller;

import java.io.File;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import br.com.usp.labis.service.EnrichmentAnalysisService;

@RestController
@RequestMapping("/pes")
public class EnrichmentAnalysisController {

	@Autowired
	private EnrichmentAnalysisService enrichmentAnalysisService;

	@CrossOrigin
	@PostMapping
	public ResponseEntity<Resource> processEnrichmentAnalysis(@RequestParam("file") MultipartFile file,
			@RequestParam("taxonId") Integer taxonId, @RequestParam("minProteins") Integer minProteins,
			@RequestParam("toleranceFactor") Double toleranceFactor,
			@RequestParam("nullDistributions") Integer nullDistributions, @RequestParam("pvalue") Double pvalue) {
		
		String resultFilePath = enrichmentAnalysisService.processEnrichmentAnalysis(file, taxonId, minProteins, toleranceFactor,
				nullDistributions, pvalue);
        
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		
		File fileOutput = new File(resultFilePath);
		
		FileSystemResource fileSystemResource = new FileSystemResource(fileOutput);
		
		return new ResponseEntity<>(fileSystemResource, headers, HttpStatus.OK);

	}
}
