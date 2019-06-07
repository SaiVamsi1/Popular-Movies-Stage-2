package com.vamsi.popularmoviesstage1final;

import android.net.Uri;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


class NetworkTask {
    private final static String MOVIEDB_BASE_URL = "http://api.themoviedb.org/3/movie";
    private final static String API_KEY="c52dc26bff4c4ab80ffd0bcf9ac469cf";

    public static URL buildURL(String[] query) throws MalformedURLException
    {
        Uri builtUri = Uri.parse(MOVIEDB_BASE_URL).buildUpon().appendPath(query[0]).appendQueryParameter("api_key",API_KEY).build();
        return new URL(builtUri.toString());
    }

    public static String getResponsefromurl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.connect();
        InputStream inputStream = urlConnection.getInputStream();
        StringBuilder buffer = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line).append("\n");
        }
        String response = buffer.toString();
        urlConnection.disconnect();
        return response;
    }

}
