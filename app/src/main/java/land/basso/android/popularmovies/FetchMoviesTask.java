package land.basso.android.popularmovies;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ProgressBar;

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
public class FetchMoviesTask extends AsyncTask<Void, Void, ArrayList<Movie>>
{

    private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();
    private final Context mContext;
    private final int mPosition;
    private ArrayList<Movie> mMovies;
    private boolean DEBUG = true;

    public FetchMoviesTask(Context context, int position)
    {
        mContext = context;
        mPosition = position;
    }

    @Override
    protected void onPostExecute(ArrayList<Movie> movies)
    {
        super.onPostExecute(movies);

        ((MainActivity) mContext).mMovies = this.mMovies;
        GridView posterGrid = (GridView) ((MainActivity) mContext).findViewById(R.id.main_fragment_grid);
        posterGrid.invalidateViews();
        if (mPosition != GridView.INVALID_POSITION)
        {
            posterGrid.smoothScrollToPosition(mPosition);
        }

        hideProgressSpinner();
    }

    private void hideProgressSpinner()
    {
        ProgressBar progressBar = (ProgressBar) (((MainActivity) mContext).findViewById(R.id.main_fragment_progress));
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    protected ArrayList<Movie> doInBackground(Void... params)
    {
        return getMovies(1);
    }

    private ArrayList<Movie> getMovies(int retries)
    {
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String jsonStr = null;

        try
        {
            ((MainActivity) mContext).mSort = Utility.getCurrentSort(mContext);
            URL url;
            if (Utility.isSortByPopularity(mContext))
            {//i.e. URL url = new URL("http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=cc363d0ba96599c03d034675c48f570a");
                url = new URL(mContext.getString(R.string.api_discover_url)
                                      .replace(mContext.getString(R.string.api_sort_placeholder), mContext
                                              .getString(R.string.api_sort_by_popularity))
                                      .replace(mContext.getString(R.string.api_key_placeholder), mContext
                                              .getString(R.string.api_key))
                );
            }
            else //by rating
            {
                url = new URL(mContext.getString(R.string.api_discover_url)
                                      .replace(mContext.getString(R.string.api_sort_placeholder), mContext
                                              .getString(R.string.api_sort_by_rating))
                                      .replace(mContext.getString(R.string.api_key_placeholder), mContext
                                              .getString(R.string.api_key))
                );
            }
            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null)
            {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null)
            {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0)
            {
                // Stream was empty.  No point in parsing.
                return null;
            }
            jsonStr = buffer.toString();
            getMovieDataFromJson(jsonStr);
        }

        catch(Exception e)
        {
            Log.e("getMovies attempt " + String.valueOf(retries), "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            if (retries > 3)
                return null;
            else
            {
                try
                {
                    Thread.sleep(500);
                }
                catch (InterruptedException ex)
                {
                }
                return getMovies(++ retries);
            }
        }

        finally
        {
            if (urlConnection != null)
            {
                urlConnection.disconnect();
            }
            if (reader != null)
            {
                try
                {
                    reader.close();
                }
                catch (final IOException e)
                {
                    Log.e("PlaceholderFragment", "Error closing stream", e);
                }
            }
        }

        return mMovies;
    }

    private void getMovieDataFromJson(String jsonStr)
            throws JSONException
    {
        // These are the names of the JSON objects that need to be extracted.
        final String OWM_ID = "id";
        final String OWM_RESULTS = "results";
        final String OWM_OVERVIEW = "overview";
        final String OWM_RELASE_DATE = "release_date";
        final String OWM_POSTER_PATH = "poster_path";
        final String OWM_TITLE = "title";
        final String OWM_VOTE_AVERAGE = "vote_average";

        try
        {
            JSONObject json = new JSONObject(jsonStr);
            JSONArray jsonArray = json.getJSONArray(OWM_RESULTS);
            Movie movie;
            mMovies = new ArrayList<Movie>();

            for(int i = 0; i < jsonArray.length(); i++)
            {
                movie = new Movie();
                JSONObject rec = jsonArray.getJSONObject(i);

                movie.ID            =   rec.getString(OWM_ID);
                movie.overview      =   rec.getString(OWM_OVERVIEW);
                movie.releaseDate   =   rec.getString(OWM_RELASE_DATE).substring(0,4);
                movie.posterURL     =   mContext.getString(R.string.api_image_url) +
                                        mContext.getString(R.string.api_imagesize_medium) +
                                        rec.getString(OWM_POSTER_PATH);
                movie.title         =   rec.getString(OWM_TITLE);
                movie.rating        =   rec.getString(OWM_VOTE_AVERAGE);
                movie.trailers      =   getTrailersForMovie(movie.ID);
                movie.reviews       =   getReviewsForMovie(movie.ID);
                movie.runningTime   =   getRunningTimeForMovie(movie.ID);

                mMovies.add(movie);
            }

            Log.d(LOG_TAG, "FetchMobiesTask Complete. " + "inserted" + " Inserted");

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    private ArrayList<Trailer> getTrailerDataFromJson(String jsonStr)
            throws JSONException
    {
        // Now we have a String representing the complete forecast in JSON Format.
        // Fortunately parsing is easy:  constructor takes the JSON string and converts it
        // into an Object hierarchy for us.

        // These are the names of the JSON objects that need to be extracted.
        final String OWM_ID = "id";
        final String OWM_RESULTS = "results";
        final String OWM_iso_639_1 = "iso_639_1";
        final String OWM_KEY = "key";
        final String OWM_NAME = "name";
        final String OWM_SITE = "site";
        final String OWM_SIZE = "size";
        final String OWM_TYPE = "type";

        ArrayList<Trailer> returnVal = new ArrayList<Trailer>();

        try
        {
            JSONObject json = new JSONObject(jsonStr);
            JSONArray jsonArray = json.getJSONArray(OWM_RESULTS);
            Trailer trailer;

            for(int i = 0; i < jsonArray.length(); i++)
            {
                trailer = new Trailer();
                JSONObject rec = jsonArray.getJSONObject(i);

                trailer.id          =   rec.getString(OWM_ID);
                trailer.url         =   mContext.getString(R.string.api_youtube_url).replace("{ID}", rec
                        .getString(OWM_KEY));
                trailer.name        =   rec.getString(OWM_NAME);
                trailer.site        =   rec.getString(OWM_SITE);
                trailer.size        =   rec.getString(OWM_SIZE);
                trailer.type        =   rec.getString(OWM_TYPE);

                returnVal.add(trailer);
            }

            Log.d(LOG_TAG, "FetchMobiesTask Complete. " + "inserted" + " Inserted");

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        return returnVal;
    }

    private ArrayList<Review> getReviewDataFromJson(String jsonStr)
            throws JSONException
    {
        // Now we have a String representing the complete forecast in JSON Format.
        // Fortunately parsing is easy:  constructor takes the JSON string and converts it
        // into an Object hierarchy for us.

        // These are the names of the JSON objects that need to be extracted.
        final String OWM_ID = "id";
        final String OWM_RESULTS = "results";
        final String OWM_iso_639_1 = "iso_639_1";
        final String OWM_URL = "url";
        final String OWM_AUTHOR = "author";
        final String OWM_CONTENT = "content";

        ArrayList<Review> returnVal = new ArrayList<Review>();

        try
        {
            JSONObject json = new JSONObject(jsonStr);
            JSONArray jsonArray = json.getJSONArray(OWM_RESULTS);
            Review review;

            for(int i = 0; i < jsonArray.length(); i++)
            {
               review = new Review();
                JSONObject rec = jsonArray.getJSONObject(i);

                review.id          =   rec.getString(OWM_ID);
                review.url         =   rec.getString(OWM_URL);
                review.author       =   rec.getString(OWM_AUTHOR);
                review.content       =   rec.getString(OWM_CONTENT);

                returnVal.add(review);
            }

            Log.d(LOG_TAG, "FetchReviewsTask Complete. " + "inserted" + " Inserted");

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        return returnVal;
    }

    /**
     * Take the String representing the complete forecast in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     *
     * Fortunately parsing is easy:  constructor takes the JSON string and converts it
     * into an Object hierarchy for us.
     */
    private String getRunningTimeDataFromJson(String jsonStr)
            throws JSONException
    {
        // Now we have a String representing the complete forecast in JSON Format.
        // Fortunately parsing is easy:  constructor takes the JSON string and converts it
        // into an Object hierarchy for us.

        // These are the names of the JSON objects that need to be extracted.
        final String OWM_ID = "id";
        final String OWM_RESULTS = "results";
        final String OWM_RUNTIME = "runtime";

        String returnVal = "";

        try
        {
            JSONObject json = new JSONObject(jsonStr);

            returnVal        =   json.getString(OWM_RUNTIME);

            Log.d(LOG_TAG, "FetchRuntimeTask Complete. " + "inserted" + " Inserted");

        } catch (JSONException e)
        {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        return returnVal;
    }

    protected  ArrayList<Trailer> getTrailersForMovie(String movieID)
    {
        return getTrailersForMovie(movieID, 1);
    }

    protected  ArrayList<Trailer> getTrailersForMovie(String movieID, int retries)
    {
        ArrayList<Trailer> returnVal = new ArrayList<Trailer>();

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String jsonStr = null;

        try
        {
            ((MainActivity)mContext).mSort = Utility.getCurrentSort(mContext);
            URL url;
            url = new URL(mContext.getString(R.string.api_trailers_url)
                                  .replace(mContext.getString(R.string.api_id_placeholder), movieID)
                                  .replace(mContext.getString(R.string.api_key_placeholder), mContext.getString(R.string.api_key)));

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null)
            {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null)
            {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0)
            {
                // Stream was empty.  No point in parsing.
                return null;
            }
            jsonStr = buffer.toString();
            returnVal = getTrailerDataFromJson(jsonStr);
        }
        catch (Exception e)
        {
            Log.e("getTrailers attempt " + String.valueOf(retries), "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            if(retries > 3)
                return null;
            else
            {
                try{Thread.sleep(500);}catch(InterruptedException ex){}
                return getTrailersForMovie(movieID, ++ retries);
            }
        }
        finally
        {
            if (urlConnection != null)
            {
                urlConnection.disconnect();
            }
            if (reader != null)
            {
                try
                {
                    reader.close();
                }
                catch (final IOException e)
                {
                    Log.e("PlaceholderFragment", "Error closing stream", e);
                }
            }
        }

        return returnVal;
    }

    protected  ArrayList<Review> getReviewsForMovie(String movieID)
    {
        return getReviewsForMovie(movieID, 1);
    }

    protected  ArrayList<Review> getReviewsForMovie(String movieID, int retries)
    {
        ArrayList<Review> returnVal = new ArrayList<Review>();

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String jsonStr = null;

        try
        {
            ((MainActivity)mContext).mSort = Utility.getCurrentSort(mContext);
            URL url;
            url = new URL(mContext.getString(R.string.api_reviews_url)
                                  .replace(mContext.getString(R.string.api_id_placeholder), movieID)
                                  .replace(mContext.getString(R.string.api_key_placeholder), mContext.getString(R.string.api_key)));

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null)
            {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null)
            {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0)
            {
                // Stream was empty.  No point in parsing.
                return null;
            }
            jsonStr = buffer.toString();
            returnVal = getReviewDataFromJson(jsonStr);
        }
        catch (Exception e)
        {
            Log.e("getReviews attempt " + String.valueOf(retries), "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            if(retries > 3)
                return null;
            else
            {
                try{Thread.sleep(500);}catch(InterruptedException ex){}
                return getReviewsForMovie(movieID, ++ retries);
            }
        }
        finally
        {
            if (urlConnection != null)
            {
                urlConnection.disconnect();
            }
            if (reader != null)
            {
                try
                {
                    reader.close();
                }
                catch (final IOException e)
                {
                    Log.e("PlaceholderFragment", "Error closing stream", e);
                }
            }
        }

        return returnVal;
    }

    protected  String getRunningTimeForMovie(String movieID)
    {
        return getRunningTimeForMovie(movieID, 1);
    }

    protected  String getRunningTimeForMovie(String movieID, int retries)
    {
        String returnVal = "";

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String jsonStr = null;

        try
        {
            URL url;
            url = new URL(mContext.getString(R.string.api_movie_url)
                                  .replace(mContext.getString(R.string.api_id_placeholder), movieID)
                                  .replace(mContext.getString(R.string.api_key_placeholder), mContext.getString(R.string.api_key)));

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null)
            {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null)
            {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0)
            {
                // Stream was empty.  No point in parsing.
                return null;
            }
            jsonStr = buffer.toString();
            returnVal = getRunningTimeDataFromJson(jsonStr);
        }
        catch (Exception e)
        {
            Log.e("getRunningTime attempt " + String.valueOf(retries), "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            if(retries > 3)
                return null;
            else
            {
                try{Thread.sleep(500);}catch(InterruptedException ex){}
                return getRunningTimeForMovie(movieID, ++ retries);
            }
        }
        finally
        {
            if (urlConnection != null)
            {
                urlConnection.disconnect();
            }
            if (reader != null)
            {
                try
                {
                    reader.close();
                }
                catch (final IOException e)
                {
                    Log.e("PlaceholderFragment", "Error closing stream", e);
                }
            }
        }

        return returnVal;
    }
}
