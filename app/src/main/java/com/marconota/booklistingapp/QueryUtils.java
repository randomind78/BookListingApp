package com.marconota.booklistingapp;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;


public class QueryUtils {
    private static final String LOG_TAG = QueryUtils.class.getName();
    private static Context context;

    private QueryUtils(Context context) {
        this.context = context;
    }

    public static List<Book> fetchBookData(String stringUrl) {
        //transform string to url
        URL url = createUrl(stringUrl);

        //make http request
        String jsonResponse = "";
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "make http request error: ", e.getCause());
        }
        List<Book> bookList = extractBooksData(jsonResponse);
        return bookList;
    }

    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "Create url error: ", e.getCause());
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        if (url == null) {
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readInputStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error making URL connection. Code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "Request Connection error: ", e.getCause());
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readInputStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader streamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader bufferedReader = new BufferedReader(streamReader);
            String line = bufferedReader.readLine();
            while (line != null) {
                output.append(line);
                line = bufferedReader.readLine();
            }
        }
        return output.toString();
    }

    public static List<Book> extractBooksData(String jsonResponse) {

        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }
        List<Book> books = new ArrayList<Book>();
        try {
            JSONObject root = new JSONObject(jsonResponse);
            JSONArray items = new JSONArray();
            if (root.has("items")) {
                items = root.getJSONArray("items");
            }

            //iterate through result items
            for (int i = 0; i < items.length(); i++) {
                JSONObject book = items.getJSONObject(i);

                JSONObject volumeInfo = book.getJSONObject("volumeInfo");
                String title = volumeInfo.getString("title");

                String authorString = "";
                if (volumeInfo.has("authors")) {
                    JSONArray authors = volumeInfo.getJSONArray("authors");
                    authorString = authors.toString();
                    authorString = authorString.replaceAll("\\[|\\]", "").replaceAll("\"", "").replace(",", ", ");
                }

                String thumbnail;
                if (volumeInfo.has("imageLinks")) {
                    JSONObject images = volumeInfo.getJSONObject("imageLinks");
                    thumbnail = images.getString("thumbnail");
                } else {
                    thumbnail = "";
                }
                double rating;
                String url = volumeInfo.getString("infoLink");
                if (volumeInfo.has("averageRating")) {
                    rating = volumeInfo.getDouble("averageRating");
                } else {
                    rating = 0;
                }

                Book bookItem = new Book(title, authorString, url, rating, thumbnail);
                books.add(bookItem);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "Error parsing JSON" + e.getMessage());
        }
        return books;
    }
}
