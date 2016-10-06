package app.com.example.android.popularmovies.network;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

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

import app.com.example.android.popularmovies.BuildConfig;

/**
 * Created by shuna on 9/28/16.
 */
public class FetchTrailerTask extends AsyncTask<String, Void, ArrayList<Trailer> > {
    private final String LOG_TAG = FetchTrailerTask.class.getSimpleName();
    private OnTrailerTaskCompleted listener;


    public FetchTrailerTask(OnTrailerTaskCompleted listener) {
        this.listener = listener;
    }

    public interface OnTrailerTaskCompleted{
        void trailerTaskCompleted(ArrayList<Trailer> list);
    }

    @Override
    protected ArrayList<Trailer> doInBackground(String... params) {
        if (params.length == 0) {
            return null;
        }
        ArrayList<Trailer> trailerList;
        String movieId = params[0];
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String forecastJsonStr = null;

        try {
            final String BASE_URL = "http://api.themoviedb.org/3/movie/";
            final String KEYID_PARAM = "api_key";
            Uri builtUri = Uri.parse(BASE_URL + movieId + "/" + "videos" + "?").buildUpon()
                    .appendQueryParameter(KEYID_PARAM, BuildConfig.THE_MOVIE_DATABASE_API_KEY)
                    .build();
            Log.d(LOG_TAG, builtUri.toString());
            URL url = new URL(builtUri.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            forecastJsonStr = buffer.toString();
            Log.d(LOG_TAG, forecastJsonStr);
            trailerList = getMovieDataFromJson(forecastJsonStr);
            return trailerList;
        }catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            //return null;
        }catch (JSONException e){
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        return null;
    }

    private ArrayList<Trailer> getMovieDataFromJson(String forecastJsonStr) throws JSONException{
        final String TRAILER_ID = Trailer.TRAILER_ID;
        final String TRAILER_KEY = Trailer.TRAILER_KEY;
        final String TRAILER_NAME = Trailer.TRAILER_NAME;
        final String TRAILER_SITE = Trailer.TRAILER_SITE;
        final String TRAILER_SIZE = Trailer.TRAILER_SIZE;

        try{
            JSONObject forecastJson = new JSONObject(forecastJsonStr);
            JSONArray movieArray = forecastJson.getJSONArray("results");
            ArrayList<Trailer> list = new ArrayList<Trailer>();

            for (int i = 0; i < movieArray.length(); i++){
                String id;
                String key;
                String name;
                String site;
                String size;

                JSONObject movieJson = movieArray.getJSONObject(i);
                id = movieJson.getString(TRAILER_ID);
                key = movieJson.getString(TRAILER_KEY);
                name = movieJson.getString(TRAILER_NAME);
                site = movieJson.getString(TRAILER_SITE);
                size = movieJson.getString(TRAILER_SIZE);

                Trailer newTrailer = new Trailer(id, key, name, site, size);
                list.add(newTrailer);
            }
            Log.d("listsize", list.size() + "");

            return list;
        }catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<Trailer> trailers) {
        listener.trailerTaskCompleted(trailers);
    }
}
