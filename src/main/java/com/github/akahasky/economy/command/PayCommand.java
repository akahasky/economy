package com.github.akahasky.economy.command;

import com.github.akahasky.economy.cache.EconomyCache;
import com.github.akahasky.economy.repository.IRepository;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.math.BigDecimal;

public class PayCommand extends Command {

    private final IRepository repository;
    private final EconomyCache economyCache;

    public PayCommand(IRepository repository, EconomyCache economyCache) {

        super("pay");

        setDescription("Send money to another player.");

        this.repository = repository;
        this.economyCache = economyCache;

    }

    @Override
    public boolean execute(CommandSender commandSender, String label, String[] arguments) {

        if (commandSender instanceof ConsoleCommandSender) {

            commandSender.sendMessage("§cThis command cannot be executed from the console.");
            return false;

        }

        if (arguments.length != 2) {

            commandSender.sendMessage(String.format("§cUse /%s <player> <amount>.", getName()));
            return false;

        }

        Player receiver = Bukkit.getPlayerExact(arguments[0]);

        if (receiver == null) {

            commandSender.sendMessage("§cThis player is currently not online.");
            return false;

        }

        if (receiver.getName().equals(commandSender.getName())) {

            commandSender.sendMessage("§cYou can't send money to yourself.");
            return false;

        }

        Double amount = null;

        try { amount = Double.parseDouble(arguments[1]); }

        catch (Exception ignored) {}

        if (amount == null || amount <= 0.0 || amount.isInfinite() || amount.isNaN()) {

            commandSender.sendMessage("§cYou have entered an invalid amount.");
            return false;

        }

        BigDecimal senderMoney = economyCache.get(commandSender.getName());
        BigDecimal receiverMoney = economyCache.get(receiver.getName());

        if (senderMoney.doubleValue() < amount) {

            commandSender.sendMessage("§cYou don't have enough money to complete this transaction.");
            return false;

        }

        BigDecimal transactionMoney = BigDecimal.valueOf(amount);

        senderMoney = senderMoney.subtract(transactionMoney);
        receiverMoney = receiverMoney.add(transactionMoney);

        economyCache.put(commandSender.getName(), senderMoney);
        economyCache.put(receiver.getName(), receiverMoney);

        repository.insertOne(commandSender.getName(), senderMoney);
        repository.insertOne(receiver.getName(), receiverMoney);

        commandSender.sendMessage(String.format("§aYou sent %s money to %s.", transactionMoney.doubleValue(), receiver.getName()));
        receiver.sendMessage(String.format("§aYou received %s money from %s.", transactionMoney.doubleValue(), commandSender.getName()));

        return true;

    }

}
