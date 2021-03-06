package com.tickluo.redis;

import com.tickluo.LawLockConfig;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisException;

import java.util.concurrent.locks.ReentrantLock;

public class RedisUtils {

    protected static ReentrantLock lockPool = new ReentrantLock();
    protected static ReentrantLock lockJedis = new ReentrantLock();

    private static Logger _log = LoggerFactory.getLogger(RedisUtils.class);

    private static JedisPool jedisPool = null;

    /**
     * 初始化Redis连接池
     */
    private static void initialPool() {
        try {
            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxTotal(LawLockConfig.MAX_ACTIVE);
            config.setMaxIdle(LawLockConfig.MAX_IDLE);
            config.setMaxWaitMillis(LawLockConfig.MAX_WAIT);
            config.setTestOnBorrow(LawLockConfig.TEST_ON_BORROW);
            jedisPool = new JedisPool(config, LawLockConfig.IP, LawLockConfig.PORT, LawLockConfig.TIMEOUT);
        } catch (Exception e) {
            _log.error("First create JedisPool error : " + e);
        }
    }

    /**
     * 在多线程环境同步初始化
     */
    private static void poolInit() {
        if (jedisPool == null) {
            initialPool();
        }
    }


    /**
     * 同步获取Jedis实例
     *
     * @return Jedis
     */
    public static Jedis getJedis() {
        poolInit();
        Jedis jedis = null;
        try {
            if (null != jedisPool) {
                jedis = jedisPool.getResource();
                try {
                    jedis.auth(LawLockConfig.PASSWORD);
                } catch (Exception e) {

                }
            }
        } catch (Exception e) {
            _log.error("Get jedis error : " + e);
        }
        return jedis;
    }

    /**
     * 设置 String
     *
     * @param key
     * @param value
     */
    public static void set(String key, String value) {
        try {
            value = StringUtils.isBlank(value) ? "" : value;
            Jedis jedis = getJedis();
            jedis.set(key, value);
            jedis.close();
        } catch (Exception e) {
            _log.error("Set key error : " + e);
        }
    }

    /**
     * 设置 byte[]
     *
     * @param key
     * @param value
     */
    public static void set(byte[] key, byte[] value) {
        try {
            Jedis jedis = getJedis();
            jedis.set(key, value);
            jedis.close();
        } catch (Exception e) {
            _log.error("Set key error : " + e);
        }
    }

    /**
     * 设置 String 过期时间
     *
     * @param key
     * @param value
     * @param seconds 以秒为单位
     */
    public static void set(String key, String value, int seconds) {
        try {
            value = StringUtils.isBlank(value) ? "" : value;
            Jedis jedis = getJedis();
            jedis.setex(key, seconds, value);
            jedis.close();
        } catch (Exception e) {
            _log.error("Set keyex error : " + e);
        }
    }

    /**
     * 设置 byte[] 过期时间
     *
     * @param key
     * @param value
     * @param seconds 以秒为单位
     */
    public static void set(byte[] key, byte[] value, int seconds) {
        try {
            Jedis jedis = getJedis();
            jedis.set(key, value);
            jedis.expire(key, seconds);
            jedis.close();
        } catch (Exception e) {
            _log.error("Set key error : " + e);
        }
    }

    public static boolean setnx(String key, String value, int seconds) {
        try {
            Jedis jedis = getJedis();
            boolean result = jedis.setnx(key, value) > 0;
            jedis.expire(key, seconds);
            jedis.close();
            return result;
        } catch (Exception e) {
            _log.error("Setnx key error : " + e);
            return false;
        }
    }

    /**
     * 获取String值
     *
     * @param key
     * @return value
     */
    public static String get(String key) {
        Jedis jedis = getJedis();
        if (null == jedis) {
            return null;
        }
        String value = jedis.get(key);
        jedis.close();
        return value;
    }

    /**
     * 获取byte[]值
     *
     * @param key
     * @return value
     */
    public static byte[] get(byte[] key) {
        Jedis jedis = getJedis();
        if (null == jedis) {
            return null;
        }
        byte[] value = jedis.get(key);
        jedis.close();
        return value;
    }

    /**
     * 删除值
     *
     * @param key
     */
    public static void remove(String key) {
        try {
            Jedis jedis = getJedis();
            jedis.del(key);
            jedis.close();
        } catch (Exception e) {
            _log.error("Remove keyex error : " + e);
        }
    }

    /**
     * 删除值
     *
     * @param key
     */
    public static void remove(byte[] key) {
        try {
            Jedis jedis = getJedis();
            jedis.del(key);
            jedis.close();
        } catch (Exception e) {
            _log.error("Remove keyex error : " + e);
        }
    }

    /**
     * lpush
     *
     * @param key
     * @param key
     */
    public static void lpush(String key, String... strings) {
        try {
            Jedis jedis = RedisUtils.getJedis();
            jedis.lpush(key, strings);
            jedis.close();
        } catch (Exception e) {
            _log.error("lpush error : " + e);
        }
    }

    /**
     * lrem
     *
     * @param key
     * @param count
     * @param value
     */
    public static void lrem(String key, long count, String value) {
        try {
            Jedis jedis = RedisUtils.getJedis();
            jedis.lrem(key, count, value);
            jedis.close();
        } catch (Exception e) {
            _log.error("lpush error : " + e);
        }
    }

    /**
     * sadd
     *
     * @param key
     * @param value
     * @param seconds
     */
    public static void sadd(String key, String value, int seconds) {
        try {
            Jedis jedis = RedisUtils.getJedis();
            jedis.sadd(key, value);
            jedis.expire(key, seconds);
            jedis.close();
        } catch (Exception e) {
            _log.error("sadd error : " + e);
        }
    }

    public static Long ttl(String key) throws JedisException {
        Jedis jedis = RedisUtils.getJedis();
        Long timeout = jedis.ttl(key);
        jedis.close();
        return timeout;
    }

    public static void setExpire(String key, int seconds) throws JedisException {
        Jedis jedis = RedisUtils.getJedis();
        jedis.expire(key, seconds);
        jedis.close();
    }

    public static boolean exist(String key) throws JedisException {
        Jedis jedis = RedisUtils.getJedis();
        boolean exist = jedis.exists(key);
        jedis.close();
        return exist;
    }
}
