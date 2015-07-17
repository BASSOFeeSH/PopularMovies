package land.basso.android.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by jim on 7/16/2015.
 */
public class Utility
{
    public static boolean isSortByPopularity(Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_sort_key),
                               context.getString(R.string.pref_sort_popular_value))
                    .equals(context.getString(R.string.pref_sort_popular_value));
    }

    public static String getCurrentSort(Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_sort_key),
                               context.getString(R.string.pref_sort_popular_value));
    }
}
