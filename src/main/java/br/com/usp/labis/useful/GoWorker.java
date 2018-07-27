package br.com.usp.labis.useful;

import java.util.List;

import br.com.usp.labis.bean.GoAnnotation;
import br.com.usp.labis.bean.GoTerm;
import br.com.usp.labis.bean.Protein;
import br.com.usp.labis.service.go.GoAnnotationService;
import br.com.usp.labis.service.go.GoAntologyService;
import br.com.usp.labis.service.statistic.StatisticService;

public class GoWorker implements Runnable {

	private GoAnnotationService goAnnotationService;

	private GoAnnotationFilter goAnnotationFilter;
	
	private GoAntologyService goAntologyService;

	private Protein protein;
	
	private List<GoAnnotation> annotations;
	
	private GoTerm goTerm;
	
	private GoWorkerCoreParams goWorkerCoreParams;
	
	private StatisticService statisticService;

	public GoWorker(Protein protein, GoAnnotationService goAnnotationService, GoAnnotationFilter goAnnotationFilter) {

		this.protein = protein;
		this.goAnnotationService = goAnnotationService;
		this.goAnnotationFilter = goAnnotationFilter;
	}
	
	public GoWorker(List<GoAnnotation> annotations, GoAntologyService goAntologyService) {
		this.annotations = annotations;
		this.goAntologyService = goAntologyService;
	}
	
	public GoWorker(GoTerm goTerm, GoWorkerCoreParams goWorkerCoreParams, StatisticService statisticService) {

		this.goTerm = goTerm;
		this.goWorkerCoreParams = goWorkerCoreParams;
		this.statisticService = statisticService;
	}


	@Override
	public void run() {
		
		if (this.protein != null && this.goAnnotationService != null) {
			
			goAnnotationService.getGoAnnotationsForProteinAndTaxon(protein, goAnnotationFilter);
			
		} else if (this.goTerm != null && this.goWorkerCoreParams != null && statisticService != null) {
			
			statisticService.getCoreProteinsForGoTerm(this.goTerm , goWorkerCoreParams.getMaxMean(), goWorkerCoreParams.getMaxCv(), goWorkerCoreParams.getMaxStatisticTest(), 
					goWorkerCoreParams.getNumberOfNullDistributions(),
					goWorkerCoreParams.getToleranceFactor(), goWorkerCoreParams.getPvalueDesired(), goWorkerCoreParams.getProteinsOriginal());

		} else {
			
			goAntologyService.getGoAntologyForAnnotation(annotations);
		}
	}

}
