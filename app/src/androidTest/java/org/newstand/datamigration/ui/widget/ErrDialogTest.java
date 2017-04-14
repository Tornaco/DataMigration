package org.newstand.datamigration.ui.widget;

import android.os.Handler;
import android.os.Looper;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by Nick@NewStand.org on 2017/4/14 13:36
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
@RunWith(AndroidJUnit4.class)
public class ErrDialogTest {
    @Test
    public void attach() throws Exception {

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                ErrDialog.attach(InstrumentationRegistry.getTargetContext(), new IllegalStateException(), null);
            }
        });
    }

}