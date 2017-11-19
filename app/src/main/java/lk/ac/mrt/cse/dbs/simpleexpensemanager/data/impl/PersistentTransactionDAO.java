package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

/**
 * Created by Lasanga on 11/19/2017.
 */

public class PersistentTransactionDAO implements TransactionDAO {
    private DatabaseHelper dbHelper;

    public PersistentTransactionDAO(Context context){
        dbHelper = new DatabaseHelper(context);
    }
    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("accountNo", accountNo);
        values.put("expenseType", expenseType.toString());
        values.put("amount", amount);
        values.put("date", date.toString());

        long result = db.insert("Transactions", null, values);

    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM Transactions", null);

        List transactions = new ArrayList<>();

        while (res.moveToNext()){
            String accountNo = res.getString(res.getColumnIndexOrThrow("accountNo"));
            String expenseType = res.getString(res.getColumnIndexOrThrow("expenceType"));
            double amount = Double.parseDouble(res.getString(res.getColumnIndexOrThrow("amount")));
            String date = res.getString(res.getColumnIndexOrThrow("date"));

            ExpenseType eType;

            if(expenseType.equals("EXPENSE")){
                eType = ExpenseType.EXPENSE;
            }
            else {
                eType = ExpenseType.INCOME;
            }

            Date d = null;
            try {
                d = new SimpleDateFormat("EEE MMM d HH:mm:ss zzz yyy").parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Transaction acc = new Transaction(d, accountNo, eType, amount);
            transactions.add(acc);
        }
        res.close();
        return transactions;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        long result = DatabaseUtils.queryNumEntries(db, "Transactions");
        if(limit<result){
            return getAllTransactionLogs();
        }
        else {
            Cursor res = db.rawQuery("SELECT * FROM Transactions", null);
            List transactions = new ArrayList<>();
            int rows = 0;

            while (res.moveToNext() && rows<limit){
                String accountNo = res.getString(res.getColumnIndexOrThrow("accountNo"));
                String expenseType = res.getString(res.getColumnIndexOrThrow("expenceType"));
                double amount = Double.parseDouble(res.getString(res.getColumnIndexOrThrow("amount")));
                String date = res.getString(res.getColumnIndexOrThrow("date"));

                ExpenseType eType;

                if(expenseType.equals("EXPENSE")){
                    eType = ExpenseType.EXPENSE;
                }
                else {
                    eType = ExpenseType.INCOME;
                }

                Date d = null;
                try {
                    d = new SimpleDateFormat("EEE MMM d HH:mm:ss zzz yyy").parse(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Transaction acc = new Transaction(d, accountNo, eType, amount);
                transactions.add(acc);
                rows++;
            }
            res.close();
            return transactions;
        }
    }
}
