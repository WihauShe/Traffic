package cn.cslg.traffic.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context,String name,SQLiteDatabase.CursorFactory factory,int version){
        super(context, name, factory, version);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql1 = "create table user(_id integer primary key autoincrement,headimg integer,account text,password text,"+
                "age integer,sex text,email text)";
        db.execSQL(sql1);
        String sql2 = "create table city(_id integer primary key,name text,code text)";
        db.execSQL(sql2);
        String sql3 = "create table history(_id integer primary key autoincrement,type integer,city text,station text,route integer,start_station text,"+
                "end_station text,user_id integer)";
        db.execSQL(sql3);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql1 = "drop table if exists user";
        db.execSQL(sql1);
        String sql2 = "drop table if exists city";
        db.execSQL(sql2);
        String sql3 = "drop table if exists history";
        db.execSQL(sql3);
        onCreate(db);
    }
}
