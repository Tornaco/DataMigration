package org.newstand.datamigration.loader;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.newstand.logger.Logger;

import dev.tornaco.vangogh.loader.Loader;
import dev.tornaco.vangogh.loader.LoaderObserver;
import dev.tornaco.vangogh.media.Image;
import dev.tornaco.vangogh.media.ImageSource;

/**
 * Created by guohao4 on 2017/8/27.
 * Email: Tornaco@163.com
 */

public class ContactsImageLoader implements Loader<Image> {
    @Nullable
    @Override
    public Image load(@NonNull ImageSource source, @Nullable LoaderObserver observer) {
        Logger.v("ContactsImageLoader, load: %s", source);
        return null;
    }

    @Override
    public int priority() {
        return -100;
    }
}
