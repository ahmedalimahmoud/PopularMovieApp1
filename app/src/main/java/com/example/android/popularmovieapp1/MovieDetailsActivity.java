
package com.example.android.popularmovieapp1;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovieapp1.data.MovieContract;
import com.example.android.popularmovieapp1.models.MovieModel;
import com.example.android.popularmovieapp1.models.MovieTrailer;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.R.drawable.btn_star_big_off;
import static android.R.drawable.btn_star_big_on;

public class MovieDetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<MovieTrailer>>{
    @Bind(R.id.ivMovieIcon2)  ImageView ivMovieIcon;
    @Bind(R.id.original_title)  TextView original_title;
    @Bind(R.id.release_date)  TextView release_date;
    @Bind(R.id.overview)  TextView overview;
    @Bind(R.id.vote_count)  RatingBar vote_count;
    @Bind(R.id.button) Button bt;
    @Bind(R.id.favourite_btn) ImageButton ibt;
    private static final int GITHUB_SEARCH_LOADER = 23;
    private static final String SEARCH_QUERY_URL_EXTRA = "queryy";
    private static boolean star=true;
    private static int id;
    private ListView lvTrailers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        lvTrailers=(ListView) findViewById(R.id.trailer_list);
//        View header = (View)getLayoutInflater().inflate(R.layout.header,null);
//        final View footer =(View)getLayoutInflater().inflate(R.layout.footer,null);
//        lvTrailers.addHeaderView(header,null,false);
//        lvTrailers.addFooterView(footer,null,false);
        getSupportLoaderManager().initLoader(GITHUB_SEARCH_LOADER, null, this);
        ActionBar actionBar=this.getSupportActionBar();
        if(actionBar!=null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        Bundle extras = getIntent().getExtras();
        ButterKnife.bind(this);
        final MovieModel movie = extras.getParcelable("movie");

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
        id=movie.getId();
        runQuery(id);
        final boolean x=check(id);
        if(x)
        {
            ibt.setImageResource(btn_star_big_on);
        }
        else {ibt.setImageResource(btn_star_big_off);}
        bt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent=new Intent(MovieDetailsActivity.this, MovieReviewsActivity.class);
                intent.putExtra("id",id);
                startActivity(intent);
            }
        });
        ibt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(x) {
                    // Build appropriate uri with String row id appended
                    String stringId = Integer.toString(movie.getId());
                    Uri uri = MovieContract.MovieEntry.CONTENT_URI;
                    uri = uri.buildUpon().appendPath(stringId).build();
                    getContentResolver().delete(uri, null, null);
                    ibt.setImageResource(btn_star_big_off);
                    star=true;
                }
                else
                {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movie.getId());
                    contentValues.put(MovieContract.MovieEntry.COLUMN_TITLE, movie.getOriginal_title());
                    contentValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, movie.getOverview());
                    contentValues.put(MovieContract.MovieEntry.COLUMN_RELEASE, movie.getRelease_date());
                    contentValues.put(MovieContract.MovieEntry.COLUMN_RATE, movie.getVote_average());
                    contentValues.put(MovieContract.MovieEntry.COLUMN_POSTER, movie.getPoster_path());
                    Uri uri = getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, contentValues);

                    if(uri != null) {
                        Toast.makeText(getBaseContext(), uri.toString(), Toast.LENGTH_LONG).show();

                    }
                    ibt.setImageResource(btn_star_big_on);
                    star=false;
                }

            }
        });

    }

