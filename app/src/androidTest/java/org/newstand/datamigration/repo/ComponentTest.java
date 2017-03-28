package org.newstand.datamigration.repo;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.runner.RunWith;

/**
 * Created by Nick@NewStand.org on 2017/3/28 10:19
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
@RunWith(AndroidJUnit4.class)
public class ComponentTest {
    protected Context getContext() {
        return InstrumentationRegistry.getTargetContext();
    }
}
