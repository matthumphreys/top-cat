package topcat;

import static org.junit.Assert.*;

import org.junit.Test;

public class CategoryTest {
	private static final String CAT_NAME = "sport";

	@Test
	public void testSoftProb_oneEvent() {
		final int DEFAULT_PROB = 30;
		Category stat = null;
		
		stat = new Category(CAT_NAME, true);
		assertEquals("hitCount", 1, stat.getHitCount());
		assertEquals("eventCount", 1, stat.getEventCount());
		assertEquals("hardProb", 100, stat.getHardProbability(DEFAULT_PROB));
		assertEquals("anchoredProb", 58, stat.getAnchoredProbability(DEFAULT_PROB));
		
		stat = new Category(CAT_NAME, false);
		assertEquals("hitCount", 0, stat.getHitCount());
		assertEquals("eventCount", 1, stat.getEventCount());
		assertEquals("hardProb", 0, stat.getHardProbability(DEFAULT_PROB));
		assertEquals("anchoredProb", 18, stat.getAnchoredProbability(DEFAULT_PROB));
	}
	
	@Test
	public void testSoftProb_twoEvents() {
		final int DEFAULT_PROB = 30;
		Category stat = null;
		
		stat = new Category(CAT_NAME, true);
		stat.addEvent(false);
		assertHitAndEvents(stat, 1, 2);
		assertEquals("hardProb", 50, stat.getHardProbability(DEFAULT_PROB));
		assertEquals("anchoredProb", 43, stat.getAnchoredProbability(DEFAULT_PROB));
		
		stat = new Category(CAT_NAME, true);
		stat.addEvent(true);
		assertHitAndEvents(stat, 2, 2);
		assertEquals("hardProb", 100, stat.getHardProbability(DEFAULT_PROB));
		assertEquals("anchoredProb", 75, stat.getAnchoredProbability(DEFAULT_PROB));
		
		stat = new Category(CAT_NAME, false);
		stat.addEvent(false);
		assertHitAndEvents(stat, 0, 2);
		assertEquals("hardProb", 0, stat.getHardProbability(DEFAULT_PROB));
		assertEquals("anchoredProb", 11, stat.getAnchoredProbability(DEFAULT_PROB));
	}
	
	@Test
	public void testSoftProb_threeEvents() {
		final int DEFAULT_PROB = 30;
		Category stat = null;
		
		stat = new Category(CAT_NAME, false);
		stat.addEvent(false).addEvent(false);
		assertHitAndEvents(stat, 0, 3);
		assertEquals("hardProb", 0, stat.getHardProbability(DEFAULT_PROB));
		assertEquals("anchoredProb", 6, stat.getAnchoredProbability(DEFAULT_PROB));
		
		stat = new Category(CAT_NAME, true);
		stat.addEvent(true).addEvent(false);
		assertHitAndEvents(stat, 2, 3);
		assertEquals("hardProb", 67, stat.getHardProbability(DEFAULT_PROB));
		assertEquals("anchoredProb", 59, stat.getAnchoredProbability(DEFAULT_PROB));
	}
	
	@Test
	public void testSoftProb_fiveEvents() {
		final int DEFAULT_PROB = 30;
		Category stat = null;
		
		stat = new Category(CAT_NAME, false);
		stat.addEvent(false).addEvent(false).addEvent(false).addEvent(false);
		assertHitAndEvents(stat, 0, 5);
		assertEquals("hardProb", 0, stat.getHardProbability(DEFAULT_PROB));
		assertEquals("anchoredProb", 2, stat.getAnchoredProbability(DEFAULT_PROB));
	}
	
	private void assertHitAndEvents(Category stat, int hitCount, int eventCount) {
		assertEquals("hits", hitCount, stat.getHitCount());
		assertEquals("events", eventCount, stat.getEventCount());
	}

}
