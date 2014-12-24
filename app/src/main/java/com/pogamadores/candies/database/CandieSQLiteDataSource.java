package com.pogamadores.candies.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.pogamadores.candies.domain.Token;

/**
 * Created by igorcferreira on 12/24/14.
 */
public class CandieSQLiteDataSource
{
    private SQLiteDatabase database;
    private SQLiteOpenHelper dbHelper;

    public CandieSQLiteDataSource(Context context) {
        dbHelper = new CandieSQLiteHelper(context);
    }

    private void open() throws SQLiteException {
        database = dbHelper.getWritableDatabase();
    }

    private SQLiteDatabase getWritableDatabase() throws SQLiteException {
        if(database == null)
            open();
        return database;
    }

    public void close() {
        dbHelper.close();
    }

    public Token getToken() {

        Cursor cursor =  getWritableDatabase().query(
                Token.DomainNamespace.TABLE_NAME,
                Token.getColumns(),
                null,
                null,
                null,
                null,
                null
        );

        if(cursor == null)
            return null;

        if(cursor.moveToFirst()) {
            Token token = new Token(cursor);
            if(!cursor.isClosed())cursor.close();
            return token;
        }

        return null;
    }

    public boolean saveToken(Token token) {
        boolean successfull = false;
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            long insertId = db.insert(
                    Token.DomainNamespace.TABLE_NAME,
                    null,
                    token.toContentValues()
            );
            if(insertId >= 0)
                successfull = true;
        } catch (Exception ex) {
            successfull = false;
        } finally {
            db.endTransaction();
        }

        return successfull;
    }

    public boolean updateToken(Token token) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor =  getWritableDatabase().query(
                Token.DomainNamespace.TABLE_NAME,
                Token.getColumns(),
                null,
                null,
                null,
                null,
                null
        );
        if(cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(Token.DomainNamespace.ID));
            if(!cursor.isClosed())cursor.close();
            if(id >= 0) {
                int updated = db.update(
                        Token.DomainNamespace.TABLE_NAME,
                        token.toContentValues(),
                        Token.DomainNamespace.ID + "=?",
                        new String[]{String.valueOf(id)}
                );
                if(updated > 0)
                    return true;
            }
        }
        return false;
    }

}
