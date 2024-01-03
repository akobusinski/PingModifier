package wtf.gacek.pingmodifier.cache;

import org.jetbrains.annotations.Nullable;

public interface ITimedCacheMap<K, V> {
    @Nullable V getItem(K key);
    long getItemTimeLeft(K key);
    void setItem(K key, V item);
    void setItem(K key, V item, long cacheTimeMs);
    long getDefaultCacheTimeMs();
    void setDefaultCacheTimeMs(long cacheTimeMs);
}