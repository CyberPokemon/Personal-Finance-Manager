package com.neuromancers.personalfinancemanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "finance_app.db";
    public static final int DB_VERSION = 3;

    private static final String CREATE_TABLE_FD = "CREATE TABLE IF NOT EXISTS fd (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "user_id INTEGER," +
            "principal REAL," +
            "rate REAL," +
            "time_months INTEGER," +
            "start_date TEXT," +
            "maturity_date TEXT," +
            "interest REAL," +
            "maturity_amount REAL)";


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

        db.execSQL("CREATE TABLE transaction_log (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER, " +
                "amount REAL, " +
                "type TEXT, " +
                "datetime TEXT, " +
                "description TEXT, " +
                "person TEXT, " +
                "account_name TEXT, " +
                "FOREIGN KEY(user_id) REFERENCES user(id))");

        db.execSQL(CREATE_TABLE_FD);



    }

    public boolean insertFD(long userId, double principal, double rate, int timeMonths,
                            String startDate, String maturityDate, double interest, double maturityAmount) {

        Log.d("FD_SAVE", "User ID: " + userId);


        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("principal", principal);
        values.put("rate", rate);
        values.put("time_months", timeMonths);
        values.put("start_date", startDate);
        values.put("maturity_date", maturityDate);
        values.put("interest", interest);
        values.put("maturity_amount", maturityAmount);

        long result = db.insert("fd", null, values);
        return result != -1;
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

    public boolean insertTransaction(long userId, double amount, String type, String datetime,
                                     String description, String person, String accountName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put("user_id", userId);
            values.put("amount", amount);
            values.put("type", type);
            values.put("datetime", datetime);
            values.put("description", description);
            values.put("person", person);
            values.put("account_name", accountName);

            long result = db.insert("transaction_log", null, values);

            if (result == -1) {
                return false;
            }

            if (accountName.equalsIgnoreCase("Cash")) {
                if (type.equalsIgnoreCase("outgoing")) {
                    db.execSQL("UPDATE user SET cash = cash - ? WHERE id = ?", new Object[]{amount, userId});
                } else if (type.equalsIgnoreCase("incoming")) {
                    db.execSQL("UPDATE user SET cash = cash + ? WHERE id = ?", new Object[]{amount, userId});
                }
            } else {
                if (type.equalsIgnoreCase("outgoing")) {
                    db.execSQL("UPDATE bank_account SET balance = balance - ? WHERE user_id = ? AND bank_name = ?",
                            new Object[]{amount, userId, accountName});
                } else if (type.equalsIgnoreCase("incoming")) {
                    db.execSQL("UPDATE bank_account SET balance = balance + ? WHERE user_id = ? AND bank_name = ?",
                            new Object[]{amount, userId, accountName});
                }
            }

            db.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            db.endTransaction();
        }
    }


    public List<Transaction> getAllTransactions(long userId) {
        List<Transaction> transactionList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM transaction_log WHERE user_id = ? ORDER BY datetime DESC",
                new String[]{String.valueOf(userId)});

        if (cursor != null && cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow("id"));
                double amount = cursor.getDouble(cursor.getColumnIndexOrThrow("amount"));
                String type = cursor.getString(cursor.getColumnIndexOrThrow("type"));
                String datetime = cursor.getString(cursor.getColumnIndexOrThrow("datetime"));
                String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                String person = cursor.getString(cursor.getColumnIndexOrThrow("person"));
                String accountName = cursor.getString(cursor.getColumnIndexOrThrow("account_name"));

                transactionList.add(new Transaction(id, userId, amount, type, datetime, description, person, accountName));
            } while (cursor.moveToNext());
            cursor.close();
        }

        return transactionList;
    }


    public List<FDModel> getFDsForUser(long userId) {
        List<FDModel> fdList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM fd WHERE user_id = ?", new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            do {
                FDModel fd = new FDModel();
                fd.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                fd.setPrincipal(cursor.getDouble(cursor.getColumnIndexOrThrow("principal")));
                fd.setRate(cursor.getDouble(cursor.getColumnIndexOrThrow("rate")));
                fd.setTimeMonths(cursor.getInt(cursor.getColumnIndexOrThrow("time_months")));
                fd.setStartDate(cursor.getString(cursor.getColumnIndexOrThrow("start_date")));
                fd.setMaturityDate(cursor.getString(cursor.getColumnIndexOrThrow("maturity_date")));
                fd.setInterest(cursor.getDouble(cursor.getColumnIndexOrThrow("interest")));
                fd.setMaturityAmount(cursor.getDouble(cursor.getColumnIndexOrThrow("maturity_amount")));
                fdList.add(fd);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return fdList;
    }



}
