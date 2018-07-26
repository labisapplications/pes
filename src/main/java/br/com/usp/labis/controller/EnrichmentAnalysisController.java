package br.com.usp.labis.controller;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import br.com.usp.labis.bean.Result;
import br.com.usp.labis.exception.ApiErrorResponse;
import br.com.usp.labis.exception.CustomException;
import br.com.usp.labis.service.EnrichmentAnalysisService;
import br.com.usp.labis.service.file.OutputService;

@RestController
@CrossOrigin
@RequestMapping("/pes")
public class EnrichmentAnalysisController {

	@Autowired
	private EnrichmentAnalysisService enrichmentAnalysisService;

	@Autowired
	private OutputService outputService;

	@Autowired
	private MessageSource messageSource;

	// @Autowired
	// private UploadFileService uploadFileService;

	@PostMapping
	@ResponseBody
	public String processEnrichmentAnalysisToExcel(@RequestParam("file") MultipartFile file,
			@RequestParam("taxonId") Integer taxonId, @RequestParam("minProteins") Integer minProteins,
			@RequestParam("toleranceFactor") Double toleranceFactor,
			@RequestParam("nullDistributions") Integer nullDistributions, @RequestParam("pvalue") Double pvalue) {

		String resultFile = null;

		try {
			resultFile = enrichmentAnalysisService.processEnrichmentAnalysisToExcel(file, taxonId, minProteins,
					toleranceFactor, nullDistributions, pvalue);
		} catch (RuntimeException e) {
			System.out.println("An error occurred: " + e.getMessage() + e.getCause());
		}

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

		Map<String, List<Result>> resultMap = null;

		 resultMap = enrichmentAnalysisService.processEnrichmentAnalysisToMap(file,
		 taxonId, minProteins, toleranceFactor, nullDistributions, pvalue);

		return resultMap;
	}

	@GetMapping("download2")
	public ResponseEntity<Resource> downloadFile(@RequestParam("fileName") String fileName) {

		System.out.println("Downloading: " + fileName);

		File fileOutput = outputService.getFileByName(fileName);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

		FileSystemResource fileSystemResource = fileOutput != null ? new FileSystemResource(fileOutput) : null;

		return new ResponseEntity<>(fileSystemResource, headers, HttpStatus.OK);
	}

	/*
	 * @GetMapping("delete") public void deleteFile(@RequestParam("fileName") String
	 * fileName) { File file = outputService.getFileByName(fileName); if (file !=
	 * null) { uploadFileService.removeUploadedFile(file); } }
	 */

	@ExceptionHandler(value = { CustomException.class })
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseEntity<ApiErrorResponse> handleException(CustomException ex) {
		return new ResponseEntity<>(new ApiErrorResponse(400, ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
