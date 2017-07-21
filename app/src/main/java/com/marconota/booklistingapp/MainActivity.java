package com.marconota.booklistingapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Book>> {

    public static final String LOG_TAG = MainActivity.class.getName();
    private static final int LOADER_ID = 1;
    private static final String JSON_URL = "https://www.googleapis.com/books/v1/volumes?q=";
    private GridLayoutManager gridLayoutManager;
    public String query = "";
    public String searchQuery = "";
    public boolean searching = false;
    int startIndex = 0;
    int endIndex = 40;
    RecyclerViewAdapter adapter;
    @BindView(R.id.empty_tv)
    TextView empty_tv;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.search_view)
    SearchView searchView;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //setup grid depending on screen size
        int screenWidth = getResources().getConfiguration().screenWidthDp;
        if (screenWidth <= 360) {//j5 portrait
            gridLayoutManager = new GridLayoutManager(MainActivity.this, 2);
        } else if (screenWidth > 360 && screenWidth <= 600) {//asus portrait
            gridLayoutManager = new GridLayoutManager(MainActivity.this, 3);
        } else if (screenWidth > 600 && screenWidth <= 800) {//j5 landscape
            gridLayoutManager = new GridLayoutManager(MainActivity.this, 4);
        } else if (screenWidth > 800) {//asus landscape
            gridLayoutManager = new GridLayoutManager(MainActivity.this, 5);
        }

        //setup recyclerView
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(gridLayoutManager);
        RecyclerView.ItemAnimator animator = new DefaultItemAnimator();
        animator.setAddDuration(1000);
        animator.setRemoveDuration(1000);
        recyclerView.setItemAnimator(animator);

        //attach adapter
        adapter = new RecyclerViewAdapter(this, new ArrayList<Book>());
        recyclerView.setAdapter(adapter);

        //check network connectivity
        if (isOnline()) {
            //default query
            query = JSON_URL + "android&orderBy=newest&maxResults=40";
            getLoaderManager().initLoader(LOADER_ID, null, this).forceLoad();
        } else {
            progressBar.setVisibility(View.GONE);
            empty_tv.setVisibility(View.VISIBLE);
            empty_tv.setText("Network not available");
        }

        //search query
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //perform search only if there is network connectivity
                if (isOnline()) {
                    searching = true;
                    progressBar.setVisibility(View.VISIBLE);
                    String clientQuery = searchView.getQuery().toString();
                    clientQuery = clientQuery.replace(" ", "+");
                    searchQuery = "https://www.googleapis.com/books/v1/volumes?q=" + clientQuery.trim() + "&orderBy=relevance&maxResults=40&startIndex=" + startIndex + "&endIndex=" + endIndex;

                    Log.v(LOG_TAG, clientQuery);
                    getLoaderManager().restartLoader(LOADER_ID, null, MainActivity.this);
                    searchView.clearFocus();
                } else {
                    progressBar.setVisibility(View.GONE);
                    empty_tv.setVisibility(View.VISIBLE);
                    empty_tv.setText("Network not available");
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


    }

    //helper function to check nework connectivity
    public boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    @Override
    public Loader<List<Book>> onCreateLoader(int id, Bundle args) {
        if (searching) {
            return new BookLoader(this, searchQuery);
        }
        return new BookLoader(this, query);
    }

    //recycler view doesn't have a clear addAll methods so I have to write them ourselves (in the adapter)
    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> data) {
        progressBar.setVisibility(View.GONE);
        if (data != null && !data.isEmpty()) {
            adapter.addAll(data);
            recyclerView.setVisibility(View.VISIBLE);
            empty_tv.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.GONE);
            empty_tv.setVisibility(View.VISIBLE);
            empty_tv.setText("Your search returned no results. Try again?");
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
        recyclerView.getRecycledViewPool().clear();
    }
}
