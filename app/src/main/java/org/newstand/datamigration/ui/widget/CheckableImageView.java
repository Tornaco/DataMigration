package org.newstand.datamigration.ui.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.ImageView;

import org.newstand.datamigration.R;

public class CheckableImageView extends ImageView implements Checkable {
    private boolean mChecked = false;
    private int mCheckMarkBackgroundColor;
    private CheckableFlipDrawable mDrawable;

    public CheckableImageView(Context context) {
        super(context);
        init(context);
    }

    public CheckableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CheckableImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CheckableImageView(Context context, AttributeSet attrs,
                              int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        setCheckMarkBackgroundColor(context.getResources().getColor(R.color.accent));
    }

    public void setCheckMarkBackgroundColor(int color) {
        mCheckMarkBackgroundColor = color;
        if (mDrawable != null) {
            mDrawable.setCheckMarkBackgroundColor(color);
        }
    }

    public void toggle() {
        setChecked(!mChecked);
    }

    @Override
    public boolean isChecked() {
        return mChecked;
    }

    @Override
    public void setChecked(boolean checked) {
        setChecked(checked, true);
    }

    public void setChecked(boolean checked, boolean animate) {
        if (mChecked == checked) {
            return;
        }

        mChecked = checked;
        applyCheckState(animate);
    }

    @Override
    public void setImageDrawable(Drawable d) {
        if (d != null) {
            if (mDrawable == null) {
                mDrawable = new CheckableFlipDrawable(d, getResources(),
                        mCheckMarkBackgroundColor, 150);
                applyCheckState(false);
            } else {
                mDrawable.setFront(d);
            }
            d = mDrawable;
        }
        super.setImageDrawable(d);
    }

    private void applyCheckState(boolean animate) {
        mDrawable.flipTo(!mChecked);
        if (!animate) {
            mDrawable.reset();
        }
    }
}
