package com.github.akahasky.economy.command;

import com.github.akahasky.economy.cache.EconomyCache;
import com.github.akahasky.economy.repository.IRepository;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.math.BigDecimal;

public class SetBalanceCommand extends Command {

    private final IRepository repository;
    private final EconomyCache economyCache;

    public SetBalanceCommand(IRepository repository, EconomyCache economyCache) {

        super("setbalance");

        setDescription("Set the balance of another player.");

        setPermission("economy.setbalance");
        setPermissionMessage("§cRequires permission \"economy.setbalance\".");

        this.repository = repository;
        this.economyCache = economyCache;

    }

    @Override
    public boolean execute(CommandSender commandSender, String label, String[] arguments) {

        if (!commandSender.hasPermission(getPermission())) {

            commandSender.sendMessage(getPermissionMessage());
            return false;

        }

        if (arguments.length != 2) {

            commandSender.sendMessage(String.format("§cUse /%s <player> <amount>.", getName()));
            return false;

        }

        Player target = Bukkit.getPlayerExact(arguments[0]);

        if (target == null) {

            commandSender.sendMessage("§cThis player is currently not online.");
            return false;

        }

        Double amount = null;

        try { amount = Double.parseDouble(arguments[1]); }

        catch (Exception ignored) {}

        if (amount == null || amount <= 0.0 || amount.isInfinite() || amount.isNaN()) {

            commandSender.sendMessage("§cYou have entered an invalid amount.");
            return false;

        }

        BigDecimal newMoney = BigDecimal.valueOf(amount);

        economyCache.put(target.getName(), newMoney);
        repository.insertOne(target.getName(), newMoney);

        commandSender.sendMessage(String.format("§aPlayer %s now has %s money.", target.getName(), newMoney.doubleValue()));
        return true;

    }

}
