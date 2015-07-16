package land.basso.android.popularmovies;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by jbasso on 7/15/2015.
 */
public class AspectRatioImageView extends ImageView
{
    public AspectRatioImageView(Context context)
    {
        super(context);
    }

    public AspectRatioImageView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public AspectRatioImageView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        if(getDrawable() != null)
        {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = width * getDrawable().getIntrinsicHeight() / getDrawable().getIntrinsicWidth();
            setMeasuredDimension(width, height);
        }
        else
        {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = MeasureSpec.getSize(heightMeasureSpec);
            setMeasuredDimension(width, height);
        }
    }
}
