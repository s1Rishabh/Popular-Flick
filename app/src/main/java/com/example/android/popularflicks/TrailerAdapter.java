package com.example.android.popularflicks;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Adapter to populate the RecyclerView in the DetailActivity with
 * {@link com.example.android.popularflicks.Movie.Trailer} objects which contain
 * data of the trailers queried
 */

class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerAdapterViewHolder> {

    // ArrayList to store the data of the trailers
    private ArrayList<Movie.Trailer> mTrailers;

    // Stores context
    private Context mContext;

    TrailerAdapter(Context context){
        mContext = context;
    }

    /**
     * ViewHolder to store the trailer data as cache
     */
    class TrailerAdapterViewHolder extends RecyclerView.ViewHolder {

        // TextView to display the title of the trailer
        @BindView(R.id.tv_detail_trailer_title)
        TextView mTrailerTitleTextView;

        /**
         * Constructor that binds the TextView
         */
        TrailerAdapterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    /**
     * Creates a new TrailerAdapterViewHolder for storing movie data
     *
     * @param parent   the ViewGroup containing all the views
     * @param viewType the view type of the new View
     * @return the created TrailerAdapterViewHolder
     */
    @Override
    public TrailerAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // Gets context of the parent view group
        Context context = parent.getContext();

        // Specifies whether the viewHolder is directly attached to parent
        final boolean attachDirectlyToParent = false;

        // Gets the id for the list item
        int layoutIdForListItem = R.layout.trailer_list_item;

        // Gets a Layout inflater to inflate the ViewHolder
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflates the layout for the ViewHolder
        View createdView = inflater.inflate(layoutIdForListItem, parent, attachDirectlyToParent);

        // Returns the created ViewHolder
        return new TrailerAdapterViewHolder(createdView);
    }

    /**
     * Sets the data of the trailer in the respective Views
     *
     * @param holder   ViewHolder which will display the movie information
     * @param position position of the selected ViewHolder in the RecyclerView
     */
    @Override
    public void onBindViewHolder(final TrailerAdapterViewHolder holder, int position) {

        // Sets the title of the trailer in its TextView
        Movie.Trailer currentTrailer = mTrailers.get(position);
        holder.mTrailerTitleTextView.setText(currentTrailer.getTitle());
        holder.mTrailerTitleTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchTrailer(holder.getAdapterPosition());
            }
        });

    }

    /**
     * Launches the Youtube trailer
     * @param position position of the selected trailer in the RecyclerView
     */
        private void launchTrailer(int position){

            Uri youtubeUri = Uri.parse(mTrailers.get(position).getLink());
            Intent launchTrailerIntent = new Intent(Intent.ACTION_VIEW,youtubeUri);
            mContext.startActivity(launchTrailerIntent);

        }


    @Override
    public int getItemCount() {

        return mTrailers == null ? 0 : mTrailers.size();

    }

    /**
     * @param trailerData used to initialise the value for the ArrayList in the Adapter object
     */
    void setTrailerData(ArrayList<Movie.Trailer> trailerData) {

        mTrailers = trailerData;
        // Notifies any registered observers that the data set has changed
        notifyDataSetChanged();
    }

    /**
     * @return list of trailers
     */
    ArrayList<Movie.Trailer> getTrailerData(){
        return mTrailers;
    }

}