private boolean check(int id)
{
    String stringId = Integer.toString(id);
    String[] selectionArgs=new String[]{stringId};
    Cursor res=getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
            null,
            "movie_id=?",
            selectionArgs,
            null);
    res.close();
    return res.getCount() != 0;
}
    private void runQuery(int id) {

        Bundle queryBundle = new Bundle();
        queryBundle.putString(SEARCH_QUERY_URL_EXTRA, "http://api.themoviedb.org/3/movie/"+id+"/videos?api_key="+MainActivity.api_key);

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> githubSearchLoader = loaderManager.getLoader(GITHUB_SEARCH_LOADER);
        if (githubSearchLoader == null) {
            loaderManager.initLoader(GITHUB_SEARCH_LOADER, queryBundle, this);
        } else {
            loaderManager.restartLoader(GITHUB_SEARCH_LOADER, queryBundle, this);
        }
    }

    private void openWebPage(String url)
    {
        Uri web=Uri.parse(url);
        Intent intent=new Intent(Intent.ACTION_VIEW,web);
        if(intent.resolveActivity(getPackageManager())!=null)
        {
            startActivity(intent);
        }
    }

    @Override
    public Loader<List<MovieTrailer>> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<List<MovieTrailer>>(this) {
            List<MovieTrailer> data;

            @Override
            protected void onStartLoading() {
                if (args == null) {
                    return;
                }
                if (data != null)
                {
                    deliverResult(data);
                }
                else {
                    forceLoad();
                }
            }

            @Override
            public List<MovieTrailer> loadInBackground() {
                String searchQueryUrlString = args.getString(SEARCH_QUERY_URL_EXTRA);
                if (searchQueryUrlString == null || TextUtils.isEmpty(searchQueryUrlString)) {
                    return null;
                }
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    URL url = new URL(searchQueryUrlString);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.connect();
                    InputStream stream = connection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(stream));
                    StringBuilder buffer = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line);
                    }
                    String finalJson = buffer.toString();
                    JSONObject parent = new JSONObject(finalJson);
                    JSONArray parentArray = parent.getJSONArray("results");
                    List<MovieTrailer> movieTrailersList = new ArrayList<>();
                    for (int i = 0; i < parentArray.length(); i++) {
                        JSONObject finalObject = parentArray.getJSONObject(i);
                        MovieTrailer movieTrailer = new MovieTrailer();
                        movieTrailer.setKey(finalObject.getString("key"));
                        movieTrailer.setName(finalObject.getString("name"));
                        movieTrailersList.add(movieTrailer);
                    }
                    return movieTrailersList;
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                    try {
                        if (reader != null) {
                            reader.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                return null;
            }

            @Override
            public void deliverResult(List<MovieTrailer> data) {
                this.data=data;
                super.deliverResult(data);
            }
        };

    }
    @Override
    public void onLoadFinished(Loader<List<MovieTrailer>> loader, final List<MovieTrailer> result) {
        if (result != null) {

            MovieAdapter adapter=new MovieAdapter(getApplicationContext(), result);
            lvTrailers.setAdapter(adapter);

            lvTrailers.setOnItemClickListener(new AdapterView.OnItemClickListener() {  // list item click opens a new detailed activity
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                    openWebPage("https://www.youtube.com/watch?v="+result.get(position).getKey());
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), R.string.error, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onLoaderReset(Loader<List<MovieTrailer>> loader) {

    }



    public class MovieAdapter extends ArrayAdapter {
        private final List<MovieTrailer> movieTrailerList;
        private final int resource;
        private final LayoutInflater inflater;
        public MovieAdapter(Context context, List<MovieTrailer> objects) {
            super(context, R.layout.trailer_row, objects);
            movieTrailerList=objects;
            this.resource= R.layout.trailer_row;
            inflater=(LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            MovieAdapter.ViewHolder viewHolder=null;
            if(convertView==null)
            {   viewHolder=new MovieAdapter.ViewHolder();
                convertView=inflater.inflate(resource,null);
                viewHolder.trailer_name =(TextView) convertView.findViewById(R.id.trailer_name);
                convertView.setTag(viewHolder);
            }else {
                viewHolder=(MovieAdapter.ViewHolder)convertView.getTag();
            }

            viewHolder.trailer_name.setText(movieTrailerList.get(position).getName());
            return convertView;

        }
        class ViewHolder
        {
            private TextView trailer_name;
        }
    }









    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();

        if (id==R.id.activity_settings)
        {
            Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(startSettingsActivity);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
