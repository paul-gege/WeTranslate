package com.example.wetranslate;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Favorites.db";
    public static final String TABLE_NAME = "favorites_table";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "INPUT_TEXT";
    public static final String COL_3 = "RESULT_TEXT";
    public static final String COL_4 = "LANGUAGE";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, INPUT_TEXT TEXT, RESULT_TEXT TEXT, LANGUAGE TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public ArrayList<Favorites> getAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        ArrayList<Favorites> all_favorites = new ArrayList<Favorites>();

        while(res.moveToNext()){
            Favorites current_favorite = new Favorites(res.getString(1), res.getString(2), res.getString(3));
            current_favorite.setId(Integer.parseInt(res.getString(0)));
            all_favorites.add(current_favorite);
        }
        return all_favorites;
    }

    public boolean insertData(Favorites new_favorite) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, new_favorite.getInput());
        contentValues.put(COL_3, new_favorite.getResult());
        contentValues.put(COL_4, new_favorite.getLanguage());

        long result = db.insert(TABLE_NAME, null, contentValues);

        if(result == -1){
            return false;
        } else {
            return true;
        }

    }

}
