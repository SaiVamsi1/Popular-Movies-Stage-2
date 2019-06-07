package com.vamsi.popularmoviesstage1final;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;
import com.vamsi.popularmoviesstage1final.RoomDatabase.FavMovieViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity
{
    ArrayList<YoutubeData> list = new ArrayList<YoutubeData>();
    RecyclerView YoutubeRecycler;
    YoutubeAdapter youtubeAdapter;


    List<ReviewData> reviewDataList;
    RecyclerView reviewRecycler;
    ReviewAdapter reviewAdapter;

    ImageView favimage;

    FavMovieViewModel viewModel;
    ModelMovieData movie;

    Button fav;


    boolean state=false;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        reviewDataList=new ArrayList<>();
        favimage = findViewById(R.id.favicon);
        ButterKnife.bind(this);
        viewModel = ViewModelProviders.of(this).get(FavMovieViewModel.class);

        fav=findViewById(R.id.button2);


        YoutubeRecycler = findViewById(R.id.youtube_recycler);
        reviewRecycler=findViewById(R.id.review_recycler_id);

        LinearLayoutManager yl = new LinearLayoutManager(this);
        YoutubeRecycler.setLayoutManager(yl);

        LinearLayoutManager r1 = new LinearLayoutManager(this);
        reviewRecycler.setLayoutManager(r1);

        Intent intent=getIntent();
        ModelMovieData movie = (ModelMovieData) intent.getSerializableExtra("movie");

        ImageView thumbnailIV= findViewById(R.id.imageView);
        TextView titleTV=findViewById(R.id.movie_title);
        TextView ratingTV=findViewById(R.id.movie_rating);
        TextView releasedateTV=findViewById(R.id.movie_release_date);
        TextView overviewTV=findViewById(R.id.movie_overview);

        Picasso.with(this)
                .load(movie.getPosterpath())
                .fit()
                .error(R.drawable.movie_icon)
                .placeholder(R.drawable.movie_icon)
                .into(thumbnailIV);
        titleTV.setText(movie.getOriginalTitle());
        ratingTV.setText(movie.getVoters()+"/10");
        releasedateTV.setText(movie.getReleaseDate());
        overviewTV.setText(movie.getOverview());
        Youtube(movie.getId());
        reviewfetcher(movie.getId());
        updateButton(movie.getId());
/*
        ModelMovieData modelMovieData = viewModel.checkMovieInDatabase(movie.getId());
        if(modelMovieData!=null){
            Toast.makeText(this, "MOVIE IS HERE", Toast.LENGTH_SHORT).show();
            favimage.setImageResource(R.drawable.favadded);
            state = false;
        }
        else
        {
            Toast.makeText(this, "THE MOVIE IS NOT YET INSERTED", Toast.LENGTH_SHORT).show();
            favimage.setImageResource(R.drawable.fav1);
            state = true;
        }*/
    }

    public void Youtube(String id)
    {
        RequestQueue queue=Volley.newRequestQueue(this);
        Uri uri=Uri.parse("http://api.themoviedb.org/3/movie/"+ id + "/videos?&api_key=c52dc26bff4c4ab80ffd0bcf9ac469cf");
        String url=uri.toString();
        StringRequest request=new StringRequest(Request.Method.GET, url, response -> {
            try {
                JSONObject root = new JSONObject(response);
                JSONArray array = root.optJSONArray("results");
                int l = array.length();
                for (int i = 0; i < l; i++) {
                    JSONObject object = array.optJSONObject(i);
                    String key = object.optString("key");
                    String name = object.optString("name");
                    list.add(new YoutubeData(name , key));
                    youtubeAdapter = new YoutubeAdapter(list , DetailActivity.this);
                    YoutubeRecycler.setAdapter(youtubeAdapter);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            if (error instanceof TimeoutError
                    || error instanceof AuthFailureError || error instanceof ParseError
                    || error instanceof NetworkError || error instanceof ServerError) {
                new AlertDialog.Builder(DetailActivity.this)
                        .setMessage("error")
                        .show();
            }

        });
        queue.add(request);
    }

    public void reviewfetcher(String id) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://api.themoviedb.org/3/movie/"+ id + "/reviews?api_key=c52dc26bff4c4ab80ffd0bcf9ac469cf";
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject root = new JSONObject(response);
                            JSONArray array = root.optJSONArray("results");
                            if (array.length() == 0) {
                                Toast.makeText(DetailActivity.this, "There are no Reviews for this movie", Toast.LENGTH_SHORT).show();
                            }
                            int len = array.length();
                            for (int i = 0; i < len; i++) {
                                JSONObject jsonObject = array.getJSONObject(i);
                                String author = jsonObject.optString("author");
                                String comment = jsonObject.optString("content");

                                ReviewData data=new ReviewData(author,comment);
                                reviewDataList.add(data);
                                reviewAdapter=new ReviewAdapter(reviewDataList,DetailActivity.this);
                                reviewRecycler.setAdapter(reviewAdapter);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof NoConnectionError || error instanceof TimeoutError
                        ||error instanceof AuthFailureError||error instanceof ParseError
                        ||error instanceof NetworkError||error instanceof ServerError) {
                    new AlertDialog.Builder(DetailActivity.this)
                            .setMessage("error")
                            .show();
                }

            }
        });
        queue.add(request);
    }

