package br.com.usp.labis.service.file;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import br.com.usp.labis.bean.Condition;
import br.com.usp.labis.bean.Protein;
import br.com.usp.labis.bean.Replicate;
import br.com.usp.labis.exception.CustomException;

@Component
public class ExcelReaderService {

	private final String PROTEIN_IDS = "Protein IDs";
	private final String GENE_NAMES = "Gene names";
	private final String LFQ_INTENSITY = "LFQ intensity";
	
	@Autowired
	private MessageSource messageSource;

	/**
	 * Process excel data file to extract proteins, conditions and replicates.
	 * 
	 * @param file
	 *            data file to be processed
	 * @return List<Protein> protein list
	 */
	//public List<Protein> processExcelFile(File file) {
	public List<Protein> processExcelFile(MultipartFile file) {
		Workbook workbook = null;
		List<Protein> proteins = new ArrayList<Protein>();

		try {
			//FileInputStream excelFile = new FileInputStream(file);
			//workbook = new XSSFWorkbook(excelFile);
			workbook = new XSSFWorkbook(file.getInputStream());
			Sheet datatypeSheet = workbook.getSheetAt(0);
			Iterator<Row> iterator = datatypeSheet.iterator();

			Map<String, List<Object>> rows = new HashMap<String, List<Object>>();

			while (iterator.hasNext()) {
				Row currentRow = iterator.next();
				this.processRow(currentRow, rows);
			}

			if (rows != null) {

				Iterator it = rows.entrySet().iterator();

				// get gene name column position
				Map<String, List<Integer>> geneNames = this.getGeneNames(rows);

				// get condition name e replicates columns positions
				Map<String, List<Integer>> conditionsAndReplicates = this.getConditionsAndReplicates(rows);
				/**
				 * process each protein row data to conditions and gene names. rowData.getKey()
				 * : protein ids rowData.getValue() : other columns in the row
				 */
				while (it.hasNext()) {
					Map.Entry rowData = (Map.Entry) it.next();
					Protein protein = null;

					try {

						protein = this.processColumnsToProtein((String) rowData.getKey(),
								(List<Object>) rowData.getValue(), geneNames, conditionsAndReplicates);
						proteins.add(protein);
					} catch (RuntimeException e) {
						System.out.println("Error key =>" + rowData.getKey());
						System.out.println("Error values =>" + rowData.getValue());
						System.out.println("Error geneNames =>" + geneNames);
						System.out.println("Error conditionsAndReplicates =>" + conditionsAndReplicates);
						System.out.println("Error =>" + e.getMessage() + e.getCause());

					}
				}
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RuntimeException e) {
			e.printStackTrace();
				throw new CustomException(messageSource.getMessage("messages.errorProcessExcelFile", 
						new Object[] {e.getMessage() + "-" + e.getCause()}, Locale.US));
		} finally {
			if (workbook != null) {
				try {
					workbook.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			return proteins;
		}
	}

	/**
	 * Process row of the data file
	 * 
	 * @param currentRow
	 * @param List<Object>
	 *            rows of the data file
	 */
	private void processRow(Row currentRow, Map<String, List<Object>> rows) {
		Iterator<Cell> cellIterator = currentRow.iterator();
		Integer columnNumber = 0;
		String key = null;
		Boolean isGeneNamePresent = currentRow.getCell(3) != null ? true : false;
		Boolean isProteinNamePresent = currentRow.getCell(2) != null ? true : false;
		Boolean isMajorityProteinNamePresent = currentRow.getCell(1) != null ? true : false;

		while (cellIterator.hasNext()) {

			Cell currentCell = cellIterator.next();
			columnNumber += 1;

			if (columnNumber == 1) {
				key = currentCell.getStringCellValue();
				rows.put(key, new ArrayList<Object>());

			} else if (columnNumber == 2 && !isMajorityProteinNamePresent) {
				rows.get(key).add(" ");
				
			} else if (columnNumber == 3 && !isProteinNamePresent) {
				rows.get(key).add(" ");
				
			} else if (columnNumber == 4 && !isGeneNamePresent) {
				rows.get(key).add(" ");
			}

			if (currentCell.getCellTypeEnum() == CellType.STRING) {
				rows.get(key).add(currentCell.getStringCellValue());
			} else if (currentCell.getCellTypeEnum() == CellType.NUMERIC) {
				rows.get(key).add(currentCell.getNumericCellValue());
			} else {
				rows.get(key).add(0);
			}
		}
	}

	/**
	 * Process conditions and replicates for the protein
	 * 
	 * @param proteinNames
	 *            protein to be processed
	 * @param rowColumns
	 *            data related to the protein
	 * @param Protein
	 *            protein with conditions and replicates
	 */
	private Protein processColumnsToProtein(String proteinNames, List<Object> rowColumns,
			Map<String, List<Integer>> geneNames, Map<String, List<Integer>> conditionsAndReplicates) {

		// protein
		Protein protein = new Protein();
		protein.setConditions(new ArrayList<Condition>());
		try {
			// just considerer the first protein id in the list of protein'ids because it is
			// probably the most significant
			// put the other proteins id in a separated array to be used if necessary

			if (proteinNames != null) {
				String[] proteinIdsArray = proteinNames.split(";");

				for (int i = 0; i < proteinIdsArray.length; i++) {
					String proteinId = proteinIdsArray[i];

					// ignores '-' in the protein id (if it exists)
					if (proteinId.lastIndexOf("-") != -1) {
						proteinId = proteinId.substring(0, proteinId.lastIndexOf("-"));
					}

					if (i == 0) {
						protein.setProteinId(proteinId);
					} else {

						if (protein.getOtherProteinsIdAssociated() == null) {
							protein.setOtherProteinsIdAssociated(new ArrayList<String>());
						}

						protein.getOtherProteinsIdAssociated().add(proteinId);
					}
				}
			}

		} catch (RuntimeException e) {
			System.out.println("Error to get protein id" + proteinNames + " => gene is null");
		}

		try {
			// just considerer the first gene in the list of genes because it is probably
			// the most significant
			String geneNamesValue = (String) rowColumns.get(geneNames.get(GENE_NAMES).get(0));
			if (geneNamesValue != null) {
				String[] geneNamesValuesArray = geneNamesValue.split(";");
				protein.setGeneNames(geneNamesValuesArray[0]);
			}

		} catch (RuntimeException e) {
			System.out.println("Error protein " + proteinNames + " => gene is null");
		}

		Iterator itConditionsAndReplicates = conditionsAndReplicates.entrySet().iterator();

		while (itConditionsAndReplicates.hasNext()) {

			// get condition and replicates from the map
			Map.Entry conditionReplicates = (Map.Entry) itConditionsAndReplicates.next();

			// create a new condition
			Condition condition = new Condition();
			condition.setName((String) conditionReplicates.getKey());
			condition.setReplicates(new ArrayList<Replicate>());

			// get condition replicates
			List<Integer> replicatePositions = (List<Integer>) conditionReplicates.getValue();

			// add the replicates to the condition
			for (int index = 0; index < replicatePositions.size(); index++) {
				Replicate replicate = new Replicate();

				replicate.setName(conditionReplicates.getKey() + "_" + replicatePositions.get(index));

				try {
					replicate.setValue((Double) rowColumns.get(replicatePositions.get(index)));
				} catch (RuntimeException e) {
					replicate.setValue(0.00);
				}
				condition.getReplicates().add(replicate);
			}

			// add condition to the protein
			protein.getConditions().add(condition);

		}

		return protein;
	}

	/**
	 * Get position of column gene names in the data file
	 * 
	 * @param Map<String,
	 *            List<Object>> rows rows in the data file
	 * @param Map<String,
	 *            List<Integer>> map with position for gene names
	 */
	private Map<String, List<Integer>> getGeneNames(Map<String, List<Object>> rows) {
		Map<String, List<Integer>> dataColumnPosition = new HashMap<String, List<Integer>>();
		List<Object> columns = rows.get(PROTEIN_IDS);

		for (int index = 0; index < columns.size(); index++) {
			String column = (String) columns.get(index);

			if (column.contains(GENE_NAMES)) {
				// put the geneNames in the map
				if (dataColumnPosition.get(GENE_NAMES) == null) {
					// put the column gene name position
					dataColumnPosition.put(GENE_NAMES, new ArrayList<Integer>());
					dataColumnPosition.get(GENE_NAMES).add(index);
				}
				break;
			}
		}

		return dataColumnPosition;
	}

	/**
	 * Get positions of columns related to protein conditions in the data file
	 * 
	 * @param Map<String,
	 *            List<Object>> rows rows in the data file
	 * @param Map<String,
	 *            List<Integer>> map with conditions and respectives replicates
	 */
	private Map<String, List<Integer>> getConditionsAndReplicates(Map<String, List<Object>> rows) {
		Map<String, List<Integer>> dataColumnPosition = new HashMap<String, List<Integer>>();
		List<Object> columns = rows.get(PROTEIN_IDS);
		
		for (int index = 0; index < columns.size(); index++) {
			String column = (String) columns.get(index);
			
			if (column.contains(LFQ_INTENSITY)) {
				// get the condition name. Expected format: LFQ_INTENSITY Condition_Replicate
				String condition = column.replaceAll(LFQ_INTENSITY, "");
				Integer indexLastUnderscore = column.lastIndexOf("_");
				condition = condition.replaceAll(column.substring(indexLastUnderscore), " ");
				condition = condition.replaceAll(" ", "");
				//condition = condition.replaceAll("\\d+", "");
				System.out.println("CONDITION: " + condition);

				// put the condition in the map
				if (dataColumnPosition.get(condition) == null) {
					dataColumnPosition.put(condition, new ArrayList<Integer>());
				}
				System.out.println("COLUMN: " + column + " - INDEX: " + index);
				// put the column replicate position for the condition
				dataColumnPosition.get(condition).add(index);
			}
		}

		return dataColumnPosition;
	}
}
