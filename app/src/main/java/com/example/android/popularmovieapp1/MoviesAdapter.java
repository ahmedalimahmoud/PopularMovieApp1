package com.example.android.popularmovieapp1;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmovieapp1.models.MovieModel;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by ahmed on 2/19/2017.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MyViewHolder> {

    private List<MovieModel> movieModelList;

    Context context ;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public MyViewHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.ivMovieIcon);

        }
    }

    public MoviesAdapter(List<MovieModel> movieModelList) {
        this.movieModelList = movieModelList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row, parent, false);
        context = parent.getContext();
        return new MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        MovieModel movie = movieModelList.get(position);
        Picasso.with(context)
                .load("http://image.tmdb.org/t/p/w185/"+movie.getPoster_path())
                .placeholder(R.drawable.loading)
                .error(R.drawable.notfound)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return movieModelList.size();
    }
}