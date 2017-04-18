package org.newstand.datamigration.repo;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.List;

/**
 * Created by Nick@NewStand.org on 2017/3/28 10:03
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public interface RepoService<T> {

    boolean insert(Context context, @NonNull T t);

    boolean delete(Context context, @NonNull T t);

    boolean update(Context context, @NonNull T t);

    T findFirst(Context context);

    T findLast(Context context);

    List<T> findAll(Context context);

    int size(Context context);
}
