package org.newstand.datamigration.model.message;

/**
 * Created by Nick@NewStand.org on 2017/3/8 9:37
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public interface EventDefinations {

    String KEY_CATEGORY = "category";
    String KEY_SOURCE = "session";
    String KEY_CATEGORY_DATA_LIST = "category_data_list";

    int ON_CATEGORY_OF_DATA_SELECT_COMPLETE = 0x1;
}
