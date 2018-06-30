package br.com.usp.labis.useful;

import br.com.usp.labis.bean.Protein;
import br.com.usp.labis.service.go.GoAnnotationService;

public class GoAnnotationWorker implements Runnable{
	
	private GoAnnotationService goAnnotationService;
	
	private GoAnnotationFilter goAnnotationFilter;
	
	private Protein protein;

	public GoAnnotationWorker(Protein protein, GoAnnotationService goAnnotationService,  GoAnnotationFilter goAnnotationFilter) {

		this.protein = protein;
		this.goAnnotationService = goAnnotationService;
		this.goAnnotationFilter = goAnnotationFilter;
	}

    @Override
	public void run()  {
		 goAnnotationService.getGoAnnotationsForProteinAndTaxon(protein, goAnnotationFilter);
	}

}
