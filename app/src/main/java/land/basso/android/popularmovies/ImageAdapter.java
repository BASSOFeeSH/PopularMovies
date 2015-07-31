package land.basso.android.popularmovies;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.squareup.picasso.Picasso;

/**
 * Created by jbasso on 7/10/2015.
 */
public class ImageAdapter extends BaseAdapter
{
    private Context mContext;

    public ImageAdapter(Context c)
    {
        mContext = c;
    }

    public int getCount()
    {
        if(((MainActivity)mContext).mMovies == null)
            return 0; //return mThumbIds.length;
        else
            return ((MainActivity)mContext).mMovies.size();
    }

    public Object getItem(int position)
    {
        return null;
    }

    public long getItemId(int position)
    {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        AspectRatioImageView imageView;// = new AspectRatioImageView(mContext);

        if (convertView == null)
        {
            // if it's not recycled, initialize some attributes
            imageView = new AspectRatioImageView(mContext);
        } else
        {
            imageView = (AspectRatioImageView)convertView;
        }

        if(((MainActivity)mContext).mMovies != null)
        {
            Picasso.with(mContext).load(((MainActivity) mContext).mMovies.get(position).posterURL)
                   .placeholder(R.drawable.loading)
                    .error(R.drawable.error)
                    .into(imageView);
        }

        return imageView;
    }
}
