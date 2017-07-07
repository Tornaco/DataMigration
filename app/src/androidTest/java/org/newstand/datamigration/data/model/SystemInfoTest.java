package org.newstand.datamigration.data.model;

import org.junit.Test;
import org.newstand.logger.Logger;

/**
 * Created by Nick on 2017/6/28 18:31
 */
public class SystemInfoTest {
    @Test
    public void fromSystem() throws Exception {
        SystemInfo.fromSystem();
    }

    @Test
    public void toJson() throws Exception {
        Logger.d("ToJson:\n%s", SystemInfo.fromSystem().toJson());
    }

}