package com.neuromancers.personalfinancemanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "finance_app.db";
    public static final int DB_VERSION = 1;

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
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle DB upgrade
        db.execSQL("DROP TABLE IF EXISTS user");
        db.execSQL("DROP TABLE IF EXISTS bank_account");
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
}
