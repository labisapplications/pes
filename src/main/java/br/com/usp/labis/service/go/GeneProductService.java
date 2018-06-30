package br.com.usp.labis.service.go;

import java.net.URI;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.usp.labis.bean.GeneProduct;
import br.com.usp.labis.bean.GoAnnotation;
import br.com.usp.labis.bean.GoSearchResult;
import br.com.usp.labis.bean.Protein;

@Component
public class GeneProductService {

	private final String BASE_URL = "https://www.ebi.ac.uk/QuickGO/services/geneproduct/search";
	
	public List<GeneProduct> getGeneProductService(Protein protein) {
		System.out.println("GENE PRODUCTS FOR =>>>> " + protein.getProteinId());
		List<GeneProduct> geneProducts = null;
		RestTemplate restTemplate = new RestTemplate();
		String base_url = BASE_URL;
		URI targetUrl= UriComponentsBuilder.fromUriString(base_url)  // Build the url
			    .queryParam("query", protein.getProteinId())         // Add query params
			    .build()                                            
			    .encode()                                            
			    .toUri(); 

		try {
			
			GoSearchResult geneProductSearch = restTemplate.getForObject(targetUrl, GoSearchResult.class);
			geneProducts = (List<GeneProduct>) (Object) geneProductSearch.getResults();
			
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		
		System.out.println("GENE PRODUCTS =>>>> " + geneProducts.size());

		return geneProducts;
	}

}
