package org.newstand.datamigration.repo;

import org.newstand.datamigration.utils.Closer;

import java.io.Closeable;

import lombok.AllArgsConstructor;

/**
 * Created by Nick@NewStand.org on 2017/3/28 14:13
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
@AllArgsConstructor
public class Transaction<T extends Closeable> {

    private T host;

    public T event() {
        return host;
    }

    public void end() {
        Closer.closeQuietly(host);
    }
}
