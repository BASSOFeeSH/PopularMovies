package land.basso.android.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

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

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }
}
