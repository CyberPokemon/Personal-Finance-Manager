package com.neuromancers.personalfinancemanager;

public class Transaction {
    private long id;
    private long userId;
    private double amount;
    private String type;
    private String datetime;
    private String description;
    private String person;
    private String account;

    public Transaction() {
    }

    public Transaction(long id, long userId, double amount, String type, String datetime, String description, String person, String account) {
        this.id = id;
        this.userId = userId;
        this.amount = amount;
        this.type = type;
        this.datetime = datetime;
        this.description = description;
        this.person = person;
        this.account = account;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }
}

