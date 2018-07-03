package br.com.usp.labis.useful;

import java.util.List;

import br.com.usp.labis.bean.GoAnnotation;
import br.com.usp.labis.bean.Protein;
import br.com.usp.labis.service.go.GoAnnotationService;
import br.com.usp.labis.service.go.GoAntologyService;

public class GoWorker implements Runnable {

	private GoAnnotationService goAnnotationService;

	private GoAnnotationFilter goAnnotationFilter;
	
	private GoAntologyService goAntologyService;

	private Protein protein;
	
	private List<GoAnnotation> annotations;

	public GoWorker(Protein protein, GoAnnotationService goAnnotationService, GoAnnotationFilter goAnnotationFilter) {

		this.protein = protein;
		this.goAnnotationService = goAnnotationService;
		this.goAnnotationFilter = goAnnotationFilter;
	}
	
	public GoWorker(List<GoAnnotation> annotations, GoAntologyService goAntologyService) {
		this.annotations = annotations;
		this.goAntologyService = goAntologyService;
	}


	@Override
	public void run() {
		if (this.protein != null && this.goAnnotationService != null) {
			goAnnotationService.getGoAnnotationsForProteinAndTaxon(protein, goAnnotationFilter);
		} else {
			goAntologyService.getGoAntologyForAnnotation(annotations);
		}
	}

}
