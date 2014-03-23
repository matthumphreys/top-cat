package topcat;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

public class CategoryManagerTest {
	private final String CAT_NEWS	= "news";
	private final String CAT_SPORT	= "sport";

	@Test
	public void example() {
		int defaultProb = 30;
		CategoryManager mngr = new CategoryManager(defaultProb);
		
		// User reads a "news" article
		mngr.addCategoryEvent("news", true);
		// User skips a "sport" article, then reads one
		mngr.addCategoryEvent("sport", false);
		mngr.addCategoryEvent("sport", true);
		
		List<Category> cats = mngr.getTopCategories();
		assertEquals("Should have two elements", 2, cats.size());
		assertEquals("1st should be news", CAT_NEWS, cats.get(0).getName());
		assertEquals("2nd should be sport", CAT_SPORT, cats.get(1).getName());
		int diff = cats.get(0).getSoftProbability(defaultProb) - cats.get(1).getSoftProbability(defaultProb);
		assertTrue("News should have higher probability than sport", diff > 0);
	}
	
	@Test
	public void moreThoroughTest() {
		CategoryManager mngr = new CategoryManager();
		List<Category> cats = null;
		
		cats = mngr.getTopCategories();
		assertNotNull(cats);
		assertEquals("cats.size", 0, cats.size());
		
		// User skips a news article
		mngr.addCategoryEvent(CAT_NEWS, false);
		
		cats = mngr.getTopCategories();
		assertEquals("cats.size", 1, cats.size());
		assertEquals("cats[0].name", CAT_NEWS, cats.get(0).getName());
		
		// User reads a sport article
		mngr.addCategoryEvent(CAT_SPORT, true);
		
		cats = mngr.getTopCategories();
		assertEquals("cats.size", 2, cats.size());
		assertEquals("cats[0].name", CAT_SPORT, cats.get(0).getName());
		assertEquals("cats[1].name", CAT_NEWS, cats.get(1).getName());
	}	

}
