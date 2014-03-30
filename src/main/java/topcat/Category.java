package topcat;

public class Category {
	private static final double ROUGHLY_GOLDEN_RATIO = 0.6d;
	private String name;
	private int hitCount;
	private int eventCount;
	
	public Category(String name, boolean isHit) {
		this.name = name;
		addEvent(isHit);
	}
	
	public String getName() {
		return name;
	}
	
	public Category addEvent(boolean isHit) {
		eventCount++;
		if (isHit) {
			this.hitCount++;
		}
		return this;
	}
	
	public int getHitCount() {
		return hitCount;
	}
	
	public int getEventCount() {
		return eventCount;
	}
	
	/**
	 * @param defaultProb prob returned if eventCount == 0 
	 */
	public int getHardProbability(int defaultProb) {
		if (eventCount == 0) {
			return defaultProb;
		}
		double result = (hitCount * 100.0) / eventCount;
		return (int) Math.round(result);
	}
	
	static int getHardProbability(int hits, int events) {
		double result = (hits * 100.0) / events;
		return (int) Math.round(result);
	}
	
	/**
	 * For small samples, probabilities are pulled closer to <i>anchorProb</i>
	 * @param anchorProb
	 */
	public int getAnchoredProbability(int anchorProb) {
		double cushionFactor = getCushionFactor();
		final int hardProb = getHardProbability(anchorProb);
		double result;
		
		if (hardProb <= anchorProb) {
			// E.g. 0 + ((40 - 0) * 0.6) = 24
			result = ((anchorProb - hardProb) * cushionFactor);
		} else {
			// E.g. 40 + ( (100 - 40) * (1 - 0.6) )
			result = anchorProb + ( (hardProb - anchorProb) * (1 - cushionFactor) );
		}
		return (int) Math.round(result);
	}
	
	private double getCushionFactor() {
		return Math.pow(ROUGHLY_GOLDEN_RATIO, eventCount);
	}
	
	static double getCushionFactor(int count) {
		return Math.pow(ROUGHLY_GOLDEN_RATIO, count);
	}
	
	public String toString() {
		return ("Cat[" + name + " " + hitCount + "/" + eventCount + "]");
	}
}
