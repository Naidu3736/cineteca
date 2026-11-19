package com.example.cineteca;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class MovieDbHelper extends SQLiteOpenHelper {

    // Cambiar a "movies.db" para evitar conflicto con la BD de usuarios
    private static final String DB_NAME = "movies.db";
    private static final int DB_VERSION = 1;

    private static final String TABLE = "movies";
    private static final String COL_ID = "id";
    private static final String COL_TITLE = "title";
    private static final String COL_RATING = "rating";
    private static final String COL_POSTER = "poster_url";

    public MovieDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_TITLE + " TEXT NOT NULL, "
                + COL_RATING + " REAL, "
                + COL_POSTER + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }

    public long insert(Movie movie) {
        ContentValues values = new ContentValues();
        values.put(COL_TITLE, movie.getTitle());
        values.put(COL_RATING, movie.getRating());
        values.put(COL_POSTER, movie.getPosterUrl());
        return getWritableDatabase().insert(TABLE, null, values);
    }

    public int update(Movie movie) {
        ContentValues values = new ContentValues();
        values.put(COL_TITLE, movie.getTitle());
        values.put(COL_RATING, movie.getRating());
        values.put(COL_POSTER, movie.getPosterUrl());
        return getWritableDatabase().update(TABLE, values, COL_ID + " = ?",
                new String[]{String.valueOf(movie.getId())});
    }

    public int delete(long id) {
        return getWritableDatabase().delete(TABLE, COL_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    public List<Movie> getAll() {
        List<Movie> movies = new ArrayList<>();
        try (Cursor c = getReadableDatabase().query(TABLE, null, null, null,
                null, null, COL_TITLE + " COLLATE NOCASE ASC")) {
            while (c.moveToNext()) {
                movies.add(new Movie(
                        c.getLong(c.getColumnIndexOrThrow(COL_ID)),
                        c.getString(c.getColumnIndexOrThrow(COL_TITLE)),
                        c.getDouble(c.getColumnIndexOrThrow(COL_RATING)),
                        c.getString(c.getColumnIndexOrThrow(COL_POSTER))));
            }
        }
        return movies;
    }
}