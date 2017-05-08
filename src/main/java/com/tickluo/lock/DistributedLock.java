package com.tickluo.lock;


public class DistributedLock implements LawLock {

    @Override
    public void lock(String key) {

    }

    @Override
    public void unlock() {

    }

    @Override
    public boolean tryLock() {
        return false;
    }

    @Override
    public boolean isLocked() {
        return false;
    }
}
