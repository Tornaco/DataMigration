package org.newstand.datamigration.data.model;

import androidx.annotation.NonNull;

import org.newstand.datamigration.loader.DataLoader;

/**
 * Created by Nick@NewStand.org on 2017/3/7 11:15
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

interface LoaderGetter<T> {
    @NonNull
    DataLoader<T> getLoader();
}
