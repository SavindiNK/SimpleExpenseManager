package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.LinkedList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.Database.DBConstants;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.Database.DBHelper;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

/**
 * Created by Savindi Niranthara on 11/20/2016.
 */
public class PersistentAccountDAO implements AccountDAO {

    private DBHelper dbHelper;

    public PersistentAccountDAO(Context context){
        dbHelper = new DBHelper(context);
    }

    @Override
    public List<String> getAccountNumbersList() {
        String query = "SELECT "+DBConstants.COL_ACC_NO+" FROM "+DBConstants.TABLE_ACCOUNT;
        List<String> accountNumbers = new LinkedList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(query,null);
        if(cursor.moveToFirst()){
            do {
                accountNumbers.add(cursor.getString(cursor.getColumnIndex(DBConstants.COL_ACC_NO)));
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return accountNumbers;
    }

    @Override
    public List<Account> getAccountsList() {
        String query = "SELECT * FROM "+DBConstants.TABLE_ACCOUNT;
        List<Account> accounts = new LinkedList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(query,null);
        Account account = null;
        if(cursor.moveToFirst()){
            do {
                String accountNo = cursor.getString(cursor.getColumnIndex(DBConstants.COL_ACC_NO));
                String bankName = cursor.getString(cursor.getColumnIndex(DBConstants.COL_BANK_NAME));
                String accountHolderName = cursor.getString(cursor.getColumnIndex(DBConstants.COL_ACC_HOLDER_NAME));
                double balance = cursor.getDouble(cursor.getColumnIndex(DBConstants.COL_BALANCE));
                account = new Account(accountNo,bankName,accountHolderName,balance);
                accounts.add(account);
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return accounts;
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {DBConstants.COL_BANK_NAME, DBConstants.COL_ACC_HOLDER_NAME, DBConstants.COL_BALANCE};
        String selection = DBConstants.COL_ACC_NO + " = ? ";
        String[] selectionArgs = {accountNo};

        Cursor cursor = db.query(DBConstants.TABLE_ACCOUNT, projection, selection, selectionArgs, null, null, null);
        if(cursor == null){
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }else {
            cursor.moveToFirst();
            String bankName = cursor.getString(cursor.getColumnIndex(DBConstants.COL_BANK_NAME));
            String accountHolderName = cursor.getString(cursor.getColumnIndex(DBConstants.COL_ACC_HOLDER_NAME));
            double balance = cursor.getDouble(cursor.getColumnIndex(DBConstants.COL_BALANCE));
            Account account = new Account(accountNo,bankName,accountHolderName,balance);
            cursor.close();
            return account;
        }
    }

    @Override
    public void addAccount(Account account){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBConstants.COL_ACC_NO, account.getAccountNo());
        values.put(DBConstants.COL_BANK_NAME, account.getBankName());
        values.put(DBConstants.COL_ACC_HOLDER_NAME, account.getAccountHolderName());
        values.put(DBConstants.COL_BALANCE, account.getBalance());
        db.insert(DBConstants.TABLE_ACCOUNT,null,values);
        db.close();
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String selection = DBConstants.COL_ACC_NO + " LIKE ? ";
        String[] selectionArgs = {accountNo};
        int noOfRowsDeleted = db.delete(DBConstants.TABLE_ACCOUNT, selection, selectionArgs);
        db.close();
        if(noOfRowsDeleted == 0){
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Account account = getAccount(accountNo);
        String selection = DBConstants.COL_ACC_NO + " LIKE ? ";
        String[] selectionArgs = {accountNo};
        ContentValues values = new ContentValues();
        switch (expenseType) {
            case EXPENSE:
                values.put(DBConstants.COL_BALANCE, account.getBalance() - amount);
                break;
            case INCOME:
                values.put(DBConstants.COL_BALANCE, account.getBalance() + amount);
                break;
        }
        int noOfRowsUpdated = db.update(DBConstants.TABLE_ACCOUNT, values, selection, selectionArgs);
        db.close();
        if(noOfRowsUpdated == 0){
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }
    }
}
