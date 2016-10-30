package StudyCraft;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;

/**
 * This class represents a study module; which is defined as a term, its
 * definitions, and submodules. See the example below before looking at the
 * following abstract definition: There is only one term per module; this term
 * may have many definitions. There may also be submodules for a term. A
 * submodule is in some way related the term.
 * 
 * For example, the term: cat. Cat has two definitions: 1. an animal 2. a mammal
 * It has submodules: Term: fur, Definition: made of Keratin; Term: teeth,
 * Definition: coated with enamel.
 * 
 * The class behaves recursively in that submodules are also study modules.
 * 
 * @author Matt Stone
 * @version 5/16/2016
 * @field tabDepth: The # of tabs before the items.
 * 
 * @field nextTerm -signifies that the next string from quiz is the term
 * @field nextDefinition - signifies that the next string returned from quiz is
 *        a definition
 * @field nextSubModules - signifies that next string returned is from a sub
 *        module
 * @field nextModuleComplete - signifies that all strings from module and
 *        submodules have been returned
 *
 */
public class StudyModule {
	// Module Related Variables
	protected int tabDepth = 0;
	protected int lineNumber = 0;
	protected String term;
	protected ArrayList<String> definitions = new ArrayList<>();
	protected ArrayList<StudyModule> subModules = new ArrayList<>();
	protected TokenFromLineBuilder buildState = new TokenFromLineBuilder();
	protected boolean moduleSortedAsSubModule = false;
	protected StudyModule parentModule = null;

	// Quiz Related Variables
	protected QuizState quizState = new QuizState(this);
	protected StringBuilder quizStringState = new StringBuilder();
	protected int quizSubModIter = 0;
	protected int quizDefIter = 0;
	protected boolean nextTerm = false;
	protected boolean nextDefinition = false;
	protected boolean nextSubModules = false;
	protected boolean nextModuleComplete = false;
	protected boolean randomMode = false;
	protected boolean firstSubModule = true;
	protected RandomizeIndexes scrambler;

	/**
	 * The public constructor. This creates a study module that will contain all
	 * of the terms and definitions as a submodules. Essentially, this is the
	 * base of entire study module. It will have a tabDepth of -1 to represent
	 * this module as the base module.
	 */
	public StudyModule() {
		this(-1);
	}

	/**
	 * Constructor that creates a new study module at a given tab depth.
	 * 
	 * @param tabDepth
	 */
	private StudyModule(int tabDepth) {
		this.tabDepth = tabDepth;
	}

	public void createModuleFromFile(String filename) throws IOException {
		ArrayList<String> lines = loadFile(filename);
		Modularize(lines); // O(n)
		sortSubModules();
		// setSubModuleParents(this, 0); // O(n)
	}

	/**
	 * The initialize building of the StudyModules does not assign submodules
	 * for modules. This method iterators through all the StudyModules contained
	 * in the base module. It analyzes the tab depth of each of these lines and
	 * assigns subModules appropriately.
	 * 
	 * Submodules are useful in random quizzing. This allows for random quizzing
	 * to quiz on a basis of modules, this keeps related terms (e.g. terms that
	 * build upon one another) associated which can retain context that might be
	 * necessary to answer a definition.
	 * 
	 * For example, fur may be a quizzed term, but without the context of dog,
	 * or cat, the correct answer cannot be determined.
	 * 
	 * A submodule is composed of lines that have more leading tab characters
	 * than a line above it. See class description for a more comprehensive
	 * definition.
	 */
	private void setSubModuleParents() {
		Stack<StudyModule> parents = new Stack<>();
		parents.push(this);
		for (int i = 0; i < subModules.size(); ++i) {
			StudyModule current = subModules.get(i);

			// Current has more tabs than the parent
			if (current.tabDepth > parents.peek().tabDepth) {
				current.parentModule = parents.peek();
				if (current.parentModule.tabDepth > -1) {
					current.parentModule.subModules.add(current);
				}
				parents.push(current); // will be poped if equal
				continue;
			}
			// Current has less than (or equal) tabs of parent
			if (current.tabDepth <= parents.peek().tabDepth) {
				parents.pop();
				// Will cycle again on same module if not base
				if (current.tabDepth > -1) {
					--i;
					continue;
				}
			}
		}
	}

	public void setRandom(boolean randomQuiz) {
		this.randomMode = randomQuiz;
		for (int i = 0; i < subModules.size(); ++i) {
			subModules.get(i).setRandom(randomQuiz);
		}
	}

	private void removeInvalidChildren() {
		ArrayList<StudyModule> newFilteredSubmodules = new ArrayList<>();
		for (int i = 0; i < this.subModules.size(); ++i) {
			StudyModule current = subModules.get(i);
			if (current.parentModule == this) {
				newFilteredSubmodules.add(current);
			}
		}
		subModules = newFilteredSubmodules;
	}

