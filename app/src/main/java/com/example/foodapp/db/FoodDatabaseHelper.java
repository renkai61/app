package com.example.foodapp.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;        //繼承這個
//管理食物的資料庫.

public class FoodDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "food.db";      //資料庫檔案名稱
    private static final int DATABASE_VERSION = 2;      //版本號

    //資料表
    public static final String TABLE_NAME = "FoodItem";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_IMAGE_PATH = "image_path";  // 假設欄位叫 image_path

    public static final String COLUMN_PHOTO_URI = "photo_uri";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_QUANTITY = "quantity";
    public static final String COLUMN_EXPIRY_DATE = "expiry_date";
    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME + " TEXT, " +
                    COLUMN_TYPE + " TEXT, " +
                    COLUMN_QUANTITY + " INTEGER, " +
                    COLUMN_EXPIRY_DATE + " TEXT, " +
                    COLUMN_PHOTO_URI + " TEXT);";  // 新增此行


    public FoodDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override                   //當資料庫第一次被創建時執行，執行建表 SQL 語句
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override                  //當資料庫版本升級時執行，採用簡單的升級策略：刪除舊表並重新創建
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
