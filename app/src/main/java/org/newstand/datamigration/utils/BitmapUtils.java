package org.newstand.datamigration.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.ContextCompat;

import org.newstand.logger.Logger;

/**
 * Created by Nick on 2017/5/5 14:44
 * E-Mail: Tornaco@163.com
 * All right reserved.
 */

public abstract class BitmapUtils {

    private static final Object LOCK = new Object();

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public static Bitmap getBitmap(VectorDrawable vectorDrawable) {
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return bitmap;
    }

    public static Bitmap getBitmap(VectorDrawableCompat vectorDrawable) {
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return bitmap;
    }

    @Nullable
    public static Bitmap getBitmap(Context context, @DrawableRes int drawableResId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableResId);
        return getBitmap(context, drawable);
    }

    @Nullable
    public static Bitmap getBitmap(Context context, Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof VectorDrawableCompat) {
            return getBitmap((VectorDrawableCompat) drawable);
        } else if (drawable instanceof VectorDrawable) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                return getBitmap((VectorDrawable) drawable);
            }
            return null;
        } else if ("android.graphics.drawable.AdaptiveIconDrawable"
                .equals(drawable.getClass().getName())) {
            Logger.w("AdaptiveIconDrawable on O is not supported for now.");
            return null;

        } else {
            throw new IllegalArgumentException("Unsupported drawable type:" + drawable);
        }
    }

    public static Bitmap decodeFileLocked(String path, BitmapFactory.Options decodeOptions) {
        synchronized (LOCK) {
            return BitmapFactory.decodeFile(path, decodeOptions);
        }
    }
}
