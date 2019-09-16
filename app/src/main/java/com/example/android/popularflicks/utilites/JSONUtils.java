package com.example.android.popularflicks.utilites;

import com.example.android.popularflicks.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Parses the JSON Response received by querying the TMDB database to obtain usable data
 */

public final class JSONUtils {

    // Key for poster path attribute
    private static final String TMDB_POSTER_PATH = "poster_path";

    // Base path to be used to obtain poster path
    private static final String posterBasePath = "https://image.tmdb.org/t/p/w300/";

    // Key for release date attribute
    private static final String TMDB_RELEASE_DATE = "release_date";

    // Key for title attribute
    private static final String TMDB_ORIGINAL_TITLE = "original_title";

    // Key for synopsis attribute
    private static final String TMDB_SYNOPSIS = "overview";

    // Key for user rating attribute
    private static final String TMDB_VOTE_AVERAGE = "vote_average";

    // Key for id attribute
    private static final String TMDB_ID = "id";

    // Key for results obtained in the JSON data
    private static final String TMDB_RESULTS = "results";

    // Key for author name of the review
    private static final String TMDB_REVIEWS_AUTHOR = "author";

    // Key for review content attribute
    private static final String TMDB_REVIEWS_CONTENT = "content";

    // Key for type of video attribute
    private static final String TMDB_TRAILERS_TYPE_KEY = "type";

    // Value for trailer type of video attribute
    private static final String TMDB_TRAILERS_TYPE_VALUE = "Trailer";

    // Key for link of video attribute
    private static final String TMDB_TRAILERS_LINK_KEY = "key";

    // Key for site on which the trailer exists
    private static final String TMDB_TRAILERS_SITE_KEY = "site";

    // Key for name of the trailer
    private static final String TMDB_TRAILERS_NAME_KEY = "name";

    // Value for YouTube for the site key
    private static final String TMDB_TRAILERS_SITE_VALUE = "YouTube";

    // Base link for trailers on YouTube
    private static final String trailerBasePath = "https://www.youtube.com/watch?v=";

    /**
     * Converts the JSON response to usable data
     *
     * @param jsonResponse the JSON response received from querying the TMDB database
     * @return ArrayList containing data of movies
     * @throws JSONException incase the JSON is not correctly formatted
     */
    public static ArrayList<Movie> extractMoviesFromJson(String jsonResponse) throws JSONException {

        ArrayList<Movie> listOfMovies = new ArrayList<>();

        // Required to parse the JSON Response
        JSONObject parentObject = new JSONObject(jsonResponse);
        JSONArray results = parentObject.getJSONArray(TMDB_RESULTS);

        // Receives values of the attributes of movies
        for (int index = 0; index < results.length(); index++) {

            JSONObject movie = results.getJSONObject(index);

            String posterPath = posterBasePath + movie.getString(TMDB_POSTER_PATH);
            String releaseData = movie.getString(TMDB_RELEASE_DATE);
            String title = movie.getString(TMDB_ORIGINAL_TITLE);
            String userRating = movie.getString(TMDB_VOTE_AVERAGE);
            String synopsis = movie.getString(TMDB_SYNOPSIS);
            String id = movie.getString(TMDB_ID);

            // Adds the data of the movie to the ArrayList to be returned
            listOfMovies.add(new Movie(title, synopsis, posterPath, releaseData, userRating, id));
        }

        return listOfMovies;
    }

    /**
     * Extracts required fields of the reviews from the JSON Response
     * @param selectedMovie the selected movie to get reviews from
     * @param jsonResponse the JSON Response received
     * @return the movie object after addition of reviews
     * @throws JSONException might arise while parsing JSON
     */
    public static Movie extractReviewsFromJson(Movie selectedMovie, String jsonResponse) throws JSONException {

        JSONObject parentObject = new JSONObject(jsonResponse);
        JSONArray results = parentObject.getJSONArray(TMDB_RESULTS);

        for (int index = 0; index < results.length(); index++) {
            JSONObject review = results.getJSONObject(index);
            String author = review.getString(TMDB_REVIEWS_AUTHOR);
            String content = review.getString(TMDB_REVIEWS_CONTENT);

            selectedMovie.addReview(author, content);
        }
        return selectedMovie;
    }
    /**
     * Extracts required fields of the trailers from the JSON Response
     * @param selectedMovie the selected movie to get trailers from
     * @param jsonResponse the JSON Response received
     * @return the movie object after addition of trailers
     * @throws JSONException might arise while parsing JSON
     */
    public static Movie extractTrailersFromJson(Movie selectedMovie, String jsonResponse) throws JSONException {


        JSONObject parentObject = new JSONObject(jsonResponse);
        JSONArray results = parentObject.getJSONArray(TMDB_RESULTS);

        for (int index = 0; index < results.length(); index++)

        {
            JSONObject video = results.getJSONObject(index);
            String site = video.getString(TMDB_TRAILERS_SITE_KEY);
            String type = video.getString(TMDB_TRAILERS_TYPE_KEY);

            // Only YouTube videos which are trailers are required
            if (site.equals(TMDB_TRAILERS_SITE_VALUE) && type.equals(TMDB_TRAILERS_TYPE_VALUE)) {
                String title = video.getString(TMDB_TRAILERS_NAME_KEY);
                String link = trailerBasePath + video.getString(TMDB_TRAILERS_LINK_KEY);

                selectedMovie.addTrailer(link, title);
            }
        }
        return selectedMovie;
    }
}
