package org.ijsberg.iglu.util.misc;

import java.math.BigDecimal;
import java.util.Map;

public class NumberSupport {
    public static <K, V extends Integer> void incrementIntegerByKey(Map<K, Integer> map, K key, V increment) {
        Integer value = map.get(key);
        if(value == null) {
            value = 0;

        }
        map.put(key, Integer.valueOf(value.intValue() + (increment == null ? 0 : increment.intValue())));
    }

    public static <K, V extends Long> void incrementLongByKey(Map<K, Long> map, K key, V increment) {
        Long value = map.get(key);
        if(value == null) {
            value = 0l;

        }
        map.put(key, Long.valueOf(value.longValue() + (increment == null ? 0l : increment.longValue())));
    }

    public static <K, V extends Float> void incrementFloatByKey(Map<K, Float> map, K key, V increment) {
        Float value = map.get(key);
        if(value == null) {
            value = 0.0f;

        }
        map.put(key, Float.valueOf(value.floatValue() + (increment == null ? 0f: increment.floatValue())));
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
        if(increment instanceof Integer) {
            map.put(key, (V) Integer.valueOf((value).intValue() + (increment == null ? 0 : increment.intValue())));
            return;
        }
        if(increment instanceof Float) {
            map.put(key, (V) Float.valueOf(value.floatValue() + (increment == null ? 0f: increment.floatValue())));
            return;
        }
        if(increment instanceof Long) {
            map.put(key, (V) Long.valueOf((value).longValue() + (increment == null ? 0l : increment.longValue())));
            return;
        }
        throw new IllegalArgumentException("type " + value.getClass().getSimpleName() + " not supported");
    }

    public static <V extends Number> V sum(V ... n) {
        if(n.length > 0) {
            if(n[0] instanceof Integer) {
                Integer total = 0;
                for(Number a : n) {
                    total += a.intValue();
                }
                return (V)total;
            }
            if(n[0] instanceof Float) {
                Float total = 0.0f;
                for(Number a : n) {
                    total += a.floatValue();
                }
                return (V)total;
            }
            if(n[0] instanceof Long) {
                Long total = 0l;
                for(Number a : n) {
                    total += a.longValue();
                }
                return (V)total;
            }
            throw new IllegalArgumentException("type " + n[0].getClass().getSimpleName() + " not supported");
        }
        throw new IllegalArgumentException("array may not be empty");
    }

}