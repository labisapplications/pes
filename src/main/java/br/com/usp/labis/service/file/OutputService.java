package br.com.usp.labis.service.file;

import java.io.File;
import java.io.FileOutputStream;
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
public class OutputService {

	private final String UPLOADED_FOLDER = "C:" + File.separator + "uploaded_file" + File.separator;

	private final String FILE_EXTENSION = ".XLS";

	private static String[] COLUMNS = { "GO_ID", "GO_NAME", "GENE", "QUALIFIER", "GO_ASPECT", "PVALUE_RATIO_A_B", "PVALUE", "QVALUE", "RANK", "WEIGHT",
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

						row.createCell(6).setCellValue(goTermCondition2.getFinalPvalue());

						row.createCell(7).setCellValue(goTermCondition2.getQvalue());

						row.createCell(8).setCellValue(goTermCondition2.getRank());

						row.createCell(9).setCellValue(goTermCondition2.getFinalWeight());

						StringBuilder coreProteins = new StringBuilder();

						for (Protein protein : goTermCondition2.getCoreProteins()) {
							coreProteins.append(protein.getProteinId());
							coreProteins.append(" ");
						}

						row.createCell(10).setCellValue(coreProteins.toString());
						
						row.createCell(11).setCellValue(goTermCondition2.getOriginalWeight());
						row.createCell(12).setCellValue(goTermCondition2.getPvalueOriginal());
						
						StringBuilder originalProteins = new StringBuilder();

						for (Protein protein : goTermCondition2.getOriginalProteins()) {
							originalProteins.append(protein.getProteinId());
							originalProteins.append(" ");
						}
						
						row.createCell(13).setCellValue(originalProteins.toString());

						// adjust the cells width
						for (int colNum = 0; colNum < row.getLastCellNum(); colNum++) {
							sheet.autoSizeColumn(colNum);
						}
						

					}
				}
			}
		}
		
		filePath = UPLOADED_FOLDER + "result" + conditionsNames + System.currentTimeMillis() + FILE_EXTENSION;
		
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
