package br.com.usp.labis.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import br.com.usp.labis.bean.Result;
import br.com.usp.labis.service.EnrichmentAnalysisService;
import br.com.usp.labis.service.file.OutputService;
import br.com.usp.labis.service.file.UploadFileService;

@RestController
@CrossOrigin
@RequestMapping("/pes")
public class EnrichmentAnalysisController {

	@Autowired
	private EnrichmentAnalysisService enrichmentAnalysisService;

	@Autowired
	private OutputService outputService;

	@Autowired
	private UploadFileService uploadFileService;

	
	@PostMapping
    @ResponseBody
	public String processEnrichmentAnalysisToExcel(@RequestParam("file") MultipartFile file,
			@RequestParam("taxonId") Integer taxonId, @RequestParam("minProteins") Integer minProteins,
			@RequestParam("toleranceFactor") Double toleranceFactor,
			@RequestParam("nullDistributions") Integer nullDistributions, @RequestParam("pvalue") Double pvalue) {

		String resultFile = enrichmentAnalysisService.processEnrichmentAnalysisToExcel(file, taxonId, minProteins,
				toleranceFactor, nullDistributions, pvalue);

		System.out.println("resultFilePath: " + resultFile);

		File fileOutput = new File(resultFile);
		resultFile = fileOutput != null ? fileOutput.getName() : null;

		return resultFile;
	}
	
	@PostMapping("map")
    @ResponseBody
	public Map<String, List<Result>> processEnrichmentAnalysisToMap(@RequestParam("file") MultipartFile file,
			@RequestParam("taxonId") Integer taxonId, @RequestParam("minProteins") Integer minProteins,
			@RequestParam("toleranceFactor") Double toleranceFactor,
			@RequestParam("nullDistributions") Integer nullDistributions, @RequestParam("pvalue") Double pvalue) {

		Map<String, List<Result>> resultMap = enrichmentAnalysisService.processEnrichmentAnalysisToMap(file, taxonId, minProteins,
				toleranceFactor, nullDistributions, pvalue);

		return resultMap;
	}


	@GetMapping("download2")
	public ResponseEntity<Resource> downloadFile2(@RequestParam("fileName") String fileName) {

		System.out.println("Downloading: " + fileName);

		File fileOutput = outputService.getFileByName(fileName);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

		FileSystemResource fileSystemResource = fileOutput != null ? new FileSystemResource(fileOutput) : null;

		return new ResponseEntity<>(fileSystemResource, headers, HttpStatus.OK);
	}
	

	@RequestMapping(value = "download", method = RequestMethod.GET, produces = "application/msexcel")
	public ResponseEntity<byte[]> downloadFile(@RequestParam("fileName") String fileName) {
		System.out.println("Downloading: " + fileName);

		Path fileLocation = Paths.get("C:" + File.separator + "uploaded_file" + File.separator + fileName);
		byte[] data = null;
		try {
			data = Files.readAllBytes(fileLocation);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HttpHeaders headers = new HttpHeaders();
		headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
		headers.add("Pragma", "no-cache");
		headers.add("Expires", "0");
		return ResponseEntity.ok().headers(headers).contentLength(data.length)
				.contentType(MediaType.parseMediaType("application/msexcel"))
				.body(data);
	}

	
	@GetMapping("delete")
	public void deleteFile(@RequestParam("fileName") String fileName) {
		File file = outputService.getFileByName(fileName);
		if (file != null) {
			uploadFileService.removeUploadedFile(file);
		}
	}
}
