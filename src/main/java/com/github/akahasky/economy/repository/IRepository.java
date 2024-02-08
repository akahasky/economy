package com.github.akahasky.economy.repository;

import java.math.BigDecimal;

public interface IRepository {

    void init();

    void shutdown(Runnable preCloseRunnable);

    BigDecimal selectOne(String name);

    void insertOne(String name, BigDecimal money);

}
