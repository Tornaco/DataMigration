package org.newstand.datamigration.common;

import java.util.Observable;

/**
 * Created by Nick@NewStand.org on 2017/3/29 12:50
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class StartSignal extends Observable {
    public void start() {
        setChanged();
        notifyObservers("Start...");
    }
}
