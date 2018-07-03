package br.com.usp.labis.service;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import br.com.usp.labis.bean.Condition;
import br.com.usp.labis.bean.GoAnnotation;
import br.com.usp.labis.bean.Protein;
import br.com.usp.labis.bean.Replicate;
import br.com.usp.labis.service.file.ExcelReaderService;
import br.com.usp.labis.service.file.UploadFileService;
import br.com.usp.labis.service.go.GeneProductService;
import br.com.usp.labis.service.go.GoAnnotationService;
import br.com.usp.labis.service.go.GoAntologyService;
import br.com.usp.labis.service.statistic.StatisticService;
import br.com.usp.labis.useful.DataUtil;
import br.com.usp.labis.useful.GoAnnotationFilter;
import br.com.usp.labis.useful.GoWorker;

@Component
public class EnrichmentAnalysisService {

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

	@Autowired
	private GoAntologyService goAntologyService;
	
	private Map<String, List<Double>> conditionsMean;
	
	private Map<String, List<Double>> conditionsCv;
	
	private List<Double> statisticsTest;
	
	private Map<String, Double> maxMean;
	
	private Map<String, Double> maxCv;
	
	private Double maxStatisticTest;


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

		GoAnnotationFilter filters = new GoAnnotationFilter();
		filters.setTaxonId(9606);

		if (proteins != null && !proteins.isEmpty()) {

			// get test t or anova for conditions
			this.getStatisticsForProteins(proteins);

			// get annotations for each protein
			//this.getAnnotationsForProteins(proteins, filters);

			// get antology for each annotation
			 //this.getGoAntologyForAnnotations(proteins);
		}
	}
	
	private void getStatisticsForProteins(List<Protein> proteins) {
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

			// to calculate the ttest or anova test for protein
			if (protein.getConditions().size() > 2) {
				protein.setStatisticTest(statisticService.oneWayAnovaTest(protein.getConditions()));
			} else {
				protein.setStatisticTest(statisticService.tTest(protein.getConditions()));
			}
			
			//calculate mean and cv for each protein condition
			statisticService.calculateProteinConditionMeanAndCv(protein);
			
			System.out.println("-------------------end--------------------");
		}
		
		conditionsCv = DataUtil.getConditionCvs(proteins);
		conditionsMean = DataUtil.getConditionMeans(proteins);
		statisticsTest = DataUtil.getProteinsStatisticTest(proteins);
		
		maxMean = statisticService.getMaxMean(conditionsMean);
		maxCv = statisticService.getMaxCv(conditionsCv);
		maxStatisticTest = statisticService.getMaxStatisticTest(statisticsTest);
		
		statisticService.calculateProteinWeightForEachCondition(proteins, maxMean, maxCv, maxStatisticTest);
	}

	private void getAnnotationsForProteins(List<Protein> proteins, GoAnnotationFilter filters) {

		for (Protein protein : proteins) {
			this.executeGoWorker(protein, filters, null);
		}

		for (Protein protein : proteins) {
			// if some protein is without annotations try again
			if (protein.getGoAnnotations() == null || protein.getGoAnnotations().isEmpty()) {
				System.out.println("Protein: " + protein.getProteinId());
				this.goAnnotationService.getGoAnnotationsForProteinAndTaxon(protein, filters);
			} else {
				System.out.println("go id for protein => " + protein.getGoAnnotations().get(0).getGoId());
			}
		}

		System.out.println("The search for annotations is ended!!!");

	}

	private void getGoAntologyForAnnotations(List<Protein> proteins) {
		for (Protein protein : proteins) {
			if (protein.getGoAnnotations() != null && !protein.getGoAnnotations().isEmpty()) {
				this.executeGoWorker(null, null, protein.getGoAnnotations());
			}
		}
		System.out.println("The search for antology is ended!!!");
	}

	private void executeGoWorker(Protein protein, GoAnnotationFilter filters, List<GoAnnotation> annotations) {

		// starting threads to speed up the search
		ExecutorService executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(25);
		GoWorker worker = null;

		if (annotations != null) {
			worker = new GoWorker(annotations, goAntologyService);
		} else {
			worker = new GoWorker(protein, goAnnotationService, filters);
		}

		executor.execute(worker);

		try {
			executor.shutdown();
			executor.awaitTermination(15, TimeUnit.SECONDS);

		} catch (InterruptedException e) {
			executor.shutdownNow();
			Thread.currentThread().interrupt();
		}
	}

}
