package org.ijsberg.iglu.util.collection;

import java.util.*;

/**
 * Created by jeroe on 23/08/2018.
 */
public class ListLinkedHashMap<K, V> extends ListHashMap<K, V> {

    static final long serialVersionUID = 27L;

    public ListLinkedHashMap() {
        this.internalMap = new LinkedHashMap<>();
    }

    public ListLinkedHashMap(int loadFactor) {
        this();
        this.loadFactor = loadFactor;
    }

    public ListLinkedHashMap(AbstractListMap<K, V> listMap) {
        this();
        for(K key : listMap.internalMap.keySet()) {
            put(key, listMap.internalMap.get(key));
        }
    }

    public Set<K> reverseKeySet() {
        List<K> list = new ArrayList<>(internalMap.keySet());
        Collections.reverse(list);
        return new HashSet<>(list);
    }


    public ListLinkedHashMap<K, V> append(K key, V ... values) {
        put(key, values);
        return this;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ListLinkedHashMap)) return false;
        ListLinkedHashMap<?, ?> that = (ListLinkedHashMap<?, ?>) o;
        return Objects.equals(internalMap, that.internalMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(internalMap);
    }

}
