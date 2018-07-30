package br.com.usp.labis.service.go;

import java.util.List;

import org.springframework.stereotype.Component;

import br.com.usp.labis.bean.GeneProduct;
import br.com.usp.labis.bean.Protein;

@Component
public interface IGeneProductService {
	
	List<GeneProduct> getGeneProductService(Protein protein) ;

}
