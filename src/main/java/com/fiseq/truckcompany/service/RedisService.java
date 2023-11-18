package com.fiseq.truckcompany.service;

public interface RedisService {
    Object getValue(String key);
    void setValue(String key, Object value);
    void deleteValue(String key);
}
