
package com.example.android.popularmovieapp1;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.android.popularmovieapp1.models.MovieModel;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MovieDetailsActivity extends AppCompatActivity {
    @Bind(R.id.ivMovieIcon2)  ImageView ivMovieIcon;
    @Bind(R.id.original_title)  TextView original_title;
    @Bind(R.id.release_date)  TextView release_date;
    @Bind(R.id.overview)  TextView overview;
    @Bind(R.id.vote_count)  RatingBar vote_count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        ActionBar actionBar=this.getSupportActionBar();
        if(actionBar!=null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        Bundle extras = getIntent().getExtras();
            ButterKnife.bind(this);
        MovieModel movie = extras.getParcelable("movie");

            if(movie!=null) {
                original_title.setText(movie.getOriginal_title());
                release_date.setText(movie.getRelease_date());
                Picasso.with(this)
                        .load("http://image.tmdb.org/t/p/w185/" + movie.getPoster_path())
                        .placeholder(R.drawable.loading)
                        .error(R.drawable.notfound)
                        .into(ivMovieIcon);

                overview.setText(movie.getOverview());
                vote_count.setRating(movie.getVote_average() / 2);
            }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if(id==android.R.id.home)
        {
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }
}
