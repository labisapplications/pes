package br.com.usp.labis.service.go;

import java.util.List;

import org.springframework.stereotype.Component;

import br.com.usp.labis.bean.GoAnnotation;

@Component
public interface IGoAntologyService {

	void getGoAntologyForAnnotation(List<GoAnnotation> annotations) ;
	
	void getGoAntologyForAnnotation(GoAnnotation annotation) ;
	
	void getGoAntologyForAnnotation() ;
}
