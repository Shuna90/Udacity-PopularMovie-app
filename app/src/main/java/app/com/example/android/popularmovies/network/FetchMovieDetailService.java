package app.com.example.android.popularmovies.network;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface FetchMovieDetailService {

    @GET("3/movie/{sort_by}")
    Call<Movie> fetchMoviesByOrder(
            @Path("sort_by") String sortBy,
            @Query("api_key") String apiKey);

    @GET("3/movie/{id}/videos")
    Call<Trailer> fetchTrailersById(
            @Path("id") String movieId,
            @Query("api_key") String apiKey);

    @GET("3/movie/{id}/reviews")
    Call<Review> fetchReviewsById(
            @Path("id") String movieId,
            @Query("api_key") String apiKey);

    @GET("3/movie/{id}/reviews")
    Call<Cast> fetchCastById(
            @Path("id") String movieId,
            @Query("api_key") String apiKey);

    @GET("3/movie/{id}")
    Call<Information> fetchInfoById(
            @Path("id") String movieId,
            @Query("api_key") String apiKey);
}
