package org.newstand.datamigration.service;

import android.support.annotation.NonNull;

import org.newstand.datamigration.data.event.UserAction;

import java.util.List;

/**
 * Created by Nick@NewStand.org on 2017/3/29 16:25
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public interface UserActionHandler {
    void onUserAction(@NonNull UserAction action);

    @NonNull
    List<UserAction> getAll();
}
