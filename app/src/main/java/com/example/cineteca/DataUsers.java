package com.example.cineteca;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataUsers extends SQLiteOpenHelper {

    private static final String NAME = "cineteca.db";
    private static final int VERSION = 1;

    private static final String TABLE_USERS = "usuarios";
    private static final String ID = "id";
    private static final String USERNAME = "username";
    private static final String EMAIL = "email";
    private static final String PASSWORD = "password";

    public DataUsers(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_USERS + " (" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                USERNAME + " TEXT UNIQUE, " +
                EMAIL + " TEXT UNIQUE, " +
                PASSWORD + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    public boolean registeruser(String username, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USERNAME, username);
        values.put(EMAIL, email);
        values.put(PASSWORD, password);

        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result != -1; // true si se guardó, false si el usuario/correo ya existe
    }

    public String getUsername(String userOrEmail) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + USERNAME + " FROM " + TABLE_USERS +
                " WHERE " + USERNAME + " = ? OR " + EMAIL + " = ?", new String[]{userOrEmail, userOrEmail});
        String name = userOrEmail;
        if (cursor.moveToFirst()) {
            name = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return name;
    }

    public boolean validateLogin(String userOrEmail, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + ID + " FROM " + TABLE_USERS +
                " WHERE (" + USERNAME + " = ? OR " + EMAIL + " = ?) AND " + PASSWORD + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{userOrEmail, userOrEmail, password});
        boolean exist = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exist;
    }

    public boolean updatePassword(String userOrEmail, String nuevaPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PASSWORD, nuevaPassword);

        int filasupdate = db.update(
                TABLE_USERS,
                values,
                USERNAME + " = ? OR " + EMAIL + " = ?",
                new String[]{userOrEmail, userOrEmail}
        );
        db.close();
        return filasupdate > 0; // true si encontró al usuario y cambió la contraseña
    }
}
