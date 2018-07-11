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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import br.com.usp.labis.bean.Condition;
import br.com.usp.labis.bean.GoAnnotation;
import br.com.usp.labis.bean.GoTerm;
import br.com.usp.labis.bean.GoTermCondition;
import br.com.usp.labis.bean.Protein;
import br.com.usp.labis.bean.Replicate;
import br.com.usp.labis.service.file.ExcelReaderService;
import br.com.usp.labis.service.file.OutputFileService;
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
	private OutputFileService outputFileService;

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

	public String processEnrichmentAnalysis(MultipartFile file, Integer taxonId, Integer minProteinsPerGoTerm,
			Double toleranceFactor, Integer numberOfNullDistributions, Double pvalue) {

		String resultFilePath = "";
		
		List<Protein> proteinsAnnotationNotFound = new ArrayList<Protein>();

		Map<String, Double> maxMean = new HashMap<String, Double>();

		Map<String, Double> maxCv = new HashMap<String, Double>();

		Double maxStatisticTest = null;

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
			maxStatisticTest = this.getStatistics(proteins, maxMean, maxCv);

			// get annotations for each protein
			this.getAnnotationsForProteins(proteins, filters, proteinsAnnotationNotFound);

			System.out.println("Mapping go terms and proteins");
			// associate each go id to the proteins in the data file
			List<GoTerm> goTerms = mapGoTermsAndProteins(proteins, minProteinsPerGoTerm);

			for (GoTerm goTerm : goTerms) {
				String print = goTerm.getGoAnnotation().getGoId() + " - proteins: ";
				StringBuilder prot = new StringBuilder();
				for (Protein protein : goTerm.getProteins()) {
					prot.append(protein.getProteinId());
					prot.append(" ");
				}
				System.out.println(print + prot.toString());

			}

			System.out.println("Calculating original go term weight");
			statisticService.calculateGoTermWeight(goTerms, proteins, null);

			System.out.println("Calculating the null distributions");
			// calculate the null distributions for randomly selected proteins
			statisticService.getNullDistributions(numberOfNullDistributions, toleranceFactor, goTerms, null, proteins,
					false);

			System.out.println("Getting null distribution pvalues");
			// get the null distribution higher than pvalue for each condition
			statisticService.compareNullDistributionPvalues(goTerms, pvalue, false);
			
			System.out.println("Before the Core analysis: ");
			for (GoTerm goTerm : goTerms) {
				for (GoTermCondition goTermCondition : goTerm.getConditions()) {
					String print = goTerm.getGoAnnotation().getGoId() + " - pvalue:  "
							+ goTermCondition.getPvalueOriginal() + " - weight: " + goTermCondition.getOriginalWeight()
							+ " - proteins: ";
					StringBuilder prot = new StringBuilder();
					for (Protein protein : goTermCondition.getOriginalProteins()) {
						prot.append(protein.getProteinId());
						prot.append(" ");
					}
					System.out.println(print + prot.toString());
				}
			}

			// get the core proteins for each go term
			System.out.println("Getting the core proteins");
			statisticService.getCoreProteins(goTerms, maxMean, maxCv, maxStatisticTest, numberOfNullDistributions,
					toleranceFactor, pvalue, proteins);

			System.out.println("Getting the result: ");
			for (GoTerm goTerm : goTerms) {
				for (GoTermCondition goTermCondition : goTerm.getConditions()) {
					String print = goTerm.getGoAnnotation().getGoId() + " - pvalue:  "
							+ goTermCondition.getFinalPvalue() + " - weight: " + goTermCondition.getFinalWeight()
							+ " - proteins: ";
					StringBuilder prot = new StringBuilder();
					for (Protein protein : goTermCondition.getCoreProteins()) {
						prot.append(protein.getProteinId());
						prot.append(" ");
					}
					System.out.println(print + prot.toString());
				}
			}

			// create output file
			resultFilePath = outputFileService.exportToExcel(goTerms);

			// get antology for each annotation
			// this.getGoAntologyForAnnotations(proteins);

			// create the relationship matrix
		}
		
		return resultFilePath;
	}

	private Double getStatistics(List<Protein> proteins, Map<String, Double> maxMean, Map<String, Double> maxCv) {

		Map<String, List<Double>> conditionsMean = new HashMap<String, List<Double>>();

		Map<String, List<Double>> conditionsCv = new HashMap<String, List<Double>>();

		List<Double> statisticsTest = new ArrayList<Double>();

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

	private void getAnnotationsForProteins(List<Protein> proteins, GoAnnotationFilter filters,
			List<Protein> proteinsAnnotationNotFound) {

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
				proteinsAnnotationNotFound.add(protein);
			}
		}
		System.out.println("The search for annotations is ended!!!");
	}

	private List<GoTerm> mapGoTermsAndProteins(List<Protein> proteins, Integer minProteinsPerGoTerm) {

		List<GoTerm> listGoTerms = new ArrayList<GoTerm>();
		Map<String, List<Protein>> goTermWithProteins = new HashMap<String, List<Protein>>();
		Map<String, GoAnnotation> goAnnotation = new HashMap<String, GoAnnotation>();

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
			}
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
