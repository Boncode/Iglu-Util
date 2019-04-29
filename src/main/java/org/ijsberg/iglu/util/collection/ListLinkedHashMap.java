package org.ijsberg.iglu.util.collection;

import java.io.Serializable;
import java.util.*;

/**
 * Created by jeroe on 23/08/2018.
 */
public class ListLinkedHashMap<K, V> implements Serializable, ListMap<K, V> {

    protected LinkedHashMap<K, List<V>> internalMap = new LinkedHashMap<K, List<V>>();
    private int loadFactor = 10;

    public ListLinkedHashMap() {
    }

    public ListLinkedHashMap(int loadFactor) {
        this.loadFactor = loadFactor;
    }

    public ListLinkedHashMap(ListTreeMap<K, V> listMap) {
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
        if(!(key instanceof Comparable)) {
            throw new ClassCastException("key of type " + key.getClass().getSimpleName() + " K must implement Comparable");
        }
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
        if(!(key instanceof Comparable)) {
            throw new ClassCastException("key K must implement Comparable");
        }
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
    public LinkedHashMap<K, List<V>> getMap() {
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

/*    public Set<K> descendingKeySet() {
        return internalMap.descendingKeySet();
    }
*/
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

 /*   public List<V> valuesDescending() {
        List<V> retval = new ArrayList<V>();
        for(K key : internalMap.descendingKeySet()) {
            retval.addAll(internalMap.get(key));
        }
        return retval;
    }*/

/*    public List<V> getTop(int x) {

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
*/
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

}
