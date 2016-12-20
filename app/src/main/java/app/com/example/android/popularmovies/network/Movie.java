package app.com.example.android.popularmovies.network;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Movie implements Parcelable {

    public static final String LOG_TAG = Movie.class.getSimpleName();
    public static final String URL_POSTER = "http://image.tmdb.org/t/p/w342/";
    public static final String URL_POSTER_ORIGENAL = "http://image.tmdb.org/t/p/original/";
    private String id;
    private String title;
    private String poster_path;
    private String overview;
    private String rating;
    private String releaseDate;
    private String backdrop;
    private boolean favored;

    public static final float POSTER_ASPECT_RATIO = 1.5f;

    public Movie(){

    }

    public Movie(String id, String title, String overview, String rating,
                 String releaseDate, String poster_path, String backdrop_path) {
        this.id = id;
        this.title = title;
        this.poster_path = URL_POSTER + poster_path;
        this.overview = overview;
        this.rating = rating;
        this.releaseDate = releaseDate;
        this.backdrop = backdrop_path;
        //genreIds = new ArrayList<>();
    }

    public String getId(){
        return id;
    }
    public void setId(String id){
        this.id = id;
    }
    public String getTitle(){
        return title;
    }
    public void setTitle(String title){
        this.title = title;
    }

    //http://image.tmdb.org/t/p/w342//e1mjopzAS2KNsvpbpahQ1a6SkSn.jpg
    public String getPoster_path(){
        if (poster_path != null && !poster_path.isEmpty()){
            return poster_path;
        }
        return null;
    }
    public void setPoster_path(String path){
        poster_path = path;
    }

    public String getBackdropUrl() {
        if (backdrop != null && !backdrop.isEmpty()) {
            return URL_POSTER_ORIGENAL + backdrop;
        }
        // Placeholder/Error/Title will be shown instead of a crash.
        return null;
    }

    public String getOverview(){
        return overview;
    }
    public void setOverview(String overview){
        this.overview = overview;
    }

    public String getRating(){
        return rating;
    }
    public void setRating(String rating){
        this.rating = rating;
    }

    public String getReleaseDate(){
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        try {
            Date date = inputFormat.parse(releaseDate);
            return DateFormat.getDateInstance().format(date);
        } catch (ParseException e) {
            Log.e(LOG_TAG, "Release Date error" + releaseDate);
        }
        return releaseDate;
    }
    public void setReleaseDate(String date){
        releaseDate = date;
    }

    public void setBackdrop(String backdrop){
        this.backdrop = backdrop;
    }

    public boolean isFavored() {
        return favored;
    }

    public void setFavored(boolean favored) {
        this.favored = favored;
    }

    public String getMovieString(){
        return "The Movie" + title + "is released on" + releaseDate + "with average rating around" + rating;
    }

    public Movie(Parcel in){
        id = in.readString();
        title = in.readString();
        poster_path = in.readString();
        overview = in.readString();
        rating = in.readString();
        releaseDate = in.readString();
        backdrop = in.readString();

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(title);
        parcel.writeString(poster_path);
        parcel.writeString(overview);
        parcel.writeString(rating);
        parcel.writeString(releaseDate);
        parcel.writeString(backdrop);

    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel parcel) {
            return new Movie(parcel);
        }

        @Override
        public Movie[] newArray(int i) {
            return new Movie[i];
        }

    };

}
