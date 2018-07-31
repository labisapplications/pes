package br.com.usp.labis.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import br.com.usp.labis.bean.Condition;
import br.com.usp.labis.bean.GoAnnotation;
import br.com.usp.labis.bean.GoTerm;
import br.com.usp.labis.bean.Protein;
import br.com.usp.labis.bean.Replicate;
import br.com.usp.labis.bean.Result;
import br.com.usp.labis.exception.CustomException;
import br.com.usp.labis.service.file.IExcelReaderService;
import br.com.usp.labis.service.file.IOutputService;
import br.com.usp.labis.service.go.GoAnnotationService;
import br.com.usp.labis.service.statistic.IStatisticService;
import br.com.usp.labis.useful.DataUtil;
import br.com.usp.labis.useful.GoAnnotationFilter;
import br.com.usp.labis.useful.GoCoreParams;

@Component
public class EnrichmentAnalysisService implements IEnrichmentAnalysisService{

	@Autowired
	private IOutputService outputService;

	@Autowired
	private IExcelReaderService excelReaderService;

	@Autowired
	private IStatisticService statisticService;

	@Autowired
	private GoAnnotationService goAnnotationService;

	@Autowired
	private MessageSource messageSource;

	public String processEnrichmentAnalysisToExcel(MultipartFile file, Integer taxonId, Integer minProteinsPerGoTerm,
			Double toleranceFactor, Integer numberOfNullDistributions, Double pvalue) {

		String resultFilePath = "";

		List<GoTerm> goTerms = null;

		try {
			
			goTerms = processEnrichmentAnalysis(file, taxonId, minProteinsPerGoTerm, toleranceFactor,
					numberOfNullDistributions, pvalue);

			System.out.println("exporting to excel ...");

			resultFilePath = outputService.exportToExcel(goTerms);

			System.out.println("exported to excel");

		} catch (RuntimeException e) {
			throw new CustomException(messageSource.getMessage("messages.errorAnalysis",
					new Object[] { e.getMessage() + " -  " + e.getCause() }, Locale.US));
		}

		return resultFilePath;
	}

	public byte[] processEnrichmentAnalysisToCSV(MultipartFile file, Integer taxonId, Integer minProteinsPerGoTerm,
			Double toleranceFactor, Integer numberOfNullDistributions, Double pvalue) {

		byte[] data;

		try {
			
			List<GoTerm> goTerms = processEnrichmentAnalysis(file, taxonId, minProteinsPerGoTerm, toleranceFactor,
					numberOfNullDistributions, pvalue);

			System.out.println("exporting to csv ...");

			data  = outputService.exportToCSV(goTerms);

		} catch (RuntimeException e) {
			throw new CustomException(messageSource.getMessage("messages.errorAnalysis",
					new Object[] { e.getMessage() + " -  " + e.getCause() }, Locale.US));
		}
		
		return data;
	}
	
	public Map<String, List<Result>> processEnrichmentAnalysisToMap(MultipartFile file, Integer taxonId,
			Integer minProteinsPerGoTerm, Double toleranceFactor, Integer numberOfNullDistributions, Double pvalue) {

		Map<String, List<Result>> resultMap = null;

		List<GoTerm> goTerms = null;

		try {
			
			goTerms = processEnrichmentAnalysis(file, taxonId, minProteinsPerGoTerm, toleranceFactor,
					numberOfNullDistributions, pvalue);

			resultMap = outputService.exportToMap(goTerms);

			System.out.println("exported to map");

		} catch (RuntimeException e) {
			throw new CustomException(messageSource.getMessage("messages.errorAnalysis",
					new Object[] { e.getMessage() + " -  " + e.getCause() }, Locale.US));
		}

		return resultMap;
	}

