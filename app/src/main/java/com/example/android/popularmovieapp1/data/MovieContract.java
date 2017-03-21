package com.example.android.popularmovieapp1.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by ahmed on 2/24/2017.
 */

public class MovieContract {
    public static final String AUTHORITY = "com.example.android.popularmovieapp1";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_MOVIES = "movies";
    public static final class MovieEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();
        public static final String TABLE_NAME = "movies";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_RELEASE = "release";
        public static final String COLUMN_POSTER = "poster";
        public static final String COLUMN_RATE = "rate";
        public static final String COLUMN_MOVIE_ID = "movie_id";

    }

}
