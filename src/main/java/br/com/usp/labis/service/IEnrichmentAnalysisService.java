package br.com.usp.labis.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import br.com.usp.labis.bean.GoTerm;
import br.com.usp.labis.bean.Result;

@Component
public interface IEnrichmentAnalysisService {

	String processEnrichmentAnalysisToExcel(MultipartFile file, Integer taxonId, Integer minProteinsPerGoTerm,
			Double toleranceFactor, Integer numberOfNullDistributions, Double pvalue);
	
	Map<String, List<Result>> processEnrichmentAnalysisToMap(MultipartFile file, Integer taxonId,
			Integer minProteinsPerGoTerm, Double toleranceFactor, Integer numberOfNullDistributions, Double pvalue);
	
	List<GoTerm> processEnrichmentAnalysis(MultipartFile file, Integer taxonId, Integer minProteinsPerGoTerm,
			Double toleranceFactor, Integer numberOfNullDistributions, Double pvalue); 
	
	byte[] processEnrichmentAnalysisToCSV(MultipartFile file, Integer taxonId, Integer minProteinsPerGoTerm,
				Double toleranceFactor, Integer numberOfNullDistributions, Double pvalue) ;
}
