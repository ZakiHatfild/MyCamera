package com.example.zaki.mycamera.SQLite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Zaki on 10/19/2016.
 */

public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context) {

        super(context, "PointedFilesDB", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE pointed("
                + "name string,"
                + "path string" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