	private void sortSubModules() {
		// setSubModuleParents(this, 0);
		setSubModuleParents();
		removeInvalidChildren();
	}

	private void Modularize(ArrayList<String> lines) {
		// Loop through all lines in file
		for (int i = 0; i < lines.size(); ++i) {
			// Set the string to be analyzed
			buildState.setTargetString(lines.get(i));

			// Determine number of tabs
			countTabs();

			// Find hyphens
			findHyphens();

			// Build tokens (term, definitions, etc)
			buildStudyModule();

			// Reset the state for new loop
			buildState.reset();
		}
		// Save number of lines in file for progress
	}

	/**
	 * This method use the buildState data structure to generate tokens for a
	 * term and its definitions. These are contained in a new studyModule
	 * object.
	 * 
	 * @warning StudyModule is a recursive class in that it contains objects of
	 *          its own type. This method should only be called at the base
	 *          object.
	 * 
	 * @disclaimer This class works by creating a single base studyModule (tab
	 *             depth -1) and adds every line in the file as submodule. These
	 *             submodules are then analyzed based on their tab number and
	 *             assigned to the appropriate submodules. This could have been
	 *             done in the initial loop that created each line into a
	 *             studyModule; which would have been O(n), but I chose to do
	 *             sorting later. This doesn't affect the bigOh. This way gives
	 *             the 2 loops O(2n) which is overall O(n). Since this is a
	 *             small program, there other implementation may provide a
	 *             significant boost in performance; but it would be in
	 *             negligible considering the scope of this project.
	 * 
	 * 
	 * @precondition The current buildState is for the passed string and the
	 */
	private void buildStudyModule() {
		boolean firstIteration = true;
		StudyModule currentLine = new StudyModule(buildState.getTabNumber());

		for (String str : buildState) {
			// First iteration is the term, and is treated special.
			if (firstIteration) {
				currentLine.term = str;
				firstIteration = false;
				// Remaining iterations are definitions
			} else {
				currentLine.definitions.add(str);
			}
		}
		this.subModules.add(currentLine);
	}

	/**
	 * Finds all the hyphens and generates tokens based on these hyphens.
	 * 
	 * @param string
	 */
	private void findHyphens() {
		String targetString = buildState.getTargetString();

		// Tab number is equal to the index of first character.
		buildState.setFirstIndex(buildState.getTabNumber());

		// Loop through hyphens
		for (int i = buildState.getFirstIndex(); i < targetString.length(); ++i) {
			// TODO create bypass for /-
			if (targetString.charAt(i) == '-' || targetString.charAt(i) == '–') {
				// Finish current token boundary; i.e. token before hyphen
				int endingIndex = getCurrentTokenEndIndex(i, targetString);

				// Will skip this hyphen and break token at next
				if (buildState.getSkipNextHyphen()) {
					buildState.setSkipNextHyphen(false);
					continue;
				}
				buildState.setSecondIndex(endingIndex);
				buildState.tokenFinished();

				// Start new token
				int newStartIndex = getNextTokenStartIndex(i, targetString.length());
				buildState.setFirstIndex(newStartIndex);
			}
		}
		// complete the last token
		buildState.setSecondIndex(targetString.length() - 1);

	}

	/**
	 * Returns the index before hyphen index if it is not out of bounds or
	 * invalid (ie negatives); method will also flag to skip hyphens that do not
	 * have definitions.
	 * 
	 * @param hyphenIndex
	 *            the index at which the hyphen was found
	 * @return the index before the hyphen, or the index of the hyphen if before
	 *         the hyphen is out of bounds or invalid.
	 */
	private int getCurrentTokenEndIndex(int hyphenIndex, String string) {
		// Get index before the hyphen
		if (hyphenIndex > 0 && string.charAt(hyphenIndex - 1) != '\t') {
			--hyphenIndex;
		}
		// If the previous character is a tab
		else if (hyphenIndex > 0) {
			buildState.setSkipNextHyphen(true);
		}
		return hyphenIndex;
	}

	private int getNextTokenStartIndex(int hyphenIndex, int stringLength) {
		// Get index after hyphen
		if (hyphenIndex < stringLength - 1) {
			++hyphenIndex;
		}
		return hyphenIndex;
	}

	/**
	 * Counts the tabs of the string contained in the buildState object.
	 */
	private void countTabs() {
		String line = buildState.getTargetString();

		// scan string characters
		for (int i = 0; i < line.length(); ++i) {
			if (line.charAt(i) == '\t') {
				buildState.incrementTabNumber();
			} else {
				return;
			}
		}
		return;
	}

