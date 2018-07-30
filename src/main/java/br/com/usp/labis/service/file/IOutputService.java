package br.com.usp.labis.service.file;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import br.com.usp.labis.bean.GoTerm;
import br.com.usp.labis.bean.Result;

@Component
public interface IOutputService {
	
	String exportToExcel(List<GoTerm> goTerms) ;
	
	Map<String, List<Result>> exportToMap(List<GoTerm> goTerms);
	
	File getFileByName(String fileName);
}
