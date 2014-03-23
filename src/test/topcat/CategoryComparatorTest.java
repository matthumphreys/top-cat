package topcat;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

public class CategoryComparatorTest {
	private final String CAT_NEWS	= "news";
	private final String CAT_SPORT	= "sport";
	private final int ANCHOR_PROB = 30;

	@Test
	public void testSort() {
		List<Category> cats = new ArrayList<Category>();
		cats.add(new Category(CAT_NEWS, false));
		cats.add(new Category(CAT_SPORT, true));
		
		Collections.sort(cats, new CategoryComparator(ANCHOR_PROB));
		assertEquals("cats[0]", CAT_SPORT, cats.get(0).getName());
		assertEquals("cats[1]", CAT_NEWS, cats.get(1).getName());
	}
	
	@Test
	public void testSort_reverseInput() {
		List<Category> cats = new ArrayList<Category>();
		cats.add(new Category(CAT_SPORT, true));
		cats.add(new Category(CAT_NEWS, false));
		
		Collections.sort(cats, new CategoryComparator(ANCHOR_PROB));
		assertEquals("cats[0]", CAT_SPORT, cats.get(0).getName());
		assertEquals("cats[1]", CAT_NEWS, cats.get(1).getName());
	}

}
