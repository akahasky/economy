package com.github.akahasky.economy.cache;

import com.google.common.collect.Maps;

import java.math.BigDecimal;
import java.util.Map;

public class EconomyCache {

    private final Map<String, BigDecimal> cache = Maps.newHashMap();

    public void put(String name, BigDecimal money) { cache.put(name, money); }

    public void invalidate(String name) { cache.remove(name); }

    public BigDecimal get(String name) { return cache.get(name); }

    public Map<String, BigDecimal> cloneMap() { return Maps.newHashMap(cache); }

}
