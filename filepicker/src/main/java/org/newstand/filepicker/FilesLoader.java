package org.newstand.filepicker;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;
import java.util.Collection;

/**
 * Created by Nick@NewStand.org on 2017/3/31 10:41
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class FilesLoader implements Loader<Collection<File>, String> {

    @NonNull
    @Override
    public Collection<File> load(@Nullable String p) {
        return null;
    }

    @Override
    public void abort() {

    }
}
