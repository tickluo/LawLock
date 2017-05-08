package com.tickluo.service;

import org.springframework.stereotype.Service;

@Service
public class RedisService {

    public boolean acquire(String key) {
        return false;
    }
}
