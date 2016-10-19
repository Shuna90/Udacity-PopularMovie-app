package app.com.example.android.popularmovies.network;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;


public class Review implements Parcelable {

    @SerializedName("id")
    private String reviewId;
    @SerializedName("author")
    private String reviewAuthor;
    @SerializedName("content")
    private String reviewContent;
    @SerializedName("url")
    private String reviewUrl;

    public static final String REVIEW_ID = "id";
    public static final String REVIEW_AUTHOR = "author";
    public static final String REVIEW_CONTENT = "content";
    public static final String REVIEW_URL = "url";

    public Review(String id, String author, String content, String url){
        reviewId = id;
        reviewAuthor = author;
        reviewContent = content;
        reviewUrl = url;
    }
    protected Review(Parcel in) {
        reviewId = in.readString();
        reviewAuthor = in.readString();
        reviewContent = in.readString();
        reviewUrl = in.readString();
    }

    public String getAuthor(){
        return reviewAuthor;
    }

    public String getContent(){
        return reviewContent;
    }

    public String getUrl(){
        return reviewUrl;
    }

    public static final Creator<Review> CREATOR = new Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(reviewId);
        parcel.writeString(reviewAuthor);
        parcel.writeString(reviewContent);
        parcel.writeString(reviewUrl);
    }
}
