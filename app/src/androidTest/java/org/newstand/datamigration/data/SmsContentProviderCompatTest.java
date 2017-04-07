package org.newstand.datamigration.data;

import android.support.test.InstrumentationRegistry;

import org.junit.Test;
import org.newstand.logger.Logger;

/**
 * Created by Nick@NewStand.org on 2017/4/7 13:47
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
public class SmsContentProviderCompatTest {
    @Test
    public void waitUtilBecomeDefSmsApp() throws Exception {
        boolean res = SmsContentProviderCompat.waitUtilBecomeDefSmsApp(InstrumentationRegistry.getTargetContext(), 10);
        Logger.d("waitUtilBecomeDefSmsApp %s", res);
    }

    @Test
    public void restoreDefSmsApp() throws Exception {
        SmsContentProviderCompat.restoreDefSmsApp(InstrumentationRegistry.getTargetContext());
    }

    @Test
    public void setAsDefaultSmsApp() throws Exception {
        SmsContentProviderCompat.setAsDefaultSmsApp(InstrumentationRegistry.getTargetContext());
        waitUtilBecomeDefSmsApp();
    }


}