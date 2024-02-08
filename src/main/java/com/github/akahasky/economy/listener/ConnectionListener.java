package com.github.akahasky.economy.listener;

import com.github.akahasky.economy.cache.EconomyCache;
import com.github.akahasky.economy.repository.IRepository;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.math.BigDecimal;

public class ConnectionListener implements Listener {

    private final IRepository repository;
    private final EconomyCache economyCache;

    public ConnectionListener(IRepository repository, EconomyCache economyCache) {

        this.repository = repository;
        this.economyCache = economyCache;

    }

    @EventHandler
    void on(PlayerJoinEvent event) {

        Player player = event.getPlayer();
        BigDecimal money = repository.selectOne(player.getName());

        if (money == null) money = BigDecimal.ZERO;

        economyCache.put(player.getName(), money);

    }

    @EventHandler
    void on(PlayerQuitEvent event) {

        Player player = event.getPlayer();
        BigDecimal money = economyCache.get(player.getName());

        if (money == null) return;

        repository.insertOne(player.getName(), money);
        economyCache.invalidate(player.getName());

    }

}
