package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.Database;

/**
 * Created by Savindi Niranthara on 11/20/2016.
 * Contains the string constants used to create the database
 */
public class DBConstants {
    public static final String DB_NAME = "140294N_DB";      //database name
    public static final int DB_VERSION = 1;                 //database version

    //Account relation
    public static final String TABLE_ACCOUNT = "account";
    //attributes
    public static final String COL_ACC_NO = "accountNo";
    public static final String COL_BANK_NAME = "bankName";
    public static final String COL_ACC_HOLDER_NAME = "accountHolderName";
    public static final String COL_BALANCE = "balance";
    //create Account table
    /*
     * A better solution will be to use both account no and bank name as primary keys.
     * But in the transaction object only account number is used to identify the relevant account.
     * so it is assumed that account number alone is sufficient to identify a account entity
     */
    public static final String CREATE_TABLE_ACCOUNT = "CREATE TABLE "+TABLE_ACCOUNT+" (" +
            COL_ACC_NO +" TEXT PRIMARY KEY, " +
            COL_BANK_NAME +" TEXT, " +
            COL_ACC_HOLDER_NAME +" TEXT, " +
            COL_BALANCE +" REAL" +
            ")";


    //Transaction relation
    public static final String TABLE_TRANSACTION = "log";
    //attributes
    /*
     * An additional attribute transaction no is used to uniquely identify each transaction
     */
    public static final String COL_TRANSACTION_NO = "transactionNo";
    public static final String COL_TRANSACTION_ACC_NO = "transAccountNo";
    public static final String COL_EXPENSE_TYPE = "expenseType";
    public static final String COL_DATE = "transDate";
    public static final String COL_AMOUNT = "amount";
    //create Transaction table
    public static final String CREATE_TABLE_TRANSACTION = "CREATE TABLE "+TABLE_TRANSACTION+"(" +
            COL_TRANSACTION_NO + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL_TRANSACTION_ACC_NO +" TEXT, "+
            COL_EXPENSE_TYPE +" TEXT, " +
            COL_DATE +" TEXT, " +
            COL_AMOUNT +" REAL, " +
            "CHECK ("+COL_EXPENSE_TYPE+" IN ('EXPENSE', 'INCOME')), " +
            "FOREIGN KEY ("+COL_TRANSACTION_ACC_NO+") REFERENCES " +TABLE_ACCOUNT+"("+COL_ACC_NO+") "+
            ")";
}
