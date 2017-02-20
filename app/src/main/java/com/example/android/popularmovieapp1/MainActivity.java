
package com.example.android.popularmovieapp1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.android.popularmovieapp1.models.MovieModel;

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

@SuppressWarnings("ALL")
public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<MovieModel>>,SharedPreferences.OnSharedPreferenceChangeListener{

    private static final String SEARCH_QUERY_URL_EXTRA = "query";
    private static final int GITHUB_SEARCH_LOADER = 22;
    private   MoviesAdapter mAdapter;
    private RecyclerView recyclerView;
    //GridView gvMovies;
    private GridLayoutManager layoutManager;
    final String api_key="21ba08e3b68174860025a5de7e5640cc";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //gvMovies=(GridView) findViewById(R.id.gvMovies);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        layoutManager= new GridLayoutManager(MainActivity.this, 2);
        recyclerView.setLayoutManager(layoutManager);
        getSupportLoaderManager().initLoader(GITHUB_SEARCH_LOADER, null, this);
        setupSharedPreferences();
    }


    private void setupSharedPreferences() {
        // Get all of the values from shared preferences to set it up
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        loadURL(sharedPreferences);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    private void loadURL(SharedPreferences sharedPreferences) {
        runQuery(sharedPreferences.getString(getString(R.string.pref_order_key),
                getString(R.string.pref_order_popular_value)));
    }


    private void runQuery(String word) {

        Bundle queryBundle = new Bundle();
        queryBundle.putString(SEARCH_QUERY_URL_EXTRA, "http://api.themoviedb.org/3/movie/"+word+"?api_key="+api_key);


        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> githubSearchLoader = loaderManager.getLoader(GITHUB_SEARCH_LOADER);
        if (githubSearchLoader == null) {
            loaderManager.initLoader(GITHUB_SEARCH_LOADER, queryBundle, this);
        } else {
            loaderManager.restartLoader(GITHUB_SEARCH_LOADER, queryBundle, this);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
         if (key.equals(getString(R.string.pref_order_key))) {
            loadURL(sharedPreferences);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }


    @Override
    public Loader<List<MovieModel>> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<List<MovieModel>>(this) {
            List<MovieModel> data;

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
            public List<MovieModel> loadInBackground() {
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
                    List<MovieModel> movieModelList = new ArrayList<>();
                    movieModelList.clear();
                    for (int i = 0; i < parentArray.length(); i++) {
                        JSONObject finalObject = parentArray.getJSONObject(i);
                        MovieModel movieModel = new MovieModel();
                        movieModel.setOriginal_title(finalObject.getString("original_title"));
                        movieModel.setOverview(finalObject.getString("overview"));
                        movieModel.setRelease_date(finalObject.getString("release_date"));
                        movieModel.setPoster_path(finalObject.getString("poster_path"));
                        movieModel.setVote_average((float) finalObject.getDouble("vote_average"));

                        movieModelList.add(movieModel);
                    }
                    return movieModelList;
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
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
            public void deliverResult(List<MovieModel> data) {
                this.data=data;
                super.deliverResult(data);
            }
        };

    }

    @Override
    public void onLoadFinished(Loader<List<MovieModel>> loader, final List<MovieModel> result) {
        if (result != null) {
            mAdapter = new MoviesAdapter((List<MovieModel>) result);
            recyclerView.setAdapter(mAdapter);
            recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    MovieModel movie = result.get(position);
                    Toast.makeText(getApplicationContext(), movie.getOriginal_title(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, MovieDetailsActivity.class);
                    intent.putExtra("movie", movie);
                    startActivity(intent);
                }
            }));
//            MovieAdapter adapter=new MovieAdapter(getApplicationContext(),R.layout.row,result);
//            gvMovies.setAdapter(adapter);
//
//            gvMovies.setOnItemClickListener(new AdapterView.OnItemClickListener() {  // list item click opens a new detailed activity
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    MovieModel movieModel = result.get(position); // getting the model
//                    Toast.makeText(getApplicationContext(),movieModel.getOriginal_title(),Toast.LENGTH_LONG).show();
//                    Intent intent = new Intent(MainActivity.this, MovieDetailsActivity.class);
//                    intent.putExtra("movie", movieModel);
//                    startActivity(intent);
//                }
//            });
        } else {
            Toast.makeText(getApplicationContext(), R.string.error, Toast.LENGTH_SHORT).show();
        }

    }
//    public class MovieAdapter extends ArrayAdapter{
//        private List<MovieModel> movieModelList;
//        private final int resource;
//        private final LayoutInflater inflater;
//        public MovieAdapter(Context context, int resource, List<MovieModel> objects) {
//            super(context, resource, objects);
//            movieModelList=objects;
//            this.resource=resource;
//            inflater=(LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
//        }
//
//        @NonNull
//        @Override
//        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
//            ViewHolder viewHolder=null;
//            if(convertView==null)
//            {   viewHolder=new ViewHolder();
//                convertView=inflater.inflate(resource,null);
//                viewHolder.ivMovieIcon =(ImageView)convertView.findViewById(R.id.ivMovieIcon);
//                convertView.setTag(viewHolder);
//            }else {
//                viewHolder=(ViewHolder)convertView.getTag();
//            }
//            Picasso.with(getApplicationContext())
//                    .load("http://image.tmdb.org/t/p/w185/"+movieModelList.get(position).getPoster_path())
//                    .placeholder(R.drawable.loading)
//                    .error(R.drawable.notfound)
//                    .into(viewHolder.ivMovieIcon);
//            return convertView;
//
//        }
//        class ViewHolder
//        {
//            private ImageView ivMovieIcon;
//        }
//    }

    @Override
    public void onLoaderReset(Loader<List<MovieModel>> loader) {

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();

        if (id==R.id.popular)
        {
            runQuery("popular");
            return true;
        }
        if (id==R.id.rate)
        {
            runQuery("top_rated");
            return true;
        }
        if (id==R.id.activity_settings)
        {
            Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(startSettingsActivity);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
