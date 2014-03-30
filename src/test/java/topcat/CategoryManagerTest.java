package topcat;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

public class CategoryManagerTest {
	private final String CAT_NEWS	= "news";
	private final String CAT_SPORT	= "sport";
	private final String CAT_ENTS	= "ents-tech";
	private final String CAT_LIFE_STYLE = "life-style";

	/**
	 * This shows how hit-rate is more meaningful than number of hits
	 */
	@Test
	public void simpleExample() {
		int defaultProb = 30;
		CategoryManager mngr = new CategoryManager(defaultProb);
		
		// User reads 1 out of 2 news articles (50%)
		mngr.addCategoryEvent("news", true);
		mngr.addCategoryEvent("news", false);
		// User reads 2 out of 5 sport articles (40%)
		mngr.addCategoryEvent("sport", true);
		mngr.addCategoryEvent("sport", true);
		mngr.addCategoryEvent("sport", false);
		mngr.addCategoryEvent("sport", false);
		mngr.addCategoryEvent("sport", false);
		
		// News should rank higher than sport, even though more sport articles were read
		List<Category> cats = mngr.getCategories();
		assertEquals("Should have two elements", 2, cats.size());
		assertEquals("1st should be news", CAT_NEWS, cats.get(0).getName());
		assertEquals("2nd should be sport", CAT_SPORT, cats.get(1).getName());
		int diff = cats.get(0).getSoftProbability(defaultProb) - cats.get(1).getSoftProbability(defaultProb);
		assertTrue("News should have higher probability than sport", diff > 0);
	}
	
	/**
	 * We can get categories exceeding a given hit-rate.
	 * <p>
	 * For example, you might consider hot as a hit-rate greater than 34%...
	 * reading 1 out of 3 articles (=33%) wouldn't qualify, but these would...
	 * <p>
	 * 1 out of 1 (100%)<br>
	 * 1 out of 2 (50%)<br>
	 * 2 out of 3 (66%)<br>
	 * 2 out of 5 (40%)<br>
	 * 7 out of 20 (35%)<br>
	 */
	@Test
	public void testGetCategoriesHotterThan() {
		int defaultProb = 30;	// Just used to calc initial soft prob
		int threshold = 34;
		CategoryManager mngr = new CategoryManager(defaultProb);
		
		mngr.addCategoryEvent(CAT_NEWS, true);
		mngr.addCategoryEvent(CAT_NEWS, false);
		mngr.addCategoryEvent(CAT_NEWS, false);
		mngr.addCategoryEvent(CAT_SPORT, true);
		mngr.addCategoryEvent(CAT_SPORT, true);
		mngr.addCategoryEvent(CAT_SPORT, false);
		mngr.addCategoryEvent("guilty-pleasures", false);
		mngr.addCategoryEvent(CAT_ENTS, true);
		mngr.addCategoryEvent(CAT_ENTS, false);
		mngr.addCategoryEvent(CAT_LIFE_STYLE, true);
		
		List<Category> cats = mngr.getCategoriesHotterThan(threshold);
		assertNotNull(cats);
		assertEquals("cats.size", 3, cats.size());
		assertEquals("cats[0].name", CAT_LIFE_STYLE, cats.get(0).getName());
		assertEquals("cats[1].name", CAT_SPORT, cats.get(1).getName());
		assertEquals("cats[2].name", CAT_ENTS, cats.get(2).getName());
	}
	
	/**
	 * We can get categories with a hit-rate greater than the user's overall hit-rate
	 */
	@Test
	public void testGetHotCategories() {
		int defaultProb = 34;
		CategoryManager mngr = new CategoryManager(defaultProb);
		
		mngr.addCategoryEvent(CAT_NEWS, true);
		mngr.addCategoryEvent(CAT_NEWS, false);
		mngr.addCategoryEvent(CAT_NEWS, false);
		mngr.addCategoryEvent(CAT_SPORT, true);
		mngr.addCategoryEvent(CAT_SPORT, true);
		mngr.addCategoryEvent(CAT_SPORT, false);
		mngr.addCategoryEvent("guilty-pleasures", false);
		mngr.addCategoryEvent(CAT_ENTS, true);
		mngr.addCategoryEvent(CAT_ENTS, false);
		mngr.addCategoryEvent(CAT_LIFE_STYLE, true);
		
		// Overall hit-rate is 50%
		// Only LIFE_STYLE and SPORT have a higher hit-rate
		
		List<Category> cats = mngr.getHotCategories(defaultProb);
		assertNotNull(cats);
		assertEquals("cats.size", 2, cats.size());
		assertEquals("cats[0].name", CAT_LIFE_STYLE, cats.get(0).getName());
		assertEquals("cats[1].name", CAT_SPORT, cats.get(1).getName());
	}
	
	@Test
	public void testOneEventAtATime() {
		CategoryManager mngr = new CategoryManager();
		List<Category> cats = null;
		
		cats = mngr.getCategories();
		assertNotNull(cats);
		assertEquals("cats.size", 0, cats.size());
		
		// User skips a news article
		mngr.addCategoryEvent(CAT_NEWS, false);
		
		cats = mngr.getCategories();
		assertEquals("cats.size", 1, cats.size());
		assertEquals("cats[0].name", CAT_NEWS, cats.get(0).getName());
		
		// User reads a sport article
		mngr.addCategoryEvent(CAT_SPORT, true);
		
		cats = mngr.getCategories();
		assertEquals("cats.size", 2, cats.size());
		assertEquals("cats[0].name", CAT_SPORT, cats.get(0).getName());
		assertEquals("cats[1].name", CAT_NEWS, cats.get(1).getName());
	}	

}
