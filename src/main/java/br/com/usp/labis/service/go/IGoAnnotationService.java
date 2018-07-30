package br.com.usp.labis.service.go;

import org.springframework.stereotype.Component;

import br.com.usp.labis.bean.Protein;
import br.com.usp.labis.useful.GoAnnotationFilter;

@Component
public interface IGoAnnotationService {

	Protein getGoAnnotationsForProteinAndTaxon(Protein protein, GoAnnotationFilter filters) ;

}
