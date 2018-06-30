package br.com.usp.labis.service.go;

import java.net.URI;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.usp.labis.bean.GoAnnotation;
import br.com.usp.labis.bean.GoSearchResult;
import br.com.usp.labis.bean.Protein;

@Component
public class GoAnnotationService {
	
	private final String BASE_URL = "https://www.ebi.ac.uk/QuickGO/services/annotation/search";

	public List<GoAnnotation> getGoAnnotationsForProteinAndTaxon(Protein protein, Integer taxonId) {
		System.out.println("ANNOTATION FOR protein =>>>> " + protein.getProteinId());
		System.out.println("ANNOTATION FOR taxon =>>>> " + taxonId);

		List<GoAnnotation> annotations = null;
		RestTemplate restTemplate = new RestTemplate();
		URI targetUrl= UriComponentsBuilder.fromUriString(BASE_URL)  // Build the url
			    .queryParam("geneProductId", protein.getProteinId()) // Add query params
			    .queryParam("taxonId", taxonId)        				  
			    .build()                                            
			    .encode()                                            
			    .toUri(); 

		try {
			
			GoSearchResult goSearchResult = restTemplate.getForObject(targetUrl, GoSearchResult.class);
			annotations = (List<GoAnnotation>) (Object)  goSearchResult.getResults();
			
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		
		System.out.println("ANNOTATIONS SIZE =>>>> " + annotations.size());

		return annotations;
	}

	
}
