package StudyCraft;

import java.io.PrintWriter;
import java.util.ArrayList;

public class CourseModule {
	private String moduleName;
	private ArrayList<String> files = new ArrayList<>();

	public CourseModule(String moduleName) {
		this.moduleName = moduleName;
	}

	public void addFile(String fileName) {
		files.add('\t' + fileName);
	}

	public String getFileNameAt(int i) {
		return files.get(i);
	}

	public String getModuleName() {
		return moduleName;
	}

	public int numberOfFiles() {
		return files.size();
	}

	public void writeFiles(PrintWriter pwriter) {
		// loop through all files to save them
		for (int i = 0; i < files.size(); ++i) {
			pwriter.println('\t' + files.get(i));
		}
	}

	@SuppressWarnings("unchecked")
	public ArrayList<String> getCopyOfFileList() {
		return (ArrayList<String>) files.clone();
	}

	/**
	 * Will remove the file name if it corresponds to the file at the target index.
	 * @param fileIndex
	 * @param fileName
	 */
	public void removeFile_SquareComplexity(int fileIndex, String fileName) {
		// will cause array list shuffling, but the files lists will be so small
		// that this is neglicible.
		if (fileIndex > 0 && fileIndex < files.size()) {
			String targetFile = files.get(fileIndex);
			targetFile = convertFilePathToFileName(targetFile);
			if (targetFile != null && fileName != null && fileName.equals(targetFile)) {
				files.remove(fileIndex);
			}
		}
		// this could be easily avoided using linked list or some other data
		// structure, but that may cause
		// some restructure of other methods, therefore I'm just allowing a
		// simple array list shuffling.
	}

	private String convertFilePathToFileName(String filePath) {
		// find the back slash that ends the directory portion of string
		if(filePath == null){
			return null;
		}
		
		for (int i = filePath.length() - 1; i >= 0; --i) {
			// start at the end in case file extensions change from .txt
			if (filePath.charAt(i) == '\\' || filePath.charAt(i) == '/') {
				if (i + 1 < filePath.length()) {
					// valid string, will return a name representation
					return filePath.substring(i + 1);
				}
			}
		}
		return filePath;
	}

}
