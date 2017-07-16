package org.newstand.datamigration.data.event;

/**
 * Created by Nick@NewStand.org on 2017/3/8 9:37
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public interface IntentEvents {

    String KEY_CATEGORY = "category";
    String KEY_SOURCE = "session";
    String KEY_TRANSPORT_TYPE = "transport_type";
    String KEY_HOST = "host";
    String KEY_USER_ACTION_FINGER_PRINT = "ua_fp";
    String KEY_PKG_NAME = "pkg";
    String KEY_LOG_PATH = "log_path";

    String ACTION_SCHEDULE_TASK = "org.newstand.datamigration.ACTION_SCHEDULE_TASK";
    String KEY_ACTION_SCHEDULE_TASK = "key.org.newstand.datamigration.ACTION_SCHEDULE_TASK";


    int EVENT_TRANSPORT_COMPLETE = 0x99;
    int EVENT_ON_CATEGORY_OF_DATA_SELECT_COMPLETE = 0x100;
    int EVENT_ON_USER_ACTION = 0x101;

    int REQUEST_CODE_FILE_PICKER = 0x102;
    int EVENT_FILE_PICKER = 0x103;
}
