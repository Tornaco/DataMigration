package dev.tornaco.vangogh.display;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.FloatRange;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import org.newstand.logger.Logger;

import dev.tornaco.vangogh.media.Image;

/**
 * Created by guohao4 on 2017/8/25.
 * Email: Tornaco@163.com
 */

public class ImageViewDisplayer implements ImageDisplayer {

    private ImageView target;

    public ImageViewDisplayer(ImageView target) {
        this.target = target;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ImageViewDisplayer that = (ImageViewDisplayer) o;

        return target.equals(that.target);
    }

    @Override
    public int hashCode() {
        return target.hashCode();
    }

    @Override
    public void display(@Nullable Image image) {
        Drawable drawable = image != null ? image.asDrawable(target.getContext()) : null;
        if (drawable != null) {
            target.setImageBitmap(null);
            target.setImageDrawable(drawable);
            return;
        }
        Bitmap bitmap = image == null ? null : image.asBitmap(target.getContext());
        Logger.v("ImageViewDisplayer, display: %s", bitmap);
        target.setImageDrawable(null);
        target.setImageBitmap(bitmap);
    }

    @Nullable
    @Override
    public View getView() {
        return target;
    }

    @Override
    public int getWidth() {
        return target.getWidth();
    }

    @Override
    public int getHeight() {
        return target.getHeight();
    }

    @Override
    public String getLabel() {
        return "ImageView-Displayer#" + hashCode();
    }

    public void setAlpha(@FloatRange(from = 0.0, to = 1.0) float alpha) {
        target.setAlpha(alpha);
    }

    public void setRotation(float rotation) {
        target.setRotation(rotation);
    }

    public void setRotationX(float rotationX) {
        target.setRotationX(rotationX);
    }

    public void setRotationY(float rotationY) {
        target.setRotationY(rotationY);
    }

    public void setScaleX(float scaleX) {
        target.setScaleX(scaleX);
    }

    public void setScaleY(float scaleY) {
        target.setScaleY(scaleY);
    }

    public void setScrollX(int value) {
        target.setScrollX(value);
    }

    public void setScrollY(int value) {
        target.setScrollY(value);
    }

    public void setTranslationX(float translationX) {
        target.setTranslationX(translationX);
    }

    public void setTranslationY(float translationY) {
        target.setTranslationY(translationY);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void setTranslationZ(float translationZ) {
        target.setTranslationZ(translationZ);
    }
}
