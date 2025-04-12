package com.neuromancers.personalfinancemanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "finance_app.db";
    public static final int DB_VERSION = 2;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // User table
        db.execSQL("CREATE TABLE user (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "dob TEXT, " +
                "address TEXT, " +
                "cash REAL)");

        // Bank account table
        db.execSQL("CREATE TABLE bank_account (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER, " +
                "bank_name TEXT, " +
                "account_number TEXT, " +
                "ifsc TEXT, " +
                "balance REAL, " +
                "FOREIGN KEY(user_id) REFERENCES user(id))");

        //transaction logs
        db.execSQL("CREATE TABLE transaction_log (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER, " +
                "amount REAL, " +
                "type TEXT, " + // 'incoming' or 'outgoing'
                "datetime TEXT, " +
                "description TEXT, " +
                "person TEXT, " + // from/to whom
                "account_name TEXT, " + // from/to which account
                "FOREIGN KEY(user_id) REFERENCES user(id))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle DB upgrade
        db.execSQL("DROP TABLE IF EXISTS user");
        db.execSQL("DROP TABLE IF EXISTS bank_account");
        db.execSQL("DROP TABLE IF EXISTS transaction_log");
        onCreate(db);
    }

    public long insertUser(String name, String dob, String address, double cash) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("dob", dob);
        values.put("address", address);
        values.put("cash", cash);
        return db.insert("user", null, values);
    }

    public void insertBankAccount(long userId, String bankName, String accountNumber, String ifsc, double balance) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("bank_name", bankName);
        values.put("account_number", accountNumber);
        values.put("ifsc", ifsc);
        values.put("balance", balance);
        db.insert("bank_account", null, values);
    }

    public long getLastInsertedUserId() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM user ORDER BY id DESC LIMIT 1", null);
        if (cursor.moveToFirst()) {
            return cursor.getLong(0);
        }
        cursor.close();
        return -1; // invalid id
    }

    public User getUser(long userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM user WHERE id = ?", new String[]{String.valueOf(userId)});

        if (cursor != null && cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            String dob = cursor.getString(cursor.getColumnIndexOrThrow("dob"));
            String address = cursor.getString(cursor.getColumnIndexOrThrow("address"));
            double cash = cursor.getDouble(cursor.getColumnIndexOrThrow("cash"));

            cursor.close();
            return new User(userId, name, dob, address, cash);
        }

        return null;
    }


    public List<BankAccount> getBankAccounts(long userId) {
        List<BankAccount> accountList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM bank_account WHERE user_id = ?", new String[]{String.valueOf(userId)});

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String bankName = cursor.getString(cursor.getColumnIndexOrThrow("bank_name"));
                String accNum = cursor.getString(cursor.getColumnIndexOrThrow("account_number"));
                String ifsc = cursor.getString(cursor.getColumnIndexOrThrow("ifsc"));
                double balance = cursor.getDouble(cursor.getColumnIndexOrThrow("balance"));

                accountList.add(new BankAccount(bankName, accNum, ifsc, balance));
            } while (cursor.moveToNext());
            cursor.close();
        }

        return accountList;
    }

    public void insertTransaction(long userId, double amount, String type, String datetime,
                                  String description, String person, String accountName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("amount", amount);
        values.put("type", type);
        values.put("datetime", datetime);
        values.put("description", description);
        values.put("person", person);
        values.put("account_name", accountName);
        db.insert("transaction_log", null, values);
    }


    public List<Transaction> getTransactionsForUser(long userId) {
        List<Transaction> transactionList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM transaction_log WHERE user_id = ? ORDER BY datetime DESC",
                new String[]{String.valueOf(userId)});


        if (cursor != null && cursor.moveToFirst()) {
            do {
                Transaction transaction = new Transaction();
                transaction.setId(cursor.getLong(cursor.getColumnIndexOrThrow("id")));
                transaction.setUserId(cursor.getLong(cursor.getColumnIndexOrThrow("user_id")));
                transaction.setAmount(cursor.getDouble(cursor.getColumnIndexOrThrow("amount")));
                transaction.setType(cursor.getString(cursor.getColumnIndexOrThrow("type")));
                transaction.setDatetime(cursor.getString(cursor.getColumnIndexOrThrow("datetime")));
                transaction.setDescription(cursor.getString(cursor.getColumnIndexOrThrow("description")));
                transaction.setPerson(cursor.getString(cursor.getColumnIndexOrThrow("person")));
                transaction.setAccount(cursor.getString(cursor.getColumnIndexOrThrow("account_name")));

                transactionList.add(transaction);
            } while (cursor.moveToNext());

            cursor.close();
        }

        return transactionList;
    }


}
