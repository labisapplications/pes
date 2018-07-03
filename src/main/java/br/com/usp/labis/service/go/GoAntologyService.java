package br.com.usp.labis.service.go;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import br.com.usp.labis.bean.GoAnnotation;
import br.com.usp.labis.bean.GoAntology;
import br.com.usp.labis.bean.GoSearchGoAntologyResult;

@Component
public class GoAntologyService {

	private final String BASE_URL = "https://www.ebi.ac.uk/QuickGO/services/ontology/";
	private final String TERMS = "go/terms/";
	private final String GO_ID = "{goId}/";

	public void getGoAntologyForAnnotation(List<GoAnnotation> annotations) {
		for (GoAnnotation annotation : annotations) {
			this.getGoAntologyForAnnotation(annotation);
		}
	}

	public void getGoAntologyForAnnotation(GoAnnotation annotation) {

		try {

			if (annotation != null) {
				System.out.println("GET ANTOLOGY FOR =>>>> " + annotation.getGoId());

				RestTemplate restTemplate = new RestTemplate();

				Map<String, String> params = new HashMap<String, String>();
				params.put("goId", annotation.getGoId());

				String targetUrl = BASE_URL + TERMS + GO_ID;

				GoSearchGoAntologyResult goSearchResult = restTemplate.getForObject(targetUrl,
						GoSearchGoAntologyResult.class, params);
				List<GoAntology> antology = goSearchResult.getResults();

				if (antology != null && !antology.isEmpty()) {
					System.out.println("antology =>>>> " + antology.get(0).getId());
					annotation.setGoAntology(antology);
				}
			}

		} catch (RuntimeException e) {
			e.printStackTrace();
		}
	}

	public void getGoAntologyForAnnotation() {

		try {

			Map<String, String> params = new HashMap<String, String>();
			params.put("goId", "GO:0016740");

			RestTemplate restTemplate = new RestTemplate();
			GoSearchGoAntologyResult goSearchResult = restTemplate.getForObject(BASE_URL,
					GoSearchGoAntologyResult.class, params);
			List<GoAntology> antology = goSearchResult.getResults();

		} catch (RuntimeException e) {
			e.printStackTrace();
		}
	}
}
