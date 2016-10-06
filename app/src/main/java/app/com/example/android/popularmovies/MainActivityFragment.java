package app.com.example.android.popularmovies;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import app.com.example.android.popularmovies.data.MovieContract;
import app.com.example.android.popularmovies.network.FetchMovieTask;
import app.com.example.android.popularmovies.network.Movie;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements FetchMovieTask.OnTaskCompleted, LoaderManager.LoaderCallbacks<Cursor>{

    public static final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    private MovieListAdapter mMovieListAdapter;
    private MovieListAdapter mMovieCursorAdapter;
    private RecyclerView mRecyclerView;
    private int mPosition = RecyclerView.NO_POSITION;
    private static final String SELECTED_KEY = "selected_position";
    private static final String SAVED_MOVIE_LIST = "MovieList";

    private String morder;
    public ArrayList<Movie> MovieArrayList;
    FetchMovieTask MovieTask;
    private static final int MOVIE_LOADER = 0;
    private Cursor mCursor;

    public boolean isFavorite;
    private TextView empty_text;

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //setHasOptionsMenu(true);
        morder = Utility.getPreferredOrder(getContext());
        isFavorite = morder.equals(MainActivity.FAVORITES);

        // && savedInstanceState.containsKey(SAVED_MOVIE_LIST)
        if(savedInstanceState != null) {
            MovieArrayList = savedInstanceState.getParcelableArrayList(SAVED_MOVIE_LIST);
            Log.d(LOG_TAG, "savedInstanceState oncreate finish ");
        }else{
            MovieArrayList = new ArrayList<Movie>();
            updateMovieList(getString(R.string.sort_by_popular));
            Log.d(LOG_TAG, "START NEW FRAGMENT POPULAR");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(SAVED_MOVIE_LIST,  MovieArrayList);

        if (mPosition != RecyclerView.NO_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        if (mRecyclerView != null){
            updateEmpty();
        }
        super.onResume();
    }

    public void updateMovieList(String orderBy) {
        MovieTask = new FetchMovieTask(getActivity(), this);
        MovieTask.execute(orderBy);
        Log.d(LOG_TAG, orderBy);
    }

    @Override
    public void onTaskCompleted(ArrayList<Movie> list) {
        MovieArrayList = new ArrayList<>();

        if (list == null || list.isEmpty()){
            Log.d(LOG_TAG, "isEmptyMovie true");
        }else{
            MovieArrayList.addAll(list);
            mRecyclerView.setAdapter(mMovieListAdapter);
            mMovieListAdapter.updateArrayList(MovieArrayList);
        }
        updateEmpty();
        if (mPosition != RecyclerView.NO_POSITION) {
            // If we don't need to restart the loader, and there's a desired position to restore
            // to, do so now.
            mRecyclerView.smoothScrollToPosition(mPosition);
        }
        Log.d(LOG_TAG, "load Finish");
    }

    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(Movie movie);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        empty_text = (TextView)rootView.findViewById(R.id.empty_text);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(),getResources().getInteger(R.integer.col_number));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(layoutManager);

        mMovieListAdapter = new MovieListAdapter(getActivity(),rootView, new MovieListAdapter.MovieAdapterOnClickHandler() {

            @Override
            public void onClick(int adapterPosition) {
                mPosition = adapterPosition;
                Movie mMovie = MovieArrayList.get(adapterPosition);
                ((Callback) getActivity()).onItemSelected(mMovie);
            }
        }, MovieArrayList);

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            mPosition = savedInstanceState.getInt(SELECTED_KEY);

        }

        mMovieCursorAdapter = new MovieListAdapter(getActivity(),rootView,new MovieListAdapter.MovieAdapterOnClickHandler() {

            @Override
            public void onClick(int adapterPosition) {
                mPosition = adapterPosition;
                mCursor.moveToPosition(mPosition);
                Movie mMovie = new Movie();
                mMovie.setId(mCursor.getString(MovieContract.MovieEntry.COL_MOVIE_ID));
                mMovie.setTitle(mCursor.getString(MovieContract.MovieEntry.COL_MOVIE_TITLE));
                mMovie.setOverview(mCursor.getString(MovieContract.MovieEntry.COL_MOVIE_OVERVIEW));
                mMovie.setRating(mCursor.getString(MovieContract.MovieEntry.COL_MOVIE_VOTE_AVERAGE));
                mMovie.setReleaseDate(mCursor.getString(MovieContract.MovieEntry.COL_MOVIE_RELEASE_DATE));
                mMovie.setPoster_path(mCursor.getString(MovieContract.MovieEntry.COL_MOVIE_POSTER_PATH));
                mMovie.setBackdrop(mCursor.getString(MovieContract.MovieEntry.COL_MOVIE_BACKDROP_PATH));
                ((Callback) getActivity()).onItemSelected(mMovie);
            }
        }, MovieArrayList);


        if (!isFavorite){
            mRecyclerView.setAdapter(mMovieListAdapter);
            Log.d(LOG_TAG, "ONCREATEVIEW SET LIST ADAPTER");
            updateEmpty();
        }else{
            updateMovieCursor();
        }

        return rootView;
    }

    public void updateMovieCursor() {
        // We hold for transition here just in-case the activity
        // needs to be re-created. In a standard return transition,
        // this doesn't actually make a difference.
        mRecyclerView.setAdapter(mMovieCursorAdapter);
        Log.d(LOG_TAG, "ONCREATEVIEW SET CURSOR ADAPTER");
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mRecyclerView) {
            mRecyclerView.clearOnScrollListeners();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getContext(),
                MovieContract.MovieEntry.CONTENT_URI,
                MovieContract.MovieEntry.MOVIE_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursor = data;
        Log.d(LOG_TAG, "CURSOR onLoadFinished");
        //mMovieListAdapter.updateArrayList(null);
        mMovieCursorAdapter.swapCursor(data);
        updateEmpty();
        if (mPosition != RecyclerView.NO_POSITION) {
            // If we don't need to restart the loader, and there's a desired position to restore
            // to, do so now.
            mRecyclerView.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMovieCursorAdapter.swapCursor(null);
    }

    public void updateEmpty(){
        morder = Utility.getPreferredOrder(getContext());
        isFavorite = morder.equals(MainActivity.FAVORITES);
        int massage = R.string.text_check_internet;
        Log.d(LOG_TAG, "updateEmpty");
        if (!Utility.isNetworkAvailable(getContext()) && !isFavorite ){
            mRecyclerView.setVisibility(View.GONE);
            empty_text.setVisibility(View.VISIBLE);
            empty_text.setText(massage);
            Log.d(LOG_TAG, "updateEmpty NO CONNECTION  mRecyclerView GONE");
        }else if (isFavorite && mMovieCursorAdapter.getItemCount() == 0){
            //mMovieCursorAdapter.swapCursor(null);
            mRecyclerView.setVisibility(View.GONE);
            empty_text.setVisibility(View.VISIBLE);
            massage = R.string.text_add_more_favorite;
            empty_text.setText(massage);
            Log.d(LOG_TAG, "updateEmpty NO FAVORITE mRecyclerView GONE");
        }else{
            mRecyclerView.setVisibility(View.VISIBLE);
            empty_text.setVisibility(View.GONE);
            Log.d(LOG_TAG, "updateEmpty FINE");
        }

    }
}
