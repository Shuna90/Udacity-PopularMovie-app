package app.com.example.android.popularmovies.detail;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import app.com.example.android.popularmovies.R;
import app.com.example.android.popularmovies.network.Review;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by shuna on 9/28/16.
 */
public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewAdapterViewHolder> {

    private static final String LOG_TAG = ReviewAdapter.class.getSimpleName();
    private ArrayList<Review> mReviewList;

    public ReviewAdapter(ArrayList<Review> reviewList){
        mReviewList = reviewList;
    }

    public class ReviewAdapterViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.review_author)
        TextView text_author;
        @BindView(R.id.review_content)
        TextView text_content;
        public View mView;
        public Review mReview;

        public ReviewAdapterViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            text_author = (TextView)view.findViewById(R.id.review_author);
            text_content = (TextView)view.findViewById(R.id.review_content);
            mView = view;
        }
    }

    @Override
    public ReviewAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_list, parent, false);
        return new ReviewAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewAdapterViewHolder holder, int position) {
        Review mR = mReviewList.get(position);
        holder.mReview = mR;
        holder.text_author.setText(mR.getAuthor());
        holder.text_content.setText(mR.getContent());
    }

    @Override
    public int getItemCount() {
        return mReviewList.size();
    }

    public void updateReview(ArrayList<Review> reviews) {
        mReviewList.clear();
        mReviewList.addAll(reviews);
        notifyDataSetChanged();
    }

    public ArrayList<Review> getReviewArrayList() {
        return mReviewList;
    }
}
