package app.com.example.android.popularmovies.network;

import android.os.Parcel;
import android.os.Parcelable;

import app.com.example.android.popularmovies.Utility;

public class Information implements Parcelable {
    private String genres;
    private String homepage;
    private String runtime;
    private String adult;

    public static final String GENRES = "genres";
    public static final String GENRES_NAME = "name";
    public static final String HOMEPAGE = "homepage";
    public static final String RUNTIME = "runtime";
    public static final String ADULT = "adult";

    public Information(String adult, String runtime, String homepage, String genres){
        this.adult = adult;
        this.runtime = Utility.getRumTime(runtime);
        this.homepage = homepage;
        this.genres = genres;
    }

    protected Information(Parcel in) {
        adult = in.readString();
        runtime = in.readString();
        homepage = in.readString();
        genres = in.readString();
    }

    public String getGenres(){
        return genres;
    }

    public String getHomepage(){
        return homepage;
    }

    public String getRuntime(){
        return runtime;
    }

    public String getAdult(){
        return adult;
    }

    public static final Creator<Information> CREATOR = new Creator<Information>() {
        @Override
        public Information createFromParcel(Parcel in) {
            return new Information(in);
        }

        @Override
        public Information[] newArray(int size) {
            return new Information[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(adult);
        parcel.writeString(runtime);
        parcel.writeString(homepage);
        parcel.writeString(genres);
    }
}
