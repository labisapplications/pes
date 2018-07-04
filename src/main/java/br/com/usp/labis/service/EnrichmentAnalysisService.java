package br.com.usp.labis.service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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

	private Map<String, List<Protein>> goTermWithProteins; // GO_ID : proteins related
	
	private Map<String, List<Protein>> goTermWithProteinsFiltered; // GO_ID : proteins related after filters
	
	private HashMap<String, HashMap<String, Double>> goTermWeightPerCondition ; //GO_ID : Condition: Weight
	
	private Map<String,  HashMap<String, List<Double>>> goTermProteinsMeanForEachCondition; //GO_ID : Condition : Means
	
	private Map<String, HashMap<String, Double>> goTermProteinsCv;  //GO_ID : Condition : CVs
	
	private Map<String, Map<String, Map<String, Double>>> goTermRandomProteinsWeight; //GO_ID : Condition: NullDistribution : Weight
	
	/**
	 * Perform enrichment analysis for proteins in N conditions in order to find
	 * over expressed genes and most relevant go annotations related
	 * 
	 * @param file
	 *            file with data to be analysed
	 */
	public void processEnrichmentAnalysis(MultipartFile file, Integer taxonId, Integer minProteinsPerGoTerm, 
			Double toleranceFactor, Integer numberOfNullDistributions, Double pvalue) {

		// upload of data file to a temporary directory
		File uploadedFile = uploadFileService.uploadExcelFile(file);

		// process uploaded data file
		List<Protein> proteins = excelReaderService.processExcelFile(uploadedFile);

		// after process the data file, remove it from temporary directory
		uploadFileService.removeUploadedFile(uploadedFile);

		GoAnnotationFilter filters = new GoAnnotationFilter();
		filters.setTaxonId(taxonId);

		if (proteins != null && !proteins.isEmpty()) {
			
			proteins = DataUtil.removeWhatIsNotProteinData(proteins);
			
			// get test t or anova for conditions, get max cv, mean and ttest
			this.getStatistics(proteins);

			// get annotations for each protein
			this.getAnnotationsForProteins(proteins, filters);
			
			//associate each go id to the proteins in the data file
			this.mapGoTermsAndProteins(proteins);
			
			DataUtil.filterGoTermsAndProteins(goTermWithProteins, goTermWithProteinsFiltered, minProteinsPerGoTerm);
			
			//calculate weigth for each goterm and filter go terms according to the filters
			statisticService.calculateGoTermWeight(goTermWithProteinsFiltered, proteins, goTermWeightPerCondition);
			
			//get protein means for each go term
			goTermProteinsMeanForEachCondition = statisticService.getGoTermProteinsMeanForEachCondition(goTermWithProteinsFiltered);
			
			//calculate the coefficient of variation for goTermProteinsMeanForEachCondition
			goTermProteinsCv = statisticService.getGoTermProteinsCvForEachCondition(goTermProteinsMeanForEachCondition);
			
			//calculate the null distributions for randomly selected proteins 
			goTermRandomProteinsWeight = statisticService.getNullDistributions(numberOfNullDistributions, toleranceFactor,  proteins,
					goTermProteinsCv, goTermWithProteinsFiltered);
			
			/*get the number of null distribution are higher than pvalue for each condition and
			 *  after that, get the core proteins for each go term*/
			//statisticService.getCoreProteins(goTermProteinsCv, goTermWeightPerCondition,  pvalue);
			
			System.out.println("Process is finished");
			
			//create output file
			
			// get antology for each annotation
			// this.getGoAntologyForAnnotations(proteins);
			
			//create the relationship matrix
		}
	}
	
	private void getStatistics(List<Protein> proteins) {
		
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

			// calculate mean and cv for each protein condition
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
			} 
		}
		System.out.println("The search for annotations is ended!!!");
	}

	private void mapGoTermsAndProteins(List<Protein> proteins) {
		goTermWithProteins = new HashMap<String, List<Protein>> ();
		goTermWithProteinsFiltered = new HashMap<String, List<Protein>> ();
		for (Protein protein : proteins) {
			if (protein.getGoAnnotations() != null && !protein.getGoAnnotations().isEmpty()) {
				for(GoAnnotation annotation :  protein.getGoAnnotations()) {
					if (goTermWithProteins.get(annotation.getGoId()) == null) {
						goTermWithProteins.put(annotation.getGoId(), new ArrayList<Protein>());
					}
					if(!goTermWithProteins.get(annotation.getGoId()).contains(protein)) {
						goTermWithProteins.get(annotation.getGoId()).add(protein);
					}
				}
			}
		}
		System.out.println("The mapping is finished!!!");

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
