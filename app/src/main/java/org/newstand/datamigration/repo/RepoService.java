package org.newstand.datamigration.repo;

import android.support.annotation.NonNull;

import java.util.List;

/**
 * Created by Nick@NewStand.org on 2017/3/28 10:03
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public interface RepoService<T> {

    boolean insert(@NonNull T t);

    boolean delete(@NonNull T t);

    boolean update(@NonNull T t);

    T findFirst();

    T findLast();

    List<T> findAll();
}
