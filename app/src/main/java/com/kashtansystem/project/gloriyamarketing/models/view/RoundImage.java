package com.kashtansystem.project.gloriyamarketing.models.view;

import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

public class RoundImage extends android.support.v7.widget.AppCompatImageView
{
    public RoundImage(Context context)
    {
        super(context);
    }

    public RoundImage(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public RoundImage(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        final Drawable drawable = getDrawable();

        if (drawable == null)
            return;
        if (getWidth() == 0 || getHeight() == 0)
            return;

        final Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap().copy(Bitmap.Config.ARGB_4444, true);
        final int width = getWidth();

        final Bitmap roundBitmap = getRoundedBitmap(bitmap, width);
        canvas.drawBitmap(roundBitmap, 0, 0, null);
    }

    private Bitmap getRoundedBitmap(Bitmap bitmap, int radius)
    {
        Bitmap finalBitmap;
        if (bitmap.getWidth() != radius || bitmap.getHeight() != radius)
            finalBitmap = Bitmap.createScaledBitmap(bitmap, radius, radius, false);
        else
            finalBitmap = bitmap;

        final Bitmap resBmp = Bitmap.createBitmap(finalBitmap.getWidth(), finalBitmap.getHeight(), Bitmap.Config.ARGB_4444);
        final Canvas canvas = new Canvas(resBmp);

        final int round = finalBitmap.getWidth() / 2;

        final Rect rect = new Rect(0, 0, finalBitmap.getWidth(), finalBitmap.getHeight());
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        paint.setARGB(0, 0, 0, 0);
        paint.setColor(Color.parseColor("#BAB399"));
        //paint.setColor(Color.parseColor("#4f4a4d"));

        canvas.drawCircle(round, round - 3.5f, round - 10f, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(finalBitmap, rect, rect, paint);

        return resBmp;
    }
}