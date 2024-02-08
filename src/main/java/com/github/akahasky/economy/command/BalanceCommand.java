package com.github.akahasky.economy.command;

import com.github.akahasky.economy.cache.EconomyCache;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.math.BigDecimal;

public class BalanceCommand extends Command {

    private final EconomyCache economyCache;

    public BalanceCommand(EconomyCache economyCache) {

        super("balance");

        setDescription("View the balance of yourself or another player, if no args specified, show them their own balance.");

        this.economyCache = economyCache;

    }

    @Override
    public boolean execute(CommandSender commandSender, String label, String[] arguments) {

        if (arguments.length > 1) {

            commandSender.sendMessage(String.format("§cUse /%s [player].", getName()));
            return false;

        }

        Player target = Bukkit.getPlayerExact(arguments.length == 0 ? commandSender.getName() : arguments[0]);

        if (target == null) {

            commandSender.sendMessage("§cThis player is currently not online.");
            return false;

        }

        BigDecimal money = economyCache.get(target.getName());

        if (money == null) {

            commandSender.sendMessage("§cThe data from this player has not yet been loaded, please try again later.");
            return false;

        }

        commandSender.sendMessage(String.format("§aPlayer %s has: %s money.", target.getName(), money.doubleValue()));
        return true;

    }

}
