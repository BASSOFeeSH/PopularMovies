package land.basso.android.popularmovies;

import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment
{
    public static final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    private GridView mGridView;
    private int mPosition = GridView.INVALID_POSITION;

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

    public MainActivityFragment()
    {
    }

    @Override
    public void onResume()
    {
        String sortSetting = Utility.getCurrentSort(getActivity());

        GridView posterGrid = (GridView) getActivity().findViewById(R.id.main_fragment_grid);
        posterGrid.setAdapter(new ImageAdapter(getActivity()));

        if(sortSetting != ((MainActivity)getActivity()).mSort)
            updateGrid();
        super.onResume();
    }

    public void updateGrid()
    {
        try
        {
            FetchMoviesTask task = new FetchMoviesTask(getActivity());
            task.execute();
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

        updateGrid();

        // Get a reference to the ListView, and attach this adapter to it.
        mGridView = (GridView) rootView.findViewById(R.id.main_fragment_grid);
//        mGridView.setAdapter(mForecastAdapter);
        // We'll call our MainActivity
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
            {
                 //TODO
                Toast.makeText(getActivity().getApplicationContext(), ((MainActivity)getActivity()).mMovies.get(position).title, Toast.LENGTH_SHORT).show();
//                    String locationSetting = Utility.getPreferredLocation(getActivity());
//                    ((Callback) getActivity())
//                            .onItemSelected(WeatherContract.WeatherEntry.buildWeatherLocationWithDate(
//                                    locationSetting, cursor.getLong(COL_WEATHER_DATE)
//                    ));
//                }
                mPosition = position;
                ((MainActivity)getActivity()).showDetailsForMovie(position);
            }
        });

        return rootView;
    }
}
