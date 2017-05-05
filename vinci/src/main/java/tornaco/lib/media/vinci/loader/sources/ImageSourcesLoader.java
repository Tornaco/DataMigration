package tornaco.lib.media.vinci.loader.sources;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.HashSet;
import java.util.Set;

import tornaco.lib.media.vinci.Enforcer;
import tornaco.lib.media.vinci.loader.Loader;
import tornaco.lib.media.vinci.loader.Priority;
import tornaco.lib.media.vinci.utils.Logger;

/**
 * Created by Nick on 2017/5/5 14:32
 * E-Mail: Tornaco@163.com
 * All right reserved.
 */

public class ImageSourcesLoader implements Loader {

    private static final Set<SourceLoader> LOADERS = new HashSet<>();

    static {
        register(new DrawableSourceLoader());
        register(new MediaStoreSourceLoader());
    }

    public static void register(SourceLoader loader) {
        synchronized (LOADERS) {
            Enforcer.enforce(!LOADERS.contains(loader), "Duplicate loader is not allowed.");
            LOADERS.add(loader);
        }
    }

    private static SourceLoader getLoaderFromSource(String sourceUrl) {
        for (SourceLoader sourceLoader : LOADERS) {
            Logger.dbg("Check %s", sourceLoader);
            if (sourceUrl.startsWith(sourceLoader.sourceUrlPrefix())) {
                return sourceLoader;
            }
        }
        return null;
    }

    private Context mContext;

    public ImageSourcesLoader(Context context) {
        this.mContext = context;
    }

    @Nullable
    @Override
    public Bitmap load(@NonNull String sourceUrl) {
        SourceLoader sourceLoader = getLoaderFromSource(sourceUrl);

        Logger.dbg("match loader %s", sourceLoader);

        if (sourceLoader == null) return null;// FIXME Log.

        Source source = new Source();
        source.setContext(mContext);
        source.setSourceUrl(sourceUrl);

        return sourceLoader.loadFromSource(source);
    }

    @Override
    public int priority() {
        return Priority.C;
    }
}