	/**
	 * Loads the lines of a file into an array list of Strings.
	 * 
	 * @param filepath
	 *            is the path of the file to be loaded
	 * @return returns each line as an entry in an arrayList to be parsed
	 * @throws IOException
	 *             Thrown if the file cannot be loaded.
	 */
	private ArrayList<String> loadFile(String filepath) throws IOException {
		ArrayList<String> lineList = new ArrayList<>();
		Scanner fscan = new Scanner(Paths.get(filepath));

		// Load each line into a list
		while (fscan.hasNext()) {
			lineList.add(fscan.nextLine());
		}

		lineNumber = lineList.size();

		fscan.close();
		return lineList;
	}

	public void printAll() {
		for (int i = 0; i < tabDepth; ++i) {
			System.out.print('\t');
		}
		if (term != null) {
			System.out.print(term);
		}
		for (int i = 0; i < definitions.size(); ++i) {
			System.out.print(" - " + definitions.get(i) + " ");
		}
		System.out.println("");
		for (int i = 0; i < subModules.size(); ++i) {
			subModules.get(i).printAll();
		}
	}

	/**
	 * Main method for testing purposes.
	 * 
	 * @param args
	 *            command line arguments
	 */
	public static void main(String[] args) {
		try {
			StudyModule quiz = new StudyModule();
			quiz.createModuleFromFile("StudyCraftTestFile.txt");
			quiz.setRandom(true);
			quiz.printAll();
			System.out.println("\n\n\n\n");

			quiz.consoleQuiz();

		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	public void consoleQuiz() {
		StringBuilder quizsb = new StringBuilder();
		Scanner quizScanner = new Scanner(System.in);
		startQuiz();
		// Get first term non-null term
		quizsb.append(quizNextStr(true));
		System.out.println(quizsb);
		while (!quizFinished()) {
			quizsb.append(quizNextStr(true));
			System.out.println(quizsb);
			// ask for a return
			// quizScanner.nextLine();
		}
		// TODO ensure that the quiz can be re-run
		quizScanner.close();
		resetQuiz();
	}

	private String getTabsAsStr(StudyModule mod) {
		StringBuilder sb = new StringBuilder("");
		for (int i = 0; i < mod.tabDepth; ++i) {
			sb.append('\t');
		}
		return sb.toString();
	}

	/**
	 * Conceptual testing method. This will not look correct because scanner
	 * creates newline in console. A better implementation will use
	 * stringBuilder and reprint the entire string after answer is submitted,
	 * incrementally.
	 * 
	 * @param input
	 */
	public void consoleQuizConcept(Scanner input) {
		for (int i = 0; i < subModules.size(); ++i) {
			StudyModule currentModule = subModules.get(i);
			int defSize = currentModule.definitions.size();

			// Print term
			System.out.print(getTabsAsStr(currentModule) + currentModule.term);
			if (defSize > 0) {
				System.out.print(" - ");
			}

			// Ask for definitions, then print definitions
			for (int j = 0; j < defSize; ++j) {
				input.nextLine();
				System.out.print(currentModule.definitions.get(j));
				// print hyphen if there are more definitions
				if (j < defSize - 1) {
					System.out.print(" - ");
				}
				++j;
			}

			// Create a new line after this modules line is complete.
			if (defSize == 0) {
				// asks for a new line if there were no definitions
				input.nextLine();
			} else {
				System.out.println("");
			}

			// Quiz on submodules
			currentModule.consoleQuizConcept(input);
		}
	}

	public void resetQuiz() {
		quizSubModIter = 0;
		quizDefIter = 0;
		quizStringState = new StringBuilder();
		nextTerm = true;
		nextDefinition = false;
		nextSubModules = false;
		nextModuleComplete = false;

		for (int i = 0; i < subModules.size(); ++i) {
			StudyModule current = subModules.get(i);
			current.resetQuiz();
		}
	}

	public void startQuiz() {
		resetQuiz();
	}

	public String quizNextStr() {
		// return quizNextStr_NullFilter(false);
		return quizNextStr(false);
	}

	/**
	 * This method returns the next token (string) for quizzing using the
	 * modules.
	 * 
	 * @disclaimer this method is exceptionally complicated to debug because of
	 *             the amount of recursion present; there may still be bugs
	 *             present. It will probably become deprecated via a less
	 *             recursive alternative implementation.
	 * 
	 * @param returnTabs
	 *            - signals if the tabs should be returned with term
	 * @return the next string from the quiz
	 */
	public String quizNextStr(boolean returnTabs) {
		String result;
		do {
			// Find next token that isn't null.
			result = quizNextStrWithNull(returnTabs);
		} while (result == null || result.equals("null") || result.equals("\nnull"));// ||
																						// result.equals(""));
		// if (term==null && result.equals(""))
		// result = quizNextStr()

		return result;
	}

	/**
	 * Returns a string representation of the next token. The state of the quiz
	 * is updated in every branch's method that feeds the return statement.
	 * 
	 * This method behaves like the .next() method of the iterator interface.
	 * 
	 * @param returnTabs
	 *            - determines whether tabs should be included before the "term"
	 *            string
	 * @return the string for the next portion of the quiz.
	 * 
	 *         TODO: START:the method has issues when a module only contains
	 *         terms and it completes the module (between test hyphen 3 and end)
	 */
	public String quizNextStrWithNull(boolean returnTabs) {
		if (nextTerm) {
			return returnTerm(returnTabs);
		} else if (nextDefinition) {
			return returnDefinition(returnTabs);
		} else if (nextSubModules) {
			if (!randomMode) {
				return returnSubModules(returnTabs);
			} else {
				return returnSubModulesRandom(returnTabs);
			}
		} else if (nextModuleComplete) {
			return "";
		} else {
			throw new RuntimeException("Error in quizNextStr - invalid state");
		}
	}

	private String returnTerm(boolean returnTabs) {
		analyzeQuizState();
		if (returnTabs) {
			return "\n" + getTabsAsStr(this) + term;
		} else {
			return "\n" + term;
		}
	}

	private String returnDefinition(boolean returnTabs) {
		String result = null;
		if (definitions.size() > 0) {
			result = definitions.get(quizDefIter);
		}
		++quizDefIter;
		analyzeQuizState();
		return result;
	}

	private String returnSubModules(boolean returnTabs) {
		String result = null;
		if (subModules.size() > 0) {
			StudyModule currentSubModule = subModules.get(quizSubModIter);
			// If current module is complete, go to next subModule
			if (currentSubModule.nextModuleComplete) {
				++quizSubModIter;
				if (quizSubModIter < subModules.size())
					currentSubModule = subModules.get(quizSubModIter);
			}
			result = currentSubModule.quizNextStr(returnTabs);
		}
		analyzeQuizState();
		// Signals end of definitions of the given sub module
		if (result != null && result.equals("") && quizSubModIter < subModules.size() - 1) {
			// this will rerun the method, if it is done it will return null;
			result = returnSubModules(returnTabs);
		}
		return result;
	}

	private String returnSubModulesRandom(boolean returnTabs) {
		String result = null;
		if (subModules.size() > 0) {
			if (firstSubModule) {
				firstSubModule = false;
				scrambler = new RandomizeIndexes();
				scrambler.create(quizSubModIter, subModules.size() - 1);
				if (scrambler.hasNext()) {
					quizSubModIter = scrambler.getRandomNext();
				}
			}
			if (quizSubModIter != -1) {
				StudyModule currentSubModule = subModules.get(quizSubModIter);
				// If current module is complete, go to next subModule
				if (currentSubModule.nextModuleComplete && scrambler.hasNext()) {
					quizSubModIter = scrambler.getRandomNext();
					if (quizSubModIter != -1) {
						currentSubModule = subModules.get(quizSubModIter);
					}
				}
				// TODO this may cause problems being outside if statement
				result = currentSubModule.quizNextStr(returnTabs);
			}
		}
		analyzeQuizStateRandom();
		// Signals end of definitions of the given sub module
		if (result != null && result.equals("") && quizSubModIter != -1) {
			// this will rerun the method, if it is done it will return null;
			result = returnSubModulesRandom(returnTabs);
		}
		return result;
	}

	// TODO: re-organize if statements so that last situation is checked first;
	// this is the most probable to be called
	private void analyzeQuizState() {
		if (nextTerm) {
			nextTerm = false;
			nextDefinition = true;
			// check if there is definitions
			analyzeQuizState();
			return;
		} else if (nextDefinition) {
			// Tests if more definitions are remaining
			if (quizDefIter < definitions.size()) {
				return;
			} else {
				nextDefinition = false;
				nextSubModules = true;
				return;
			}
		} else if (nextSubModules) {
			if (quizSubModIter < this.subModules.size()) {
				return;
			} else {
				nextSubModules = false;
				nextModuleComplete = true;
				return;
			}
		}
	}

	// this is the most probable to be called
	private void analyzeQuizStateRandom() {
		if (nextTerm) {
			nextTerm = false;
			nextDefinition = true;
			// check if there is definitions
			analyzeQuizState();
			return;
		} else if (nextDefinition) {
			// Tests if more definitions are remaining
			if (quizDefIter < definitions.size()) {
				return;
			} else {
				nextDefinition = false;
				nextSubModules = true;
				return;
			}
		} else if (nextSubModules) {
			if (quizSubModIter != -1 && subModules.size() > 0) {
				return;
			} else {
				nextSubModules = false;
				nextModuleComplete = true;
				return;
			}
		}
	}

	public boolean quizFinished() {
		return nextModuleComplete;
	}

	public int getLineNumber() {
		return lineNumber;
	}

}
