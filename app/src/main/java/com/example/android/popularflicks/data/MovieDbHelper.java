package com.example.android.popularflicks.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.popularflicks.data.MovieContract.MovieEntry;

/**
 * Helps in the opening of a SQLite Database
 */
class MovieDbHelper extends SQLiteOpenHelper {

    // Name of the database
    private static final String DATABASE_NAME = "movies.db";

    // Version of the database
    private static final int VERSION = 1;

    // Constructor
    MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // SQL command String to create database table with the given parameters
        final String CREATE_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + " ("+
                MovieEntry._ID + " INTEGER PRIMARY KEY NOT NULL, " +
                MovieEntry.COLUMN_TITLE + " TEXT, " +

                MovieEntry.COLUMN_POSTER_PATH + " TEXT, " +
                MovieEntry.COLUMN_RATING + " TEXT, " +
                MovieEntry.COLUMN_TMDB_ID + " TEXT UNIQUE, " +
                MovieEntry.COLUMN_RELEASE_DATE + " TEXT, " +
                MovieEntry.COLUMN_SYNOPSIS + " TEXT);";

        // Execution of SQL command
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // Drop and recreate table if it exists
        db.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME + " ;");
        onCreate(db);
    }
}
