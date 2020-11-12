package org.ijsberg.iglu.util.collection;

import java.io.Serializable;
import java.util.*;

/**
 * Created by jeroe on 23/08/2018.
 */
public class ListHashMap<K, V> implements Serializable, ListMap<K, V> {

    protected HashMap<K, List<V>> internalMap = new HashMap<K, List<V>>();
    private int loadFactor = 10;

    public ListHashMap() {
    }

    public ListHashMap(int loadFactor) {
        this.loadFactor = loadFactor;
    }

    public ListHashMap(Map<K, V> map) {
        for(K key : map.keySet()) {
            put(key, map.get(key));
        }
    }

    public ListHashMap(ListHashMap<K, V> listMap) {
        for(K key : listMap.internalMap.keySet()) {
            put(key, listMap.internalMap.get(key));
        }
    }

    protected List<V> createOrRetrieveList(K key) {
        List<V> list = internalMap.get(key);
        if(list == null) {
            list = new ArrayList<V>(loadFactor);
            internalMap.put(key, list);
        }
        return list;
    }

    @Override
    public List<V> put(K key, V value) {
        List<V> list = createOrRetrieveList(key);
        list.add(value);
        return list;
    }

    @Override
    public List<V> putDistinct(K key, V value) {
        List<V> list = createOrRetrieveList(key);
        if(!list.contains(value)) {
            put(key, value);
        }
        return list;
    }

    @Override
    public List<V> put(K key, V... values) {
        return put(key, Arrays.asList(values));
    }

    @Override
    public List<V> put(K key, List<V> values) {
        List<V> list = createOrRetrieveList(key);
        list.addAll(values);
        return list;
    }

    @Override
    public void putAll(Map<K, V> values) {
        for(K key : values.keySet()) {
            put(key, values.get(key));
        }
    }

    @Override
    public void putAll(ListTreeMap<K, V> values) {
        for(K key : values.keySet()) {
            put(key, values.get(key));
        }
    }

    @Override
    public HashMap<K, List<V>> getMap() {
        return internalMap;
    }

    @Override
    public List<V> get(K key) {
        return internalMap.get(key);
    }

    @Override
    public List<V> getByIndex(int index) {
        return internalMap.get(new ArrayList<K>(internalMap.keySet()).get(index));
    }

    public String toString() {
        return internalMap.toString();
    }

    @Override
    public Set<K> keySet() {
        return internalMap.keySet();
    }

    @Override
    public int size() {
        //nr of items
        int retval = 0;
        for(List<V> list : internalMap.values()) {
            retval += list.size();
        }
        return retval;
    }

    @Override
    public Collection<List<V>> lists() {
        return internalMap.values();
    }

    @Override
    public List<V> values() {
        List<V> retval = new ArrayList<V>();
        for(List<V> list : internalMap.values()) {
            retval.addAll(list);
        }
        return retval;
    }

    @Override
    public V removeFirst(K key) {

        List<V> list = internalMap.get(key);
        if(!list.isEmpty()) {
            return list.remove(0);
        }
        return null;

    }

    @Override
    public boolean remove(K key, V value) {

        List<V> list = internalMap.get(key);
        if(!list.isEmpty()) {
            return list.remove(value);
        }
        return false;

    }

    @Override
    public List<V> removeAll(K key) {
        return internalMap.remove(key);
    }

    @Override
    public boolean contains(K key, V value) {
        List<V> values;
        return ((values = get(key)) != null && values.contains(value));
    }

    @Override
    public boolean containsKey(K key) {
        return internalMap.containsKey(key);
    }

    @Override
    public int indexOf(K key, V value) {
        List<V> values;
        return (values = get(key)) == null ? -1 : values.indexOf(value);
    }

    @Override
    public void clear() {
        internalMap.clear();
    }

    @Override
    public Map<K, V> toMap() {
        HashMap<K, V> map = new HashMap<>();
        for(K key : internalMap.keySet()) {
            List<V> values = get(key);
            if(!values.isEmpty()) {
                map.put(key, values.get(0));
            }
        }
        return map;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ListHashMap)) return false;
        ListHashMap<?, ?> that = (ListHashMap<?, ?>) o;
        return Objects.equals(internalMap, that.internalMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(internalMap);
    }
}
