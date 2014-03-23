package topcat;

import java.util.Comparator;

public class CategoryComparator implements Comparator<Category> {
	private int anchorProb;
	
	public CategoryComparator(int anchorProb) {
		this.anchorProb = anchorProb;
	}
	
	@Override
	public int compare(Category cat1, Category cat2) {
		// XXX: Check for nulls
		return cat2.getAnchoredProbability(anchorProb) - cat1.getAnchoredProbability(anchorProb);
	}

}
