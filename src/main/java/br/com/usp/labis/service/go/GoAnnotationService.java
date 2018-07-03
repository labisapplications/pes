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
	private final String PROTEIN_IDS = "Protein IDs";

	public Protein getGoAnnotationsForProteinAndTaxon(Protein protein, GoAnnotationFilter filters) {
		System.out.println("ANNOTATION FOR protein =>>>> " + protein.getProteinId());
		System.out.println("ANNOTATION FOR taxon =>>>> " + filters.getTaxonId());

		try {
			if (!protein.getProteinId().equalsIgnoreCase(PROTEIN_IDS)) {
				RestTemplate restTemplate = new RestTemplate();
				URI targetUrl = UriComponentsBuilder.fromUriString(BASE_URL) // Build the url
						.queryParam("geneProductId", protein.getProteinId()) // Add query params
						.queryParam("taxonId", filters.getTaxonId()).build().encode().toUri();

				GoSearchAnnotationResult goSearchResult = restTemplate.getForObject(targetUrl, GoSearchAnnotationResult.class);
				List<GoAnnotation> annotations = goSearchResult.getResults();
				protein.setGoAnnotations(annotations);

				System.out.println("ANNOTATIONS SIZE =>>>> " + annotations.size());
								
			}

		} catch (RuntimeException e) {
			e.printStackTrace();
		}

		return protein;
	}

}
