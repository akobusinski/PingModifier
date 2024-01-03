package wtf.gacek.pingmodifier.cache;

import org.jetbrains.annotations.Nullable;

public interface ICacheItem<T> {
    @Nullable T getItem();
    long getExpiresAt();
    boolean isExpired();
    boolean doesExpire();
}
