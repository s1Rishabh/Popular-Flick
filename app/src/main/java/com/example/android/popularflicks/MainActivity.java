package com.example.android.popularflicks;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularflicks.data.MovieContract.MovieEntry;
import com.example.android.popularflicks.utilites.JSONUtils;
import com.example.android.popularflicks.utilites.QueryUtils;

import java.net.URL;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Displays a grid of popular movies or top rated movies based on the option selected
 * <p>
 * Selecting a movie from the grid launches a detail activity which displays additional information
 * about the movie
 */
public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler,
        LoaderManager.LoaderCallbacks<ArrayList<Movie>> {

    // Binding TextViews, RecyclerView, ProgressBar using ButterKnife

    // Displays error message if any
    @BindView(R.id.tv_error_message)
    TextView mErrorMessageTextView;

    // Displays network error message if any
    @BindView(R.id.tv_network_error_message)
    TextView mNetworkErrorTextView;

    // Displayed while data is fetched from the TMDB API
    @BindView(R.id.pb_loading_indicator)
    ProgressBar mLoadingIndicator;

    // RecyclerView object
    @BindView(R.id.rv_main)
    RecyclerView mRecyclerView;

    // Displayed if the favourites option is selected and there are no favourites selected
    @BindView(R.id.ll_favourites_empty_view_button)
    LinearLayout mFavouritesEmptyView;

    // Button to load popular movies; displayed on favourites view
    // when there are no favourites selected
    @BindView(R.id.button_load_popular_movies)
    Button mLoadMoviesButton;


    // MovieAdapter object
    private MovieAdapter mMovieAdapter;

    // Menu object
    private Menu mMenu;

    // Constant identifier for Loader to load data from the TMDB API response
    private static final int URL_LOADER_ID = 589;

    // Constant identifier for Loader to load data from the favourites stored in an offline database
    private static final int DB_LOADER_ID = 756;

    // Boolean to keep track of whether the favourites screen is selected or not
    private static boolean isFavouritesScreen = false;

    // Boolean to keep track of whether the main activity is being resumed for the first time or not
    private static boolean isNotFirstResume;

    private static final String MOVIES_KEY = "movies";

    private static final String SCROLL_POSITION_KEY = "position";
    private ArrayList<Movie> saveInstanceMovies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // onCreate being called, this is the first time onResume is called
        isNotFirstResume = false;

        // Binding Views using ButterKnife in the MainActivity
        ButterKnife.bind(this);

        // The RecyclerView will have views of fixed size only
        mRecyclerView.setHasFixedSize(true);

        // Creating grid layout for the RecyclerView and setting number of columns 2 for portrait
        // orientation and 3 for landscape
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        } else {
            mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        }

        // Creating MovieAdapter object
        mMovieAdapter = new MovieAdapter(this);

        // Wiring up the RecyclerView with the MovieAdapter
        mRecyclerView.setAdapter(mMovieAdapter);

        // Using saved data
        if (savedInstanceState != null) {


            // Sets the scroll position to the saved position
            int scrollPosition = savedInstanceState.getInt(SCROLL_POSITION_KEY);
            ((GridLayoutManager) mRecyclerView.getLayoutManager())
                    .scrollToPosition(scrollPosition);

            // Sets the saved list of movies
            ArrayList<Movie> movies = savedInstanceState.getParcelableArrayList(MOVIES_KEY);
            if (movies != null) {
                mMovieAdapter.setMovieData(movies);
            } else {
                populateActivity();
            }
        } else {
            populateActivity();
        }
    }

    /**
     * Loads list of movies
     */
    void populateActivity() {

        // Loads a list of movies to be displayed in a grid using a Loader
        if (QueryUtils.ratingIsChecked || QueryUtils.popularityIsChecked) {
            // List is loaded using the TMDB API
            loadMovieData();
        } else {
            // List of favourites movie is loaded from the offline favourites database
            loadMovieDataFromDatabase();
        }
    }

    /**
     * Displays the Network Error Message, hiding the RecyclerView, and the error message TextView
     */
    private void showNetworkErrorMessage() {
        mNetworkErrorTextView.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageTextView.setVisibility(View.INVISIBLE);
    }


    @Override
    protected void onResume() {
        super.onResume();

        // Loads favourite movies from the offline database if the favourites option is selected
        if (isNotFirstResume && mMenu != null) {
            if (mMenu.findItem(R.id.menu_main_action_sort_favourites).isChecked()) {
                loadMovieDataFromDatabase();
            }
        } else isNotFirstResume = true;
    }

    /**
     * If network is available, displays the RecyclerView and initialises or restarts the Loader
     * to fetch the required data from the TMDB API
     */
    private void loadMovieData() {

        // Displays the RecyclerView
        showMovieDataView();

        // Sets the data to null
        mMovieAdapter.setMovieData(null);

        // Executed if network is available
        if (QueryUtils.isNetworkAvailable(this)) {

            // Handles loading using Loader
            LoaderManager loaderManager = getSupportLoaderManager();
            Loader<String> moviesLoader = loaderManager.getLoader(URL_LOADER_ID);
            if (moviesLoader == null) {
                loaderManager.initLoader(URL_LOADER_ID, null, this);
            } else {
                loaderManager.restartLoader(URL_LOADER_ID, null, this);
            }
        } else {
            showNetworkErrorMessage();
        }
    }

    /**
     * If favourites view is selected, displays the RecyclerView and initialises or restarts
     * the Loader to fetch the required data from the offline favourites database
     */
    private void loadMovieDataFromDatabase() {

        // Displays the RecyclerView
        showMovieDataView();

        // Sets the data to null
        mMovieAdapter.setMovieData(null);


        // Handles loading using Loader
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> moviesLoader = loaderManager.getLoader(DB_LOADER_ID);
        if (moviesLoader == null) {
            loaderManager.initLoader(DB_LOADER_ID, null, this);
        } else {
            loaderManager.restartLoader(DB_LOADER_ID, null, this);
        }
    }

    /**
     * Shows the RecyclerView which has the movie data, hiding the error TextViews
     */
    private void showMovieDataView() {

        mErrorMessageTextView.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
        mNetworkErrorTextView.setVisibility(View.INVISIBLE);
    }

    /**
     * Shows error message if results are not obtained, hiding the movie RecyclerView and
     * network error message
     **/
    private void showErrorMessage() {

        mErrorMessageTextView.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
        mNetworkErrorTextView.setVisibility(View.INVISIBLE);
    }

    /**
     * Starts the detail activity sending it the selected movie's data as an extra in the intent
     *
     * @param selectedMovie The movie selected in the Main Activity
     */
    @Override
    public void onClick(Movie selectedMovie) {

        Intent intentToStartDetailActivity = new Intent(this, DetailActivity.class);
        intentToStartDetailActivity.putExtra("Movie", selectedMovie);
        intentToStartDetailActivity.putExtra("Favourite", isFavouritesScreen);
        startActivity(intentToStartDetailActivity);
    }

    /**
     * Corrects the sort order selected when the app is paused and reopened
     *
     * @param menu the menu being displayed in the activity
     * @return default behaviour of super class
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        // Gets reference to the popularity,rating and favourites menu items
        MenuItem popularity = menu.findItem(R.id.menu_main_action_sort_popularity);
        MenuItem rating = menu.findItem(R.id.menu_main_action_sort_rating);
        MenuItem favourites = menu.findItem(R.id.menu_main_action_sort_favourites);

        // Selects the item based on the previously selected preference
        if (QueryUtils.popularityIsChecked) {
            popularity.setChecked(true);
        } else if (QueryUtils.ratingIsChecked) {
            rating.setChecked(true);
        } else {
            favourites.setChecked(true);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Creates the Menu for the activity
     * Displays the sort by action in the menu
     *
     * @param menu the menu being displayed in the activity
     * @return true if the menu is created
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Stores the menu object in a mMenu variable for later use
        mMenu = menu;

        // Inflates the menu from its XML file
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    /**
     * Handles behavior when a sort order is selected.
     * Loads the movie data based on the sort order selected
     *
     * @param item the sort order selected
     * @return true if the selection is handled correctly or super class behaviour
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Resource ID of the item selected
        int itemSelected = item.getItemId();

        switch (itemSelected) {
            case R.id.menu_main_action_sort_popularity:

                // Sets the radio button as checked
                item.setChecked(true);

                // Corrects the values of the variables
                QueryUtils.popularityIsChecked = true;
                QueryUtils.ratingIsChecked = false;
                isFavouritesScreen = false;

                // Loads the movie data
                loadMovieData();
                return true;

            case R.id.menu_main_action_sort_rating:

                // Sets the radio button as checked
                item.setChecked(true);

                // Corrects the values of the variables
                QueryUtils.ratingIsChecked = true;
                QueryUtils.popularityIsChecked = false;
                isFavouritesScreen = false;

                // Loads the movie data
                loadMovieData();
                return true;
            case R.id.menu_main_action_sort_favourites:

                // Sets the radio button as checked
                item.setChecked(true);

                // Corrects the values of the variables
                QueryUtils.ratingIsChecked = false;
                QueryUtils.popularityIsChecked = false;
                isFavouritesScreen = true;

                // Loads favourite movies from the offline database
                loadMovieDataFromDatabase();

                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * Loader Callback methods to fetch on a background thread the movie data from TMDB
     * or the favourite movie data from the offline database
     */

    @Override
    public Loader<ArrayList<Movie>> onCreateLoader(int id, Bundle args) {
        switch (id) {

            // Called to load data from TMDB
            case URL_LOADER_ID:
                return new AsyncTaskLoader<ArrayList<Movie>>(this) {
                    ArrayList<Movie> mMovies = null;

                    @Override
                    protected void onStartLoading() {

                        // Hides the empty view
                        mFavouritesEmptyView.setVisibility(View.INVISIBLE);

                        if (mMovies != null) {
                            deliverResult(mMovies);
                        } else {
                            mLoadingIndicator.setVisibility(View.VISIBLE);
                            forceLoad();
                        }
                    }

                    @Override
                    public ArrayList<Movie> loadInBackground() {

                        // URL for query is created
                        URL url = QueryUtils.createMainQueryUrl();

                        // String to store the JSON Response received from the query
                        String jsonResponse;

                        // ArrayList to store the movie data
                        ArrayList<Movie> listOfMovies = null;

                        try {
                            // Http request is made and the returning JSON Response is stored in a String
                            jsonResponse = QueryUtils.makeHttpRequest(url);

                            // Strings are obtained from JSON response and stored in ArrayList
                            listOfMovies = JSONUtils.extractMoviesFromJson(jsonResponse);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return listOfMovies;
                    }

                    @Override
                    public void deliverResult(ArrayList<Movie> data) {
                        mMovies = data;
                        super.deliverResult(data);
                    }
                };

            //Called to load data from the offline database
            case DB_LOADER_ID:
                return new AsyncTaskLoader<ArrayList<Movie>>(this) {

                    // Object to store the list of movies received
                    ArrayList<Movie> mMovies = null;

                    @Override
                    protected void onStartLoading() {
                        if (mMovies != null) {
                            deliverResult(mMovies);
                        } else {
                            mLoadingIndicator.setVisibility(View.VISIBLE);
                            forceLoad();
                        }
                    }

                    @Override
                    public ArrayList<Movie> loadInBackground() {


                        // ArrayList to store the movie data
                        ArrayList<Movie> listOfMovies = new ArrayList<>();

                        // Cursor to store the query result
                        Cursor favouritesCursor = null;

                        try {

                            // Fetches the data from the offline database using its Content Provider
                            favouritesCursor = getContentResolver().query(MovieEntry.CONTENT_URI,
                                    null,
                                    null,
                                    null,
                                    null);

                            // Starts from the first item in the list
                            favouritesCursor.moveToFirst();

                            // Stores the movies obtained in an ArrayList
                            while (!favouritesCursor.isAfterLast()) {
                                String title = favouritesCursor.getString
                                        (favouritesCursor.getColumnIndexOrThrow
                                                (MovieEntry.COLUMN_TITLE));
                                String rating = favouritesCursor.getString
                                        (favouritesCursor.getColumnIndexOrThrow
                                                (MovieEntry.COLUMN_RATING));
                                String posterPath = favouritesCursor.getString
                                        (favouritesCursor.getColumnIndexOrThrow
                                                (MovieEntry.COLUMN_POSTER_PATH));
                                String synopsis = favouritesCursor.getString
                                        (favouritesCursor.getColumnIndexOrThrow
                                                (MovieEntry.COLUMN_SYNOPSIS));
                                String releaseDate = favouritesCursor.getString
                                        (favouritesCursor.getColumnIndexOrThrow
                                                (MovieEntry.COLUMN_TITLE));
                                String id = favouritesCursor.getString
                                        (favouritesCursor.getColumnIndexOrThrow
                                                (MovieEntry.COLUMN_TMDB_ID));

                                // Adds the movie parameters to the ArrayList
                                listOfMovies.add(new Movie(title, synopsis, posterPath, releaseDate, rating, id));

                                // Moves the cursor to the next movie
                                favouritesCursor.moveToNext();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {

                            // Closes the cursor
                            favouritesCursor.close();

                        }
                        return listOfMovies;
                    }

                    @Override
                    public void deliverResult(ArrayList<Movie> data) {
                        mMovies = data;
                        super.deliverResult(data);
                    }
                };
            default:
                throw new UnsupportedOperationException("Unknown Loader Id " + id);
        }
    }


    @Override
    public void onLoadFinished
            (Loader<ArrayList<Movie>> loader, ArrayList<Movie> listOfMovies) {

        // Results have been obtained and so loading indicator is hidden
        mLoadingIndicator.setVisibility(View.INVISIBLE);

        // If the list is received is not null, displays the list
        if (listOfMovies != null) {

            saveInstanceMovies = listOfMovies;
            showMovieDataView();
            mMovieAdapter.setMovieData(listOfMovies);
        } else {
            // Displays error message
            showErrorMessage();
        }

        // Displays the empty view if the favourites list is empty
        if (loader.getId() == DB_LOADER_ID && listOfMovies.size() == 0) {
            mFavouritesEmptyView.setVisibility(View.VISIBLE);

            // Button to load popular movies; displayed if favourites list is empty
            mLoadMoviesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadPopularMovies();
                }
            });
        }
    }


    @Override
    public void onLoaderReset(Loader loader) {
        // Do nothing; Overriden to implement LoaderCallbacks
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        ArrayList<Movie> movieData = mMovieAdapter.getMovieData();
        if (movieData != null) {
            // Saves the list of movies
            outState.putParcelableArrayList(MOVIES_KEY, movieData);
        }
        // Saves the scroll position
        int scrollPosition =
                ((GridLayoutManager) mRecyclerView.getLayoutManager())
                        .findFirstCompletelyVisibleItemPosition();
        outState.putInt(SCROLL_POSITION_KEY, scrollPosition);

    }

    /**
     * Loads a list of popular movies from the TMDB API
     * Called when the favourites list is empty and the user clicks the "Load Popular Movies" button
     */
    public void loadPopularMovies() {

        // Corrects the values of the variables to  switch to the popularity view
        QueryUtils.popularityIsChecked = true;
        QueryUtils.ratingIsChecked = false;
        isFavouritesScreen = false;

        // Sets the popularity menu item as checked
        mMenu.findItem(R.id.menu_main_action_sort_popularity).setChecked(true);

        // Hides the empty view
        mFavouritesEmptyView.setVisibility(View.INVISIBLE);

        // Loads the movie data
        loadMovieData();
    }

}
