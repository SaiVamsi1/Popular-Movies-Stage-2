package com.vamsi.popularmoviesstage1final;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.vamsi.popularmoviesstage1final.RoomDatabase.FavMovieViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;



public class MainActivity extends AppCompatActivity {
    private RecyclerView mainRecyclerView;
    private final String POPULAR_QUERY="popular";

    private List<ModelMovieData> results;
    private FavMovieViewModel favMovieViewModel;
    private GridLayoutManager gridLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainRecyclerView=findViewById(R.id.recycler_view);


        results=new ArrayList<>();
        gridLayoutManager=new GridLayoutManager(this,2);
        favMovieViewModel= ViewModelProviders.of(this).get(FavMovieViewModel.class);



        if(amIConnected())
        {
            new FetchingData().execute(POPULAR_QUERY);
        }
        else{
            Toast.makeText(this, "Sorry! There is No Internet Connection", Toast.LENGTH_LONG).show();
            openFavorites();
        }

        RecyclerView.LayoutManager mainLayoutManager;
        if(getResources().getConfiguration().orientation== Configuration.ORIENTATION_LANDSCAPE)
        {
            mainLayoutManager =new GridLayoutManager(this,3);
        }else
        mainLayoutManager =new GridLayoutManager(this,2);
        mainRecyclerView.setLayoutManager(mainLayoutManager);
    }

    private boolean amIConnected()
    {
        ConnectivityManager connectivityManager=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network=connectivityManager.getActiveNetworkInfo();
        return network != null && network.isConnected();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.sort_menu,menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.popular_menu:
                setTitle("Popular Movies");
                if(amIConnected())
            {
                new FetchingData().execute(POPULAR_QUERY);
            }
            else{
                Toast.makeText(this, "Sorry! There is No Internet Connection", Toast.LENGTH_LONG).show();
            }

                break;
            case R.id.top_rated_menu:
                setTitle("Top Rated Movies");
                if(amIConnected())
                {
                    String TOP_RATED_QUERY = "top_rated";
                    new FetchingData().execute(TOP_RATED_QUERY);
                }
                else{
                    Toast.makeText(this, "Sorry! There is No Internet Connection", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.favourites:
                setTitle("Favourite Movies");
                openFavorites();

        }
        return true;
    }

    private void openFavorites() {
        setTitle("Favourite Movies");
       favMovieViewModel.getAllResults().observe(this, modelMovieData -> {
           results = modelMovieData;
           FavAdapter favAdapter = new FavAdapter(MainActivity.this, modelMovieData);
           mainRecyclerView.setLayoutManager(gridLayoutManager);
           mainRecyclerView.setAdapter(favAdapter);
       });

    }


    public class FavAdapter extends RecyclerView.Adapter<FavAdapter.ViewInformations>
    {
        final Context context;
        final List<ModelMovieData> lists;


        FavAdapter(Context context, List<ModelMovieData> lists) {
            this.context = context;
            this.lists = lists;
        }

        @NonNull
        @Override
        public FavAdapter.ViewInformations onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new ViewInformations(LayoutInflater.from(context).inflate(R.layout.movie_thumbnail,viewGroup,false));
        }

        @Override
        public void onBindViewHolder(@NonNull FavAdapter.ViewInformations viewInformations, int i)
        {
            Picasso.with(context).load("https://image.tmdb.org/t/p/w185"+lists.get(i).getPosterpath()).into(viewInformations.imageView);

            viewInformations.itemView.setOnClickListener(view ->
            {
                Intent intent=new Intent(context,DetailActivity.class);
                intent.putExtra("movie",lists.get(i));
                context.startActivity(intent);
            });
        }


        @Override
        public int getItemCount() {
            return lists.size();
        }

        public class ViewInformations extends RecyclerView.ViewHolder {
            final ImageView imageView;
            ViewInformations(@NonNull View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.movie_main_image_view);

            }
        }
    }

    private ModelMovieData[] moviesDataToArray(String movieJsonResults) throws JSONException
    {
        final String Results="results";
        final String Original_title="original_title";
        final String Poster_path="poster_path";
        final String Overview="overview";
        final String Voter_Average="vote_average";
        final String Release_Date="release_date";
        final String Id="id";
        final String Vote_Count="vote_count";

        JSONObject movieJson = new JSONObject(movieJsonResults);
        JSONArray resultsArray = movieJson.getJSONArray(Results);

        ModelMovieData[] movies = new ModelMovieData[resultsArray.length()];

        for (int i=0;i<resultsArray.length();i++)
        {
            movies[i] = new ModelMovieData();

            JSONObject movie=resultsArray.getJSONObject(i);
            movies[i].setOriginalTitle(movie.getString(Original_title));
            movies[i].setOverview(movie.getString(Overview));
            movies[i].setPosterpath(movie.getString(Poster_path));
            movies[i].setVoters(movie.getDouble(Voter_Average));
            movies[i].setReleaseDate(movie.getString(Release_Date));
            movies[i].setId(movie.getString(Id));
            movies[i].setVoteCount(movie.getInt(Vote_Count));
        }
        return movies;

    }


    class FetchingData extends AsyncTask<String,Void,ModelMovieData[]>
    {

        @Override
        protected ModelMovieData[] doInBackground(String... strings)
        {
            String movieResults=null;
            try {
                URL url =NetworkTask.buildURL(strings);
                movieResults=NetworkTask.getResponsefromurl(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                return moviesDataToArray(movieResults);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(ModelMovieData[] movies)
        {
            ImageAdapter imageAdapter = new ImageAdapter(getApplicationContext(), movies);
            mainRecyclerView.setAdapter(imageAdapter);
        }
    }
}
