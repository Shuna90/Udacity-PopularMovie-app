package app.com.example.android.popularmovies.network;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Trailer implements Parcelable {
    @SerializedName("id")
    private String trailerId;
    @SerializedName("key")
    private String trailerKey;
    @SerializedName("name")
    private String trailerName;
    @SerializedName("site")
    private String trailerSite;
    @SerializedName("size")
    private String trailerSize;

    public static final String TRAILER_ID = "id";
    public static final String TRAILER_KEY = "key";
    public static final String TRAILER_NAME = "name";
    public static final String TRAILER_SITE = "site";
    public static final String TRAILER_SIZE = "size";

    public Trailer(String id, String key, String name, String site, String size){
        trailerId = id;
        trailerKey = key;
        trailerName = name;
        trailerSite = site;
        trailerSize = size;
    }

    protected Trailer(Parcel in) {
        trailerId = in.readString();
        trailerKey = in.readString();
        trailerName = in.readString();
        trailerSite = in.readString();
        trailerSize = in.readString();
    }

    public String getName(){
        return trailerName;
    }

    public String getKey(){
        return trailerKey;
    }

    public String getUrl(){
        return "http://www.youtube.com/watch?v=" + trailerKey;
    }

    public String getthumbnailUrl(){
        return "http://img.youtube.com/vi/" + trailerKey + "/0.jpg";
    }

    public static final Creator<Trailer> CREATOR = new Creator<Trailer>() {
        @Override
        public Trailer createFromParcel(Parcel in) {
            return new Trailer(in);
        }

        @Override
        public Trailer[] newArray(int size) {
            return new Trailer[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(trailerId);
        parcel.writeString(trailerKey);
        parcel.writeString(trailerName);
        parcel.writeString(trailerSite);
        parcel.writeString(trailerSize);
    }
}