	public List<GoTerm> processEnrichmentAnalysis(MultipartFile file, Integer taxonId, Integer minProteinsPerGoTerm,
			Double toleranceFactor, Integer numberOfNullDistributions, Double pvalue) {

		List<Protein> proteinsAnnotationNotFound = new ArrayList<Protein>();

		Map<String, Double> maxMean = new HashMap<String, Double>();

		Map<String, Double> maxCv = new HashMap<String, Double>();

		List<GoTerm> goTerms = null;

		Double maxStatisticTest = null; // max statistic test between conditions

		// reading the file
		List<Protein> proteins = excelReaderService.processExcelFile(file);

		// filters to go annotations
		GoAnnotationFilter filters = new GoAnnotationFilter();
		
		filters.setTaxonId(taxonId);

		if (proteins != null && !proteins.isEmpty()) {

			proteins = DataUtil.removeWhatIsNotProteinData(proteins);

			if (proteins == null) {
				
				throw new CustomException(
						messageSource.getMessage("messages.removeWhatIsNotProteinData", new Object[] {}, Locale.US));
			}

			// get test t or anova for conditions, get max cv, mean and ttest
			maxStatisticTest = this.getStatistics(proteins, maxMean, maxCv);

			// get annotations for each protein
			this.getAnnotationsForProteins(proteins, filters, proteinsAnnotationNotFound);

			System.out.println("Mapping go terms and proteins");
			
			// associate each go id to the proteins in the data file
			goTerms = mapGoTermsAndProteins(proteins, minProteinsPerGoTerm);

			if (goTerms == null || goTerms.isEmpty()) {
				
				throw new RuntimeException("Could not map go terms and min proteins ");
			}

			System.out.println("Calculating original go term weight");
			statisticService.calculateGoTermWeight(goTerms, proteins, null);

			System.out.println("Calculating the null distributions");
			
			// calculate the null distributions for randomly selected proteins
			statisticService.getNullDistributions(numberOfNullDistributions, toleranceFactor, goTerms, null, proteins,
					false);

			System.out.println("Getting null distribution pvalues");
			
			// get the null distribution higher than pvalue for each condition
			statisticService.calcNullDistributionPvalues(goTerms, pvalue, false);

			// get the core proteins for each go term
			System.out.println("Go Terms size:" + goTerms.size());
			
			System.out.println("Getting the core proteins");

			GoCoreParams goCoreParams = new GoCoreParams();
			goCoreParams.setGoTerms(goTerms);
			goCoreParams.setMaxCv(maxCv);
			goCoreParams.setMaxMean(maxMean);
			goCoreParams.setMaxStatisticTest(maxStatisticTest);
			goCoreParams.setNumberOfNullDistributions(numberOfNullDistributions);
			goCoreParams.setProteinsOriginal(proteins);
			goCoreParams.setPvalueDesired(pvalue);
			goCoreParams.setToleranceFactor(toleranceFactor);
			
			this.calcCoreProtein(goCoreParams) ;

			// calc the ratio between the conditions
			statisticService.calcRatioBetweenConditions(goTerms, numberOfNullDistributions);

			// calc the q value
			statisticService.calcQValueUsingBenjaminiHochberg(goTerms, pvalue);

		} else {
			
			throw new CustomException(messageSource.getMessage("messages.noProteins", new Object[] {}, Locale.US));
		}

		return goTerms;
	}

	private Double getStatistics(List<Protein> proteins, Map<String, Double> maxMean, Map<String, Double> maxCv) {

		Map<String, List<Double>> conditionsMean = new HashMap<String, List<Double>>();

		Map<String, List<Double>> conditionsCv = new HashMap<String, List<Double>>();

		List<Double> statisticsTest = new ArrayList<Double>();

		for (Protein protein : proteins) {

			if (protein.getConditions() == null) {
				
				throw new CustomException(messageSource.getMessage("messages.proteinWithoutCondition",
						new Object[] { protein.getProteinId() }, Locale.US));
			}

			System.out.println("-------------------begin------------------");
			System.out.println("-PROTEIN => " + protein.getProteinId());
			System.out.println("-GENE => " + protein.getGeneNames());

			try {
				
				for (Condition condition : protein.getConditions()) {
					
					System.out.println("--CONDITION => " + condition.getName());

					if (condition.getReplicates() == null) {
						
						throw new CustomException(messageSource.getMessage("messages.conditionWithoutReplicates",
								new Object[] { protein.getProteinId() + " - " + condition.getName() }, Locale.US));
					}

					for (Replicate replicate : condition.getReplicates()) {
						
						System.out.println("---REPLICATE => " + replicate.getValue());
					}
				}

				// to calculate the ttest or anova test for protein (considering all conditions)
				if (protein.getConditions().size() > 2) {
					
					protein.setStatisticTest(statisticService.oneWayAnovaTest(protein.getConditions()));
					
				} else {
					
					protein.setStatisticTest(statisticService.tTest(protein.getConditions()));
				}

			} catch (RuntimeException e) {
				
				throw new CustomException(messageSource.getMessage("messages.errorStatisticTest",
						new Object[] { e.getMessage() + "- " + e.getCause() }, Locale.US));
			}

			// calculate mean and cv for each protein condition
			statisticService.calculateProteinConditionMeanAndCv(protein);

			System.out.println("-------------------end--------------------");
		}

		DataUtil.getConditionCvs(proteins, conditionsCv);
		
		DataUtil.getConditionMeans(proteins, conditionsMean);
		
		DataUtil.getProteinsStatisticTest(proteins, statisticsTest);

		statisticService.getMaxMean(conditionsMean, maxMean); // get the max mean per condition
		
		statisticService.getMaxCv(conditionsCv, maxCv); // get the max cv per condition
		
		Double maxStatisticTest = statisticService.getMaxStatisticTest(statisticsTest); // get the max statistic test
																						// value (considering all
																						// conditions)

		statisticService.calculateProteinWeightForEachCondition(proteins, maxMean, maxCv, maxStatisticTest);

		return maxStatisticTest;
	}

