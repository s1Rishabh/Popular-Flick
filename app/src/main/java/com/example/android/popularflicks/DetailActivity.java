package com.example.android.popularflicks;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularflicks.data.MovieContract.MovieEntry;
import com.example.android.popularflicks.utilites.JSONUtils;
import com.example.android.popularflicks.utilites.QueryUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Started when a movie is selected in the Main Activity
 * Displays information about the app such as user rating, synopsis etc.
 * Also displays views to read reviews and watch trailers of the movie
 */
public class DetailActivity extends AppCompatActivity implements LoaderCallbacks<Movie> {

    // Key for saved movie object
    private static final String MOVIE_KEY = "movie";

    // Saves instance of movie
    private static Movie saveInstanceMovie;

    // Binds the various TextViews,RecyclerView,View,ProgressBar and ImageView using ButterKnife

    // Displays view to open the review activity which displays reviews for the particular movie
    @BindView(R.id.tv_detail_review)
    TextView mReviewTextView;


    // Displays title of the movie
    @BindView(R.id.tv_detail_title)
    TextView mTitleTextView;

    // Displays synopsis of the movie
    @BindView(R.id.tv_detail_synopsis)
    TextView mSynopsisTextView;

    // Displays release date of the movie
    @BindView(R.id.tv_detail_release_date)
    TextView mReleaseDateTextView;

    // Displays user rating of the movie
    @BindView(R.id.tv_detail_user_rating)
    TextView mUserRatingTextView;

    // Displays poster of the movie
    @BindView(R.id.iv_detail_poster)
    ImageView mPosterImageView;

    // Displays a loading indicator while the reviews and trailers are being loaded
    @BindView(R.id.pb_detail_review_loading_indicator)
    ProgressBar mReviewLoadingIndicator;

    // Displays RecyclerView to display a list of the trailers
    @BindView(R.id.rv_detail_trailer)
    RecyclerView mTrailerRecyclerView;

    // A white line divider below the view to display reviews of the movie
    @BindView(R.id.white_line_below_read_reviews)
    View whiteLineBelowReadReviews;

    // Displays a "Watch:" label
    @BindView(R.id.tv_detail_watch_label)
    TextView mWatchLabelTextView;

    // TrailerAdapter object to populate the RecyclerView
    TrailerAdapter mTrailerAdapter;

    // A constant loader ID to identify the loader
    private static final int DETAIL_LOADER_ID = 643;

    // Boolean to keep track of the first onResume call
    // to hide the loading indicator on subsequent calls
    private static boolean shouldExecuteOnResume;

