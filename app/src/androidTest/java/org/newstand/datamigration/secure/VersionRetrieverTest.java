package org.newstand.datamigration.secure;

import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;

import org.junit.Test;
import org.newstand.datamigration.common.Consumer;
import org.newstand.logger.Logger;

/**
 * Created by Nick@NewStand.org on 2017/4/14 15:45
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
public class VersionRetrieverTest {
    @Test
    public void getLatestVersionStringFromServerAsync() throws Exception {
        VersionRetriever.getLatestVersionStringFromServerAsync(InstrumentationRegistry.getTargetContext(), new Consumer<String>() {
            @Override
            public void accept(@NonNull String s) {
                Logger.d("Version %s", s);
            }
        });
    }

}