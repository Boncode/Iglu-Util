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

import java.io.Serializable;
import java.util.*;

public class ListTreeMap<K, V> implements ListMap<K, V> {
	
	protected TreeMap<K, List<V>> internalMap = new TreeMap<K, List<V>>();
	private int loadFactor = 10;

	public ListTreeMap() {
	}
	
	public ListTreeMap(int loadFactor) {
		this.loadFactor = loadFactor;
	}

	public ListTreeMap(ListTreeMap<K, V> listMap) {
		for(K key : listMap.internalMap.keySet()) {
			put(key, listMap.internalMap.get(key));
		}
	}

	public void createEntry(K key) {
		createOrRetrieveList(key);
	}

	protected List<V> createOrRetrieveList(K key) {
		List<V> list = internalMap.get(key);
		if(list == null) {
			list = new ArrayList<V>(loadFactor);
			internalMap.put(key, list);
		}
		return list;
	}
	
	public List<V> put(K key, V value) {
        if(!(key instanceof Comparable)) {
            throw new ClassCastException("key of type " + key.getClass().getSimpleName() + " K must implement Comparable");
        }
		List<V> list = createOrRetrieveList(key);
		list.add(value);
		return list;
	}

	public List<V> putDistinct(K key, V value) {
		List<V> list = createOrRetrieveList(key);
		if(!list.contains(value)) {
			put(key, value);
		}
		return list;
	}

	public List<V> put(K key, V ... values) {
		return put(key, Arrays.asList(values));
	}

	public List<V> put(K key, List<V> values) {
		if(!(key instanceof Comparable)) {
			throw new ClassCastException("key " + key.getClass().getSimpleName() + " must implement Comparable");
		}
		List<V> list = createOrRetrieveList(key);
		list.addAll(values);
		return list;
	}

	public void putAll(Map<K, V> values) {
		for(K key : values.keySet()) {
			put(key, values.get(key));
		}
	}

	public void putAll(ListTreeMap<K, V> values) {
		for(K key : values.keySet()) {
			put(key, values.get(key));
		}
	}

	public TreeMap<K, List<V>> getMap() {
		return internalMap;
	}
	
	public List<V> get(K key) {
		return internalMap.get(key);
	}
	
	public List<V> getByIndex(int index) {
		return internalMap.get(new ArrayList<K>(internalMap.keySet()).get(index));
	}

	public String toString() {
		return internalMap.toString();
	}

	public Set<K> keySet() {
		return internalMap.keySet();
	}
	
	public Set<K> descendingKeySet() {
		return internalMap.descendingKeySet();
	}

	public int size() {
		//nr of items
		int retval = 0;
		for(List<V> list : internalMap.values()) {
			retval += list.size();
		}
		return retval;
	}


	public Collection<List<V>> lists() {
		return internalMap.values();
	}

	public List<V> values() {
		List<V> retval = new ArrayList<V>();
		for(List<V> list : internalMap.values()) {
			retval.addAll(list);
		}
		return retval;
	}

	public List<V> valuesDescending() {
		List<V> retval = new ArrayList<V>();
		for(K key : internalMap.descendingKeySet()) {
			retval.addAll(internalMap.get(key));
		}
		return retval;
	}

	public List<V> getTop(int x) {
		
		List<V> retval = new ArrayList<V>();
		for(List<V> values : internalMap.descendingMap().values()) {
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

	public V removeFirst(K key) {
		
		List<V> list = internalMap.get(key);
		if(!list.isEmpty()) {
			return list.remove(0);
		}
		return null;
		
	}

    public boolean remove(K key, V value) {

        List<V> list = internalMap.get(key);
        if(!list.isEmpty()) {
            return list.remove(value);
        }
        return false;

    }

	public List<V> removeAll(K key) {
		return internalMap.remove(key);
	}

	public boolean contains(K key, V value) {
		List<V> values;
		return ((values = get(key)) != null && values.contains(value));
	}

	public boolean containsKey(K key) {
		return internalMap.containsKey(key);
	}

	public int indexOf(K key, V value) {
		List<V> values;
		return (values = get(key)) == null ? -1 : values.indexOf(value);
	}

	@Override
	public void clear() {
		internalMap.clear();
	}

}
