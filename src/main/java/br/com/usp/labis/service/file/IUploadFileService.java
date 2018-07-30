package br.com.usp.labis.service.file;

import java.io.File;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public interface IUploadFileService {

	File uploadExcelFile(MultipartFile file);

}
