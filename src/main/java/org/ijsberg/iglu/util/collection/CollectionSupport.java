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

import org.ijsberg.iglu.util.formatting.PatternMatchingSupport;

import java.io.PrintStream;
import java.util.*;

/**
 * Contains static methods for formatting of collections, as well as arrays and maps.
 * Print methods print to standard out by default and use an end of line (EOL) as separator.
 */
public abstract class CollectionSupport {

	public static final String LINE_SEP = System.getProperty("line.separator");

	/**
	 * Prints a collection to standard out.
	 *
	 * @param coll
	 */
	public static void print(Collection coll) {
		print(coll, System.out);
	}

	/**
	 * Prints every item of a collection on a separate line to the given output stream.
	 *
	 * @param coll
	 * @param out stream to print to
	 */
	public static void print(Collection coll, PrintStream out) {
		print(coll, out, LINE_SEP);
	}

	/**
	 * Prints a collection to the given output stream.
	 *
	 * @param coll
	 * @param out stream to print to
	 * @param separator item separator
	 */
	public static void print(Collection coll, PrintStream out, String separator) {
		ArraySupport.print(null, null, coll.toArray(), separator, out);
	}

	/**
	 *
	 * @param coll
	 * @param separator item separator
	 * @return formatted collection
	 */
	public static String format(Collection<?> coll, String separator) {
		return format(null, coll, separator);
	}

	/**
	 *
	 * @param coll
	 * @param separator item separator
	 * @return formatted collection
	 */
	public static <T> String format(Collection<T> coll, StringFormatter<T> formatter, String separator) {
		return format(null, coll, formatter, separator);
	}

	/**
	 * @param itemPrefix
	 * @param coll
	 * @param separator item separator
	 * @return formatted collection
	 */
	public static String format(String itemPrefix, Collection coll, String separator) {
		if (coll == null) return "";
		return ArraySupport.format(itemPrefix, coll.toArray(), separator);
	}

	/**
	 * @param itemPrefix
	 * @param coll
	 * @param separator item separator
	 * @return formatted collection
	 */
	public static String format(String itemPrefix, Collection coll, StringFormatter stringFormatter, String separator) {
		if (coll == null) return "";
		return ArraySupport.format(itemPrefix, coll.toArray(), stringFormatter, separator);
	}

	/**
	 * @param itemPrefix
	 * @param coll
	 * @param itemPostfix
	 * @param separator   item separator
	 * @return formatted collection
	 */
	public static String format(String itemPrefix, Collection coll, String itemPostfix, String separator) {
		if (coll == null) return "";
		return ArraySupport.format(itemPrefix, itemPostfix, coll.toArray(), separator);
	}

	
	/**
	 * @param map
	 */
	public static void print(Map map) {
		print(map, System.out);
	}

	/**
	 * @param map
	 * @param out
	 */
	public static void print(Map map, PrintStream out) {
		print(map, System.out, System.getProperty("line.separator"));
	}

	/**
	 * @param map
	 * @param out
	 * @param separator
	 */
	public static void print(Map map, PrintStream out, String separator) {
		out.println(format(map, separator));
	}


	/**
	 * @param map
	 * @param separator
	 * @return
	 */
	public static String format(Map map, String separator) {
		StringBuffer retval = new StringBuffer();
		Iterator i = map.keySet().iterator();
		while (i.hasNext()) {
			Object key = i.next();
			retval.append(key + "=" + map.get(key) + (i.hasNext() ? separator : ""));
		}
		return retval.toString();
	}


	public static List<String> filter(List<String> strings, String wildcardExpression) {
		List<String> result = new ArrayList<>();
		for(String s : strings) {
			if(PatternMatchingSupport.valueMatchesWildcardExpression(s, wildcardExpression)) {
				result.add(s);
			}
		}
		return result;
	}

	public static <K,V> Map<K,V> sortByValue(Map<K,V> map, Comparator<Map.Entry<K,V>> comparator) {
		LinkedList<Map.Entry<K,V>> entryList = new LinkedList<>(map.entrySet());
		Collections.sort(entryList, comparator);

		LinkedHashMap<K,V> sortedMap = new LinkedHashMap<>();
		for(Map.Entry<K,V> entry : entryList){
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}

	public static <T> ArrayList<T> instantiateArrayList(T ... contents) {
		ArrayList<T> list = new ArrayList<>(Arrays.asList(contents));
		return list;
 	}

	public static <T> HashSet<T> instantiateHashSet(T ... contents) {
		HashSet<T> set = new HashSet<>(Arrays.asList(contents));
		return set;
	}

	public static <K, V extends Integer> void incrementIntegerByKey(Map<K, Integer> map, K key, V increment) {
		Integer value = map.get(key);
		if(value == null) {
			value = 0;

		}
		map.put(key, Integer.valueOf(value.intValue() + increment.intValue()));
	}

	public static <K, V extends Long> void incrementLongByKey(Map<K, Long> map, K key, V increment) {
		Long value = map.get(key);
		if(value == null) {
			value = 0l;

		}
		map.put(key, Long.valueOf(value.longValue() + increment.longValue()));
	}

	public static <K, V extends Float> void incrementFloatByKey(Map<K, Float> map, K key, V increment) {
		Float value = map.get(key);
		if(value == null) {
			value = 0.0f;

		}
		map.put(key, Float.valueOf(value.floatValue() + increment.floatValue()));
	}

	public static <K, V extends Number> void incrementNumberByKey(Map<K, V> map, K key, V increment) {
		Number value = map.get(key);
		if(value == null) {
			if(increment instanceof Integer) {
				value = 0;
			}
			if(increment instanceof Float) {
				value = 0.0f;
			}
			if(increment instanceof Long) {
				value = 0l;
			}

		}
		if(value instanceof Integer) {
			map.put(key, (V) Integer.valueOf((value).intValue() + increment.intValue()));
			return;
		}
		if(value instanceof Float) {
			map.put(key, (V) Float.valueOf(value.floatValue() + increment.floatValue()));
			return;
		}
		if(value instanceof Long) {
			map.put(key, (V) Long.valueOf((value).longValue() + increment.longValue()));
			return;
		}
		throw new IllegalArgumentException("type " + value.getClass().getSimpleName() + " not supported");
	}

}
