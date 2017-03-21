package com.example.android.popularmovieapp1.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ahmed on 2/24/2017.
 */

public class MovieReviews implements Parcelable {
    private String author;
    private String content;

    private MovieReviews(Parcel in) {
        author = in.readString();
        content = in.readString();
    }

    public static final Creator<MovieReviews> CREATOR = new Creator<MovieReviews>() {
        @Override
        public MovieReviews createFromParcel(Parcel in) {
            return new MovieReviews(in);
        }

        @Override
        public MovieReviews[] newArray(int size) {
            return new MovieReviews[size];
        }
    };

    public MovieReviews() {

    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(author);
        parcel.writeString(content);
    }
}
