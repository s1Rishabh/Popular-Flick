package com.example.android.popularflicks;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Adapter to populate the RecyclerView in the ReviewActivity with
 * {@link com.example.android.popularflicks.Movie.Review} objects which contain
 * data of the reviews queried
 */

class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewAdapterViewHolder> {

    // ArrayList to store the data of the reviews
    private ArrayList<Movie.Review> mReviews;


    /**
     * ViewHolder to store the review data as cache
     */
    class ReviewAdapterViewHolder extends RecyclerView.ViewHolder {

        // TextViews to display the author and content of the review
        @BindView(R.id.tv_review_author)
        TextView mReviewAuthorTextView;
        @BindView(R.id.tv_review_content)
        TextView mReviewContentTextView;


        /**
         * Constructor that binds the TextViews using ButterKnife
         */
        ReviewAdapterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    /**
     * Creates a new ReviewAdapterViewHolder for storing review data
     *
     * @param parent   the ViewGroup containing all the views
     * @param viewType the view type of the new View
     * @return the created ReviewAdapterViewHolder
     */
    @Override
    public ReviewAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // Gets context of the parent view group
        Context context = parent.getContext();

        // Specifies whether the viewHolder is directly attached to paren
        final boolean attachDirectlyToParent = false;

        // Gets the id for the list item
        int layoutIdForListItem = R.layout.review_list_item;

        // Gets a Layout inflater to inflate the ViewHolder
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflates the layout for the ViewHolder
        View createdView = inflater.inflate(layoutIdForListItem, parent, attachDirectlyToParent);

        // Returns the created ViewHolder
        return new ReviewAdapterViewHolder(createdView);
    }

    /**
     * Sets the data of the review in the respective Views
     *
     * @param holder   ViewHolder which will display the movie information
     * @param position position of the selected ViewHolder in the RecyclerView
     */
    @Override
    public void onBindViewHolder(ReviewAdapterViewHolder holder, int position) {

        Context context = holder.mReviewAuthorTextView.getContext();

        // Sets the author of the review in its TextView
        Movie.Review currentReview = mReviews.get(position);
        holder.mReviewAuthorTextView.setText(context.getString
                (R.string.review_author_label,currentReview.getAuthor()));

        // Sets the content of the review in its TextView
        holder.mReviewContentTextView.setText(currentReview.getContent());
    }

    @Override
    public int getItemCount() {
        return mReviews == null ? 0 : mReviews.size();
    }

    /**
     * @param reviewData used to initialise the value for the ArrayList in the Adapter object
     */
    void setReviewData(ArrayList<Movie.Review> reviewData) {

        mReviews = reviewData;
        // Notifies any registered observers that the data set has changed
        notifyDataSetChanged();
    }
}