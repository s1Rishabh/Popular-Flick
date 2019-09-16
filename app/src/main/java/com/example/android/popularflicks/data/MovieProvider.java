package com.example.android.popularflicks.data;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.android.popularflicks.data.MovieContract.MovieEntry;

/**
 * Content Provider for the favourite movies database
 */
public class MovieProvider extends ContentProvider {

    // Constant integers for the UriMatcher
    public static final int MOVIES = 100;
    public static final int MOVIE_WITH_ID = 101;

    // UriMatcher object
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    /**
     * Builds UriMatcher object with the given Uri formats
     * @return the UriMatcher object created
     */
    public static UriMatcher buildUriMatcher() {

        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_MOVIES, MOVIES);
        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_MOVIES + "/#", MOVIE_WITH_ID);

        return uriMatcher;
    }

    // MovieDbHelper object to open a database
    private MovieDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new MovieDbHelper(getContext());
        return true;
    }

    // Queries the database for movies
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {


        int match = sUriMatcher.match(uri);
        SQLiteDatabase mDb = mDbHelper.getReadableDatabase();
        Cursor retCursor;
        switch (match) {
            case MOVIE_WITH_ID:
            case MOVIES:
                retCursor = mDb.query(MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    // Inserts a new movie in the database
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        final SQLiteDatabase mDb = mDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        Uri returnUri;
        switch (match) {
            case MOVIES:

                long id = mDb.insert(MovieEntry.TABLE_NAME,
                        null,
                        values);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(MovieContract.MovieEntry.CONTENT_URI,
                            id);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    // Deletes a movie from the database
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase mDb = mDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);

        int rowsDeleted;
        switch (match) {
            case MOVIE_WITH_ID:
            case MOVIES:
                rowsDeleted = mDb.delete(MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI " + uri);
        }
        if (rowsDeleted > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    // Update operation is not required and is not implemented
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
