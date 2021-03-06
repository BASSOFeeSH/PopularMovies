package land.basso.android.popularmovies;

import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment
{
    public static final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    private GridView mGridView;
    private int mPosition = GridView.INVALID_POSITION;

    private static final String SELECTED_KEY = "selected_position";

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(Uri dateUri);
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
    // When tablets rotate, the currently selected list item needs to be saved.
        // When no item is selected, mPosition will be set to Listview.INVALID_POSITION,
        // so check for that before storing.
        if (mPosition != ListView.INVALID_POSITION)
        {   outState.putInt(SELECTED_KEY, mPosition);   }
        super.onSaveInstanceState(outState);
    }

    public MainActivityFragment()
    {
    }

    @Override
    public void onResume()
    {
        String sortSetting = Utility.getCurrentSort(getActivity());
        String currentSort = ((MainActivity)getActivity()).mSort;

        if(mGridView.getAdapter() == null)
        {   mGridView.setAdapter(new ImageAdapter(getActivity()));  }

        if(!sortSetting.equals(currentSort))
        {
            mPosition = GridView.INVALID_POSITION;
            updateGrid();
        }
        else
        {   if(mPosition != GridView.INVALID_POSITION) {   mGridView.smoothScrollToPosition(mPosition);   } }
        super.onResume();
    }

    public void updateGrid()
    {
        try
        {
            ProgressBar progressBar = (ProgressBar)getActivity().findViewById(R.id.main_fragment_progress);
            progressBar.setIndeterminate(true);
            progressBar.setVisibility(View.VISIBLE);

            mGridView.invalidateViews();
            String currentSort = Utility.getCurrentSort(getActivity());
            String sortSetting = getActivity().getString(R.string.pref_sort_favorite_value);
            if(currentSort.equals(sortSetting))
            {   FetchFavoritesTask task = new FetchFavoritesTask(getActivity(), mPosition); task.execute(); }
            else
            {   FetchMoviesTask task = new FetchMoviesTask(getActivity(), mPosition);   task.execute();}
        }
        catch(Exception exc)
        {
            Toast.makeText(getActivity().getApplicationContext(), exc.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.main_fragment_grid, container, false);

        // Get a reference to the ListView, and attach this adapter to it.
        mGridView = (GridView) rootView.findViewById(R.id.main_fragment_grid);

//        mGridView.setAdapter(mForecastAdapter);
        // We'll call our MainActivity
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
            {
                mPosition = position;
                ((MainActivity)getActivity()).showDetailsForMovie(position);
            }
        });

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            // The listview probably hasn't even been populated yet.  Actually perform the
            // swapout in onLoadFinished.
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        return rootView;
    }
}
