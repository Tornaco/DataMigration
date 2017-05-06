package tornaco.lib.media.vinci.loader.sources;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import tornaco.lib.media.vinci.utils.BitmapUtils;

/**
 * Created by Nick on 2017/5/5 14:39
 * E-Mail: Tornaco@163.com
 * All right reserved.
 */

public abstract class ResSourceLoader implements SourceLoader {
    @Nullable
    @Override
    public Bitmap loadFromSource(@NonNull Source source) {
        String url = source.getSourceUrl().substring(sourceUrlPrefix().length());
        int resId = source.getContext().getResources().getIdentifier(url, getTypeDefinition(),
                source.getContext().getPackageName());
        return getBitmapFromId(source.getContext(), resId);
    }

    protected abstract String sourceUrlPrefix();

    protected Bitmap getBitmapFromId(Context context, int id) {
        return BitmapUtils.getBitmap(context, id);
    }

    protected abstract String getTypeDefinition();
}
