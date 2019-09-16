package com.example.android.popularflicks;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Adapter to populate the RecyclerView in the MainActivity with {@link Movie} objects which contain
 * data of the movies queried
 */

class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    // ArrayList to store the data of the movies
    private ArrayList<Movie> mMovieData;

    // Handles onClick behaviour
    private final MovieAdapterOnClickHandler mClickHandler;

    /**
     * The interface that receives onClick messages
     */
    interface MovieAdapterOnClickHandler {
        void onClick(Movie selectedMovie);
    }

    /**
     * Constructor for the MovieAdapter class
     *
     * @param clickHandler is the onClickHandler for the selected movie
     */
    MovieAdapter(MovieAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    /**
     * ViewHolder to store the movie data as cache
     */
    class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // TextViews to display the title and the user rating of the movie
        final TextView mMovieTitleTextView;
        final TextView mMovieRatingTextView;

        // ImageView object for the poster
        final ImageView mPosterImageView;

        /**
         * Constructor that gets reference to the TextViews, ImageView and sets onClickListener
         */
        MovieAdapterViewHolder(View itemView) {

            super(itemView);
            mMovieTitleTextView = (TextView) itemView.findViewById(R.id.tv_title);
            mMovieRatingTextView = (TextView) itemView.findViewById(R.id.tv_rating);
            mPosterImageView = (ImageView) itemView.findViewById(R.id.iv_main_poster);
            itemView.setOnClickListener(this);
        }

        /**
         * Handles onClick behaviour
         *
         * @param v the selected View in the RecyclerView
         */
        @Override
        public void onClick(View v) {

            int adapterPosition = getAdapterPosition();
            Movie selectedMovie = mMovieData.get(adapterPosition);
            mClickHandler.onClick(selectedMovie);
        }
    }

    /**
     * Creates a new ReviewAdapterViewHolder for storing movie data
     *
     * @param parent   the ViewGroup containing all the views
     * @param viewType the view type of the new View
     * @return the created ReviewAdapterViewHolder
     */
    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // Gets context of the parent view group
        Context context = parent.getContext();

        // Specifies whether the viewHolder is directly attached to paren
        final boolean attachDirectlyToParent = false;

        // Gets the id for the list item
        int layoutIdForListItem = R.layout.movie_list_item;

        // Gets a Layout inflater to inflate the ViewHolder
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflates the layout for the ViewHolder
        View createdView = inflater.inflate(layoutIdForListItem, parent, attachDirectlyToParent);

        // Returns the created ViewHolder
        return new MovieAdapterViewHolder(createdView);
    }

    /**
     * Sets the data of the movie in the respective Views
     *
     * @param holder   ViewHolder which will display the movie information
     * @param position position of the selected ViewHolder in the RecyclerView
     */
    @Override
    public void onBindViewHolder(MovieAdapterViewHolder holder, int position) {

        // Gets context
        Context context = holder.mMovieTitleTextView.getContext();

        // Sets the title of the movie in its TextView
        Movie currentMovie = mMovieData.get(position);
        holder.mMovieTitleTextView.setText(currentMovie.getTitle());

        // Sets the user rating of the movie in its TextView
        holder.mMovieRatingTextView.setText(currentMovie.getUserRating());

        // Sets the poster image of the movie in the ImageView along with placeholder and error image
        Picasso.with(context)
                .load(currentMovie.getPosterPath())
                .placeholder(R.drawable.placeholder_movieimage)
                .error(R.drawable.placeholder_movieimage)
                .into(holder.mPosterImageView)
        ;

    }
    @Override
    public int getItemCount() {
        return mMovieData == null ? 0: mMovieData.size();
    }

    /**
     * @param movieData used to initialise the value for the ArrayList in the Adapter object
     */
    void setMovieData(ArrayList<Movie> movieData) {

        mMovieData = movieData;

        // Notifies any registered observers that the data set has changed
        notifyDataSetChanged();
    }

    /**
     * @return the movie data stored in the Adapter
     */
    ArrayList<Movie> getMovieData() {

       return mMovieData;
    }


}