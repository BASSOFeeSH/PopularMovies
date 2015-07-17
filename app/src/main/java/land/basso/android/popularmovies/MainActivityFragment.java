package land.basso.android.popularmovies;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment
{

    public MainActivityFragment()
    {
    }

    @Override
    public void onResume()
    {
        String sortSetting = Utility.getCurrentSort(getActivity());
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
        View inflatedView = inflater.inflate(R.layout.fragment_main, container, false);

        updateGrid();

        return inflatedView;
    }
}
