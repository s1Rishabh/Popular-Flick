package com.example.android.popularflicks;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Displays reviews of a movie in a RecyclerView
 */
public class ReviewActivity extends AppCompatActivity {

    private static final String REVIEWS_KEY = "reviews";
    private static final String POSITION_KEY = "position";
    // RecyclerView object
    @BindView(R.id.rv_review)
    RecyclerView mRecyclerView;

    // ReviewAdapter object
    ReviewAdapter mReviewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        // ArrayList to store the list of reviews
        ArrayList<Movie.Review> mReviews;

        // Binds the Views using ButterKnife
        ButterKnife.bind(this);

        // The RecyclerView will have views of fixed size only
        mRecyclerView.setHasFixedSize(true);

        // Setting a LinearLayoutManager for the RecyclerView
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // New ReviewAdapter object to populate the RecyclerView
        mReviewAdapter = new ReviewAdapter();

        // Wiring up the RecyclerView with the ReviewAdapter
        mRecyclerView.setAdapter(mReviewAdapter);

        // Gets a reference to the Intent that started the Review Activity
        Intent startingIntent = getIntent();
        if (startingIntent != null) {
            if (startingIntent.hasExtra("Reviews")) {

                // Gets the Reviews of the Movie opened in the DetailActivity
                // and sets it in the ReviewAdapter
                mReviews = startingIntent.getParcelableArrayListExtra("Reviews");
                mReviewAdapter.setReviewData(mReviews);

            }
        }
    }

}
