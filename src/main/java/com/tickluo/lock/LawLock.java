package com.tickluo.lock;

public interface LawLock {

    String lock(String key) throws InterruptedException;

    boolean unlock(String key, String lockId);

    String tryLock(String key);

    boolean isLocked(String key);

}
