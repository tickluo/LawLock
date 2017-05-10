package com.tickluo.lock;


import com.tickluo.LawLockConfig;

public class DistributedLock implements LawLock {

    @Override
    public void lock(String key) throws InterruptedException {
        while (!tryLock()) {
            Thread.currentThread().sleep(LawLockConfig.DEFAULT_WAITING_TIME);
        }
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
