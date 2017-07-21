package com.marconota.booklistingapp;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.List;


public class BookLoader extends AsyncTaskLoader<List<Book>> {
    private static final String LOG_TAG = BookLoader.class.getName();
    private String url;

    public BookLoader(Context context, String url) {
        super(context);
        this.url = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Book> loadInBackground() {
        if (url == null) {
            return null;
        }
        Log.v(LOG_TAG, "loadInBackground url: " + url);
        return QueryUtils.fetchBookData(url);
    }
}
