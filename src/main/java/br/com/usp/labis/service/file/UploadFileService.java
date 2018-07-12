package br.com.usp.labis.service.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class UploadFileService {

	// private final String UPLOADED_FOLDER = "C:" + File.separator +
	// "uploaded_file" + File.separator;
	private final String UPLOADED_FOLDER = System.getenv("OPENSHIFT_DATA_DIR") ;

	private final String FILE_EXTENSION = ".XLS";

	/**
	 * Upload data file to the UPLOADED_FOLDER.
	 * 
	 * @param MultipartFile
	 *            file file to upload
	 * @return File file uploaded
	 */
	public File uploadExcelFile(MultipartFile file) {

		String newFileName = System.currentTimeMillis() + FILE_EXTENSION;
		File uploadedFile = null;

		try {
			
			// Get the file and save it in the UPLOADED_FOLDER
			byte[] bytes = file.getBytes();
			
			Path teste2 = FileSystems.getDefault().getPath(newFileName);
			System.out.println( "teste2" + teste2);
			try {
				Files.write(teste2, bytes);
			} catch (Exception e) {
				System.out.println("teste1 erro: " + e.getMessage() + e.getCause());
				e.printStackTrace();
			}
			
						
			Path teste1 = FileSystems.getDefault().getPath("/upload", newFileName);
			System.out.println( "teste1" + teste1);
			try {
				Files.write(teste1, bytes);
			} catch (Exception e) {
				System.out.println("teste1 erro: " + e.getMessage() + e.getCause());
				e.printStackTrace();
			} 
			
			
			Path path = FileSystems.getDefault().getPath(UPLOADED_FOLDER, newFileName);
			Files.write(path, bytes);
			uploadedFile = new File(UPLOADED_FOLDER + newFileName);
			
		} catch (IOException e) {
			System.out.println("error " + e.getMessage() + e.getCause());
			e.printStackTrace();
		}

		return uploadedFile;
	}

	/**
	 * Remove uploaded data file of the UPLOADED_FOLDER.
	 * 
	 * @param File
	 *            file to be removed
	 */
	public void removeUploadedFile(File file) {
		try {

			if (file.delete()) {
				System.out.println(file.getName() + " was deleted!");
			} else {
				System.out.println("Delete operation is failed.");
			}

		} catch (Exception e) {

			e.printStackTrace();

		}
	}

}
