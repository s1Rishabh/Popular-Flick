package com.example.android.popularflicks;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.ArrayList;

/**
 * Contains all the fields that belong to a movie
 * i.e. title,synopsis,user rating, poster image, trailers, reviews, TMDB ID & release data
 */

public class Movie implements Parcelable {

    // Contains the title,synopsis,poster path,release date,
    // user rating and TMDB ID of the movie as Strings
    private final String mTitle, mSynopsis, mPosterPath, mReleaseDate, mTmdbId, mUserRating;

    // Contains the database ID of the movie
    private int mDbId;

    // Store the Reviews and links to Trailers
    private final ArrayList<Review> mReviews = new ArrayList<>();
    private final ArrayList<Trailer> mTrailers = new ArrayList<>();

    // Boolean to check if the movie has been marked as a favourite or not
    private boolean mFavourite = false;

    // Constructor that sets the values of the member fields to the received values
    public Movie(String title,
                 String synopsis,
                 String posterPath,
                 String releaseDate,
                 String userRating,
                 String id) {

        mTitle = title;
        mSynopsis = synopsis;
        mPosterPath = posterPath;
        mReleaseDate = releaseDate;
        mUserRating = userRating;
        mTmdbId = id;
    }

    /**
     * @return title of the movie
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * @return synopsis of the movie
     */
    String getSynopsis() {
        return mSynopsis;
    }

    /**
     * @return path of the poster image of the movie
     */
    String getPosterPath() {
        return mPosterPath;
    }

    /**
     * @return release data of the movie
     */
    String getReleaseDate() {
        return mReleaseDate;
    }

    /**
     * @return reviews of the movie
     */
    ArrayList<Review> getReviews() {
        return mReviews;
    }

    /**
     * @return trailers of the movie
     */
    ArrayList<Trailer> getTrailers() {
        return mTrailers;
    }

    /**
     * @return user rating of the movie
     */
    String getUserRating() {
        return mUserRating;
    }

    /**
     * @return TMDB id of the movie
     */
    String getTmdbId() {
        return mTmdbId;
    }

    /**
     * Removes the Database ID of the movie
     */
    void clearDbId() {
        mDbId = -1;
    }

    /**
     * @return true if the movie is marked as a favourite
     */
    boolean isFavourite() {
        return mFavourite;
    }

    /**
     * Sets or removes the movie as a favourite depending upon the parameter
     * @param value true to mark favourite, false to remove favourite
     */
    void setFavourite(boolean value) {
        mFavourite = value;
    }

    /**
     * Adds a review to the movie
     * @param author author of the review
     * @param content content of the review
     */
    public void addReview(@NonNull String author, @NonNull String content) {
        mReviews.add(new Review(author, content));
    }

    /**
     * Sets the Database Id to the parameter
     * @param id database id
     */
    public void setDbId(int id) {
        mDbId = id;
    }

    /**
     * Adds a trailer to the movie
     * @param link link to the trailer
     * @param title title of the trailer
     */
    public void addTrailer(@NonNull String link, @NonNull String title) {
        mTrailers.add(new Trailer(link, title));
    }


    // Required to implement Parcelable which is to be used to pass the movie object around
    @Override
    public int describeContents() {
        return 0;
    }

    // Required to implement Parcelable which is to be used to pass the movie object around
    // Writes the values of the member fields to the Parcelable object
    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(mTitle);
        dest.writeString(mSynopsis);
        dest.writeString(mPosterPath);
        dest.writeString(mReleaseDate);
        dest.writeString(mUserRating);
        dest.writeString(mTmdbId);

    }

    // Required to implement Parcelable which is to be used to pass the movie object around
    public static final Parcelable.Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    // Required to implement Parcelable which is to be used to pass the movie object around
    // Reads values from Parcelable to store them in the Movie object
    private Movie(Parcel in) {
        mTitle = in.readString();
        mSynopsis = in.readString();
        mPosterPath = in.readString();
        mReleaseDate = in.readString();
        mUserRating = in.readString();
        mTmdbId = in.readString();
    }

    /**
     * Contains all the fields that belong to a review
     * i.e. content and author's name
     */
    public static class Review implements Parcelable {
        private final String mAuthor, mContent;

        public Review(String author, String content) {
            mAuthor = author;
            mContent = content;
        }

        protected Review(Parcel in) {
            mAuthor = in.readString();
            mContent = in.readString();
        }

        public static final Creator<Review> CREATOR = new Creator<Review>() {
            @Override
            public Review createFromParcel(Parcel in) {
                return new Review(in);
            }

            @Override
            public Review[] newArray(int size) {
                return new Review[size];
            }
        };
        /**
         * @return author of the review
         */
        public String getAuthor() {
            return mAuthor;
        }

        /**
         * @return content of the review
         */
        public String getContent() {
            return mContent;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(mAuthor);
            dest.writeString(mContent);
        }
    }

    /**
     * Contains fields of a Trailer i.e. link and title of the trailer
     */
    public static class Trailer implements Parcelable{
        private final String mLink, mTitle;

        public Trailer(String link, String title) {
            mLink = link;
            mTitle = title;
        }

        protected Trailer(Parcel in) {
            mLink = in.readString();
            mTitle = in.readString();
        }

        public static final Creator<Trailer> CREATOR = new Creator<Trailer>() {
            @Override
            public Trailer createFromParcel(Parcel in) {
                return new Trailer(in);
            }

            @Override
            public Trailer[] newArray(int size) {
                return new Trailer[size];
            }
        };

        /**
         * @return link to the trailer
         */
        public String getLink() {
            return mLink;
        }
        /**
         * @return title of the trailer
         */
        public String getTitle() {
            return mTitle;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(mLink);
            dest.writeString(mTitle);
        }
    }
}

