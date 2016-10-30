package StudyCraft;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * This file saves to a .txt file so that users may edit files manually with a
 * text editor. I am aware of serializing objects, but chose to do it this way.
 * 
 * @version 1.0
 * @author Matt
 *
 */
public class CourseTopic {
	private String courseName;
	private String fileName;
	private ArrayList<CourseModule> modules = new ArrayList<>();
	private boolean errorLoading = false;
	private boolean autosave = true;

	public void makeNewCourseFile(String courseName) {
		clearFields();
		makeCoursesDirectory();
		this.courseName = courseName;
		fileName = "courses\\" + courseName + "-CourseFile.txt";
		try {
			FileWriter fwriter = new FileWriter(fileName, false);
			PrintWriter pwriter = new PrintWriter(fwriter);
			writeCourseNameToFile(pwriter);

			pwriter.close();
			fwriter.close();
		} catch (Exception all) {
			System.out.println("failed to write file");
			System.out.println(all.getMessage());
		}
	}

	public boolean checkForExistingFile(String courseName) {
		fileName = "courses\\" + courseName + "-CourseFile.txt";
		File file = new File(fileName);
		return file.exists() && !file.isDirectory();
	}

	public boolean makeNewModule(String str) {
		// ensure list of modules is not null
		if (modules == null) {
			modules = new ArrayList<>();
		}

		for (int i = 0; i < modules.size(); ++i) {
			CourseModule current = modules.get(i);
			if (current.getModuleName().equals(str)) {
				// module already exists
				return false; // do not add module and do not save settings
			}
		}

		// create new module
		modules.add(new CourseModule(str));

		// save
		if (autosave) {
			saveAllFieldsToTxtFile();
		}

		return true;
	}

	public boolean removeModule(String str) {
		boolean foundAndDeleted = false;
		// find string to remove
		for (int i = 0; i < modules.size(); ++i) {
			String moduleStrAtIndex = modules.get(i).getModuleName();
			if (str.equals(moduleStrAtIndex)) {
				modules.remove(i);
				foundAndDeleted = true;
				if (autosave) {
					saveAllFieldsToTxtFile();
				}
				return foundAndDeleted;
			}
		}
		if (autosave) {
			saveAllFieldsToTxtFile();
		}
		return foundAndDeleted;
	}

	public void makeNewFile(int moduleIndex, String filename) {
		// check if valid index
		if (moduleIndex < modules.size() && moduleIndex > -1) {
			CourseModule workingModule = modules.get(moduleIndex);
			// check that module is valid
			if (workingModule != null) {
				workingModule.addFile(filename);
				saveAllFieldsToTxtFile();
			}
		}
	}

	public void removeFile(int moduleIndex, int fileIndex, String fileName) {
		if (moduleIndex >= 0 && moduleIndex < modules.size()) {
			CourseModule workingModule = modules.get(moduleIndex);
			workingModule.removeFile_SquareComplexity(fileIndex, fileName);
		}
		saveAllFieldsToTxtFile();
	}

	private void makeCoursesDirectory() {
		File newDir = new File("courses");
		boolean sucessfulCreation = newDir.mkdir();
		if (sucessfulCreation) {
			System.out.println("course directory creation success");
		}
	}

	private boolean saveAllFieldsToTxtFile() {
		try {
			// resource leak if exception is thrown
			FileWriter fwriter = new FileWriter(fileName, false); // overwrites
			PrintWriter pwriter = new PrintWriter(fwriter);
			writeCourseNameToFile(pwriter);
			writeModulesToFile(pwriter);

			pwriter.close();
			fwriter.close();
			return true;
		} catch (Exception all) {
			System.out.println("failed to write file");
			System.out.println(all.getMessage());
			return false;
		}
	}

	private void writeModulesToFile(PrintWriter pwriter) {
		for (int i = 0; i < modules.size(); ++i) {
			CourseModule currentModule = modules.get(i);
			pwriter.println("module: " + currentModule.getModuleName());
			writeSubFilesToFile(pwriter, currentModule);
		}
	}

	private void writeSubFilesToFile(PrintWriter pwriter, CourseModule currentModule) {
		currentModule.writeFiles(pwriter);
	}

	private void writeCourseNameToFile(PrintWriter pwriter) {
		pwriter.println("course: " + courseName);
	}

	private void clearFields() {
		courseName = null;
		modules = new ArrayList<>();
		errorLoading = false;
	}

	public void createCourseFromFile(String fileName) {
		// clear current course fields
		clearFields();
		fileName = "courses\\" + fileName + "-CourseFile.txt";
		this.fileName = fileName;

		// try catch block for opening a scanner on file
		try (Scanner fscan = new Scanner(Paths.get(fileName));) {

			// if file successfully opens, process it into a course structure
			setCourseNameFromFile(fscan);
			if (!errorLoading) {
				populateModulesFromFile(fscan);
			}
		} catch (Exception all) {
			System.out.println("could not open file" + fileName);
		}
	}

	public void createCourseFromRawFilePath(String fileName) {
		// clear current course fields
		clearFields();
		this.fileName = fileName;
		// try catch block for opening a scanner on file
		try (Scanner fscan = new Scanner(Paths.get(fileName));) {

			// if file successfully opens, process it into a course structure
			setCourseNameFromFile(fscan);
			if (!errorLoading) {
				populateModulesFromFile(fscan);
			}
		} catch (Exception all) {
			System.out.println("could not open file" + fileName);
		}
	}

