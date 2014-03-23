package topcat;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

public class CategoryManagerTest {
	private final String CAT_NEWS	= "news";
	private final String CAT_SPORT	= "sport";

	@Test
	public void testTopCats() {
		CategoryManager mngr = new CategoryManager();
		List<Category> cats = null;
		
		cats = mngr.getTopCategories();
		assertNotNull(cats);
		assertEquals("cats.size", 0, cats.size());
		
		mngr.addCategoryEvent(CAT_NEWS, false);
		cats = mngr.getTopCategories();
		assertEquals("cats.size", 1, cats.size());
		assertEquals("cats[0].name", CAT_NEWS, cats.get(0).getName());
		
		mngr.addCategoryEvent(CAT_SPORT, true);
		cats = mngr.getTopCategories();
		assertEquals("cats.size", 2, cats.size());
		assertEquals("cats[0].name", CAT_SPORT, cats.get(0).getName());
		assertEquals("cats[1].name", CAT_NEWS, cats.get(1).getName());
	}	

}
