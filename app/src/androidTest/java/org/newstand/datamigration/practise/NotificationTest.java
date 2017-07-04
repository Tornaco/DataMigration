package org.newstand.datamigration.practise;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.newstand.logger.Logger;

/**
 * Created by Nick on 2017/7/2 11:55
 */
@RunWith(AndroidJUnit4.class)
public class NotificationTest {

    @Test
    public void testConstructNotification() {
        Notification n = Notification.builder().title("XXXX").content("eeee").build();
        Logger.d("n=" + n);

    }

}
