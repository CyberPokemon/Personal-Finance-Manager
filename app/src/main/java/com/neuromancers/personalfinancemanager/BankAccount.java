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

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getIfscCode() {
        return ifscCode;
    }

    public void setIfscCode(String ifscCode) {
        this.ifscCode = ifscCode;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}

