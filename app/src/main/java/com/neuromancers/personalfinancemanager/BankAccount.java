package com.neuromancers.personalfinancemanager;

public class BankAccount {
    public String bankName;
    public String accountNumber;
    public String ifscCode;
    public double balance;

    public BankAccount(String bankName, String accountNumber, String ifscCode, double balance) {
        this.bankName = bankName;
        this.accountNumber = accountNumber;
        this.ifscCode = ifscCode;
        this.balance = balance;
    }
}

