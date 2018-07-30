package br.com.usp.labis.service.file;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import br.com.usp.labis.bean.Protein;

@Component
public interface IExcelReaderService {

	List<Protein> processExcelFile(MultipartFile file);
}
