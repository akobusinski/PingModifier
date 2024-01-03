package wtf.gacek.pingmodifier.cache.impl;

import org.jetbrains.annotations.Nullable;
import wtf.gacek.pingmodifier.cache.ICacheItem;
import wtf.gacek.pingmodifier.cache.ITimedCacheMap;

import java.util.Map;

public abstract class AbstractTimedCacheMap<K, V> implements ITimedCacheMap<K, V> {
    public AbstractTimedCacheMap() {
        this(3600L);
    }
    public AbstractTimedCacheMap(long defaultCacheTime) {
        this.defaultCacheTimeMs = defaultCacheTime;
    }
    protected long defaultCacheTimeMs;
    protected Map<K, ICacheItem<V>> map = createCacheMap();

    abstract protected Map<K, ICacheItem<V>> createCacheMap();

    public long getDefaultCacheTimeMs() {
        return this.defaultCacheTimeMs;
    }
    public void setDefaultCacheTimeMs(long defaultCacheTimeMs) {
        this.defaultCacheTimeMs = defaultCacheTimeMs;
    }
    protected @Nullable ICacheItem<V> getCacheItem(K key) {
        ICacheItem<V> cacheItem = map.get(key);

        if (cacheItem == null || cacheItem.isExpired()) {
            return null;
        }

        return cacheItem;
    }
    public @Nullable V getItem(K key) {
        ICacheItem<V> cacheItem = getCacheItem(key);

        if (cacheItem == null) {
            return null;
        }

        return cacheItem.getItem();
    }
    public long getItemTimeLeft(K key) {
        ICacheItem<V> cacheItem = getCacheItem(key);

        if (cacheItem == null) {
            return -1;
        }

        if (!cacheItem.doesExpire()) {
            return Long.MAX_VALUE;
        }

        return cacheItem.getExpiresAt() - System.currentTimeMillis();
    }
    public void setItem(K key, V item) {
        setItem(key, item, this.defaultCacheTimeMs);
    }
    public void setItem(K key, V item, long cacheTimeMs) {
        map.put(key, new CacheItem<>(item, cacheTimeMs));
    }

    public void clearCache() {
        map.clear();
    }

    public boolean containsItem(K key) {
        return getItem(key) != null;
    }
}
