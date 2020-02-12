package com.cz.usbserial.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {
    public DbHelper(Context context) {
        super(context, "db", (SQLiteDatabase.CursorFactory) null, 1);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE data(_id INTEGER PRIMARY KEY AUTOINCREMENT,presureUnit TEXT NOT NULL,tmpUnit TEXT NOT NULL,toggle TEXT NOT NULL,presureMax TEXT NOT NULL,presureMin TEXT NOT NULL,tmpMax TEXT NOT NULL)");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
