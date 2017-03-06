package com.example.zaki.mycamera.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.zaki.mycamera.Models.FileModel;

import java.util.ArrayList;

/**
 * Created by Zaki on 3/2/2017.
 */

public class WorkSQLite {

    public static DBHelper dbHelper;

    public static void CountPoints(Context context) {
        dbHelper  = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor c = db.rawQuery("SELECT * FROM pointed", null);
        Log.d("xyi", c.getCount()+"");

        c.close();
        db.close();
    }

    public static void PutPoint(Context context, FileModel point) {
        dbHelper  = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("name", point.name);
        cv.put("path", point.path);

        db.insert("pointed", null, cv);

        db.close();
        //CountPoints(context);
    }

    public static void DeletePoint(Context context, FileModel point) {
        dbHelper  = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.execSQL("DELETE FROM pointed WHERE name = '" + point.name + "' AND path = '" + point.path + "'");

        db.close();
        //CountPoints(context);
    }

    public static boolean isPointChecked(Context context, FileModel point) {
        dbHelper  = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor c = db.query("pointed", null, "name = '" + point.name + "' AND path = '" + point.path + "'", null, null, null, null);

        boolean falag = false;
        if (c.moveToFirst()) falag = true;

        c.close();
        db.close();

        return falag;
    }

    public static void ClearPointed(Context context) {
        dbHelper  = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.rawQuery("DELETE FROM pointed", null);

        db.close();
    }

    public static ArrayList<FileModel> GetPoints(Context context) {
        dbHelper  = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor c = db.rawQuery("SELECT * FROM pointed", null);
        ArrayList<FileModel> rez = new ArrayList<>();

        if (c.moveToFirst()) {
            int name = c.getColumnIndex("name");
            int path = c.getColumnIndex("path");

            do {
                rez.add(new FileModel(
                        c.getString(name),
                        c.getString(path)));
                } while (c.moveToNext());
        }

        c.close();
        db.close();
        return rez;
    }
}
