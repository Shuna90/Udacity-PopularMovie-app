package app.com.example.android.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by shuna on 8/21/16.
 */
public class Utility {
    public static final String LOG_TAG = Utility.class.getSimpleName();
    public static final String TWO_PANE = "TWO_PANE";
    public static String getPreferredOrder(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.shared_order_by),
                context.getString(R.string.sort_by_popular));
    }
    public static void upDatePreferedOrder(Context context, String order){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(context.getString(R.string.shared_order_by), order);
        editor.commit();
    }

    static public boolean isNetworkAvailable(Context c) {
        ConnectivityManager cm =
                (ConnectivityManager)c.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean net = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        Log.d(LOG_TAG, net + "");
        return net;
    }

    public static boolean isTwoPane(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(TWO_PANE, false);
    }

    public static void putTwoPan(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putBoolean(TWO_PANE, true).commit();
    }

}