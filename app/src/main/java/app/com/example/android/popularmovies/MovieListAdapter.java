package app.com.example.android.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import app.com.example.android.popularmovies.data.MovieContract;
import app.com.example.android.popularmovies.network.Movie;

/**
 * Created by shuna on 8/19/16.
 */
public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.MovieAdapterViewHolder> {
    public static final String LOG_TAG = MovieListAdapter.class.getSimpleName();
    private ArrayList<Movie> movieArrayList;
    private Context context;
    final private MovieAdapterOnClickHandler mClickHandler;
    private Cursor mCursor;
    private RecyclerView mRV;
    private TextView empty_text;
    private String order;

    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final View mView;
        private TextView text_title;
        private ImageView image_poster;
        public Movie mMovie;

        public MovieAdapterViewHolder(View view) {
            super(view);
            mView = view;
            text_title = (TextView)view.findViewById(R.id.movie_title);
            image_poster = (ImageView) view.findViewById(R.id.movie_poster);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            //Movie movie = movieArrayList.get(adapterPosition);
            mClickHandler.onClick(adapterPosition);
        }

        public void cleanUp() {
            final Context context = mView.getContext();
            Picasso.with(context).cancelRequest(image_poster);
            image_poster.setImageBitmap(null);
            image_poster.setVisibility(View.INVISIBLE);
            text_title.setVisibility(View.GONE);
        }
    }

    public static interface MovieAdapterOnClickHandler {
        void onClick(int adapterPosition);

    }

    public MovieListAdapter(Context context, View view, MovieAdapterOnClickHandler handler, ArrayList<Movie> movieArrayList) {
        this.movieArrayList = movieArrayList;
        this.context = context;
        mRV = (RecyclerView)view.findViewById(R.id.recycler_view);
        empty_text = (TextView)view.findViewById(R.id.empty_text);
        mClickHandler = handler;
    }

    @Override
    public MovieListAdapter.MovieAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if ( parent instanceof RecyclerView ) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_item, parent, false);
            //view.setFocusable(true);

            int gridColsNumber = context.getResources()
                    .getInteger(R.integer.col_number);

            view.getLayoutParams().height = (int) (parent.getWidth() / gridColsNumber *
                    Movie.POSTER_ASPECT_RATIO);

            return new MovieAdapterViewHolder(view);
            } else {
            throw new RuntimeException("Not bound to RecyclerView");
        }
    }

    @Override
    public void onBindViewHolder(MovieListAdapter.MovieAdapterViewHolder holder, int position) {
        String orderNow = Utility.getPreferredOrder(context);
        String favorite = context.getResources().getString(R.string.sort_by_favorite);
        boolean isFavorite;
        String title;
        String post_path;
        if (orderNow.equals(favorite)){
            isFavorite = true;
            mCursor.moveToPosition(position);
            title = mCursor.getString(MovieContract.MovieEntry.COL_MOVIE_TITLE);
            post_path = mCursor.getString(MovieContract.MovieEntry.COL_MOVIE_POSTER_PATH);
        }else{
            isFavorite = false;
            holder.mMovie = movieArrayList.get(position);
            title = movieArrayList.get(position).getTitle();
            post_path = movieArrayList.get(position).getPoster_path();
        }

        if (!Utility.isNetworkAvailable(context)){
            Log.d(LOG_TAG, "onBindViewHolder");
            //holder.cleanUp();
            if (!isFavorite){
                empty_text.setText(context.getString( R.string.text_check_internet));
                Log.d(LOG_TAG, "NOT FAVORITE set empty text");
            }else{
                holder.image_poster.setVisibility(View.GONE);
                empty_text.setText(context.getString( R.string.text_add_more_favorite));
                holder.text_title.setVisibility(View.VISIBLE);
                holder.text_title.setText(title);
                Log.d(LOG_TAG, "IS FAVORITE set empty text");
            }
        }else {

            Picasso.with(context)
                    .load(post_path)
                    .config(Bitmap.Config.RGB_565)
                    .into(holder.image_poster);
        }
        Log.d(LOG_TAG, "LOAD PICTURE");
    }

    @Override
    public int getItemCount() {
        String orderNow = Utility.getPreferredOrder(context);
        String favorite = context.getResources().getString(R.string.sort_by_favorite);
        if (orderNow.equals(favorite)){
            if ( null == mCursor ) return 0;
            return mCursor.getCount();
        }else{
            if (movieArrayList == null || movieArrayList.isEmpty()){
                return 0;
            }
            return movieArrayList.size();
        }
    }

    public void updateArrayList(ArrayList<Movie> list) {
        movieArrayList = new ArrayList<>();
        if (list != null){
            movieArrayList.addAll(list);
        }
        notifyDataSetChanged();
        empty_text.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
        mRV.setVisibility(getItemCount() == 0 ? View.GONE : View.VISIBLE);
        empty_text.setText(context.getString( R.string.text_check_internet));
        Log.d(LOG_TAG, "SWAP LIST COUNT = " + getItemCount());
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        this.movieArrayList.clear();
        notifyDataSetChanged();
        empty_text.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
        mRV.setVisibility(getItemCount() == 0 ? View.GONE : View.VISIBLE);
        empty_text.setText(context.getString( R.string.text_check_internet));
        Log.d(LOG_TAG, "SWAP CURSOR COUNT = "+ getItemCount());
    }

}
