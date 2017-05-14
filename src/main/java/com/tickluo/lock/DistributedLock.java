package com.tickluo.lock;


import com.tickluo.LawLockConfig;
import com.tickluo.redis.RedisUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.exceptions.JedisException;

import java.util.UUID;

public class DistributedLock implements LawLock {
    private String KEY_DES = "lock:";
    private int LOCK_TIMEOUT = 3 * 1000;
    private int ATTEMPT_TIMEOUT = 6 * 1000;
    private int WAITING_TIME = 50;

    @Override
    public String lock(String key) throws InterruptedException {
        String wrapKey = KEY_DES.concat(key);
        Long endTime = System.currentTimeMillis() + ATTEMPT_TIMEOUT;
        while (System.currentTimeMillis() < endTime) {
            String lockId = tryLock(key);
            if (lockId != null) {
                return lockId;
            }
            if (RedisUtils.ttl(wrapKey) < 0) {
                RedisUtils.setExpire(wrapKey, LOCK_TIMEOUT);
            }
            Thread.currentThread().sleep(WAITING_TIME);
        }
        return null;
    }

    @Override
    public String tryLock(String key) {
        String lockRandomId = UUID.randomUUID().toString();

        if (RedisUtils.setnx(KEY_DES.concat(key), lockRandomId, LOCK_TIMEOUT)) {
            return lockRandomId;
        }
        return null;
    }

    @Override
    public boolean unlock(String key, String lockId) throws JedisException {
        String wrapKey = KEY_DES.concat(key);
        Long endTime = System.currentTimeMillis() + ATTEMPT_TIMEOUT;

        while (System.currentTimeMillis() < endTime) {
            Jedis jedis = RedisUtils.getJedis();
            if (lockId.equals(jedis.get(wrapKey))) {
                if (jedis.del(wrapKey) > 0) {
                    return true;
                }
                continue;
            }
            break;
        }
        return false;
    }

    @Override
    public boolean isLocked(String key) {
        String wrapKey = KEY_DES.concat(key);
        return RedisUtils.exist(wrapKey);
    }
}
