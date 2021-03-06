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
public class ReviewArrayAdapter    extends ArrayAdapter<Review>
{
    Context mContext;
    int layoutResourceId;
    ArrayList<Review> data;

    public ReviewArrayAdapter(Context mContext, int layoutResourceId, ArrayList<Review> data)
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
            convertView = inflater.inflate(R.layout.detail_review_list_item, parent, false);
        }

        // object item based on the position
        Review review = data.get(position);

        // get the TextView and then set the text (item name) and tag (item ID) values
        TextView textViewItem = (TextView) convertView.findViewById(R.id.detail_review_list_content);
        int length = review.content.length();
        if(length > 50) {   length = 50;    }
        textViewItem.setText(review.content.substring(0, length) + "...");
        textViewItem.setTag(review.url);

        return convertView;
    }

}