package br.com.usp.labis.controller;

import org.springframework.beans.factory.annotation.Autowired;
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
		 
	    @PostMapping
	    public void processEnrichmentAnalysis(@RequestParam("file") MultipartFile file) {
			//enrichmentAnalysisService.processEnrichmentAnalysis(file, minProteinsPerGoTerm);
			enrichmentAnalysisService.processEnrichmentAnalysis(file, 9606, 2, 0.5, 100, 0.05);
	    }
}
