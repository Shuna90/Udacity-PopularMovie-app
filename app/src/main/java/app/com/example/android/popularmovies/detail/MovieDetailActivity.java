package app.com.example.android.popularmovies.detail;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.WindowManager;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import app.com.example.android.popularmovies.R;
import app.com.example.android.popularmovies.network.Movie;

public class MovieDetailActivity extends AppCompatActivity {

    public static final String MOVIE_SELECTED = "Movie_Selected";
    private CollapsingToolbarLayout collapsingToolbarLayout = null;
    private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Movie movie = getIntent().getExtras().getParcelable(MOVIE_SELECTED);

        collapsingToolbarLayout.setTitle(movie.getTitle());
        //collapsingToolbarLayout.setTitleEnabled(false);
        imageView = (ImageView)findViewById(R.id.movie_detail_poster) ;
        Picasso.with(this)
                .load(movie.getBackdropUrl())
                .config(Bitmap.Config.RGB_565)
                .into(imageView);

        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(MovieDetailActivityFragment.MOVIE_Detail, movie);

            MovieDetailActivityFragment fragment = new MovieDetailActivityFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.detail_container, fragment)
                    .commit();
        }
    }

}
