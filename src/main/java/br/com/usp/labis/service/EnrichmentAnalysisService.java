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

	/**
	 * Perform enrichment analysis for proteins in N conditions in order to find
	 * over expressed genes and most relevant go annotations related
	 * 
	 * @param file
	 *            file with data to be analysed
	 */
	public void processEnrichmentAnalysis(MultipartFile file, Integer taxonId, Integer minProteinsPerGoTerm,
			Double toleranceFactor, Integer numberOfNullDistributions, Double pvalue) {

		// variables
		List<Protein> proteinsWithoutAnnotation = new ArrayList<Protein>();

		Map<String, List<Double>> conditionsMean = new HashMap<String, List<Double>>();

		Map<String, List<Double>> conditionsCv = new HashMap<String, List<Double>>();

		List<Double> statisticsTest = new ArrayList<Double>();

		Map<String, Double> maxMean = new HashMap<String, Double>();

		Map<String, Double> maxCv = new HashMap<String, Double>();

		Double maxStatisticTest = null;

		Map<String, List<Protein>> goTermWithProteins = new HashMap<String, List<Protein>>();
		; // GO_ID : proteins related

		Map<String, List<Protein>> goTermWithProteinsFiltered = new HashMap<String, List<Protein>>(); // GO_ID :
																										// proteins
																										// related after
																										// filters

		HashMap<String, HashMap<String, Double>> goTermWeightPerCondition = null; // GO_ID : Condition: Weight

		Map<String, HashMap<String, List<Double>>> goTermProteinsMeanForEachCondition = new HashMap<String, HashMap<String, List<Double>>>(); // GO_ID
																																				// :
																																				// Condition
																																				// :
																																				// Means

		Map<String, HashMap<String, Double>> goTermProteinsCv = new HashMap<String, HashMap<String, Double>>(); // GO_ID
																												// :
																												// Condition
																												// : CVs

		Map<String, Map<String, Map<String, Double>>> goTermRandomProteinsWeight = null; // GO_ID : Condition:
																							// NullDistribution : Weight

		// upload of data file to a temporary directory
		File uploadedFile = uploadFileService.uploadExcelFile(file);

		// process uploaded data file
		List<Protein> proteins = excelReaderService.processExcelFile(uploadedFile);

		// after process the data file, remove it from temporary directory
		uploadFileService.removeUploadedFile(uploadedFile);

		// filters to go annotations
		GoAnnotationFilter filters = new GoAnnotationFilter();
		filters.setTaxonId(taxonId);

		if (proteins != null && !proteins.isEmpty()) {

			proteins = DataUtil.removeWhatIsNotProteinData(proteins);

			// get test t or anova for conditions, get max cv, mean and ttest
			maxStatisticTest = this.getStatistics(proteins, conditionsCv, conditionsMean, statisticsTest, maxMean,
					maxCv);

			// get annotations for each protein
			this.getAnnotationsForProteins(proteins, filters, proteinsWithoutAnnotation);

			// associate each go id to the proteins in the data file
			this.mapGoTermsAndProteins(proteins, goTermWithProteins, goTermWithProteinsFiltered);

			// filter go terms according to the filters
			DataUtil.filterGoTermsAndProteins(goTermWithProteins, goTermWithProteinsFiltered, minProteinsPerGoTerm);

			System.out.println("Calculating original go term weight");
			goTermWeightPerCondition = statisticService.calculateGoTermWeight(goTermWithProteinsFiltered, proteins,
					goTermProteinsMeanForEachCondition, goTermProteinsCv);

			System.out.println("Calculating the null distributions");
			// calculate the null distributions for randomly selected proteins
			goTermRandomProteinsWeight = statisticService.getNullDistributions(numberOfNullDistributions,
					toleranceFactor, proteins, goTermProteinsCv, goTermWithProteinsFiltered);

			/*
			 * get the number of null distribution are higher than pvalue for each condition
			 * and after that, get the core proteins for each go term
			 */
			System.out.println("Calculating the core proteins");
			statisticService.getCoreProteins(goTermProteinsCv, goTermWeightPerCondition, goTermWithProteinsFiltered,
					goTermRandomProteinsWeight, pvalue, maxMean, maxCv, maxStatisticTest, numberOfNullDistributions,
					toleranceFactor);

			System.out.println("The analysis is finished");

			// create output file

			// get antology for each annotation
			// this.getGoAntologyForAnnotations(proteins);

			// create the relationship matrix
		}
	}

	private Double getStatistics(List<Protein> proteins, Map<String, List<Double>> conditionsCv,
			Map<String, List<Double>> conditionsMean, List<Double> statisticsTest, Map<String, Double> maxMean,
			Map<String, Double> maxCv) {

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

		DataUtil.getConditionCvs(proteins, conditionsCv);
		DataUtil.getConditionMeans(proteins, conditionsMean);
		DataUtil.getProteinsStatisticTest(proteins, statisticsTest);

		statisticService.getMaxMean(conditionsMean, maxMean);
		statisticService.getMaxCv(conditionsCv, maxCv);
		Double maxStatisticTest = statisticService.getMaxStatisticTest(statisticsTest);

		statisticService.calculateProteinWeightForEachCondition(proteins, maxMean, maxCv, maxStatisticTest);

		return maxStatisticTest;
	}

	private void getAnnotationsForProteins(List<Protein> proteins, GoAnnotationFilter filters, List<Protein> proteinsWithoutAnnotation) {

		for (Protein protein : proteins) {
			this.executeGoWorker(protein, filters, null);
		}

		// try again
		for (Protein protein : proteins) {
			// if some protein is without annotations try again
			if (protein.getGoAnnotations() == null || protein.getGoAnnotations().isEmpty()) {
				System.out.println("Trying to get annotation for protein again: " + protein.getProteinId());
				this.goAnnotationService.getGoAnnotationsForProteinAndTaxon(protein, filters);
			}
		}

		// try a second protein id (if exists)
		for (Protein protein : proteins) {
			if ((protein.getGoAnnotations() == null || protein.getGoAnnotations().isEmpty())
					&& protein.getOtherProteinsIdAssociated() != null
					&& !protein.getOtherProteinsIdAssociated().isEmpty()) {
				for (String otherProteinId : protein.getOtherProteinsIdAssociated()) {

					if (!protein.getProteinId().equalsIgnoreCase(otherProteinId)) {
						protein.setProteinId(otherProteinId);
						System.out.println(
								"Protein id changed from " + protein.getProteinId() + " to  => " + otherProteinId);
						System.out.println("Trying to get annotation for protein again => " + protein.getProteinId());
						this.goAnnotationService.getGoAnnotationsForProteinAndTaxon(protein, filters);
					}

					if (protein.getGoAnnotations() != null && !protein.getGoAnnotations().isEmpty()) {
						break;
					}
				}

			}
		}

		// give up
		for (Protein protein : proteins) {
			if (protein.getGoAnnotations() == null || protein.getGoAnnotations().isEmpty()) {
				System.out.println("Could not get annotation for => " + protein.getProteinId());
				proteinsWithoutAnnotation.add(protein);
			}
		}
		System.out.println("The search for annotations is ended!!!");
	}

	private void mapGoTermsAndProteins(List<Protein> proteins, Map<String, List<Protein>> goTermWithProteins,
			Map<String, List<Protein>> goTermWithProteinsFiltered) {
		for (Protein protein : proteins) {
			if (protein.getGoAnnotations() != null && !protein.getGoAnnotations().isEmpty()) {
				for (GoAnnotation annotation : protein.getGoAnnotations()) {
					if (goTermWithProteins.get(annotation.getGoId()) == null) {
						goTermWithProteins.put(annotation.getGoId(), new ArrayList<Protein>());
					}
					if (!goTermWithProteins.get(annotation.getGoId()).contains(protein)) {
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
		ExecutorService executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(50);
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
