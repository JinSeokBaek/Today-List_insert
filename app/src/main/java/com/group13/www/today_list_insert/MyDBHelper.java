package com.group13.www.today_list_insert;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by admin on 2017-05-31.
 */

public class MyDBHelper extends SQLiteOpenHelper{
    final static String _ID="_id";
    final static String _TITLE="title";
    final static String _DATE="date";
    final static String TABLE_NAME1="Memo";

    final static String _ID_M="_id_m";
    final static String _ALARM="_alarm";
    final static String _CHECK="_check";
    final static String _CONTEXT="context";
    final static String _CONTEXT_SUB="context_sub";
    final static String TABLE_NAME2="Context";
    final static String QUERY_SELECT_ALL1=String.format("select * from %s order by date asc",TABLE_NAME1);

    final static String QUERY_SELECT_ALL2=String.format("select * from %s",TABLE_NAME2);

    public MyDBHelper(Context context) {
        super(context, "MyData.db",null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
       String query1=String.format("create table %s(" +
                "%s integer primary key autoincrement," +
                "%s text," +
               "%s text);",TABLE_NAME1,_ID,_TITLE,_DATE);
        String query2=String.format("create table %s(" +
                "%s integer primary key autoincrement," +
                "%s integer," +
                "%s text," +
                "%s text," +
                "%s text," +
                "%s text);",TABLE_NAME2,_ID,_ID_M,_ALARM,_CHECK,_CONTEXT,_CONTEXT_SUB);
        db.execSQL(query1);
        db.execSQL(query2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
