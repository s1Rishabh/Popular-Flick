package com.example.android.popularflicks.data;


import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Stores the parameters of the Movie objects to be inserted in the offline database
 */
public class MovieContract {

    // Authority of the content provider
    static final String AUTHORITY = "com.example.android.popularflicks";

    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    // Movies path for building the correct URI
    static final String PATH_MOVIES = "movies";

    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        // String constants for the different table parameters
        static final String TABLE_NAME = "movies";

        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_POSTER_PATH = "poster";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_SYNOPSIS = "synopsis";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_TMDB_ID = "id";

    }


}