//    private void updateImageView(String id)
//    {
//        ModelMovieData r =  viewModel.checkMovieInDatabase(id);
//        if(r!=null)
//        {
//            favimage.setImageResource(R.drawable.fav1);
//            state = false;
//        }
//        else
//        {
//            favimage.setImageResource(R.drawable.favadded);
//            state = true;
//        }
//    }

    private void updateButton(String id)
    {
        ModelMovieData r=viewModel.checkMovieInDatabase(id);
        if(r!=null)
        {
            fav.setText("UnFav");
        }
        else {
            fav.setText("fav");
        }
    }

//    public void change(View view)
//    {
//
//        if (state) {
//            delete();
//            favimage.setImageResource(R.drawable.fav1);
//            state = !state;
//        } else {
//            insert();
//            favimage.setImageResource(R.drawable.favadded);
//            state = !state;
//        }
//    }

    public void loadfav(View view)
    {
        String f=fav.getText().toString();
        if (f.equalsIgnoreCase("UnFav"))
        {
            delete();
            fav.setText("fav");
        }
        else
        {
            insert();
            fav.setText("UnFav");
        }

    }


    public void insert()  {
        Intent intent=getIntent();
        movie = (ModelMovieData) intent.getSerializableExtra("movie");
        String name = movie.getOriginalTitle();
        String overview = movie.getOverview();
        String releasedate = movie.getReleaseDate();
        String poster = movie.getPosterpath();
        Double rating = movie.getVoters();
        String id = movie.getId();
        Integer votecount=movie.getVoteCount();
        ModelMovieData movie=new ModelMovieData();
        movie.setId(id);
        movie.setOriginalTitle(name);
        movie.setPosterpath(poster);
        movie.setOverview(overview);
        movie.setReleaseDate(releasedate);
        movie.setVoters(rating);
        movie.setVoteCount(votecount);
        Log.e( "test", String.valueOf(movie.getOriginalTitle()));
        viewModel.insert(movie);
        Toast.makeText(this, "Inserted", Toast.LENGTH_SHORT).show();
    }

    public void delete() {
        Intent intent=getIntent();
        movie = (ModelMovieData) intent.getSerializableExtra("movie");
        String name = movie.getOriginalTitle();
        String overview = movie.getOverview();
        String releasedate = movie.getReleaseDate();
        String poster = movie.getPosterpath();
        Double rating = movie.getVoters();
        String id = movie.getId();
        Integer voteCount = movie.getVoteCount();
        ModelMovieData movie=new ModelMovieData();
        movie.setOriginalTitle(name);
        movie.setVoteCount(voteCount);
        movie.setPosterpath(poster);
        movie.setOverview(overview);
        movie.setId(id);
        movie.setVoters(rating);
        movie.setReleaseDate(releasedate);
        viewModel.delete(movie);
        Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
    }


    public void change(View view) {
    }
}