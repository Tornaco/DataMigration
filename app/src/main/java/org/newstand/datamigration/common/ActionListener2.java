package org.newstand.datamigration.common;

/**
 * Created by Nick@NewStand.org on 2017/4/10 13:17
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public interface ActionListener2<RESULT, ERROR> {
    void onStart();

    void onError(ERROR error);

    void onComplete(RESULT result);
}
