package land.basso.android.popularmovies;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by jbasso on 7/8/2015.
 */
public class FetchMoviesTask extends AsyncTask<Void, Void, ArrayList<land.basso.android.popularmovies.Movie>>
{

    private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();
    private final Context mContext;
    private ArrayList<land.basso.android.popularmovies.Movie> mMovies;

    public FetchMoviesTask(Context context)
    {
        mContext = context;
    }

    private boolean DEBUG = true;

    @Override
    protected void onPostExecute(ArrayList<Movie> movies)
    {
        super.onPostExecute(movies);

        ((MainActivity)mContext).mMovies = this.mMovies;
        GridView posterGrid = (GridView)((MainActivity) mContext).findViewById(R.id.main_fragment_poster_grid);
        posterGrid.invalidateViews();
    }

    @Override
    protected ArrayList<land.basso.android.popularmovies.Movie> doInBackground(Void... params)
    {
        // If there's no search, there's nothing to look up.  Verify size of params.
//        if (params.length == 0)
//        {
//            return null;
//        }
//        String artistQuery = params[0];

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String jsonStr = null;

        try {
            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are avaiable at OWM's forecast API page, at
            // http://openweathermap.org/API#forecast
            URL url = new URL("http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=cc363d0ba96599c03d034675c48f570a");

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            jsonStr = buffer.toString();
            getMovieDataFromJson(jsonStr);
        } catch (Exception e) {
            Log.e("PlaceholderFragment", "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return null;
        } finally{
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("PlaceholderFragment", "Error closing stream", e);
                }
            }
        }

        return mMovies;
    }

    /**
     * Take the String representing the complete forecast in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     *
     * Fortunately parsing is easy:  constructor takes the JSON string and converts it
     * into an Object hierarchy for us.
     */
    private void getMovieDataFromJson(String jsonStr)
            throws JSONException
    {

        // Now we have a String representing the complete forecast in JSON Format.
        // Fortunately parsing is easy:  constructor takes the JSON string and converts it
        // into an Object hierarchy for us.

        // These are the names of the JSON objects that need to be extracted.

        // Location information
        final String OWM_ID = "id";
        final String OWM_RESULTS = "results";
        final String OWM_OVERVIEW = "overview";
        final String OWM_RELASE_DATE = "release_date";
        final String OWM_POSTER_PATH = "poster_path";
        final String OWM_TITLE = "title";
        final String OWM_VOTE_AVERAGE = "vote_average";

        try {
            JSONObject json = new JSONObject(jsonStr);
            JSONArray jsonArray = json.getJSONArray(OWM_RESULTS);
            land.basso.android.popularmovies.Movie movie;
            mMovies = new ArrayList<land.basso.android.popularmovies.Movie>();

            for(int i = 0; i < jsonArray.length(); i++)
            {
                movie = new land.basso.android.popularmovies.Movie();
                JSONObject rec = jsonArray.getJSONObject(i);

                movie.ID            =   rec.getString(OWM_ID);
                movie.overview      =   rec.getString(OWM_OVERVIEW);
                movie.releaseDate   =   rec.getString(OWM_RELASE_DATE);
                movie.posterURL     =   mContext.getString(R.string.api_image_url) +
                                        mContext.getString(R.string.api_imagesize_huge) +
                                        rec.getString(OWM_POSTER_PATH);
                movie.title         =   rec.getString(OWM_TITLE);
                movie.rating        =   rec.getString(OWM_VOTE_AVERAGE);

                mMovies.add(movie);
            }

            Log.d(LOG_TAG, "FetchMobiesTask Complete. " + "inserted" + " Inserted");

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

}
