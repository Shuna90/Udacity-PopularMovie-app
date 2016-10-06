package app.com.example.android.popularmovies.network;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by shuna on 10/6/16.
 */
public class Information implements Parcelable {

    protected Information(Parcel in) {
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
    }
}