	private void getAnnotationsForProteins(List<Protein> proteins, GoAnnotationFilter filters,
			List<Protein> proteinsAnnotationNotFound) {


		this.getGoAnnotationsForEachProtein(proteins,  filters) ;

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
				
				proteinsAnnotationNotFound.add(protein);
			}
		}
		
		System.out.println("The search for annotations is ended!!!");
	}

	private List<GoTerm> mapGoTermsAndProteins(List<Protein> proteins, Integer minProteinsPerGoTerm) {

		List<GoTerm> listGoTerms = new ArrayList<GoTerm>();
		
		Map<String, List<Protein>> goTermWithProteins = new HashMap<String, List<Protein>>();
		
		Map<String, GoAnnotation> goAnnotation = new HashMap<String, GoAnnotation>();
		
		Integer countAnnotationsNotFound = 0;
		
		for (Protein protein : proteins) {
			
			if (protein.getGoAnnotations() != null && !protein.getGoAnnotations().isEmpty()) {
				
				for (GoAnnotation annotation : protein.getGoAnnotations()) {
					
					if (goTermWithProteins.get(annotation.getGoId()) == null) {
						
						goTermWithProteins.put(annotation.getGoId(), new ArrayList<Protein>());
						
						goAnnotation.put(annotation.getGoId(), annotation);
						
					}
					if (!goTermWithProteins.get(annotation.getGoId()).contains(protein)) {
						
						goTermWithProteins.get(annotation.getGoId()).add(protein);
					}
				}
				
			} else {
				countAnnotationsNotFound += 1;
			}
		}

		if (countAnnotationsNotFound.equals(proteins.size())) {
			
			throw new CustomException(
					messageSource.getMessage("messages.noGoAnnotationsFound", new Object[] {}, Locale.US));
		}

		Iterator it = goTermWithProteins.entrySet().iterator();

		while (it.hasNext()) {

			Map.Entry pairs = (Map.Entry) it.next();
			
			List<Protein> proteinsGoTerm = (List<Protein>) pairs.getValue();
			
			if (proteinsGoTerm.size() >= minProteinsPerGoTerm) {
				
				GoTerm goTerm = new GoTerm();
				
				goTerm.setGoAnnotation(goAnnotation.get(pairs.getKey()));
				
				goTerm.setProteins(proteinsGoTerm);
				
				listGoTerms.add(goTerm);
			}
		}

		System.out.println("The mapping is finished!!!");
		return listGoTerms;
	}

	
	private void getGoAnnotationsForEachProtein(List<Protein> proteins, GoAnnotationFilter filters) {

		System.out.println("##### calcCoreProtein #######");

		List<Runnable> tasks = new ArrayList<Runnable>();

		for (Protein protein : proteins) {
			Runnable runnable = () -> {
				try {
					
					goAnnotationService.getGoAnnotationsForProteinAndTaxon(protein, filters);
					
				} catch (RuntimeException e) {
					e.printStackTrace();
				}
			};
			tasks.add(runnable);
		}

		ExecutorService es = Executors.newFixedThreadPool(4);

		CompletableFuture<?>[] futures = tasks.stream().map(task -> CompletableFuture.runAsync(task, es))
				.toArray(CompletableFuture[]::new);
		
		CompletableFuture.allOf(futures).join();
		
		es.shutdown();
		
		try {

			es.awaitTermination(600, TimeUnit.SECONDS);

		} catch (InterruptedException e) {
			
			es.shutdownNow();
		}
	}

	private void calcCoreProtein(GoCoreParams goWorkerCoreParams) {

		System.out.println("##### calcCoreProtein #######");

		List<Runnable> tasks = new ArrayList<Runnable>();

		for (GoTerm goTerm : goWorkerCoreParams.getGoTerms()) {
			
			Runnable runnable = () -> {
				
				try {
					
					statisticService.getCoreProteinsForGoTerm(goTerm, goWorkerCoreParams.getMaxMean(),
							goWorkerCoreParams.getMaxCv(), goWorkerCoreParams.getMaxStatisticTest(),
							goWorkerCoreParams.getNumberOfNullDistributions(), goWorkerCoreParams.getToleranceFactor(),
							goWorkerCoreParams.getPvalueDesired(), goWorkerCoreParams.getProteinsOriginal());
					
				} catch (RuntimeException e) {
					e.printStackTrace();
				}
			};
			tasks.add(runnable);
		}

		ExecutorService es = Executors.newFixedThreadPool(5);

		CompletableFuture<?>[] futures = tasks.stream().map(task -> CompletableFuture.runAsync(task, es))
				.toArray(CompletableFuture[]::new);
		
		CompletableFuture.allOf(futures).join();
		
		es.shutdown();
		
		try {

			es.awaitTermination(600, TimeUnit.SECONDS);

		} catch (InterruptedException e) {
			
			es.shutdownNow();
			
		}
	}

}
