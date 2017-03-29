package org.newstand.datamigration.worker;

/**
 * Created by Nick@NewStand.org on 2017/3/28 16:29
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public interface Stats {

    int getTotal();

    int getLeft();

    int getSuccess();

    int getFail();

    void onSuccess();

    void onFail();

    Stats merge(Stats with);
}
