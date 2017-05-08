package com.tickluo.lock;

public interface LawLock {

    void lock(String key) throws InterruptedException;

    void unlock();

    boolean tryLock();

    boolean isLocked();

}
