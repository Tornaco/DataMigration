package dev.tornaco.vangogh.media;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;

import dev.tornaco.vangogh.VangoghContext;
import lombok.ToString;

/**
 * Created by guohao4 on 2017/8/25.
 * Email: Tornaco@163.com
 */
@ToString
public class BitmapImage implements Image {

    private Bitmap reference;

    public BitmapImage(Bitmap bitmap) {
        this.reference = bitmap;
    }

    @Nullable
    @Override
    public Bitmap asBitmap() {
        return reference;
    }

    @Nullable
    @Override
    public Drawable asDrawable() {
        return new BitmapDrawable(VangoghContext.getContext().getResources(), asBitmap());
    }
}
