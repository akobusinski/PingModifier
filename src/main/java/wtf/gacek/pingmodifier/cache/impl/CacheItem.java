package wtf.gacek.pingmodifier.cache.impl;

import org.jetbrains.annotations.Nullable;
import wtf.gacek.pingmodifier.cache.ICacheItem;

public class CacheItem<T> implements ICacheItem<T> {
    public CacheItem(T item, long expiresInMs) {
        this.item = item;
        this.expires = expiresInMs == -1L;
        this.expiresAt = this.expires ? Long.MAX_VALUE : System.currentTimeMillis() + expiresInMs;
    }

    private final long expiresAt;
    private final T item;
    private final boolean expires;

    @Override
    public @Nullable T getItem() {
        if (isExpired()) return null;

        return this.item;
    }

    @Override
    public long getExpiresAt() {
        return this.expires ? Long.MAX_VALUE : this.expiresAt;
    }

    @Override
    public boolean isExpired() {
        return System.currentTimeMillis() >= this.expiresAt;
    }

    @Override
    public boolean doesExpire() {
        return this.expires;
    }
}
