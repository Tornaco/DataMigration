package org.newstand.datamigration.loader;

import java.util.Collection;

/**
 * Created by Nick@NewStand.org on 2017/3/7 10:07
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public interface DataLoader<T> {
    Collection<T> load(LoaderSource source, LoaderFilter<T> filter);
}