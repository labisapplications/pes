package br.com.usp.labis.service.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import br.com.usp.labis.bean.GoTerm;
import br.com.usp.labis.bean.GoTermCondition;
import br.com.usp.labis.bean.Protein;

@Component
public class OutputFileService {

	private final String UPLOADED_FOLDER = "C:" + File.separator + "uploaded_file" + File.separator;

	private final String FILE_EXTENSION = ".XLS";

	private static String[] COLUMNS = { "GO_ID", "GENE", "QUALIFIER", "GO_ASPECT", "PVALUE", "QVALUE", "RANK", "WEIGHT", "CORE" };

	public String exportToExcel(List<GoTerm> goTerms) {
		
		String filePath = UPLOADED_FOLDER + "result"+ System.currentTimeMillis() + FILE_EXTENSION;
		
		Workbook workbook = new XSSFWorkbook(); // new HSSFWorkbook() for generating `.xls` file

		for (GoTermCondition goTermCondition : goTerms.get(0).getConditions()) {
			String conditionName = goTermCondition.getCondition().getName();
			Sheet sheet = workbook.createSheet(conditionName);
			Row headerRow = sheet.createRow(0);

			for (int i = 0; i < COLUMNS.length; i++) {
				Cell cell = headerRow.createCell(i);
				cell.setCellValue(COLUMNS[i]);
			}

			// populate the condition sheet
			int rowNum = 1;
			for (GoTerm goTerm : goTerms) {
				for (GoTermCondition goTermCondition2 : goTerm.getConditions()) {
					if (conditionName.equalsIgnoreCase(goTermCondition2.getCondition().getName())) {
						Row row = sheet.createRow(rowNum++);

						row.createCell(0).setCellValue(goTerm.getGoAnnotation().getGoId());
						
						row.createCell(1).setCellValue(goTerm.getGoAnnotation().getSymbol());
						
						row.createCell(2).setCellValue(goTerm.getGoAnnotation().getQualifier());
						
						row.createCell(3).setCellValue(goTerm.getGoAnnotation().getGoAspect().getAspect());

						row.createCell(4).setCellValue(goTermCondition2.getFinalPvalue());
						
						row.createCell(5).setCellValue(goTermCondition2.getQvalue());
						
						row.createCell(6).setCellValue(goTermCondition2.getRank());

						row.createCell(7).setCellValue(goTermCondition2.getFinalWeight());

						StringBuilder coreProteins = new StringBuilder();
						
						for (Protein protein : goTermCondition2.getCoreProteins()) {
							coreProteins.append(protein.getProteinId());
							coreProteins.append(" ");
						}

						row.createCell(8).setCellValue(coreProteins.toString());
						
						//adjust the cells width
						for(int colNum = 0; colNum < row.getLastCellNum();colNum++)   {
							sheet.autoSizeColumn(colNum);
						}
					}
				}
			}
		}

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
