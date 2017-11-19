package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

/**
 * Created by Lasanga on 11/19/2017.
 */

public class PersistentAccountDAO implements AccountDAO {
    private DatabaseHelper dbHelper;

    public PersistentAccountDAO(Context context){
        dbHelper = new DatabaseHelper(context);
    }

    @Override
    public List<String> getAccountNumbersList() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT accountNo FROM Accounts", null);
        List accountNos = new ArrayList<>();

        while (res.moveToNext()){
            String AccNo = res.getString(res.getColumnIndexOrThrow("accountNo"));
            accountNos.add(AccNo);
        }
        res.close();
        return accountNos;
    }

    @Override
    public List<Account> getAccountsList() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM Accounts", null);
        List accounts = new ArrayList<>();
        while (res.moveToNext()){
            String accNo = res.getString(res.getColumnIndexOrThrow("accountNo"));
            String bankName = res.getString(res.getColumnIndexOrThrow("bankName"));
            String accHolder = res.getString(res.getColumnIndexOrThrow("accountHolder"));
            double balance = Double.parseDouble(res.getString(res.getColumnIndexOrThrow("balance")));

            Account account = new Account(accNo,bankName, accHolder, balance);
            accounts.add(account);
        }

        res.close();
        return accounts;
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM Accounts WHERE accountNo="+accountNo,null);
        Account account = null;

        while (res.moveToNext()){
            String accNo = res.getString(res.getColumnIndexOrThrow("accountNo"));
            String bankName = res.getString(res.getColumnIndexOrThrow("bankName"));
            String accHolder = res.getString(res.getColumnIndexOrThrow("accountHolder"));
            double balance = Double.parseDouble(res.getString(res.getColumnIndexOrThrow("balance")));

            account = new Account(accNo, bankName, accHolder, balance);
        }
        res.close();
        return account;
    }

    @Override
    public void addAccount(Account account) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("accountNo", account.getAccountNo());
        values.put("bankName", account.getBankName());
        values.put("accountHolder", account.getAccountHolderName());
        values.put("balance", account.getBalance());

        long result = db.insert("Accounts", null, values);
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("Accounts", "accountNo = ?", new String[] {accountNo});

    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Account account = getAccount(accountNo);

        double value = 0;

        switch (expenseType){
            case EXPENSE:
                value = account.getBalance() - amount;
                break;
            case INCOME:
                value = account.getBalance() + amount;
                break;
        }

        ContentValues values = new ContentValues();
        values.put("balance", value);

        db.update("Accounts", values, "accountNo = ?", new String[] {accountNo});

    }
}

