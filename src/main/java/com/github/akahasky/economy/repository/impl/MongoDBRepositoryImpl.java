package com.github.akahasky.economy.repository.impl;

import com.github.akahasky.economy.EconomyConstants;
import com.github.akahasky.economy.EconomyPlugin;
import com.github.akahasky.economy.repository.IRepository;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.ReplaceOptions;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.math.BigDecimal;
import java.util.logging.Level;

public class MongoDBRepositoryImpl implements IRepository {

    private final MongoClient mongoClient;
    private final String database;

    private MongoCollection<Document> mongoCollection;

    public MongoDBRepositoryImpl(ConfigurationSection configurationSection) {

        String host = configurationSection.getString("host");
        String user = configurationSection.getString("username");
        String password = configurationSection.getString("password");

        String url;

        if (user.isEmpty() && password.isEmpty())
            url = String.format("mongodb://%s", host);

        else url = String.format("mongodb://%s:%s@%s", user, password, host);

        mongoClient = MongoClients.create(url);
        database = configurationSection.getString("database");

        init();

    }

    @Override
    public void init() {

        if (mongoClient == null) {

            Bukkit.getLogger().log(Level.SEVERE, "An error occurred when connecting to the database. (MongoDB)");
            Bukkit.getPluginManager().disablePlugin(JavaPlugin.getPlugin(EconomyPlugin.class));

            return;

        }

        mongoCollection = mongoClient.getDatabase(database).getCollection(EconomyConstants.TABLE_NAME);

    }

    @Override
    public void shutdown(Runnable preCloseRunnable) {

        if (mongoClient == null) return;

        preCloseRunnable.run();
        mongoClient.close();

    }

    @Override
    public BigDecimal selectOne(String name) {

        Document document = mongoCollection.find(new Document("name", name)).first();

        if (document != null) return document.get("money", BigDecimal.class);

        return null;

    }

    @Override
    public void insertOne(String name, BigDecimal money) {

        Document filterDocument = new Document("name", name);
        Document updatedDocument = new Document("$set", new Document("money", money));

        ReplaceOptions replaceOptions = new ReplaceOptions().upsert(true);

        mongoCollection.replaceOne(filterDocument, updatedDocument, replaceOptions);

    }

}