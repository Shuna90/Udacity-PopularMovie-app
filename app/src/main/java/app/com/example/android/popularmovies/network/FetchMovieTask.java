package app.com.example.android.popularmovies.network;

import android.content.Context;
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
import app.com.example.android.popularmovies.Utility;
import app.com.example.android.popularmovies.data.MovieContract;

/**
 * Created by shuna on 8/18/16.
 */
public class FetchMovieTask extends AsyncTask<String, Void, ArrayList<Movie> > {
    private final String LOG_TAG = FetchMovieTask.class.getSimpleName();
    private final Context mContext;
    private boolean DEBUG = true;
    private OnTaskCompleted listener;

    public FetchMovieTask(Context mContext, OnTaskCompleted listener) {
        this.mContext = mContext;
        this.listener = listener;
    }

    public interface OnTaskCompleted{
        void onTaskCompleted(ArrayList<Movie> list);
    }

    @Override
    protected ArrayList<Movie> doInBackground(String... params)  {
        if (!Utility.isNetworkAvailable(mContext)){
            return null;
        }
        if (params.length == 0) {
            return null;
        }
        ArrayList<Movie> list;
        String orderBy = params[0];
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String forecastJsonStr = null;

        try {
            final String BASE_URL = "http://api.themoviedb.org/3/movie/";
            final String KEYID_PARAM = "api_key";
            Uri builtUri = Uri.parse(BASE_URL + orderBy + "?").buildUpon()
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
            list = getMovieDataFromJson(forecastJsonStr);
            return list;
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

    private ArrayList<Movie> getMovieDataFromJson(String forecastJsonStr) throws JSONException{
        final String MOVIE_ID = MovieContract.MovieEntry.COLUMN_MOVIE_ID;
        final String MOVIE_TITLE = MovieContract.MovieEntry.COLUMN_MOVIE_TITLE;
        final String MOVIE_OVERVIEW = MovieContract.MovieEntry.COLUMN_MOVIE_OVERVIEW;
        final String MOVIE_VOTE_AVERAGE = MovieContract.MovieEntry.COLUMN_MOVIE_VOTE_AVERAGE;
        final String MOVIE_RELEASE_DATE = MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE;
        final String MOVIE_POSTER_PATH = MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH;
        final String MOVIE_BACKDROP_PATH = MovieContract.MovieEntry.COLUMN_MOVIE_BACKDROP_PATH;
        try{
            JSONObject forecastJson = new JSONObject(forecastJsonStr);
            JSONArray movieArray = forecastJson.getJSONArray("results");
            ArrayList<Movie> list = new ArrayList<Movie>();

            for (int i = 0; i < 20; i++){
                String id;
                String title;
                String overview;
                String average;
                String release_date;
                String poster_path;
                String backdrop;

                JSONObject movieJson = movieArray.getJSONObject(i);
                id = movieJson.getString(MOVIE_ID);
                title = movieJson.getString(MOVIE_TITLE);
                overview = movieJson.getString(MOVIE_OVERVIEW);
                average = movieJson.getString(MOVIE_VOTE_AVERAGE);
                release_date = movieJson.getString(MOVIE_RELEASE_DATE);
                poster_path = movieJson.getString(MOVIE_POSTER_PATH);
                backdrop = movieJson.getString(MOVIE_BACKDROP_PATH);

                Movie newM = new Movie(id, title, overview, average, release_date, poster_path, backdrop);
                list.add(newM);
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
    protected void onPostExecute(ArrayList<Movie> movies) {
            listener.onTaskCompleted(movies);
    }
}
