package br.com.usp.labis.service.go;

import java.net.URI;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.usp.labis.bean.GoAnnotation;
import br.com.usp.labis.bean.GoSearchAnnotationResult;
import br.com.usp.labis.bean.Protein;
import br.com.usp.labis.useful.GoAnnotationFilter;

@Component
public class GoAnnotationService {

	private final String BASE_URL = "https://www.ebi.ac.uk/QuickGO/services/annotation/search";
	private final String GO_NAME = "goName";
	private final String GENE_PRODUCT_ID = "geneProductId";
	private final String TAXON_ID = "taxonId";
	private final String INCLUDE_OPTIONAL_FIELDS = "includeFields";


	public Protein getGoAnnotationsForProteinAndTaxon(Protein protein, GoAnnotationFilter filters) {
		System.out.println("Searching for annotations => Protein: " + protein.getProteinId());
		try {
				String[] optionalParams = {GO_NAME};
				RestTemplate restTemplate = new RestTemplate();
				URI targetUrl = UriComponentsBuilder.fromUriString(BASE_URL) // Build the url
						.queryParam(GENE_PRODUCT_ID, protein.getProteinId()) // Add query params
						.queryParam(INCLUDE_OPTIONAL_FIELDS, optionalParams) // Add query params
						.queryParam(TAXON_ID, filters.getTaxonId()).build().encode().toUri();

				GoSearchAnnotationResult goSearchResult = restTemplate.getForObject(targetUrl, GoSearchAnnotationResult.class);
				List<GoAnnotation> annotations = goSearchResult.getResults();
				protein.setGoAnnotations(annotations);
								
		} catch (RuntimeException e) {
			System.out.println("Erro getting annotation for : " + protein.getProteinId());
			e.printStackTrace();
		}

		return protein;
	}

}
