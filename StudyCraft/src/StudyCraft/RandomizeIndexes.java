package StudyCraft;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class RandomizeIndexes {
	protected int[] scrambleArray;
	protected Queue<Integer> outque = new LinkedList<>();
	protected boolean returnFlag = false;

	public void create(int firstIndex, int secondIndex) {
		// swap mixxed up indeces
		if (firstIndex > secondIndex) {
			int temp = firstIndex;
			firstIndex = secondIndex;
			secondIndex = temp;
		}

		// set the size
		int size = 0;
		size = (secondIndex - firstIndex + 1);

		// create array
		scrambleArray = new int[size]; // +1 for flag (-1)

		// push all integers onto right stack
		for (int i = firstIndex; i <= secondIndex; ++i) {
			scrambleArray[i] = i;
		}

		// randomize positions
		for (int i = firstIndex; i <= secondIndex; ++i) {
			Random rand = new Random();
			int swapIndex = rand.nextInt(size); // exclusive last arg
			swap(scrambleArray, i, swapIndex);
		}

		for (int i : scrambleArray)
			outque.add(i);
	}

	public int getRandomNext() {
		if (outque.size() > 0) {
			if (outque.size() == 1) {
				returnFlag = true;
				return outque.poll();
			}
			return outque.poll();
		} else if (returnFlag) {
			returnFlag = false;
			return -1;
		} else {
			//this will return if user continues to get random even after stop flag is processed 
			return -1;
		}
	}

	public boolean hasNext() {
		if (outque.size() > 0) {
			return true;
		} else if (returnFlag) {
			returnFlag = false;
			return true;
		} else {
			return false;
		}
	}

	private void swap(int[] a, int first, int second) {
		int temp = a[first];
		a[first] = a[second];
		a[second] = temp;
	}

	public static void main(String[] args) {
		RandomizeIndexes scramble = new RandomizeIndexes();
		scramble.create(0, 10);
		while (scramble.hasNext()) {
			System.out.println(scramble.getRandomNext());
		}

	}
}
