package net.codjo.test.release.util.comparisonstrategy;
import net.codjo.test.common.FileComparator;
import java.io.File;
import java.io.IOException;
/**
 *
 */

public class PlainFileComparisonStrategy implements ComparisonStrategy {
	private File expectedFile;
	private File actualFile;
	private boolean order;


	public PlainFileComparisonStrategy(File expectedFile, File actualFile, boolean order) {
		this.expectedFile = expectedFile;
		this.actualFile = actualFile;
		this.order = order;
	}


	public boolean compare() throws IOException {
		FileComparator comparator = new FileComparator("$");
		if (!order) {
			return comparator.equals(expectedFile, actualFile);
		}
		else {
			return comparator.equalsNotOrdered(expectedFile, actualFile);
		}
	}
}