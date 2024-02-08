package com.github.akahasky.economy.repository.impl;

import com.github.akahasky.economy.EconomyConstants;
import com.github.akahasky.economy.repository.IRepository;
import org.bukkit.configuration.ConfigurationSection;

import java.math.BigDecimal;
import java.sql.*;

public class MySQLRepositoryImpl implements IRepository {

    private Connection connection;

    public MySQLRepositoryImpl(ConfigurationSection configurationSection) {

        String host = configurationSection.getString("host");
        String database = configurationSection.getString("database");
        String user = configurationSection.getString("username");
        String password = configurationSection.getString("password");

        String url = String.format("jdbc:mysql://%s/%s", host, database);

        try {

            Class.forName("com.mysql.jdbc.Driver");
            Class.forName("com.mysql.jdbc.Connection");
            connection = DriverManager.getConnection(url, user, password);

            init();

        }

        catch (Exception exception) { exception.printStackTrace(); }

    }

    @Override
    public void init() {

        try (PreparedStatement statement = connection.prepareStatement(EconomyConstants.CREATE_TABLE_QUERY)) {

            statement.execute();

        }

        catch (SQLException exception) { exception.printStackTrace(); }

    }

    @Override
    public void shutdown(Runnable preCloseRunnable) {

        try {

            if (connection == null || connection.isClosed()) return;

            preCloseRunnable.run();
            connection.close();

        }

        catch (SQLException exception) { exception.printStackTrace(); }

    }

    @Override
    public BigDecimal selectOne(String name) {

        try (PreparedStatement statement = connection.prepareStatement(EconomyConstants.SELECT_MONEY_QUERY)) {

            statement.setString(1, name);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (!resultSet.next()) return null;

                return new BigDecimal(resultSet.getString("money"));

            }

        }

        catch (SQLException exception) { exception.printStackTrace(); }

        return null;

    }

    @Override
    public void insertOne(String name, BigDecimal money) {

        try (PreparedStatement statement = connection.prepareStatement(String.format("insert into %s (name, money) values(?,?) on duplicate key update money=values(money);", EconomyConstants.TABLE_NAME))) {

            statement.setString(1, name);
            statement.setString(2, money.toString());

            statement.executeUpdate();

        }

        catch (SQLException exception) { exception.printStackTrace(); }

    }

}