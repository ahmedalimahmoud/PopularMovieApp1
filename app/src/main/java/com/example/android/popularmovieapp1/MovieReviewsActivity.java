package com.example.android.popularmovieapp1;

import android.content.Context;
import android.content.Intent;
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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovieapp1.models.MovieReviews;

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

public class MovieReviewsActivity extends AppCompatActivity  implements LoaderManager.LoaderCallbacks<List<MovieReviews>>{
    private static final int GITHUB_SEARCH_LOADER = 24;
    private static final String SEARCH_QUERY_URL_EXTR = "qqqq";
    private int id;
    private ListView lvReviews;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_reviews);
        lvReviews=(ListView) findViewById(R.id.review_list);
        getSupportLoaderManager().initLoader(GITHUB_SEARCH_LOADER, null, this);
        ActionBar actionBar=this.getSupportActionBar();
        if(actionBar!=null)
        {
            actionBar.setDisplayHomeAsUpEnabled(false);
        }

        Bundle extras = getIntent().getExtras();
        id=extras.getInt("id");

        runQuery(id);
    }
    private void runQuery(int id) {

        Bundle queryBundle = new Bundle();
        queryBundle.putString(SEARCH_QUERY_URL_EXTR, "http://api.themoviedb.org/3/movie/"+id+"/reviews?api_key="+MainActivity.api_key);

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> githubSearchLoader = loaderManager.getLoader(GITHUB_SEARCH_LOADER);
        if (githubSearchLoader == null) {
            loaderManager.initLoader(GITHUB_SEARCH_LOADER, queryBundle, this);
        } else {
            loaderManager.restartLoader(GITHUB_SEARCH_LOADER, queryBundle, this);
        }


    }
    @Override
    public Loader<List<MovieReviews>> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<List<MovieReviews>>(this) {
            List<MovieReviews> data;

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
            public List<MovieReviews> loadInBackground() {
                String searchQueryUrlString = args.getString(SEARCH_QUERY_URL_EXTR);
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
                    List<MovieReviews> movieReviewsList = new ArrayList<>();
                    for (int i = 0; i < parentArray.length(); i++) {
                        JSONObject finalObject = parentArray.getJSONObject(i);
                        MovieReviews movieReviews = new MovieReviews();
                        movieReviews.setAuthor(finalObject.getString("author"));
                        movieReviews.setContent(finalObject.getString("content"));
                        movieReviewsList.add(movieReviews);
                    }
                    return movieReviewsList;
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
            public void deliverResult(List<MovieReviews> data) {
                this.data=data;
                super.deliverResult(data);
            }
        };

    }
    @Override
    public void onLoadFinished(Loader<List<MovieReviews>> loader, final List<MovieReviews> result) {
        if (result != null) {

            MovieAdapter adapter=new MovieAdapter(getApplicationContext(), result);
            lvReviews.setAdapter(adapter);

        } else {
            Toast.makeText(getApplicationContext(), R.string.error, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onLoaderReset(Loader<List<MovieReviews>> loader) {

    }
    public class MovieAdapter extends ArrayAdapter {
        private final List<MovieReviews> movieReviewsList;
        private final int resource;
        private final LayoutInflater inflater;
        public MovieAdapter(Context context, List<MovieReviews> objects) {
            super(context, R.layout.review_row, objects);
            movieReviewsList=objects;
            this.resource= R.layout.review_row;
            inflater=(LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            MovieAdapter.ViewHolder viewHolder=null;
            if(convertView==null)
            {   viewHolder=new MovieAdapter.ViewHolder();
                convertView=inflater.inflate(resource,null);
                viewHolder.author =(TextView) convertView.findViewById(R.id.author);
                viewHolder.content =(TextView) convertView.findViewById(R.id.content);
                convertView.setTag(viewHolder);
            }else {
                viewHolder=(MovieAdapter.ViewHolder)convertView.getTag();
            }

            viewHolder.author.setText(movieReviewsList.get(position).getAuthor()+" :");
            viewHolder.content.setText(movieReviewsList.get(position).getContent());
            return convertView;

        }
        class ViewHolder
        {
            private TextView author;
            private TextView content;

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
