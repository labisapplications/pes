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
	public ResponseEntity<String> processEnrichmentAnalysis(@RequestParam("file") MultipartFile file,
			@RequestParam("taxonId") Integer taxonId, @RequestParam("minProteins") Integer minProteins,
			@RequestParam("toleranceFactor") Double toleranceFactor,
			@RequestParam("nullDistributions") Integer nullDistributions, @RequestParam("pvalue") Double pvalue) {

		String resultFile = enrichmentAnalysisService.processEnrichmentAnalysis(file, taxonId, minProteins,
				toleranceFactor, nullDistributions, pvalue);
		
		System.out.println("resultFilePath: " + resultFile);

		File fileOutput = new File(resultFilePath);
		resultFile = fileOutput != null ? fileOutput.getName() : null;
		
		HttpHeaders headers = new HttpHeaders();
	    headers.add("Content-Type", "application/json; charset=utf-8");
		
		
		return new ResponseEntity<>(resultFile,  headers , HttpStatus.OK);
	}


	@CrossOrigin
	@GetMapping("/download/{fileName}")
	public ResponseEntity<Resource> downloadFile(@PathParam("fileName")  String fileName, HttpServletResponse response) {

		System.out.println("Downloading: " + fileName);

		File fileOutput = outputFileService.getFileByName(fileName);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

		FileSystemResource fileSystemResource = fileOutput != null ? new FileSystemResource(fileOutput) : null;

		return new ResponseEntity<>(fileSystemResource, headers, HttpStatus.OK);
	}
	
	@CrossOrigin
	@GetMapping("/delete/{fileName}")
	public void deleteFile(@PathParam("fileName") String fileName) {
		File file = outputFileService.getFileByName(fileName);
		if(file != null) {
			uploadFileService.removeUploadedFile(file);
		}
	}
}
