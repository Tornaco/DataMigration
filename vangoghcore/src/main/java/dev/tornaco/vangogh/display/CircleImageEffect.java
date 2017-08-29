package dev.tornaco.vangogh.display;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.support.annotation.NonNull;

import dev.tornaco.vangogh.media.BitmapImage;
import dev.tornaco.vangogh.media.Image;

/**
 * Created by guohao4 on 2017/8/27.
 * Email: Tornaco@163.com
 */

public class CircleImageEffect implements ImageEffect {
    @NonNull
    @Override
    public Image process(Context context, @NonNull Image image) {
        if (image.asBitmap(context) == null) return image;
        return new BitmapImage(createCircleImage(image.asBitmap(context),
                Math.min(image.asBitmap(context).getWidth(), image.asBitmap(context).getHeight())));
    }

    private static Bitmap createCircleImage(Bitmap source, int min) {
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        Bitmap target = Bitmap.createBitmap(min, min, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(target);
        canvas.drawCircle(min / 2, min / 2, min / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(source, 0, 0, paint);
        source = null;
        return target;
    }
}
