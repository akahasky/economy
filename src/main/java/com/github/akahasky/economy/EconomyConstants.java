package com.github.akahasky.economy;

public interface EconomyConstants {

    String TABLE_NAME = "economyUsers";
    String CREATE_TABLE_QUERY = String.format("create table if not exists %s (name varchar(16) primary key not null, money text not null);", EconomyConstants.TABLE_NAME);
    String SELECT_MONEY_QUERY = String.format("select money from %s where name=?;", TABLE_NAME);

}
