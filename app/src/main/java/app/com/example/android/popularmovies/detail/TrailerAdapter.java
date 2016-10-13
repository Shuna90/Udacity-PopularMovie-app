package app.com.example.android.popularmovies.detail;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import app.com.example.android.popularmovies.R;
import app.com.example.android.popularmovies.network.Trailer;
import butterknife.BindView;
import butterknife.ButterKnife;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerAdapterViewHolder>  {
    public static final String LOG_TAG = TrailerAdapter.class.getSimpleName();
    private ArrayList<Trailer> trailerArrayList;
    private Context context;
    private TrailerAdapterOnClickHandler mClickHandler;

    public TrailerAdapter(Context context, TrailerAdapterOnClickHandler handler, ArrayList<Trailer> trailers){
        trailerArrayList = trailers;
        this.context = context;
        mClickHandler = handler;
    }

    public interface TrailerAdapterOnClickHandler {
        void onClick(Trailer trailer, int adapterPosition);

    }

    public class TrailerAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @BindView(R.id.trailer_name)
        TextView text_trailer_name;
        @BindView(R.id.trailer_thumbnail)
        ImageView trailer_thumbnail;
        public View mView;
        public Trailer mTrailer;

        public TrailerAdapterViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            text_trailer_name = (TextView)view.findViewById(R.id.trailer_name);
            trailer_thumbnail = (ImageView)view.findViewById(R.id.trailer_thumbnail);
            mView = view;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            mClickHandler.onClick(mTrailer, adapterPosition);
        }
    }

    @Override
    public TrailerAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trailer_list, parent, false);
        return new TrailerAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerAdapterViewHolder holder, int position) {
        holder.mTrailer = trailerArrayList.get(position);
        holder.text_trailer_name.setText(holder.mTrailer.getName());
        Picasso.with(context)
                .load(holder.mTrailer.getthumbnailUrl())
                .config(Bitmap.Config.RGB_565)
                .into(holder.trailer_thumbnail);
    }

    @Override
    public int getItemCount() {
        return trailerArrayList.size();
    }

    public ArrayList<Trailer>  getTrailerArrayList() {
        return trailerArrayList;
    }

    public void updateTrailer(ArrayList<Trailer> trailers){
        trailerArrayList.clear();
        trailerArrayList.addAll(trailers);
        notifyDataSetChanged();
    }

}
