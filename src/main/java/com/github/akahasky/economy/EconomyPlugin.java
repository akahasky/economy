package com.github.akahasky.economy;

import com.github.akahasky.economy.cache.EconomyCache;
import com.github.akahasky.economy.command.AddBalanceCommand;
import com.github.akahasky.economy.command.BalanceCommand;
import com.github.akahasky.economy.command.PayCommand;
import com.github.akahasky.economy.command.SetBalanceCommand;
import com.github.akahasky.economy.listener.ConnectionListener;
import com.github.akahasky.economy.repository.IRepository;
import com.github.akahasky.economy.repository.impl.MongoDBRepositoryImpl;
import com.github.akahasky.economy.repository.impl.MySQLRepositoryImpl;
import com.github.akahasky.economy.repository.impl.SQLiteRepositoryImpl;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Method;
import java.math.BigDecimal;

public class EconomyPlugin extends JavaPlugin {

    private IRepository repository;
    private EconomyCache economyCache;

    @Override
    public void onEnable() {

        saveDefaultConfig();

        repository = getRepository();
        economyCache = new EconomyCache();

        Bukkit.getPluginManager().registerEvents(new ConnectionListener(repository, economyCache), this);

        registerCommand(new BalanceCommand(economyCache));

        registerCommand(new PayCommand(repository, economyCache));

        registerCommand(new SetBalanceCommand(repository, economyCache));
        registerCommand(new AddBalanceCommand(repository, economyCache));

        Bukkit.getOnlinePlayers().forEach(player -> {

            BigDecimal money = repository.selectOne(player.getName());

            if (money == null) money = BigDecimal.ZERO;

            economyCache.put(player.getName(), money);

        });

    }

    @Override
    public void onDisable() {

        repository.shutdown(() ->
                economyCache.cloneMap().forEach((name, money) -> repository.insertOne(name, money)));

    }

    private IRepository getRepository() {

        ConfigurationSection configurationSection = getConfig().getConfigurationSection("repository");

        switch (configurationSection.getString("type").toUpperCase()) {

            case "MONGODB": return new MongoDBRepositoryImpl(configurationSection);

            case "MYSQL": return new MySQLRepositoryImpl(configurationSection);

            default: return new SQLiteRepositoryImpl(getDataFolder().toPath().resolve("storage").toFile());

        }

    }

    private void registerCommand(Command command) {

        try {

            String packageName = getServer().getClass().getPackage().getName();

            Class<?> craftServerClass = Class.forName(String.format("org.bukkit.craftbukkit.%s.CraftServer", packageName.substring(packageName.lastIndexOf('.') + 1)));
            Method getServerMethod = Bukkit.class.getMethod("getServer");

            Object craftServer = getServerMethod.invoke(null);

            Class<?> simpleCommandMapClass = Class.forName("org.bukkit.command.SimpleCommandMap");

            Method getCommandMapMethod = craftServerClass.getMethod("getCommandMap");

            Object simpleCommandMap = getCommandMapMethod.invoke(craftServer);

            Method registerMethod = simpleCommandMapClass.getMethod("register", String.class, Command.class);
            registerMethod.invoke(simpleCommandMap, command.getName(), command);

        }

        catch (Exception exception) { exception.printStackTrace(); }

    }

}
