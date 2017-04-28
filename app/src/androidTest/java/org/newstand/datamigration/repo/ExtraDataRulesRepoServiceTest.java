package org.newstand.datamigration.repo;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.newstand.datamigration.policy.ExtraDataRule;
import org.newstand.logger.Logger;

import java.util.Arrays;

/**
 * Created by Nick@NewStand.org on 2017/4/28 9:39
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
@RunWith(AndroidJUnit4.class)
public class ExtraDataRulesRepoServiceTest {

    @Test
    public void createTemplate() {
        ExtraDataRulesRepoService.get().update(InstrumentationRegistry.getTargetContext(),
                ExtraDataRule.builder()
                        .packageName("com.tencent.mm")
                        .alias("微信")
                        .extraDataDirs("/sdcard/tencent/MicroMsg")
                        .build());

        ExtraDataRulesRepoService.get().update(InstrumentationRegistry.getTargetContext(),
                ExtraDataRule.builder()
                        .packageName("org.newstand.datamigration")
                        .alias("DataMigration")
                        .extraDataDirs("/sdcard/.DataMigration/Data|/sdcard/.DataMigration/Test")
                        .build());

    }

    @Test
    public void parse() {
        Logger.d("Dirs %s", Arrays.toString(ExtraDataRulesRepoService.get().findFirst(InstrumentationRegistry.getTargetContext())
                .parseDir()));
    }
}