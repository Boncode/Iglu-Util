package org.ijsberg.iglu.util.collection;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jeroe on 23/08/2018.
 */
public class ListHashMap<K, V> extends AbstractListMap<K, V> {

    static final long serialVersionUID = 27L;

    public ListHashMap() {
        this.internalMap = new HashMap<>();
    }

    public ListHashMap(int loadFactor) {
        this();
        this.loadFactor = loadFactor;
    }

    public ListHashMap(Map<K, V> map) {
        this();
        for(K key : map.keySet()) {
            put(key, map.get(key));
        }
    }

    public ListHashMap(ListHashMap<K, V> listMap) {
        this();
        for(K key : listMap.internalMap.keySet()) {
            put(key, listMap.internalMap.get(key));
        }
    }
}
