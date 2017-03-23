package org.newstand.datamigration.service;

/**
 * Created by Nick@NewStand.org on 2017/3/21 13:28
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public abstract class Session {
    abstract void start();

    abstract void stop();
}
