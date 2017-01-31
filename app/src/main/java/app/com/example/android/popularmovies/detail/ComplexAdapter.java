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
import app.com.example.android.popularmovies.network.Review;
import app.com.example.android.popularmovies.network.Trailer;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ComplexAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static final String LOG_TAG = ComplexAdapter.class.getSimpleName();
    private Context context;
    private ArrayList<Object> items = new ArrayList<>();
    private ComplexAdapterOnClickHandler mClickHandler;

    private static final int VIEW_TYPE_TRAILER = 0;
    private static final int VIEW_TYPE_REVIEW = 1;

    public ComplexAdapter(Context context, ComplexAdapterOnClickHandler handler, ArrayList<Trailer> trailers,
                          ArrayList<Review> reviews){
        items.clear();
        if (trailers != null){
            items.addAll(trailers);
        }else{
            items.addAll(reviews);
        }
        this.context = context;
        mClickHandler = handler;
    }

    public interface ComplexAdapterOnClickHandler {
        void onClick(Object o, int adapterPosition);
    }

    public class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @BindView(R.id.trailer_name)
        TextView text_trailer_name;
        @BindView(R.id.trailer_thumbnail)
        ImageView trailer_thumbnail;
        public View mView;
        public Trailer mTrailer;

        public TrailerViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            mView = view;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            mClickHandler.onClick(mTrailer, adapterPosition);
        }
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @BindView(R.id.review_author)
        TextView text_author;
        @BindView(R.id.review_content)
        TextView text_content;
        public View mView;
        public Review mReview;

        public ReviewViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            mView = view;
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            mClickHandler.onClick(mReview, adapterPosition);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view;
        switch (viewType){
            case VIEW_TYPE_TRAILER:
                view = inflater.inflate(R.layout.trailer_list, parent, false);
                viewHolder = new TrailerViewHolder(view);
                break;

            case VIEW_TYPE_REVIEW:
                view = inflater.inflate(R.layout.review_list, parent, false);
                viewHolder = new ReviewViewHolder(view);
                break;
            default:
                View v = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
                viewHolder = new RecyclerView.ViewHolder(v) {
                    @Override
                    public String toString() {
                        return super.toString();
                    }
                };
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)){
            case VIEW_TYPE_TRAILER:
                TrailerViewHolder vh1 = (TrailerViewHolder) holder;
                configureTrailerViewHolder(vh1, position);
                break;

            case VIEW_TYPE_REVIEW:
                ReviewViewHolder vh2 = (ReviewViewHolder) holder;
                configureReviewViewHolder(vh2, position);
                break;
        }
    }

    private void configureTrailerViewHolder(TrailerViewHolder holder, int position){
        holder.mTrailer = (Trailer) items.get(position);
        holder.text_trailer_name.setText(holder.mTrailer.getName());
        Picasso.with(context)
                .load(holder.mTrailer.getthumbnailUrl())
                .config(Bitmap.Config.RGB_565)
                .into(holder.trailer_thumbnail);
    }

    private void configureReviewViewHolder(ReviewViewHolder holder, int position){
        Review mR = (Review) items.get(position);
        holder.mReview = mR;
        holder.text_author.setText(mR.getAuthor());
        holder.text_content.setText(mR.getContent());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof Review){
            return VIEW_TYPE_REVIEW;
        }else if (items.get(position) instanceof Trailer){
            return VIEW_TYPE_TRAILER;
        }else{
            return -1;
        }
    }

    public Object getObject(int position) {
        if (items.get(position) != null) {
            return items.get(position);
        }else{
            return null;
        }
    }

    public void updateTrailer(ArrayList<Trailer> trailers){
        items.clear();
        items.addAll(trailers);
        notifyDataSetChanged();
    }

    public void updateReview(ArrayList<Review> reviews) {
        items.clear();
        items.addAll(reviews);
        notifyDataSetChanged();
    }

}
