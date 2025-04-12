package com.neuromancers.personalfinancemanager;

import java.util.ArrayList;
import java.util.List;

public class BankDataHolder {
    private static final BankDataHolder instance = new BankDataHolder();
    private final List<BankAccount> accounts = new ArrayList<>();

    private BankDataHolder() {}

    public static BankDataHolder getInstance() {
        return instance;
    }

    public void addAccount(BankAccount account) {
        accounts.add(account);
    }

    public List<BankAccount> getAccounts() {
        return accounts;
    }

    public void clear() {
        accounts.clear();
    }
}

