package br.com.usp.labis.service.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import br.com.usp.labis.bean.GoTerm;
import br.com.usp.labis.bean.GoTermCondition;
import br.com.usp.labis.bean.Protein;
import br.com.usp.labis.bean.Result;

@Component
public class OutputService implements IOutputService{

	private final String UPLOADED_FOLDER = "C:" + File.separator + "uploaded_file" + File.separator;

	private final String FILE_EXTENSION_XLS = ".XLS";
	
	private final String FILE_EXTENSION_CSV = ".XLS";

	private static String[] COLUMNS = { "GO_ID", "GO_NAME", "GENE", "QUALIFIER", "GO_ASPECT", "PVALUE_RATIO_A_B", "PVALUE_RATIO_B_A","PVALUE", "QVALUE", "RANK", "WEIGHT",
			"CORE", "ORIGINAL_WEIGHT", "ORIGINAL_PVALUE", "ORIGINAL_PROTEINS" };

	public String exportToExcel(List<GoTerm> goTerms) {

		String filePath = null;
		
		String conditionsNames = "";

		Workbook workbook = new XSSFWorkbook(); // new HSSFWorkbook() for generating `.xls` file

		for (GoTermCondition goTermCondition : goTerms.get(0).getConditions()) {
			
			String conditionName = goTermCondition.getCondition().getName();
			
			conditionsNames += "_" + conditionName + "_";
			
			Sheet sheet = workbook.createSheet(conditionName);
			
			Row headerRow = sheet.createRow(0);

			for (int i = 0; i < COLUMNS.length; i++) {
				
				Cell cell = headerRow.createCell(i);
				cell.setCellValue(COLUMNS[i]);
			}

			// populate the condition sheet
			int rowNum = 1;
			for (GoTerm goTerm : goTerms) {
				System.out.println("##### excel row for: " + goTerm.getGoAnnotation().getId());

				for (GoTermCondition goTermCondition2 : goTerm.getConditions()) {
					
					if (conditionName.equalsIgnoreCase(goTermCondition2.getCondition().getName())) {
						Row row = sheet.createRow(rowNum++);

						row.createCell(0).setCellValue(goTerm.getGoAnnotation().getGoId());

						row.createCell(1).setCellValue(goTerm.getGoAnnotation().getGoName());
						
						row.createCell(2).setCellValue(goTerm.getGoAnnotation().getSymbol());

						row.createCell(3).setCellValue(goTerm.getGoAnnotation().getQualifier());

						row.createCell(4).setCellValue(goTerm.getGoAnnotation().getGoAspect().getAspect());
						
						row.createCell(5).setCellValue(goTerm.getPvalueRatioAB());
						
						row.createCell(6).setCellValue(goTerm.getPvalueRatioBA());

						row.createCell(7).setCellValue(goTermCondition2.getFinalPvalue());

						row.createCell(8).setCellValue(goTermCondition2.getQvalue());

						row.createCell(9).setCellValue(goTermCondition2.getRank());

						row.createCell(10).setCellValue(goTermCondition2.getFinalWeight());

						StringBuilder coreProteins = new StringBuilder();

						for (Protein protein : goTermCondition2.getCoreProteins()) {
							coreProteins.append(protein.getProteinId());
							coreProteins.append(" ");
						}

						row.createCell(11).setCellValue(coreProteins.toString());
						
						row.createCell(12).setCellValue(goTermCondition2.getOriginalWeight());
						row.createCell(13).setCellValue(goTermCondition2.getPvalueOriginal());
						
						StringBuilder originalProteins = new StringBuilder();

						for (Protein protein : goTermCondition2.getOriginalProteins()) {
							originalProteins.append(protein.getProteinId());
							originalProteins.append(" ");
						}
						
						row.createCell(14).setCellValue(originalProteins.toString());

						// adjust the cells width
						for (int colNum = 0; colNum < row.getLastCellNum(); colNum++) {
							sheet.autoSizeColumn(colNum);
						}
						

					}
				}
			}
		}
		
		filePath = UPLOADED_FOLDER + "result" + conditionsNames + System.currentTimeMillis() + FILE_EXTENSION_XLS;
		
		FileOutputStream fileOut;
		
		try {
			fileOut = new FileOutputStream(filePath);
			workbook.write(fileOut);
			fileOut.close();
			// Closing the workbook
			workbook.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return filePath;
	}
	
	public byte[] exportToCSV(List<GoTerm> goTerms) {
		
		StringBuilder output = new StringBuilder();

		//String filePath = null;
		
		String conditionsNames = "";

		for (GoTermCondition goTermCondition : goTerms.get(0).getConditions()) {
			
			String conditionName = goTermCondition.getCondition().getName();
			
			conditionsNames += "_" + conditionName + "_";
						
			for (int i = 0; i < COLUMNS.length; i++) {
				
				output.append(COLUMNS[i]);
			}
			
			output.append('\n');

			// populate the condition sheet
			for (GoTerm goTerm : goTerms) {
				
				System.out.println("##### row for: " + goTerm.getGoAnnotation().getId());

				for (GoTermCondition goTermCondition2 : goTerm.getConditions()) {
					
					if (conditionName.equalsIgnoreCase(goTermCondition2.getCondition().getName())) {
						
						output.append(goTerm.getGoAnnotation().getGoId());
						
						output.append(",");

						output.append(goTerm.getGoAnnotation().getGoName());
						
						output.append(",");
	
						output.append(goTerm.getGoAnnotation().getSymbol());
						
						output.append(",");

						output.append(goTerm.getGoAnnotation().getQualifier());
						
						output.append(",");
						
						output.append(goTerm.getGoAnnotation().getGoAspect().getAspect());
						
						output.append(",");
	
						output.append(goTerm.getPvalueRatioAB());
						
						output.append(",");
						
						output.append(goTerm.getPvalueRatioBA());
						
						output.append(",");

						output.append(goTermCondition2.getFinalPvalue());
						
						output.append(",");

						output.append(goTermCondition2.getQvalue());
						
						output.append(",");

						output.append(goTermCondition2.getRank());
						
						output.append(",");

						output.append(goTermCondition2.getFinalWeight());
						
						output.append(",");

						StringBuilder coreProteins = new StringBuilder();

						for (Protein protein : goTermCondition2.getCoreProteins()) {
							
							coreProteins.append(protein.getProteinId());
							coreProteins.append(" ");
						}

						output.append(coreProteins.toString());
						
						output.append(",");
					
						output.append(goTermCondition2.getOriginalWeight());
						
						output.append(",");

						output.append(goTermCondition2.getPvalueOriginal());
						
						output.append(",");

						StringBuilder originalProteins = new StringBuilder();

						for (Protein protein : goTermCondition2.getOriginalProteins()) {
							
							originalProteins.append(protein.getProteinId());
							originalProteins.append(" ");
						}
						
						output.append(originalProteins.toString());
						
						output.append('\n');

					}
				}
			}
		}
		
		//filePath = UPLOADED_FOLDER + "result" + conditionsNames + System.currentTimeMillis() + FILE_EXTENSION_CSV;
		
		byte[] bytesFromBuilder = output.toString().getBytes() != null ?  output.toString().getBytes() : null;
			
		return bytesFromBuilder;
	}	

	public Map<String, List<Result>> exportToMap(List<GoTerm> goTerms) {
		Map<String, List<Result>> resultMap = new HashMap<String, List<Result>>();

		for (GoTerm goTerm : goTerms) {
			for (GoTermCondition goTermCondition : goTerm.getConditions()) {
				if(resultMap.get(goTermCondition.getCondition().getName()) == null) {
					resultMap.put(goTermCondition.getCondition().getName(), new ArrayList<Result>());
				}
			}
		}

		for (GoTerm goTerm : goTerms) {
			for (GoTermCondition goTermCondition : goTerm.getConditions()) {

				StringBuilder coreProteins = new StringBuilder();
				for (Protein protein : goTermCondition.getCoreProteins()) {
					coreProteins.append(protein.getProteinId());
					coreProteins.append(" ");
				}

				Result result = new Result();
				result.setConditionName(goTermCondition.getCondition().getName());
				result.setAspect(goTerm.getGoAnnotation().getGoAspect().getAspect());
				result.setGoId(goTerm.getGoAnnotation().getGoId());
				result.setGeneName(goTerm.getGoAnnotation().getSymbol());
				result.setQualifier(goTerm.getGoAnnotation().getQualifier());
				result.setPvalue(goTermCondition.getFinalPvalue() + "");
				result.setQvalue(goTermCondition.getQvalue() + "");
				result.setRank(goTermCondition.getRank() + "");
				result.setWeight(goTermCondition.getFinalWeight() + "");
				result.setCore(coreProteins.toString());

				resultMap.get(goTermCondition.getCondition().getName()).add(result);
			}
		}

		return resultMap;
	}

	public File getFileByName(String fileName) {
		File fileOutput = null;
		try {
			fileOutput = new File(UPLOADED_FOLDER + fileName);
		} catch (RuntimeException e) {
			System.out.println("IO Exception: " + fileName + " - " + e.getMessage() + e.getCause());
		}
		return fileOutput;
	}

}
