package br.com.usp.labis.service;

import java.io.File;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import br.com.usp.labis.bean.Condition;
import br.com.usp.labis.bean.Protein;
import br.com.usp.labis.bean.Replicate;
import br.com.usp.labis.service.file.ExcelReaderService;
import br.com.usp.labis.service.file.UploadFileService;
import br.com.usp.labis.service.go.GeneProductService;
import br.com.usp.labis.service.go.GoAnnotationService;
import br.com.usp.labis.service.statistic.StatisticService;

@Component
public class EnrichmentAnalysisService {

	private final Double significance = 0.05;

	@Autowired
	private UploadFileService uploadFileService;

	@Autowired
	private ExcelReaderService excelReaderService;

	@Autowired
	private StatisticService statisticService;
	
	@Autowired
	private GeneProductService geneProductService;
	
	@Autowired
	private GoAnnotationService goAnnotationService;

	/**
	 * Perform enrichment analysis for proteins in N conditions in order to find
	 * over expressed genes.
	 * 
	 * @param file
	 *            file with data to be analysed
	 */
	public void processEnrichmentAnalysis(MultipartFile file) {

		// upload of data file to a temporary directory
		File uploadedFile = uploadFileService.uploadExcelFile(file);

		// process uploaded data file
		List<Protein> proteins = excelReaderService.processExcelFile(uploadedFile);

		// after process the data file, remove it from temporary directory
		uploadFileService.removeUploadedFile(uploadedFile);

		for (Protein protein : proteins) {
			System.out.println("-------------------begin------------------");
			System.out.println("-PROTEIN => " + protein.getProteinId());
			System.out.println("-GENE => " + protein.getGeneNames());
			for (Condition condition : protein.getConditions()) {
				System.out.println("--CONDITION => " + condition.getName());
				for (Replicate replicate : condition.getReplicates()) {
					System.out.println("---REPLICATE => " + replicate.getValue());
				}
			}
			
			//to calculate the ttest or anova test for protein
			if(protein.getConditions().size() > 2) {
				statisticService.oneWayAnovaTest(protein.getConditions());
			} else {
				statisticService.tTest(protein.getConditions());
			}
			//geneProductService.getGeneProductService(protein);
			goAnnotationService.getGoAnnotationsForProteinAndTaxon(protein, 9606);

			System.out.println("-------------------end--------------------");
		}
	}
}
