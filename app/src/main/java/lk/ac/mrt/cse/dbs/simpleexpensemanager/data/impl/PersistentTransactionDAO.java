package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.Database.DBConstants;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.Database.DBHelper;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

/**
 * Created by Savindi Niranthara on 11/20/2016.
 */
public class PersistentTransactionDAO implements TransactionDAO {
    private DBHelper dbHelper;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
    public PersistentTransactionDAO(Context context){
        dbHelper = new DBHelper(context);
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBConstants.COL_TRANSACTION_ACC_NO, accountNo);
        values.put(DBConstants.COL_EXPENSE_TYPE, expenseType.toString());
        values.put(DBConstants.COL_DATE, sdf.format(date));
        values.put(DBConstants.COL_AMOUNT, amount);
        db.insert(DBConstants.TABLE_TRANSACTION, null, values);
        db.close();
    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        String query = "SELECT * FROM "+DBConstants.TABLE_TRANSACTION;
        List<Transaction> transactions = new LinkedList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(query,null);
        Transaction transaction;
        if(cursor.moveToLast()){
            do{
                Date date = null;
                try {
                    date = sdf.parse(cursor.getString(cursor.getColumnIndex(DBConstants.COL_DATE)));
                } catch (ParseException e) {}
                String accountNo = cursor.getString(cursor.getColumnIndex(DBConstants.COL_TRANSACTION_ACC_NO));
                ExpenseType expenseType = null;
                switch (cursor.getString(cursor.getColumnIndex(DBConstants.COL_EXPENSE_TYPE))){
                    case "EXPENSE":
                        expenseType = ExpenseType.EXPENSE;
                        break;
                    case "INCOME":
                        expenseType = ExpenseType.INCOME;
                        break;
                }
                double amount = cursor.getDouble(cursor.getColumnIndex(DBConstants.COL_AMOUNT));
                transaction = new Transaction(date,accountNo,expenseType,amount);
                transactions.add(transaction);
            }while (cursor.moveToPrevious());
        }
        cursor.close();
        db.close();
        return transactions;
    }

    /*
     * Returns the most recent transactions
     */
    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        long noOfRows = DatabaseUtils.queryNumEntries(db,DBConstants.TABLE_TRANSACTION);
        if(noOfRows <= limit){
            db.close();
            return getAllTransactionLogs();
        }else {
            String query = "SELECT * FROM "+DBConstants.TABLE_TRANSACTION;
            List<Transaction> transactions = new LinkedList<>();
            Cursor cursor = db.rawQuery(query,null);
            cursor.moveToLast();
            Transaction transaction;
            int count = 0;
            while (count < limit){
                Date date = null;
                try {
                    date = sdf.parse(cursor.getString(cursor.getColumnIndex(DBConstants.COL_DATE)));
                } catch (ParseException e) {}
                String accountNo = cursor.getString(cursor.getColumnIndex(DBConstants.COL_TRANSACTION_ACC_NO));
                ExpenseType expenseType = null;
                switch (cursor.getString(cursor.getColumnIndex(DBConstants.COL_EXPENSE_TYPE))){
                    case "EXPENSE":
                        expenseType = ExpenseType.EXPENSE;
                        break;
                    case "INCOME":
                        expenseType = ExpenseType.INCOME;
                        break;
                }
                double amount = cursor.getDouble(cursor.getColumnIndex(DBConstants.COL_AMOUNT));
                transaction = new Transaction(date,accountNo,expenseType,amount);
                transactions.add(transaction);
                count++;
                cursor.moveToPrevious();
            }
            cursor.close();
            db.close();
            return transactions;
        }
    }
}
