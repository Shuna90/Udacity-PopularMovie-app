package app.com.example.android.popularmovies.network;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;

import app.com.example.android.popularmovies.BuildConfig;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FetchInfoTask extends AsyncTask<String, Void, Information> {

    private final String LOG_TAG = FetchCastTask.class.getSimpleName();
    private OnFetchInfoTaskCompleted listener;

    public FetchInfoTask(OnFetchInfoTaskCompleted listener) {
        this.listener = listener;
    }

    public interface OnFetchInfoTaskCompleted{
        void infoTaskCompleted(Information info);
    }

    @Override
    protected Information doInBackground(String... params) {
        if (params.length == 0) {
            return null;
        }
        Information info;
        String movieId = params[0];
        final String BASE_URL = "http://api.themoviedb.org/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        FetchMovieDetailService service = retrofit.create(FetchMovieDetailService.class);
        Call<Information> call = service.fetchInfoById(movieId,
                BuildConfig.THE_MOVIE_DATABASE_API_KEY);
        try {
            Response<Information> response = call.execute();
            info = response.body();
            return info;
        } catch (IOException e) {
            Log.e(LOG_TAG, "A problem occurred talking to the movie db ", e);
        }

        /*
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String forecastJsonStr;

        try {
        //https://api.themoviedb.org/3/movie/297761?api_key=4cdf3d6a44f1b31cf1b0a47d56bb7b53
            final String BASE_URL = "http://api.themoviedb.org/3/movie/";
            final String KEYID_PARAM = "api_key";
            Uri builtUri = Uri.parse(BASE_URL + movieId + "?").buildUpon()
                    .appendQueryParameter(KEYID_PARAM, BuildConfig.THE_MOVIE_DATABASE_API_KEY)
                    .build();
            Log.d(LOG_TAG, builtUri.toString());
            URL url = new URL(builtUri.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder buffer = new StringBuilder();
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
                buffer.append(line).append("\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            forecastJsonStr = buffer.toString();
            Log.d(LOG_TAG, forecastJsonStr);
            info = getMovieDataFromJson(forecastJsonStr);
            return info;
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
        */
        return null;
    }

    /*
    private Information getMovieDataFromJson(String forecastJsonStr) throws JSONException{

        try{
            JSONObject forecastJson = new JSONObject(forecastJsonStr);
            boolean ad = forecastJson.getBoolean(Information.ADULT);
            String adult = ad ? "True" : "False";

            JSONArray genreArray = forecastJson.getJSONArray(Information.GENRES);
            StringBuilder genreBuilder = new StringBuilder();

            for (int i = 0; i < genreArray.length() && i <= 5; i++){
                String credit;

                JSONObject genreJson = genreArray.getJSONObject(i);
                credit = genreJson.getString(Information.GENRES_NAME);

                genreBuilder.append(credit).append(", ");
            }
            genreBuilder.delete(genreBuilder.length() - 2, genreBuilder.length());

            int run = forecastJson.getInt(Information.RUNTIME);
            String runtime = String.valueOf(run);

            String homepage = forecastJson.getString(Information.HOMEPAGE);

            return new Information(adult, runtime, homepage, genreBuilder.toString());
        }catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }
    */
    @Override
    protected void onPostExecute(Information info) {
        listener.infoTaskCompleted(info);
    }
}