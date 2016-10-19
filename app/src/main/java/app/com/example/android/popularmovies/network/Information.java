package app.com.example.android.popularmovies.network;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Information implements Parcelable {
    //@SerializedName("genres")
    private String genreName;

    @SerializedName("genres")
    private List<Genre> genres = new ArrayList<>();
    @SerializedName("homepage")
    private String homepage;
    @SerializedName("runtime")
    //private String runtime;
    private Integer runtime;
    @SerializedName("adult")
    //private String adult;
    private Boolean adult;

    public static final String GENRES = "genres";
    public static final String GENRES_NAME = "name";
    public static final String HOMEPAGE = "homepage";
    public static final String RUNTIME = "runtime";
    public static final String ADULT = "adult";

    /*
    public Information(String adult, String runtime, String homepage, String genres){
        this.adult = adult;
        this.runtime = Utility.getRumTime(runtime);
        this.homepage = homepage;
        this.genres = genres;
    }
    */

    protected Information(Parcel in) {
        adult = Boolean.getBoolean(in.readString());
        runtime = in.readInt();
        homepage = in.readString();
        genreName = in.readString();
    }

    public String getGenres(){
        setGenres();
        return genreName;
    }

    public void setGenres(){
        StringBuilder sb = new StringBuilder();
        for (Genre g : genres){
            sb.append(g.getName()).append(", ");
        }
         if (sb.length() > 2){
             sb.delete(sb.length() - 2, sb.length());
         }
        genreName = sb.toString();
    }

    public List<Genre> getGenresList() {
        return genres;
    }

    public void setGenresList(List<Genre> genres) {
        this.genres = genres;
    }

    public String getHomepage(){
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public int getRuntime(){
        return runtime;
    }

    public void setRuntime(Integer runtime) {
        this.runtime = runtime;
    }

    public boolean getAdult(){
        return adult;
    }

    public void setAdult(Boolean adult) {
        this.adult = adult;
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
        parcel.writeString(Boolean.toString(adult));
        parcel.writeInt(runtime);
        parcel.writeString(homepage);
        parcel.writeString(genreName);
    }
}
