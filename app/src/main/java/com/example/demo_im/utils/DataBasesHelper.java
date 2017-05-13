package com.example.demo_im.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2017/5/13.
 */

public class DataBasesHelper extends SQLiteOpenHelper {
    private String sql_create_message_table="create table if not exist message_records" +
            "(_id integer primary key antoincrement not null," +
            "identifier text not null," +
            "txt_message text not null)";
    private String sql_create_friend_table="create table if not exist friends" +
            "(_id integer primary key antoincrement not null," +
            "identifier text not null," +
            "nick text" +
            "remark text," +
            "face_url text)";


    public DataBasesHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(sql_create_message_table);
        db.execSQL(sql_create_friend_table);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }




}
