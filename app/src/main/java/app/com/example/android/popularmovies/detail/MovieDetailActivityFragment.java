package app.com.example.android.popularmovies.detail;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import app.com.example.android.popularmovies.R;
import app.com.example.android.popularmovies.Utility;
import app.com.example.android.popularmovies.data.MovieContract;
import app.com.example.android.popularmovies.network.Cast;
import app.com.example.android.popularmovies.network.FetchCastTask;
import app.com.example.android.popularmovies.network.FetchInfoTask;
import app.com.example.android.popularmovies.network.FetchReviewTask;
import app.com.example.android.popularmovies.network.FetchTrailerTask;
import app.com.example.android.popularmovies.network.Information;
import app.com.example.android.popularmovies.network.Movie;
import app.com.example.android.popularmovies.network.Review;
import app.com.example.android.popularmovies.network.Trailer;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailActivityFragment extends Fragment implements FetchTrailerTask.OnTrailerTaskCompleted,
        FetchReviewTask.OnReviewTaskCompleted, FetchCastTask.OnFetchCastTaskCompleted, FetchInfoTask.OnFetchInfoTaskCompleted
{

    private final String LOG_TAG = MovieDetailActivityFragment.class.getSimpleName();
    public static final String MOVIE_Detail = "MOVIE_Detail";
    public static final String MOVIE_TRAILERS = "MOVIE_TRAILERS";
    public static final String MOVIE_REVIEWS = "MOVIE_REVIEWS";
    public static final String MOVIE_CAST = "MOVIE_CAST";
    public static final String MOVIE_INFO = "MOVIE_INFO";

    @BindView(R.id.movie_title)
    TextView movie_title;
    @BindView(R.id.movie_rating)
    TextView movie_rating;
    @BindView(R.id.movie_description)
    TextView movie_description;
    @BindView(R.id.movie_release_date)
    TextView movie_date;
    @BindView(R.id.movie_favorite_button)
    ImageButton movie_favorite;
    @BindView(R.id.movie_poster_image_view)
    ImageView movie_poster;
    @BindView(R.id.trailer_list)
    RecyclerView trailerRecyclerView;
    @BindView(R.id.review_list)
    RecyclerView reviewRecyclerView;
    @BindView(R.id.review_text)
    TextView review_text;
    @BindView(R.id.trailer_text)
    TextView trailer_text;

    @BindView(R.id.movie_detail_poster)
    ImageView imageView;
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout collapsingToolbarLayout;

    @BindView(R.id.information_movie_rated)
    TextView movie_adult;
    @BindView(R.id.information_movie_in_theaters)
    TextView movie_in_theater;
    @BindView(R.id.information_movie_genre)
    TextView movie_genre;
    @BindView(R.id.information_movie_director)
    TextView movie_director;
    @BindView(R.id.information_movie_cast)
    TextView movie_cast;
    @BindView(R.id.information_movie_run_time)
    TextView movie_runtime;

    private Movie movie;
    private boolean isFavorite;

    FetchReviewTask reviewTask;
    FetchTrailerTask trailerTask;
    FetchCastTask castTask;
    FetchInfoTask infoTask;

    private ArrayList<Review> reviewList;
    private ArrayList<Trailer> trailerList;
    private Cast cast;
    private Information info;

    private ComplexAdapter reviewAdapter;
    private ComplexAdapter trailerAdapter;

    public MovieDetailActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        Bundle arguments = getArguments();
        if (arguments != null) {
            movie = arguments.getParcelable(MovieDetailActivityFragment.MOVIE_Detail);
            //Log.d(LOG_TAG, movie.getTitle());
        }

        trailerList = new ArrayList<>();
        reviewList = new ArrayList<>();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        ButterKnife.bind(this, rootView);

        if (!Utility.isTwoPane(getContext())) {
            View parent = getActivity().findViewById(R.id.toolbar_layout);
            imageView = (ImageView) parent.findViewById(R.id.movie_detail_poster);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (trailerList != null && trailerList.size() > 0) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(trailerList.get(0).getUrl())));
                        Log.d(LOG_TAG, " click");
                    }
                }
            });
        }

        if (movie != null) {
            Picasso.with(getContext())
                    .load(movie.getPoster_path())
                    .config(Bitmap.Config.RGB_565)
                    .into(movie_poster);
        }

        isFavorite = checkFavorite();
        movie_favorite.setSelected(isFavorite);
        movie_favorite.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                ContentValues contentMovie = new ContentValues();
                contentMovie.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movie.getId());
                contentMovie.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE, movie.getTitle());
                contentMovie.put(MovieContract.MovieEntry.COLUMN_MOVIE_OVERVIEW, movie.getOverview());
                contentMovie.put(MovieContract.MovieEntry.COLUMN_MOVIE_VOTE_AVERAGE, movie.getRating());
                contentMovie.put(MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE, movie.getReleaseDate());
                contentMovie.put(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH, movie.getPoster_path());
                contentMovie.put(MovieContract.MovieEntry.COLUMN_MOVIE_BACKDROP_PATH, movie.getBackdropUrl());

                isFavorite = checkFavorite();
                if (!isFavorite) {
                    getContext().getContentResolver()
                            .insert(MovieContract.MovieEntry.CONTENT_URI, contentMovie);
                    movie_favorite.setSelected(true);
                    Toast.makeText(getContext(), "Make " + movie.getTitle() + " Favorite.", Toast.LENGTH_SHORT).show();
                } else {
                    getContext().getContentResolver()
                            .delete(MovieContract.MovieEntry.buildMovieUriId(movie.getId()),
                                    null,
                                    new String[]{movie.getId()});
                    movie_favorite.setSelected(false);
                    Toast.makeText(getContext(), "Remove " + movie.getTitle() + " From Favorite.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        post();

        LinearLayoutManager layoutManagerTrailer
                = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        trailerRecyclerView.setLayoutManager(layoutManagerTrailer);

        LinearLayoutManager layoutManagerReview
                = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        reviewRecyclerView.setLayoutManager(layoutManagerReview);

        trailerAdapter = new ComplexAdapter(getContext(), new ComplexAdapter.ComplexAdapterOnClickHandler() {
            @Override
            public void onClick(Object o, int adapterPosition) {
                if (trailerAdapter.getObject((adapterPosition)) instanceof Trailer
                        && trailerAdapter.getItemCount() > 0) {
                    Trailer trailer = (Trailer) trailerAdapter.getObject(adapterPosition);
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(trailer.getUrl())));
                }
            }
        }, trailerList, null);

        reviewAdapter = new ComplexAdapter(getContext(), new ComplexAdapter.ComplexAdapterOnClickHandler() {
            @Override
            public void onClick(Object o, int adapterPosition) {
                if (reviewAdapter.getObject((adapterPosition)) instanceof Review
                        && reviewAdapter.getItemCount() > 0) {
                    Review review = (Review) reviewAdapter.getObject(adapterPosition);
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(review.getUrl())));
                }
            }
        }, null, reviewList);

        reviewRecyclerView.setAdapter(reviewAdapter);
        trailerRecyclerView.setAdapter(trailerAdapter);

        reviewRecyclerView.setFocusable(false);
        trailerRecyclerView.setFocusable(false);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null){
            trailerList = savedInstanceState.getParcelableArrayList(MOVIE_TRAILERS);
            trailer_text.setText(getString(R.string.trailer_text));
            trailerAdapter.updateTrailer(trailerList);

            reviewList = savedInstanceState.getParcelableArrayList(MOVIE_REVIEWS);
            review_text.setText(getString(R.string.review_text));
            reviewAdapter.updateReview(reviewList);

            cast = savedInstanceState.getParcelable(MOVIE_CAST);
            castTaskCompleted(cast);

            info = savedInstanceState.getParcelable(MOVIE_INFO);
            infoTaskCompleted(info);
        }else{
            updateTrailerList();
            updateReviewList();
            updateCast();
            updateInfo();
        }
    }

    private void post(){
        movie_title.setText(movie.getTitle());
        movie_rating.setText(getActivity().getString(R.string.movie_rating, movie.getRating()));
        movie_description.setText(movie.getOverview());
        movie_date.setText(movie.getReleaseDate());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelableArrayList(MOVIE_TRAILERS, trailerList);
        outState.putParcelableArrayList(MOVIE_REVIEWS, reviewList);
        outState.putParcelable(MOVIE_CAST, cast);
        outState.putParcelable(MOVIE_INFO, info);
    }

    private void finishCreatingMenu(Menu menu) {
        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);
        menuItem.setIntent(createShareForecastIntent());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if ( getActivity() instanceof MovieDetailActivity){
            // Inflate the menu; this adds items to the action bar if it is present.
            inflater.inflate(R.menu.menu_movie_detail, menu);
            finishCreatingMenu(menu);
        }
    }

    private Intent createShareForecastIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, movie.getMovieString());
        return shareIntent;
    }

    public boolean checkFavorite(){
        Cursor cs = getContext().getContentResolver()
                .query(MovieContract.MovieEntry.CONTENT_URI,
                        MovieContract.MovieEntry.MOVIE_COLUMNS,
                        MovieContract.MovieEntry.COLUMN_MOVIE_ID + "=?",
                        new String[]{movie.getId()},
                        null);
        boolean isFavorate = false;
        assert cs != null;
        if (cs.moveToFirst()){
            isFavorate = true;
        }
        cs.close();
        return isFavorate;
    }

    public void updateReviewList(){
        reviewTask = new FetchReviewTask(this);
        reviewTask.execute(movie.getId());
        Log.d(LOG_TAG, "review Task completed");
    }

    public void updateTrailerList(){
        trailerTask = new FetchTrailerTask(this);
        trailerTask.execute(movie.getId());
        Log.d(LOG_TAG, "review Task completed");
    }

    public void updateCast(){
        castTask = new FetchCastTask(this);
        castTask.execute(movie.getId());
        Log.d(LOG_TAG, "cast Task completed");
    }

    public void updateInfo(){
        infoTask = new FetchInfoTask(this);
        infoTask.execute(movie.getId());
        Log.d(LOG_TAG, "info Task completed");
    }

    @Override
    public void castTaskCompleted(Cast cast){
        if (cast != null){
            this.cast = cast;
            movie_cast.setText(cast.getCredit());
            movie_director.setText(cast.getDirector());
        }
    }

    @Override
    public void infoTaskCompleted(Information info) {
        if (info != null){
            this.info = info;
            movie_in_theater.setText(movie.getReleaseDate());
            movie_adult.setText(Boolean.toString(info.getAdult()));
            movie_genre.setText(info.getGenres());
            movie_runtime.setText(Utility.getRumTime(info.getRuntime()));
        }
    }

    @Override
    public void reviewTaskCompleted(ArrayList<Review> reviews) {
        if (reviews != null && reviews.size() > 0){
            review_text.setText(getString(R.string.review_text));
            reviewList.clear();
            reviewList.addAll(reviews);
            reviewAdapter.updateReview(reviewList);
        }
    }

    @Override
    public void trailerTaskCompleted(ArrayList<Trailer> list) {
        if (list != null && list.size() > 0){
            trailer_text.setText(getString(R.string.trailer_text));
            trailerList.clear();
            trailerList.addAll(list);
            trailerAdapter.updateTrailer(trailerList);
        }
    }

}
