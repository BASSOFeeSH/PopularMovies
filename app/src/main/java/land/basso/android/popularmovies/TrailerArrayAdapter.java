package land.basso.android.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by jbasso on 9/3/2015.
 */
public class TrailerArrayAdapter    extends ArrayAdapter<Trailer>
{
    Context mContext;
    int layoutResourceId;
    ArrayList<Trailer> data;

    public TrailerArrayAdapter(Context mContext, int layoutResourceId, ArrayList<Trailer> data)
    {
        super(mContext, layoutResourceId, data);

        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        /*
         * The convertView argument is essentially a "ScrapView" as described is Lucas post
         * http://lucasr.org/2012/04/05/performance-tips-for-androids-listview/
         * It will have a non-null value when ListView is asking you recycle the row layout.
         * So, when convertView is not null, you should simply update its contents instead of inflating a new row layout.
         */
        if (convertView == null)
        {
            // inflate the layout
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
//            convertView = inflater.inflate(layoutResourceId, parent, false);
            convertView = inflater.inflate(R.layout.detail_trailer_list_item, parent, false);
        }

        // object item based on the position
        Trailer trailer = data.get(position);

        // get the TextView and then set the text (item name) and tag (item ID) values
        TextView textViewItem = (TextView) convertView.findViewById(R.id.detail_trailer_list_name);
        textViewItem.setText(trailer.name);
        textViewItem.setTag(trailer.url);

        return convertView;
    }

}