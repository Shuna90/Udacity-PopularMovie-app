package app.com.example.android.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.facebook.stetho.Stetho;

import app.com.example.android.popularmovies.detail.MovieDetailActivity;
import app.com.example.android.popularmovies.detail.MovieDetailActivityFragment;
import app.com.example.android.popularmovies.network.Movie;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MainActivityFragment.Callback{

    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    private static final String MAINFRAGMENT_TAG = "MFTAGE";
    private static final String EXTRA_SORT_BY = "EXTRA_SORT_BY";

    public final static String MOST_POPULAR = "popular";
    public final static String TOP_RATED = "top_rated";
    public final static String FAVORITES = "Favorite";

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.empty_state_container)
    View empty_movie;
    @BindView(R.id.empty_favorite_state_container)
    View empty_favorite_movie;
    @Nullable @BindView(R.id.detail_container)
    FrameLayout detail;

    public boolean mTwoPane;
    private String morder;
    private boolean isFavorite;
    private boolean isConnection;
    MainActivityFragment mMovieFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if (detail != null) {
            mTwoPane = true;
            Utility.putTwoPan(this);
        } else {
            mTwoPane = false;
        }

        if (savedInstanceState != null) {
            morder = savedInstanceState.getString(EXTRA_SORT_BY);
            mMovieFragment = (MainActivityFragment)getSupportFragmentManager()
                    .getFragment(savedInstanceState, MAINFRAGMENT_TAG);
            Log.d(LOG_TAG, "GET MOVIEFRAGMENT FROM SAVEDINSTANCESTATE");
        }else{
            morder = getResources().getString(R.string.sort_by_popular);
            Utility.upDatePreferedOrder(this, morder);
            mMovieFragment = new MainActivityFragment();
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, mMovieFragment, MAINFRAGMENT_TAG)
                .commit();
        Log.d(LOG_TAG, "REPLACE MOVIEFRAGMENT");

        assert morder != null;
        switch (morder){
            case MOST_POPULAR:
                toolbar.setTitle(R.string.show_setting_sort_popular);
                break;
            case TOP_RATED:
                toolbar.setTitle(R.string.show_setting_sort_top_rated);
                break;
            case FAVORITES:
                toolbar.setTitle(R.string.sort_by_favorite);
                break;
        }

        setSupportActionBar(toolbar);
        isFavorite = morder.equals(FAVORITES);

        Stetho.initializeWithDefaults(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        switch (morder) {
            case MOST_POPULAR:
                menu.findItem(R.id.menu_popluar).setChecked(true);
                break;
            case TOP_RATED:
                menu.findItem(R.id.menu_top_rated).setChecked(true);
                break;
            case FAVORITES:
                menu.findItem(R.id.menu_favorite).setChecked(true);
                break;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(EXTRA_SORT_BY, morder);
        getSupportFragmentManager().putFragment(outState, MAINFRAGMENT_TAG, mMovieFragment);
        super.onSaveInstanceState(outState);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        String order;
        MainActivityFragment movieFragment;
        switch (id){
            case R.id.menu_popluar:
                order = getResources().getString(R.string.sort_by_popular);
                Utility.upDatePreferedOrder(this, order);
                movieFragment = (MainActivityFragment)getSupportFragmentManager().findFragmentById(R.id.container);
                if ( null != movieFragment && !order.equals(morder)) {
                    movieFragment.updateMovieList(order);
                }
                morder = order;
                toolbar.setTitle(R.string.show_setting_sort_popular);
                Log.d(LOG_TAG, " MENU POPULAR");
                item.setChecked(true);
                isFavorite = false;
                break;
            case R.id.menu_top_rated:
                order = getResources().getString(R.string.sort_by_top_rated);
                Utility.upDatePreferedOrder(this, order);
                movieFragment = (MainActivityFragment)getSupportFragmentManager().findFragmentById(R.id.container);
                if ( null != movieFragment && !order.equals(morder)) {
                    movieFragment.updateMovieList(order);
                }
                morder = order;
                Log.d(LOG_TAG, " MENU TOP RATED");
                toolbar.setTitle(R.string.show_setting_sort_top_rated);
                item.setChecked(true);
                isFavorite = false;
                break;
            case R.id.menu_favorite:
                order = getResources().getString(R.string.sort_by_favorite);
                Utility.upDatePreferedOrder(this, order);
                movieFragment = (MainActivityFragment)getSupportFragmentManager().findFragmentById(R.id.container);
                if ( null != movieFragment && !order.equals(morder)) {
                    movieFragment.updateMovieCursor();
                }
                morder = order;
                Log.d(LOG_TAG, " MENU FAVORITE");
                toolbar.setTitle(R.string.sort_by_favorite);
                item.setChecked(true);
                isFavorite = true;
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onItemSelected(Movie movie) {
        if (mTwoPane) {
           // updateEmptyView(isEmptyMovie,isEmptyFavorite);

            Bundle args = new Bundle();
            args.putParcelable(MovieDetailActivityFragment.MOVIE_Detail, movie);

            MovieDetailActivityFragment fragment = new MovieDetailActivityFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_container, fragment, DETAILFRAGMENT_TAG)
                    .commit();

        } else {
            //updateEmptyView(isEmptyMovie,isEmptyFavorite);

            Intent intent = new Intent(this, MovieDetailActivity.class)
                    .putExtra(MovieDetailActivity.MOVIE_SELECTED, movie);
            startActivity(intent);
        }
    }
}
