package org.newstand.datamigration.data.event;

/**
 * Created by Nick@NewStand.org on 2017/3/8 9:37
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public interface IntentEvents {

    String KEY_CATEGORY = "category";
    String KEY_SOURCE = "session";
    String KEY_HOST = "host";

    int EVENT_TRANSPORT_COMPLETE = 0x99;
    int EVENT_ON_CATEGORY_OF_DATA_SELECT_COMPLETE = 0x100;
    int EVENT_ON_USER_ACTION = 0x101;
}
