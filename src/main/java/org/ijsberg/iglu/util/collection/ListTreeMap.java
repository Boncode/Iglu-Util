/*
 * Copyright 2011-2014 Jeroen Meetsma - IJsberg Automatisering BV
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

import java.util.ArrayList;
import java.util.List;
import java.util.NavigableSet;
import java.util.TreeMap;

public class ListTreeMap<K, V> extends AbstractListMap<K, V> {

	static final long serialVersionUID = 27L;

	public ListTreeMap() {
		internalMap = new TreeMap<>();
	}
	
	public ListTreeMap(int loadFactor) {
		this();
		this.loadFactor = loadFactor;
	}

	public ListTreeMap(ListTreeMap<K, V> listMap) {
		this();
		for(K key : listMap.internalMap.keySet()) {
			put(key, listMap.internalMap.get(key));
		}
	}

	public ListTreeMap(TreeMap<K, List<V>> listMap) {
		this();
		for(K key : listMap.keySet()) {
			put(key, listMap.get(key));
		}
	}

	private void checkKeyIsComparable(K key) {
		if(!(key instanceof Comparable)) {
			throw new ClassCastException("key of type " + key.getClass().getSimpleName() + " K must implement Comparable");
		}
	}

	@Override
	protected List<V> createOrRetrieveList(K key) {
		checkKeyIsComparable(key);
		return super.createOrRetrieveList(key);
	}


	public NavigableSet<K> descendingKeySet() {
		return ((TreeMap<K,List<V>>)internalMap).descendingKeySet();
	}

	public List<V> valuesDescending() {
		List<V> retval = new ArrayList<V>();
		for(K key : descendingKeySet()) {
			retval.addAll(internalMap.get(key));
		}
		return retval;
	}

	public List<V> getTop(int x) {
		
		List<V> retval = new ArrayList<V>();
		for(List<V> values : ((TreeMap<K, List<V>>)internalMap).descendingMap().values()) {
			if(retval.size() == x) {
				return retval;
			} else if (values.size() < x - retval.size()) {
				retval.addAll(values);
			} else {
				retval.addAll(values.subList(0, x - retval.size()));
			}
		}
		return retval;
	}
}
