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

import app.com.example.android.popularmovies.BuildConfig;

public class FetchCastTask extends AsyncTask<String, Void, Cast> {

    private final String LOG_TAG = FetchCastTask.class.getSimpleName();
    private OnFetchCastTaskCompleted listener;

    public FetchCastTask(OnFetchCastTaskCompleted listener) {
        this.listener = listener;
    }
    public interface OnFetchCastTaskCompleted{
        void castTaskCompleted(Cast cast);
    }

    @Override
    protected Cast doInBackground(String... params) {
        if (params.length == 0) {
            return null;
        }
        Cast cast;
        String movieId = params[0];
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String forecastJsonStr;

        try {
            final String BASE_URL = "http://api.themoviedb.org/3/movie/";
            final String KEYID_PARAM = "api_key";
            Uri builtUri = Uri.parse(BASE_URL + movieId + "/" + "credits" + "?").buildUpon()
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
            cast = getMovieDataFromJson(forecastJsonStr);
            return cast;
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

    private Cast getMovieDataFromJson(String forecastJsonStr) throws JSONException{

        try{
            JSONObject forecastJson = new JSONObject(forecastJsonStr);
            JSONArray CastArray = forecastJson.getJSONArray(Cast.CAST);
            JSONArray CrewArray = forecastJson.getJSONArray(Cast.CREW);
            StringBuilder castBuilder = new StringBuilder();
            Cast cast = null;
            for (int i = 0; i < CastArray.length() && i <= 5; i++){
                String credit;

                JSONObject castJson = CastArray.getJSONObject(i);
                credit = castJson.getString(Cast.CAST_CREDIT);

                castBuilder.append(credit).append(", ");
            }
            castBuilder.delete(castBuilder.length() - 2,castBuilder.length());

            for (int i = 0; i < CrewArray.length(); i++){
                String director;
                JSONObject crewJson = CrewArray.getJSONObject(i);
                director = crewJson.getString(Cast.CREW_JOB);
                if (director.equals(Cast.CREW_DIRECTOR)){
                    cast = new Cast(crewJson.getString(Cast.CAST_CREDIT), castBuilder.toString());
                    break;
                }
            }

            return cast;
        }catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Cast cast) {
        listener.castTaskCompleted(cast);
    }
}
