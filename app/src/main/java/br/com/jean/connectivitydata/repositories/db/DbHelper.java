package br.com.jean.connectivitydata.repositories.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "connectivity_data.db";
    private static final int DATABASE_VERSION = 1;
    private static final String CREATE_TABLE_CONNECTIVITY = "CREATE TABLE connectivity" +
            "(" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "wifi REAL," +
            "movel REAL," +
            "latitude REAL," +
            "longitude REAL," +
            "network_type INTEGER," +
            "level INTEGER," +
            "is_synchronized INTEGER" +
            ")";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_CONNECTIVITY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