    // Creates a Movie object to access the attributes of the movie selected in the main activity
    private static Movie mSelectedMovie;
    private boolean isSavedMovie = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // First onResume call after onCreate; boolean is set to false
        shouldExecuteOnResume = false;
        // Gets a reference to the Intent that started the Detail Activity
        Intent startingIntent = getIntent();
        if (startingIntent != null) {

            // Details of movie are present
            if (startingIntent.hasExtra("Movie")) {

                // Binds the Views using ButterKnife
                ButterKnife.bind(this);

                if (savedInstanceState != null) {

                    // Gets the details of the saved Movie object
                    Movie movie = savedInstanceState.getParcelable(MOVIE_KEY);
                    Movie actualMovie = startingIntent.getParcelableExtra("Movie");
                    if (movie != null) {

                        // Check if wrong movie is not being replaced
                        if (movie.getTmdbId().equals(actualMovie.getTmdbId())) {
                            mSelectedMovie = movie;
                            isSavedMovie = true;
                        }
                    }
                    else{
                        mSelectedMovie = startingIntent.getParcelableExtra("Movie");
                    }
                } else {
                    // Gets the details of the Movie object for the Movie selected in MainActivity
                    mSelectedMovie = startingIntent.getParcelableExtra("Movie");
                }
                // Loads the image for the poster using Picasso
                Picasso.with(this)
                        .load(mSelectedMovie.getPosterPath())
                        .into(mPosterImageView);

                // Setting the text for the TextViews
                mTitleTextView.setText(mSelectedMovie.getTitle());
                mSynopsisTextView.setText(mSelectedMovie.getSynopsis());
                mReleaseDateTextView.setText(mSelectedMovie.getReleaseDate());
                mUserRatingTextView.setText(mSelectedMovie.getUserRating());


                // Loads the trailer and review data if network is available
                // and the favourites view is not selected
                // Uses saved Movie object if it exists
                if (!startingIntent.getBooleanExtra("Favourite", false)) {

                    if (isSavedMovie) {
                        setTrailerAdapter();
                        if (mSelectedMovie.getReviews() != null || mSelectedMovie.getTrailers() != null) {
                            setReviewData(mSelectedMovie);
                            setTrailerData(mSelectedMovie);
                        } else {
                            // Initialises Loader
                            getSupportLoaderManager().initLoader(DETAIL_LOADER_ID, null, this);

                        }
                    } else if (QueryUtils.isNetworkAvailable(this)) {

                        // Initialises Loader
                        getSupportLoaderManager().initLoader(DETAIL_LOADER_ID, null, this);

                        setTrailerAdapter();
                    }
                } else {

                    // If conditions are not met, the review views are hidden
                    mReviewLoadingIndicator.setVisibility(View.GONE);
                    mReviewTextView.setVisibility(View.GONE);
                    whiteLineBelowReadReviews.setVisibility(View.GONE);
                }
            }
        }

    }

    public void setTrailerAdapter() {

        // The RecyclerView has items of fixed size
        mTrailerRecyclerView.setHasFixedSize(true);

        // Setting layout manager for the RecyclerView to a new LinearLayoutManager
        mTrailerRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // TrailerAdapter object to populate the RecyclerView
        mTrailerAdapter = new TrailerAdapter(this);

        // Wiring up the RecyclerView with the TrailerAdapter
        mTrailerRecyclerView.setAdapter(mTrailerAdapter);

    }

    /**
     * Inverts the favourite star icon in the action bar menu
     *
     * @param item the menu item for the favourite action
     */
    public void invertFavouriteIcon(MenuItem item) {


        // If the movie is added to favourites, the icon is set to a solid white star
        if (mSelectedMovie.isFavourite()) {
            item.setIcon(R.drawable.ic_star_white_24dp);
            Toast.makeText(DetailActivity.this, R.string.added_to_favourites, Toast.LENGTH_SHORT).show();
        }
        // If the movie is removed from favourites, the icon is set to a white border star
        else {
            item.setIcon(R.drawable.ic_star_border_white_24dp);
            Toast.makeText(DetailActivity.this, R.string.removed_from_favourites, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Corrects the favourite status of the movie when the activity is resumed
     *
     * @param menu the menu being displayed in the activity
     * @return default behaviour of super class
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        // Gets reference to the favourite menu item
        MenuItem favourite = menu.findItem(R.id.menu_detail_action_favourite);

        // If the movie is a favourite, the star is set to a white solid star
        if (checkMovieInDatabase()) {
            favourite.setIcon(R.drawable.ic_star_white_24dp);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Creates the Menu for the activity
     * Displays the favourite action in the menu
     *
     * @param menu the menu being displayed in the activity
     * @return true if the menu is created
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflates the menu from its XML file
        getMenuInflater().inflate(R.menu.detail, menu);

        return true;
    }

    /**
     * Handles behavior when the star is clicked.
     *
     * @param item the favourite action
     * @return true if the selection is handled correctly or super class behaviour
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Resource ID of the item selected
        int itemSelected = item.getItemId();

        switch (itemSelected) {
            case R.id.menu_detail_action_favourite:

                // Insert or delete the movie from the database
                // depending on whether it exists in it or not
                if (!checkMovieInDatabase()) {
                    insertInDatabase();
                } else {
                    deleteFromDatabase();
                }
                invertFavouriteIcon(item);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Checks whether the selected movie exists in the database or not i.e.
     * checks if the movie is a favourite or not
     *
     * @return true if the movie exists in the database
     */
    private boolean checkMovieInDatabase() {

        // Fields of the movie to be checked in the database i.e. the TMDB ID of the movie
        String[] projection = {MovieEntry.COLUMN_TMDB_ID};

        // Cursor to store the movie data
        Cursor checkMovieCursor = null;

        // Selection for the query operation
        String selection = MovieEntry.COLUMN_TMDB_ID + "=?";

        // SelectionArgs for the query operation
        String[] selectionArgs = {mSelectedMovie.getTmdbId()};

        try {

            // Query operation on the Content Provider
            // to check if the movie with the particular TMDB ID exists in the database
            checkMovieCursor = getContentResolver().query(MovieEntry.CONTENT_URI,
                    projection,
                    selection,
                    selectionArgs,
                    null);

            // If the cursor is not empty, the movie exists inside the database
            if (checkMovieCursor.getCount() != 0) {

                // Set the movie as a favourite
                mSelectedMovie.setFavourite(true);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Close the cursor if it is not null
            if (checkMovieCursor != null) {
                checkMovieCursor.close();
            }
        }
        return false;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (saveInstanceMovie != null) {
            outState.putParcelable(MOVIE_KEY, saveInstanceMovie);
        }
    }

    /**
     * Inserts the movie in the database stored on the device locally
     * Sets the movie as a favourite
     */
    private void insertInDatabase() {

        // ContentValues object to insert the movie in the database
        ContentValues values = new ContentValues();

        // Putting the attributes of the movie in the ContentValues object
        values.put(MovieEntry.COLUMN_TITLE, mSelectedMovie.getTitle());
        values.put(MovieEntry.COLUMN_POSTER_PATH, mSelectedMovie.getPosterPath());
        values.put(MovieEntry.COLUMN_RATING, mSelectedMovie.getUserRating());
        values.put(MovieEntry.COLUMN_RELEASE_DATE, mSelectedMovie.getReleaseDate());
        values.put(MovieEntry.COLUMN_SYNOPSIS, mSelectedMovie.getSynopsis());
        values.put(MovieEntry.COLUMN_TMDB_ID, mSelectedMovie.getTmdbId());

        // Uri to insert the ContentValues object
        Uri insertUri = getContentResolver().insert(MovieEntry.CONTENT_URI, values);

        // Setting the DbId of the movie to the id where the ContentValues was inserted
        int id = (int) ContentUris.parseId(insertUri);
        mSelectedMovie.setDbId(id);

        // Sets the movie as a favourite
        mSelectedMovie.setFavourite(true);
    }


    /**
     * Deletes the movie from the database stored on the device locally
     * Removes the movie as a favourite
     */
    private void deleteFromDatabase() {

        // Selection and selectionArgs to delete just the selected movie from the database
        String selection = MovieEntry.COLUMN_TMDB_ID + "=?";
        String id = String.valueOf(mSelectedMovie.getTmdbId());
        String[] selectionArgs = {id};

        // Deletes the movie from the database using the Content Provider
        getContentResolver().delete
                (MovieEntry.CONTENT_URI,
                        selection,
                        selectionArgs);

        // Removes the database ID from the movie object
        mSelectedMovie.clearDbId();

        // Removes the movie as a favourite
        mSelectedMovie.setFavourite(false);
    }

    @Override
    public Loader<Movie> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<Movie>(this) {
            @Override
            protected void onStartLoading() {

                // Displays the loading indicator and loads the review and trailer data
                mReviewLoadingIndicator.setVisibility(View.VISIBLE);
                super.onStartLoading();
                forceLoad();
            }

            @Override
            public Movie loadInBackground() {

                // URLs for review and trailer queries
                URL reviewsQueryUrl = QueryUtils.createReviewsQueryUrl(mSelectedMovie.getTmdbId());
                URL trailersQueryUrl = QueryUtils.createTrailersQueryUrl(mSelectedMovie.getTmdbId());

                // Strings to store the JSON Responses received from the query
                String reviewsJsonResponse;
                String trailersJsonResponse;

                try {

                    // Http requests are made and the returned JSON Responses are stored in Strings
                    reviewsJsonResponse = QueryUtils.makeHttpRequest(reviewsQueryUrl);
                    trailersJsonResponse = QueryUtils.makeHttpRequest(trailersQueryUrl);

                    // The review and trailer data is stored in the selected movie object
                    mSelectedMovie = JSONUtils.extractReviewsFromJson(mSelectedMovie, reviewsJsonResponse);
                    mSelectedMovie = JSONUtils.extractTrailersFromJson(mSelectedMovie, trailersJsonResponse);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
                saveInstanceMovie = mSelectedMovie;
                return mSelectedMovie;
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Hides the loading indicator on the second and subsequent resumes
        // as the data is already loaded
        if (shouldExecuteOnResume) {
            mReviewLoadingIndicator.setVisibility(View.INVISIBLE);
        } else {
            shouldExecuteOnResume = true;
        }
    }

    @Override
    public void onLoadFinished(Loader<Movie> loader, final Movie data) {

        // Hides the loading indicator
        mReviewLoadingIndicator.setVisibility(View.INVISIBLE);

        setReviewData(data);
        setTrailerData(data);
    }

    /**
     * Sets the review views to be displayed
     *
     * @param data the Movie object containing details
     */
    public void setReviewData(final Movie data) {
        // Checks if review data exists
        if (data.getReviews().size() != 0 && data.getReviews() != null) {

            // Displays the review count
            int reviewCount = data.getReviews().size();
            mReviewTextView.setText(getString(R.string.read_reviews, reviewCount));

            // Displays the divider
            whiteLineBelowReadReviews.setVisibility(View.VISIBLE);

            // Starts the review activity which displays reviews of the movie
            mReviewTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startReviewActivity(data);
                }
            });
        }

    }

    /**
     * Sets the trailer views to be displayed
     *
     * @param data the Movie object containing details
     */
    public void setTrailerData(Movie data) {
        // Checks if trailer data exists
        if (data.getTrailers().size() != 0 && data.getTrailers() != null) {

            // Displays the trailer data
            mWatchLabelTextView.setVisibility(View.VISIBLE);
            ArrayList<Movie.Trailer> mTrailers = data.getTrailers();
            mTrailerAdapter.setTrailerData(mTrailers);
        }
    }

    /**
     * Starts the ReviewActivity to display the reviews of the movie
     *
     * @param data Movie object which contains the reviews of the movie
     */
    private void startReviewActivity(Movie data) {
        Intent startReviewActivityIntent = new Intent(DetailActivity.this, ReviewActivity.class);
        startReviewActivityIntent.putExtra("Reviews", data.getReviews());
        startActivity(startReviewActivityIntent);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        // Do nothing; Overriden to implement LoaderCallbacks
    }
}
