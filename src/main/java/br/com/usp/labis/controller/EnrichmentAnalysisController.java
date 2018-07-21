package br.com.usp.labis.controller;

import java.io.File;

import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.PathParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import br.com.usp.labis.service.EnrichmentAnalysisService;
import br.com.usp.labis.service.file.OutputFileService;
import br.com.usp.labis.service.file.UploadFileService;

@RestController
@RequestMapping("/pes")
public class EnrichmentAnalysisController {

	@Autowired
	private EnrichmentAnalysisService enrichmentAnalysisService;
	
	private OutputFileService outputFileService;

	private UploadFileService uploadFileService;
	
	@CrossOrigin
	@PostMapping
	public String processEnrichmentAnalysis(@RequestParam("file") MultipartFile file,
			@RequestParam("taxonId") Integer taxonId, @RequestParam("minProteins") Integer minProteins,
			@RequestParam("toleranceFactor") Double toleranceFactor,
			@RequestParam("nullDistributions") Integer nullDistributions, @RequestParam("pvalue") Double pvalue) {

		String resultFilePath = enrichmentAnalysisService.processEnrichmentAnalysis(file, taxonId, minProteins,
				toleranceFactor, nullDistributions, pvalue);
		
		return resultFilePath;

	}


	@CrossOrigin
	@GetMapping("/download/{fileName}")
	public ResponseEntity<Resource> downloadFile(@PathParam("fileName")  String fileName, HttpServletResponse response) {
		String filePath = "C:" + File.separator + "uploaded_file" + File.separator + fileName;
		File fileOutput = outputFileService.getFileByName(fileName);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

		FileSystemResource fileSystemResource = fileOutput != null ? new FileSystemResource(fileOutput) : null;

		return new ResponseEntity<>(fileSystemResource, headers, HttpStatus.OK);
	}
	
	@CrossOrigin
	@GetMapping("/delete/{fileName}")
	public void deleteFile(@PathParam("fileName") String fileName) {
		String filePath = "C:" + File.separator + "uploaded_file" + File.separator + fileName;
		File file = outputFileService.getFileByName(fileName);
		uploadFileService.removeUploadedFile(file);
	}
}
