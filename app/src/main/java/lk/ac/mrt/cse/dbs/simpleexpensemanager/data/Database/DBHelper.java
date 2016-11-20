package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;

/**
 * This is the SQLiteOpenHelper implementation for using a SQLite database
 * Created by Savindi Niranthara on 11/20/2016.
 */
public class DBHelper extends SQLiteOpenHelper{

    public DBHelper(Context context){
        super(context, DBConstants.DB_NAME, null, DBConstants.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DBConstants.CREATE_TABLE_ACCOUNT);
        db.execSQL(DBConstants.CREATE_TABLE_TRANSACTION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+DBConstants.TABLE_ACCOUNT);
        db.execSQL("DROP TABLE IF EXISTS "+DBConstants.TABLE_TRANSACTION);
        onCreate(db);
    }
}