	public void setCourseNameFromFile(Scanner fscan) {
		// find course name
		while (fscan.hasNext()) {
			String currentLine = fscan.nextLine();
			String newCourseName;
			// ensure line is valid
			if (currentLine != null) {
				// check if line is a file (char 0 being tab designates file)
				if (currentLine.charAt(0) != '\t') {

					int[] startEnd = QuickRegex.expressionPresentStartEnd(currentLine, "course: ");
					// index 0 = start of expression (-1 represents error)
					// index 1 = end of expression (-1 represents error)

					if (startEnd[0] == 0 && startEnd[1] < currentLine.length()) {
						// found course
						newCourseName = currentLine.substring(startEnd[1]);
						// check if course has a name greater than 0
						if (newCourseName.length() > 0) {
							courseName = newCourseName;
							return; // returns if success
						}
					}
				}
			}
		}
		// did not return, file load must have failed
		errorLoading = true;
	}

	public void populateModulesFromFile(Scanner fscan) {
		// state variables
		CourseModule previousModule = null;

		// scanning loop
		while (fscan.hasNext()) {
			String currentLine = fscan.nextLine();
			// search for module name
			if (currentLine.charAt(0) == 'm') {
				// check if module keyword
				int[] startEnd = QuickRegex.expressionPresentStartEnd(currentLine, "module: ");
				// index 0 = start of expression (-1 represents error)
				// index 1 = end of expression (-1 represents error)

				// check if module keyword found, and check if line has
				// characters left for file name
				if (startEnd[0] == 0 && (startEnd[1] < currentLine.length())) {
					// remove word "module: " and set current line to module
					// name
					currentLine = currentLine.substring(startEnd[1]);

					// create a new module (and set it to previous module)
					previousModule = new CourseModule(currentLine);

					// add the newly created module to the module list of this
					// course
					modules.add(previousModule);
				}
			}
			// add files until module or end of file encountered
			else if (currentLine.charAt(0) == '\t' && previousModule != null) {
				// '\t' designate file name
				if (currentLine.length() > 1) { // check if file contains more
												// than a '\t'
					// get filepath
					currentLine = currentLine.substring(1);

					// add the corrected filepath ('\t' removed) to the module's
					// file list.
					previousModule.addFile(currentLine);
				}
			}
		}
	}

	public String getCourseName() {
		return courseName;
	}

	public String toString() {
		StringBuilder sbuild = new StringBuilder();
		// add course name
		sbuild.append("course name: ");
		sbuild.append(courseName);
		sbuild.append('\n');

		for (int i = 0; i < modules.size(); ++i) {
			sbuild.append("module name: ");
			CourseModule currentModule = modules.get(i);
			sbuild.append(currentModule.getModuleName());
			sbuild.append('\n');

			for (int j = 0; j < currentModule.numberOfFiles(); ++j) {
				sbuild.append(currentModule.getFileNameAt(j));
				sbuild.append('\n');
			}
		}
		return sbuild.toString();
	}

	@SuppressWarnings("unused")
	private void updateFileName() {
		fileName = CourseTopic.convertClassNameToFileName(courseName);
	}

	public static String convertClassNameToFileName(String rawClassName) {
		rawClassName = "courses\\" + rawClassName + "-CourseFile.txt";
		return rawClassName;
	}

	public static void main(String[] args) {
		CourseTopic testTopic = new CourseTopic();
		testTopic.makeNewCourseFile("testcreate");
		testTopic.makeNewModule("test1");
		testTopic.makeNewFile(0, "chapter1");

		testTopic.createCourseFromFile("testcreate");
		System.out.println(testTopic.toString());

	}

	public boolean getLoadSuccess() {
		return !errorLoading;
	}

	public int numModules() {
		if (modules != null) {
			return modules.size();
		}
		return 0;
	}

	public String getModuleStrAt(int i) {
		if (modules != null && (modules.size() > 0)) {
			if (((modules.size() - 1) >= i)) {
				String result = modules.get(i).getModuleName();
				return result; // added for debugger
			}
		}
		return null;
	}

	/**
	 * Obtains a file list for a designated module at passed index. Returns null
	 * if there is no file list or the index is not a valid index.
	 * 
	 * @param index
	 *            the index of the module (should correspond to the index on the
	 *            gui.
	 * @return returns the file list of the module at the specified index, or
	 *         returns null if there is no file list or the index is not valid .
	 */
	public ArrayList<String> getFileListFromModuleAtIndex(int index) {
		ArrayList<String> fileList = null;
		CourseModule workingModule = null;
		if (index < modules.size() && index >= 0 && modules != null) {
			workingModule = modules.get(index);
		}
		if (workingModule != null) {
			fileList = workingModule.getCopyOfFileList();
		}
		return fileList;
	}

	/* FILE STRUCTURE */
	// course: class X
	// module: 1
	// \tXclass/file1.txt
	// \tXclass/file2.txt
	// module: 2
	// \tXclass/file3.txt
	// module: 3
	// \txclass/file4.txt

}
