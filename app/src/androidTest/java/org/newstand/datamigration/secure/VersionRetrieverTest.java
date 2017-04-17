package org.newstand.datamigration.secure;

import android.support.test.InstrumentationRegistry;

import org.junit.Test;
import org.newstand.datamigration.common.ActionListener2Adapter;
import org.newstand.datamigration.strategy.Interval;
import org.newstand.datamigration.sync.Sleeper;
import org.newstand.logger.Logger;

/**
 * Created by Nick@NewStand.org on 2017/4/14 15:45
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
public class VersionRetrieverTest {
    @Test
    public void getLatestVersionStringFromServerAsync() throws Exception {

        VersionRetriever.hasLaterVersionAsync(InstrumentationRegistry.getTargetContext(),
                new ActionListener2Adapter<VersionCheckResult, Throwable>() {
            @Override
            public void onComplete(VersionCheckResult versionCheckResult) {
                super.onComplete(versionCheckResult);
                Logger.d("hasLaterVersionAsync %s", versionCheckResult);
            }
        });

        Sleeper.sleepQuietly(Interval.Minutes.getIntervalMills());
    }

}