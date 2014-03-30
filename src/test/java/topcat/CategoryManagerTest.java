package topcat;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

public class CategoryManagerTest {
	private final String CAT_NEWS	= "news";
	private final String CAT_SPORT	= "sport";
	private final String CAT_ENTS	= "ents-tech";
	private final String CAT_LIFE_STYLE = "life-style";

	@Test
	public void simpleExample() {
		int defaultProb = 30;
		CategoryManager mngr = new CategoryManager(defaultProb);
		
		// User reads a "news" article
		mngr.addCategoryEvent("news", true);
		// User skips a "sport" article, then reads one
		mngr.addCategoryEvent("sport", false);
		mngr.addCategoryEvent("sport", true);
		
		List<Category> cats = mngr.getCategories();
		assertEquals("Should have two elements", 2, cats.size());
		assertEquals("1st should be news", CAT_NEWS, cats.get(0).getName());
		assertEquals("2nd should be sport", CAT_SPORT, cats.get(1).getName());
		int diff = cats.get(0).getSoftProbability(defaultProb) - cats.get(1).getSoftProbability(defaultProb);
		assertTrue("News should have higher probability than sport", diff > 0);
	}
	
	/**
	 * Threshold is 34%
	 * 
	 * Category is hot if user reads at least
	 * 1 out of 1 (100%)
	 * 1 out of 2 (50%)
	 * 2 out of 3 (66%)
	 * 2 out of 5 (40%)
	 * 7 out of 20 (35%)
	 * 
	 * Category is NOT hot if user reads 1/3 (33%)
	 */
	@Test
	public void getCategoriesHotterThan() {
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
	
	/** Categories hotter than the overall hit-rate */
	@Test
	public void getHotCategories() {
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
		// Overall hit-rate is 50%!
		
		List<Category> cats = mngr.getHotCategories(defaultProb);
		assertNotNull(cats);
		assertEquals("cats.size", 2, cats.size());
		assertEquals("cats[0].name", CAT_LIFE_STYLE, cats.get(0).getName());
		assertEquals("cats[1].name", CAT_SPORT, cats.get(1).getName());
	}
	
	@Test
	public void moreThoroughTest() {
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
