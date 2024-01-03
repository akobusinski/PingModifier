package wtf.gacek.pingmodifier.cache.impl;

import wtf.gacek.pingmodifier.cache.ICacheItem;

import java.util.HashMap;
import java.util.Map;

public class TimedCacheMap<K, V> extends AbstractTimedCacheMap<K, V> {
    public TimedCacheMap() {
        super();
    }
    public TimedCacheMap(long defaultCacheTime) {
        super(defaultCacheTime);
    }

    @Override
    protected Map<K, ICacheItem<V>> createCacheMap() {
        return new HashMap<>();
    }
}
