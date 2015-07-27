package land.basso.android.popularmovies;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;


public class MainActivity   extends     ActionBarActivity
                            implements  MainActivityFragmentDetail.OnFragmentInteractionListener
{
    public ArrayList<Movie> mMovies;
    public String mSort;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        MainActivityFragment fragment = new MainActivityFragment();
        ft.add(R.id.fragment, fragment);
//        ft.addToBackStack("Main");
        ft.commit();

//        mSort = Utility.getCurrentSort(this);
//
//        GridView posterGrid = (GridView) findViewById(R.id.main_fragment_grid);
//        posterGrid.setAdapter(new ImageAdapter(this));
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        mSort = Utility.getCurrentSort(this);

//        GridView posterGrid = (GridView) findViewById(R.id.main_fragment_grid);
//        posterGrid.setAdapter(new ImageAdapter(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showDetailsForMovie(int position)
    {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        MainActivityFragmentDetail fragment = new MainActivityFragmentDetail(position);
        ft.replace(R.id.fragment, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.addToBackStack("Detail");
        ft.commit();
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri)
    {

    }
}
