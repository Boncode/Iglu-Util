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

package org.ijsberg.iglu.util.collection;

import java.util.Collection;
import java.util.List;

/**
 * 
 */
public class PagedList<T> {
	
	private List<T> list;
	private int pageSize;
	private int nrofPages;

	public PagedList(List<T> list, int pageSize) {
		this.list = list;
		this.pageSize = pageSize;
		nrofPages = (int)Math.ceil((1.0 * list.size()) / pageSize);
	}
	
	
	public List<T> getPage(int offset) {
		int max = (pageSize * (1 + offset));
		return list.subList(pageSize * offset, max >= list.size() ? list.size() : max);
	}
	
	
	public int getNrofPages() {
		return nrofPages;
	}
	
}