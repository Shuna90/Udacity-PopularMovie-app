package app.com.example.android.popularmovies.network;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by shuna on 10/6/16.
 */
public class Cast implements Parcelable {

    protected Cast(Parcel in) {
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
    }
}
