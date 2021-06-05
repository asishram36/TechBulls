package com.movie.techbulls.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.movie.techbulls.R;
import com.movie.techbulls.adapter.MovieListAdapter;
import com.movie.techbulls.api.ApiConfig;
import com.movie.techbulls.api.ApiController;
import com.movie.techbulls.helper.ConnectionHelper;
import com.movie.techbulls.model.MovieListDataModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    //Array List
    private ArrayList<MovieListDataModel> movieList;

    //Adapter
    private MovieListAdapter movieListAdapter;

    //ImageView
    private ImageView backArrow, noDataImage, iconImage;

    //Toolbar
    private Toolbar toolbar;

    //TextView
    private TextView toolbarTitle, noDataText;

    //RecyclerView
    private RecyclerView movieListRecycler;

    //ConnectionHelper
    private ConnectionHelper connection;

    //ProgressBar
    private ProgressBar progressBarCentre, progressBar;

    //PreferenceManager
    private PreferenceManager prefManager;

    //String
    private String userID = "",  error;

    private Boolean resp;

    private SharedPreferences preferences;

    private RelativeLayout iconLayout, search;

    final static int REQUEST_LOCATION = 199;

    private SearchView searchView;

    ImageView searchClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            // Activity was brought to front and not created,
            // Thus finishing this will get us to the last viewed activity
            finish();
            return;
        }

        if (!isTaskRoot()
                && getIntent().hasCategory(Intent.CATEGORY_LAUNCHER)
                && getIntent().getAction() != null
                && getIntent().getAction().equals(Intent.ACTION_MAIN)) {

            finish();
            return;
        }

        //init method use for declaration view and other components
        init();

        if (connection.isConnectingToInternet()) {

            // call home screen API
            movieListRecycler.setVisibility(View.GONE);
            progressBarCentre.setVisibility(View.VISIBLE);

            callMovieListAPI();

        } else {
            Toast.makeText(getApplicationContext(), "No Internet connection", Toast.LENGTH_SHORT).show();
        }

        iconImage.setOnClickListener(v -> {
            search.setVisibility(View.VISIBLE);
            iconLayout.setVisibility(View.GONE);
        });

        searchClose.setOnClickListener(view -> {
            search.setVisibility(View.GONE);
            iconLayout.setVisibility(View.VISIBLE);

            searchView.setQueryHint(Html.fromHtml("<font color = #b5b5b5>" + "Search Name.." + "</font>"));
            searchView.setQuery("", false);
            searchView.clearFocus();
            searchClose.setVisibility(View.GONE);
            movieListAdapter = new MovieListAdapter(movieList, MainActivity.this);
            movieListRecycler.setAdapter(movieListAdapter);

            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

            if(imm != null)
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                if (query.length() > 0){
                    searchClose.setVisibility(View.VISIBLE);
                } else {
                    searchClose.setVisibility(View.GONE);

                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (inputMethodManager != null) {
                        inputMethodManager.toggleSoftInput(0, 0);
                    }
                }

                movieListAdapter.getFilter().filter(query);

                movieListRecycler.setAdapter(movieListAdapter);
                movieListAdapter.notifyDataSetChanged();

                searchView.clearFocus();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (newText.length() > 0){
                    searchClose.setVisibility(View.VISIBLE);
                } else {
                    searchClose.setVisibility(View.GONE);
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (inputMethodManager != null) {
                        inputMethodManager.toggleSoftInput(0, 0);
                    }
                }

                movieListAdapter.getFilter().filter(newText);

                return false;
            }
        });

    }

    /*
     * Initialize view and other components
     */
    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    private void init() {

        //Toolbar
        toolbar = findViewById(R.id.toolbar);

        // set SupportActionBar
        //setSupportActionBar(toolbar);

        //ImageView
        backArrow = findViewById(R.id.backArrow);
        noDataImage = findViewById(R.id.noDataImage);
        noDataImage.setVisibility(View.GONE);

        iconImage = findViewById(R.id.iconImage);
        iconImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_search));

        iconLayout = findViewById(R.id.iconLayout);
        iconLayout.setVisibility(View.VISIBLE);

        search = findViewById(R.id.search);
        search.setVisibility(View.GONE);

        //TextView
        toolbarTitle = findViewById(R.id.toolbarTitle);
        toolbarTitle.setText("My Show");


        noDataText = findViewById(R.id.noDataText);
        noDataText.setVisibility(View.GONE);

        //ArrayList
        movieList = new ArrayList<MovieListDataModel>();


        //RecyclerView
        movieListRecycler = findViewById(R.id.movieListRecycler);

        //LinearLayoutManager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        movieListRecycler.setLayoutManager(linearLayoutManager);
        movieListRecycler.setNestedScrollingEnabled(false);

        movieListAdapter = new MovieListAdapter(movieList, MainActivity.this);
        movieListRecycler.setAdapter(movieListAdapter);

        //ProgressBar
        progressBarCentre = findViewById(R.id.progressBarCentre);
        progressBar = findViewById(R.id.progressBar);
        progressBarCentre.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);

        //ConnectionHelper
        connection = new ConnectionHelper(this);

        searchView = findViewById(R.id.search_view);
        searchView.requestFocus();
        searchView.setFocusable(false);
        searchView.setIconified(false);
        searchView.requestFocusFromTouch();
        //searchView.setQueryHint("Search....");
        searchView.setClipToPadding(true);
        searchView.clearFocus();

        searchView.setQueryHint(Html.fromHtml("<font color = #b5b5b5>" + "Search Movie Name .." + "</font>"));

        // Does help!
        searchClose = searchView.findViewById(R.id.search_close_btn);
        searchClose.setImageResource(R.drawable.ic_close);
        searchClose.setVisibility(View.GONE);

    }

    private void callMovieListAPI() {

        progressBarCentre.setVisibility(View.VISIBLE);

        // StringRequest for making Request
        /*
         * Response getting from server
         */
        // Server related error
        StringRequest strReq = new StringRequest(Request.Method.GET,
                ApiConfig.MOVIE_LIST_URL, response -> {

            if (!response.isEmpty()) {

                try {
                    // fetch Response as JSONObject
                    JSONObject jObj = new JSONObject(response);
                    // put success message
                    resp = jObj.optBoolean("Response");

                    String message = jObj.optString("message");
                    // Error
                    //error = jObj.optString("Error");

                    if (resp) {

                        final JSONArray movie_array = jObj.getJSONArray("Search");

                        if (movie_array.length() == 0) {

                        } else {

                            String dtTime;

                            for (int i = 0; i < movie_array.length(); i++) {

                                JSONObject content = movie_array.getJSONObject(i);

                                String title = content.getString("Title");
                                String year = content.getString("Year");
                                String type = content.getString("Type");
                                String poster = content.getString("Poster");

                                movieList.add(new MovieListDataModel(poster, title, year));

                            }

                            movieListAdapter = new MovieListAdapter(movieList, MainActivity.this);
                            movieListAdapter.notifyDataSetChanged();
                            movieListRecycler.setAdapter(movieListAdapter);
                            movieListAdapter.notifyDataSetChanged();
                            progressBarCentre.setVisibility(View.GONE);



                        }

                        progressBarCentre.setVisibility(View.GONE);
                        movieListRecycler.setVisibility(View.VISIBLE);
                        noDataText.setVisibility(View.GONE);
                        noDataImage.setVisibility(View.GONE);

                    } else {

                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        progressBarCentre.setVisibility(View.GONE);
                        movieListRecycler.setVisibility(View.GONE);
                        noDataText.setVisibility(View.VISIBLE);
                        noDataImage.setVisibility(View.VISIBLE);
                        noDataText.setText("No list available");
                    }

                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    progressBarCentre.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "Connection Error", Toast.LENGTH_SHORT).show();
                }
            }
        }, error -> {
            progressBarCentre.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(), "Server Error", Toast.LENGTH_SHORT).show();
        }) {

        };
        //cache disabled
        strReq.setShouldCache(false);
        // Adding request to request queue
        //RequestQueue requestQueue;
        ApiController.getInstance().addToRequestQueue(strReq);
    }
}