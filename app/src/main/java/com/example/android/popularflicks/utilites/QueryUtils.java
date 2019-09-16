package com.example.android.popularflicks.utilites;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Handles network requests to receive a JSON Response
 * containing the required details of movies, trailers and reviews
 */

public final class QueryUtils {

    // The base url to be used to query the movie database
    private static final String BASE_URL = "https://api.themoviedb.org/3/movie";

    // Path to be used for popularity sorting
    private static final String PATH_POPULARITY = "popular";

    // Path to be used for rating sorting
    private static final String PATH_RATING = "top_rated";

    // Checks whether sort order is popularity
    public static boolean popularityIsChecked = true;

    // Checks whether sort order is rating
    public static boolean ratingIsChecked = false;

    // Path for trailers to be fetched in the detail activity
    private static final String PATH_VIDEOS = "videos";

    // Path for reviews to be fetched in the detail activity
    private static final String PATH_REVIEWS = "reviews";

    // API KEY key
    private static final String QUERY_API_KEY = "api_key";

    // TODO Enter API KEY value which is to be specified by the user
    private static final String QUERY_API_KEY_VALUE = "";



    /**
     * Checks if internet connectivity is present
     *
     * @return state of network connectivity
     */
    public static boolean isNetworkAvailable(Context context) {

        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    /**
     * Creates query URL for fetching data from the TMDB API
     *
     * @return the created query URL
     */
    public static URL createMainQueryUrl() {

        // Creating a URI object to build upon to create URL
        Uri uri;

        // Creates query with sort order of decreasing popularity
        if (popularityIsChecked) {
            uri = Uri.parse(BASE_URL).buildUpon()
                    .appendPath(PATH_POPULARITY)
                    .appendQueryParameter(QUERY_API_KEY, QUERY_API_KEY_VALUE)
                    .build();
        }

        // Creates query with top rated movies
        else {
            uri = Uri.parse(BASE_URL).buildUpon()
                    .appendPath(PATH_RATING)
                    .appendQueryParameter(QUERY_API_KEY, QUERY_API_KEY_VALUE)
                    .build();
        }

        // URL object to store the created url
        URL queryUrl = null;
        try {
            // Converts the URI to a URL
            queryUrl = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return queryUrl;
    }

    /**
     * Creates URL to query for reviews
     * @param id id of the movie
     * @return the created URL
     */
    public static URL createReviewsQueryUrl(String id){
        return createDetailQueryUrl(id,PATH_REVIEWS);
    }

    /**
     * Creates URL to query for trailers
     * @param id id of the movie
     * @return the created URL
     */
    public static URL createTrailersQueryUrl(String id){
        return createDetailQueryUrl(id, PATH_VIDEOS);
    }

    /**
     * Creates URL for reviews or trailers depending on the param pathToBeUsed
     * @param idOfMovie id of the movie being queried for
     * @param pathToBeUsed trailer or review path
     * @return the created URL
     */
    private static URL createDetailQueryUrl(String idOfMovie,String pathToBeUsed) {

        // Creating a URI object to build upon to create URL
        Uri uri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(idOfMovie)
                .appendPath(pathToBeUsed)
                .appendQueryParameter(QUERY_API_KEY, QUERY_API_KEY_VALUE)
                .build();

        // URL object to store the created url
        URL queryUrl = null;
        try {
            // Converts the URI to a URL
            queryUrl = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return queryUrl;
    }

    /**
     * Makes an HTTP GET request to query the TMDB database using the URL accepted as parameter
     *
     * @param url the url to be used to query the TMDB database
     * @return JSON Response received as a String
     * @throws IOException in case there are input/output discrepancies
     */
    public static String makeHttpRequest(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
