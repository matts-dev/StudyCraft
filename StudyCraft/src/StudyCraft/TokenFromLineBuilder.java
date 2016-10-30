package StudyCraft;

import java.util.ArrayList;
import java.util.Iterator;

public class TokenFromLineBuilder implements Iterable<String> {
	// arguments, but used the saved string.
	private String targetString;
	private ArrayList<Integer> firstIndeces = new ArrayList<>(100);
	private ArrayList<Integer> secondIndeces = new ArrayList<>(100);
	private int currentTokenIndex = 0;
	private boolean skipNextHyphen = false;
	private int tabNumber = 0;

	public TokenFromLineBuilder() {
		reset();
	}

	/**
	 * Use when the string has been fully tokenized.
	 */
	public void reset() {
		firstIndeces = new ArrayList<>(10);
		// firstIndeces.add(0);
		secondIndeces = new ArrayList<>(10);
		// secondIndeces.add(0);
		tabNumber = 0;
		currentTokenIndex = 0;
		targetString = null;
	}

	/**
	 * This method sets the target string for the iterator.
	 * 
	 * @param workingString
	 *            the string to be analyzed
	 */
	public void setTargetString(String workingString) {
		targetString = workingString;
	}

	public void incrementFirstIndex() {
		firstIndeces.set(currentTokenIndex, firstIndeces.get(currentTokenIndex) + 1);
	}

	public void tokenFinished() {
		currentTokenIndex++;
	}

	public void setFirstIndex(int firstIndex) {
		// firstIndeces.set(currentTokenIndex, firstIndex);
		firstIndeces.add(firstIndex);

	}

	public void setSecondIndex(int secondIndex) {
		// secondIndeces.set(currentTokenIndex, secondIndex);
		secondIndeces.add(secondIndex);
	}

	public int getFirstIndex() {
		return firstIndeces.get(currentTokenIndex);
	}

	public int getSecondIndex() {
		return secondIndeces.get(currentTokenIndex);
	}

	public Iterator<String> iterator() {
		return new TokenIterator();
	}

	private class TokenIterator implements Iterator<String> {
		int currentToken = 0;

		// currentTokenIndex represents last created token
		public boolean hasNext() {
			return currentToken <= currentTokenIndex && targetString != null;
		}

		public String next() {
			String returnString = targetString.substring(firstIndeces.get(currentToken),
					secondIndeces.get(currentToken) + 1);
			++currentToken;
			return returnString;
		}

	}

	public void setSkipNextHyphen(boolean value) {
		skipNextHyphen = value;
	}

	/**
	 * Returns whether the the hyphen should be skipped, that is it returns
	 * whether the hyphen should be included in the token. This situation occurs
	 * when a hyphen comes before any definitions.
	 * 
	 * @return
	 */
	public boolean getSkipNextHyphen() {
		return skipNextHyphen;
	}

	public int getTabNumber() {
		return tabNumber;
	}

	public String getTargetString() {
		return targetString;
	}
	
	public void incrementTabNumber(){
		++tabNumber;
	}

}
