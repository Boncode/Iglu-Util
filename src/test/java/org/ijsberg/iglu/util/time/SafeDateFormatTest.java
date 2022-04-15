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

package org.ijsberg.iglu.util.time;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;


public class SafeDateFormatTest {

	@Test
	public void testConstructor() throws Exception {
		SafeDateFormat dateFormat = new SafeDateFormat("ddMMyyyy");
		
		assertEquals("27011970", dateFormat.format(new Date(TimeSupportTest.getTime(10, 25))));
	}
	
}

