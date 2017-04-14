package org.newstand.datamigration.secure;

import org.junit.Test;
import org.newstand.datamigration.BuildConfig;
import org.newstand.logger.Logger;

/**
 * Created by Nick@NewStand.org on 2017/4/14 16:22
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
public class VersionInfoTest {
    @Test
    public void toJson() throws Exception {
        String json = VersionInfo.builder()
                .versionCode(BuildConfig.VERSION_CODE)
                .versionName(org.newstand.datamigration.BuildConfig.VERSION_NAME)
                .downloadUrl("no_url")
                .updateDate(System.currentTimeMillis())
                .updateDescription("This is a description~")
                .build().toJson();
        Logger.d("json %s\n", json);
    }

    @Test
    public void fromJson() throws Exception {

    }

}