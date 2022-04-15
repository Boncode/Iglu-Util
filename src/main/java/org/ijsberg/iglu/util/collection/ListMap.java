package org.ijsberg.iglu.util.collection;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by jeroe on 23/08/2018.
 */
public interface ListMap<K, V> extends Serializable {
    List<V> put(K key, V value);

    List<V> putDistinct(K key, V value);

    List<V> put(K key, V... values);

    List<V> put(K key, int index, V value);

    List<V> put(K key, List<V> values);

    void putAll(Map<K, V> values);

    void putAll(ListTreeMap<K, V> values);

    Map<K, List<V>> getMap();

    List<V> get(K key);

    List<V> getByIndex(int index);

    Set<K> keySet();

    /*    public Set<K> descendingKeySet() {
                return internalMap.descendingKeySet();
            }
        */
    int size();

    Collection<List<V>> lists();

    List<V> values();

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
    V removeFirst(K key);

    boolean remove(K key, V value);

    List<V> removeAll(K key);

    void empty(K key);

    boolean contains(K key, V value);

    boolean containsKey(K key);

    int indexOf(K key, V value);

    void clear();

    /**
     *
     * @return a map with the first value of each list
     */
    Map toMap();
}
