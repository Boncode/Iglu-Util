/*
 * Copyright 2011-2013 Jeroen Meetsma - IJsberg
 *
 * This file is part of Iglu.
 *
 * Iglu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Iglu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Iglu.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.ijsberg.iglu.util.formatting;

import org.junit.Test;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class PatternMatchingSupportTest {


	@Test
	public void testMatchWildcardExp() {

		assertTrue(PatternMatchingSupport.valueMatchesWildcardExpression("hopla.mask", "*"));
		assertFalse(PatternMatchingSupport.valueMatchesWildcardExpression("hopla.mask", ""));

		assertTrue(PatternMatchingSupport.valueMatchesWildcardExpression("hopla.mask", "*.mask"));
		assertFalse(PatternMatchingSupport.valueMatchesWildcardExpression("hopla.mas", "*.mask"));
		assertFalse(PatternMatchingSupport.valueMatchesWildcardExpression("hopla.mask", "*.mas"));
		assertTrue(PatternMatchingSupport.valueMatchesWildcardExpression("hopla.mask", "*.mas*"));

		assertTrue(PatternMatchingSupport.valueMatchesWildcardExpression("/dir/subdir/file.ext", "/dir/subdir/*"));
		assertTrue(PatternMatchingSupport.valueMatchesWildcardExpression("/dir/subdir/file.ext", "/dir/*dir/*.ext"));
		assertTrue(PatternMatchingSupport.valueMatchesWildcardExpression("/dir/subdir/file.ext", "*/file.ext"));

		assertFalse(PatternMatchingSupport.valueMatchesWildcardExpression("/dir/subdir/file.ext", "sub*/file.ext"));

		assertTrue(PatternMatchingSupport.valueMatchesWildcardExpression("/dir/subdir/file.ext", "/dir/s?bdir/file.ext"));
		assertTrue(PatternMatchingSupport.valueMatchesWildcardExpression("/dir/subdir/file.ext", "*/subdir/*"));


	}

	@Test
	public void testMatchWildcardExpOr() {
		assertTrue(PatternMatchingSupport.valueMatchesWildcardExpression("hopla.mask", "*.mask|*.test"));
		assertTrue(PatternMatchingSupport.valueMatchesWildcardExpression("hopla.test", "*.mask|*.test"));
		assertFalse(PatternMatchingSupport.valueMatchesWildcardExpression("hopla.test", "*.mask|*.hopla"));
	}
	
	
	@Test
	public void testMatchRegExp() {
		assertTrue(PatternMatchingSupport.valueMatchesRegularExpression("jeroen@ijsberg.nl", "^[A-Za-z0-9](([_\\.\\-]?[a-zA-Z0-9]+)*)@([A-Za-z0-9]+)(([\\.\\-]?[a-zA-Z0-9]+)*)\\.([A-Za-z]{2,})$"
		));

		assertTrue(PatternMatchingSupport.valueMatchesRegularExpression("hopla.mask", ".*mask"));
		assertTrue(PatternMatchingSupport.valueMatchesRegularExpression("hopla.mask.mask", ".*mask"));


		//assertTrue(PatternMatchingSupport.valueMatchesRegularExpression("assertEquals", "assert*"));
	}

	@Test
	public void testIndexOf() {
		assertEquals(24, PatternMatchingSupport.indexOf("analysis-infrastructure-0.9.0-SNAPSHOT-distribution.zip", "[0-9]*\\.[0-9]*\\.[0-9]*"));
		assertEquals(10, PatternMatchingSupport.indexOf("asjkdaskd 'c' skdjs", "'.'" ));

	}

	@Test
	public void testValueMatchesRegularExpression() {
		assertTrue(PatternMatchingSupport.valueMatchesRegularExpression("/bla/javascript.js", "(.*\\.html|.*\\.js)"));
	}

	@Test
	public void testValueMatchesUrl() {
		assertTrue(PatternMatchingSupport.valueMatchesRegularExpression("http://ijsberg.nl", "https?://..*"));
	}

	@Test
	public void testValueMatchesUrl_2() {
		assertTrue(PatternMatchingSupport.valueMatchesRegularExpression("https://ijsberg.nl", "https?://..*"));
	}

	@Test
	public void testValueMatchesUrl_3() {
		assertFalse(PatternMatchingSupport.valueMatchesRegularExpression("https://", "https?://..*"));
	}

	@Test
	public void testValueMatchesUrl_4() {
		assertTrue(PatternMatchingSupport.valueMatchesRegularExpression("copyright", ".*((C|c)opyright|COPYRIGHT).*"));
	}

	@Test
	public void testValueMatchesUrl_5() {
		assertTrue(PatternMatchingSupport.valueMatchesRegularExpression("Copyright", ".*((C|c)opyright|COPYRIGHT).*"));
	}
	@Test
	public void testValueMatchesUrl_6() {
		assertTrue(PatternMatchingSupport.valueMatchesRegularExpression("COPYRIGHT", ".*((C|c)opyright|COPYRIGHT).*"));
	}

	@Test
	public void testSegmentOfValueMatchesURL() {
		assertTrue(PatternMatchingSupport.valueSegmentMatchesRegularExpression("<use xmlns:xlink=\"http://www.w3.org/1999/xlink\" xlink:href=\"#icon--file\"></use>", "https?://w+\\S*"));
	}

	@Test
	public void testFilterPublicContent() {
		assertTrue(PatternMatchingSupport.valueMatchesRegularExpression("dashboard.com/index.html", "(.*\\.html|.*\\.ico|.*\\.js|.*\\.css|/monitor/login)"));
	}

	@Test
	public void testPasswordRegex() {
		assertTrue(PatternMatchingSupport.valueMatchesRegularExpression("koenTest1", "^(?!.*:).*(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,64}$"));
	}

	@Test
	public void testPomPropertyPattern() {
		assertTrue(PatternMatchingSupport.valueMatchesRegularExpression("${some.pattern}", "\\$\\{.+\\}"));
		assertTrue(PatternMatchingSupport.valueMatchesRegularExpression("${some-other.pattern}", "\\$\\{.+\\}"));
	}

	@Test
	public void testRegularExpressionRangeMatchesSegment() {
		String value = "testing_string22";
		int[] segment = PatternMatchingSupport.getRangesMatchingRegularExpression(value, "string").get(0);
		assertEquals("string", value.substring(segment[0],segment[1]));
	}
}

