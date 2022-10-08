package org.newstand.datamigration.secure;

import android.content.Context;

import org.newstand.datamigration.BuildConfig;
import org.newstand.datamigration.common.ActionListener2;

/**
 * Created by Nick@NewStand.org on 2017/4/14 15:41
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class VersionRetriever {

    public static String currentVersionName() {
        return BuildConfig.VERSION_NAME;
    }

    public static String currentBuild() {
        return BuildConfig.BUILD_TYPE;
    }

    public static void hasLaterVersionAsync(Context context,
                                            final ActionListener2<VersionCheckResult, Throwable> listener) {

    }
}
