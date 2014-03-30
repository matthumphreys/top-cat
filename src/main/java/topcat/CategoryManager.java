package topcat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** NOT designed for access by multiple concurrent threads. */
public class CategoryManager {
	private Map<String, Category> catStatsMap = new HashMap<String, Category>();
	//private int defaultProb;
	private int overallHardProb;
	private int overallSoftProb;
	
	/** Default prob = 50 */
	public CategoryManager() {
		this(50);
	}
	
	/** 
	 * @param defaultProb just used to cal initial overallSoftProb (when sample size is 0)
	 */
	public CategoryManager(int defaultProb) {
		//this.defaultProb = defaultProb;
		overallHardProb = defaultProb;
		overallSoftProb = getSoftProbability(defaultProb, 0);
	}
	
	/**
	 * @param category "Sport", for example
	 * @param isHit true for a "hit" (e.g. reading an article), 
	 * 		false for a "miss" (e.g. skipping an article)
	 */
	public void addCategoryEvent(String categoryName, boolean isHit) {
		if (catStatsMap.containsKey(categoryName)) {
			Category catStats = catStatsMap.get(categoryName);
			catStats.addEvent(isHit);
		} else {
			catStatsMap.put(categoryName, new Category(categoryName, isHit));
		}
	}
	
	/**
	 * @return Hottest first.
	 */
	public List<Category> getCategories() {
		int overallHitCount = 0;
		int overallEventCount = 0;
		List<Category> catStats = new ArrayList<Category>();
		for (Category stats : catStatsMap.values()) {
			overallHitCount = overallHitCount + stats.getHitCount();
			overallEventCount = overallEventCount + stats.getEventCount();
			catStats.add(stats);
		}
		overallHardProb = Category.getHardProbability(overallHitCount, overallEventCount);
		overallSoftProb = getSoftProbability(overallHardProb, overallEventCount);
		
		Collections.sort(catStats, new CategoryComparator(overallSoftProb));
		return catStats;
	}
	
	/**
	 * Uses soft probabilities
	 * @param defaultProb used for calculating "soft" probabilities
	 * @return Only categories with a hit-rate higher than the "global" hit-rate
	 */
	public List<Category> getHotCategories(int defaultProb) {
		List<Category> all = getCategories();
		List<Category> hot = new ArrayList<Category>();
		for (Category cat : all) {
			if (cat.getSoftProbability(defaultProb) > overallSoftProb) {
				hot.add(cat);
			}
		}
		return hot;
	}
	
	/**
	 * Uses hard probabilities
	 * @return Only categories with a hit-rate higher than the defaultProb parameter
	 */
	public List<Category> getCategoriesHotterThan(int threshold) {
		List<Category> all = getCategories();
		List<Category> hot = new ArrayList<Category>();
		for (Category cat : all) {
			if (cat.getHardProbability(threshold) > threshold) {
				hot.add(cat);
			}
		}
		return hot;
	}
	
	public Category getStats(String category) {
		return catStatsMap.get(category);
	}
	
//	public int getHardProbability(String category) {
//		CategoryStats stat = catStatsMap.get(category);
//		return (stat == null) ? defaultProb : stat.getHardProbability(overallProb);
//	}
//	
//	public int getSoftProbability(String category) {
//		CategoryStats stat = catStatsMap.get(category);
//		return (stat == null) ? defaultProb : stat.getSoftProbability(overallProb);
//	}
//	
//	public int getAnchoredProbability(String category) {
//		CategoryStats stat = catStatsMap.get(category);
//		return (stat == null) ? defaultProb : stat.getAnchoredProbability(overallProb);
//	}
	
	/**
	 * For small samples, probabilities are moved away from extreme values. 
	 * Useful for "global" probabilities.
	 * @param defaultProb prob returned if eventCount == 0 
	 */
	static public int getSoftProbability(int hardProb, int numEvents) {
		double cushionFactor = Category.getCushionFactor(numEvents);
		double result;
		
		if (hardProb < 50) {
			result = 50 - ( (50 - hardProb) * (1 - cushionFactor) );
		} else {
			result = hardProb - ( (hardProb - 50) * (cushionFactor) );
		}
		return (int) Math.round(result);
	}
}
