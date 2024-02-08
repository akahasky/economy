package com.github.akahasky.economy.repository.impl;

import com.github.akahasky.economy.EconomyConstants;
import com.github.akahasky.economy.repository.IRepository;

import java.io.File;
import java.math.BigDecimal;
import java.sql.*;

public class SQLiteRepositoryImpl implements IRepository {

    private Connection connection;

    public SQLiteRepositoryImpl(File folder) {

        if (!folder.exists()) folder.mkdirs();

        File file = folder.toPath().resolve("database.sql").toFile();

        try {

            if (!file.exists()) file.createNewFile();

            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + file.getPath());

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

        try (PreparedStatement statement = connection.prepareStatement(String.format("replace into %s (name, money) values(?,?);", EconomyConstants.TABLE_NAME))) {

            statement.setString(1, name);
            statement.setString(2, money.toString());

            statement.executeUpdate();

        }

        catch (SQLException exception) { exception.printStackTrace(); }

    }

}