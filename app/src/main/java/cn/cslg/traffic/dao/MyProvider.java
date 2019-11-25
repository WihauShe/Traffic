package cn.cslg.traffic.dao;

import android.content.*;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class MyProvider extends ContentProvider {
    private ContentResolver resolver;
    private DBHelper dbHelper;
    private SQLiteDatabase db;
    private static UriMatcher matcher;
    static {
        matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI("cn.cslg.traffic.dao.MyProvider","user",1);
        matcher.addURI("cn.cslg.traffic.dao.MyProvider","city",2);
        matcher.addURI("cn.cslg.traffic.dao.MyProvider","history",3);
    }
    @Override
    public boolean onCreate() {
        dbHelper = new DBHelper(getContext(),"traffic.db",null,1);
        db = dbHelper.getReadableDatabase();
        resolver = getContext().getContentResolver();
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        int code=matcher.match(uri);
        Cursor cursor = null;
        switch (code){
            case 1:cursor = db.query("user",projection,selection,selectionArgs,null,null,sortOrder);break;
            case 2:cursor = db.query("city",projection,selection,selectionArgs,null,null,sortOrder);break;
            case 3:cursor = db.query("history",projection,selection,selectionArgs,null,null,sortOrder);break;
            default:System.out.println("It's an unknown uri");break;
        }
        cursor.setNotificationUri(resolver,uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        int code=matcher.match(uri);
        switch (code){
            case 1:db.insert("user",null,values);break;
            case 2:db.insert("city",null,values);break;
            case 3:db.insert("history",null,values);break;
            default:System.out.println("It's an unknown uri");break;
        }
        resolver.notifyChange(uri,null);
        return uri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int code=matcher.match(uri);
        int rowsDeleted = 0;
        switch (code){
            case 1:rowsDeleted = db.delete("user",selection,selectionArgs);break;
            case 2:rowsDeleted = db.delete("city",selection,selectionArgs);break;
            case 3:rowsDeleted = db.delete("history",selection,selectionArgs);break;
            default:System.out.println("It's an unknown uri");break;
        }
        if(rowsDeleted!=0)
            resolver.notifyChange(uri,null);
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int code=matcher.match(uri);
        int rowsUpdated = 0;
        switch (code){
            case 1:rowsUpdated = db.update("user",values,selection,selectionArgs);break;
            case 2:rowsUpdated = db.update("city",values,selection,selectionArgs);break;
            case 3:rowsUpdated = db.update("history",values,selection,selectionArgs);break;
            default:System.out.println("It's an unknown uri");break;
        }
        if(rowsUpdated != 0)
            resolver.notifyChange(uri,null);
        return rowsUpdated;
    }
}
