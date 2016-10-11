package app.com.example.android.popularmovies.network;

import android.os.Parcel;
import android.os.Parcelable;

public class Cast implements Parcelable {

    private String director;
    private String credit;

    public static final String CAST = "cast";
    public static final String CREW = "crew";
    public static final String CAST_CREDIT = "name";
    public static final String CREW_JOB= "job";
    public static final String CREW_DIRECTOR = "Director";

    public Cast(String director, String credit){
        this.director = director;
        this.credit = credit;
    }

    protected Cast(Parcel in) {
        director = in.readString();
        credit = in.readString();
    }

    public String getDirector(){
        return director;
    }

    public String getCredit(){
        return credit;
    }

    public static final Creator<Cast> CREATOR = new Creator<Cast>() {
        @Override
        public Cast createFromParcel(Parcel in) {
            return new Cast(in);
        }

        @Override
        public Cast[] newArray(int size) {
            return new Cast[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(director);
        parcel.writeString(credit);
    }
}
