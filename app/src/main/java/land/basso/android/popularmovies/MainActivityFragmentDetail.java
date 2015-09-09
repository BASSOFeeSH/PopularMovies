package land.basso.android.popularmovies;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainActivityFragmentDetail.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainActivityFragmentDetail#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainActivityFragmentDetail extends Fragment
{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private int mPosition;
    private Movie mMovie;
    private ListView mTrailerListView;
    private ListView mReviewListView;

    private OnFragmentInteractionListener mListener;

    public MainActivityFragmentDetail() { }

    public MainActivityFragmentDetail(int position)
    {
        // Required empty public constructor
        mPosition = position;
    }

//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     *
//     * @return A new instance of fragment MainActivityFragmentDetail.
//     */
//    // TODO: Rename and change types and number of parameters
    public static MainActivityFragmentDetail newInstance(String param1, String param2)
    {
        MainActivityFragmentDetail fragment = new MainActivityFragmentDetail();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.main_fragment_detail, container, false);

        mMovie = ((MainActivity)getActivity()).mMovies.get(mPosition);

        TrailerArrayAdapter mTrailerAdapter;
        ReviewArrayAdapter mReviewAdapter;

        MainActivity context = (MainActivity)getActivity();

        TextView    title       = (TextView)rootView.findViewById(R.id.detail_title);
        TextView    year        = (TextView)rootView.findViewById(R.id.detail_release_date);
        TextView    time        = (TextView)rootView.findViewById(R.id.detail_running_time);
        TextView    rating      = (TextView)rootView.findViewById(R.id.detail_rating);
        TextView    overview    = (TextView)rootView.findViewById(R.id.detail_description);
        ImageView   poster      = (ImageView)rootView.findViewById(R.id.detail_poster);
        mTrailerListView        = (ListView)rootView.findViewById(R.id.detail_list_trailers);
        mReviewListView         = (ListView)rootView.findViewById(R.id.detail_list_reviews);

        title.setText(mMovie.title);
        year.setText(mMovie.releaseDate);
        time.setText(mMovie.runningTime + "min");
        rating.setText(mMovie.rating + "/10");
        overview.setText(mMovie.overview);
        if(mMovie.posterURL != null && mMovie.posterURL.length() > 0)
        {
            Picasso.with(getActivity())
                   .load(mMovie.posterURL)
                   .into(poster);
        }

        // Get a reference to the ListView, and attach this adapter to it.
        final ToggleButton toggle = (ToggleButton) rootView.findViewById(R.id.detail_button_favorite);
        MainActivity m = (MainActivity)getActivity();
        if(m.mFavorites.contains(Integer.parseInt(((MainActivity)getActivity()).mMovies.get(mPosition).ID)))
        {   toggle.setChecked(true);    }
        else
        {   toggle.setChecked(false);   }

        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                MainActivity m = (MainActivity)getActivity();
                int movieID = Integer.parseInt(m.mMovies.get(mPosition).ID);
                if (isChecked)
                {
                    if(!m.mFavorites.contains(movieID))
                    {
                        m.mFavorites.add(movieID);
                        TinyDB tiny = new TinyDB(m);
                        tiny.putListInt(getString(R.string.favorites_list_key), m.mFavorites);
                    }
                }
                else
                {
                    if(m.mFavorites.contains(movieID))
                    {
                        m.mFavorites.remove(m.mFavorites.indexOf(movieID));
                        TinyDB tiny = new TinyDB(m);
                        tiny.putListInt(getString(R.string.favorites_list_key), m.mFavorites);
                    }
                }
            }
        });

//        mTrailerAdapter = new TrailerArrayAdapter(getActivity(), R.id.detail_list_trailers, mMovie.trailers);

        mTrailerListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
            {

                Context context = view.getContext();

                TextView trailer = ((TextView) view.findViewById(R.id.detail_trailer_list_name));

                // get the clicked item ID
                String url = trailer.getTag().toString();

                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            }
        });

//        mReviewAdapter = new ReviewArrayAdapter(getActivity(), R.id.detail_list_reviews, mMovie.reviews);

        mReviewListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                Context context = view.getContext();

                TextView review = ((TextView) view.findViewById(R.id.detail_review_list_content));

                // get the clicked item ID
                String url = review.getTag().toString();

                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            }
        });

        return  rootView;
    }

    @Override
    public void onResume()
    {
        if(mMovie.trailers != null)
        {
            if (mTrailerListView.getAdapter() == null)
            {   mTrailerListView.setAdapter(new TrailerArrayAdapter(getActivity(), R.id.detail_list_trailers, mMovie.trailers));    }


            Utility.setListViewHeightBasedOnChildren(mTrailerListView);
        }

        if(mMovie.reviews != null)
        {
            if (mReviewListView.getAdapter() == null)
            {   mReviewListView.setAdapter(new ReviewArrayAdapter(getActivity(), R.id.detail_list_reviews, mMovie.reviews));    }

            Utility.setListViewHeightBasedOnChildren(mReviewListView);
        }

        super.onResume();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri)
    {
        if (mListener != null)
        {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        try
        {   mListener = (OnFragmentInteractionListener) activity;   }
        catch (ClassCastException e)
        {   throw new ClassCastException(activity.toString() + " must implement OnFragmentInteractionListener");    }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener
    {
        void onFragmentInteraction(Uri uri);
    }

}
