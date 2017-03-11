package org.newstand.datamigration.common;

import java.util.Observable;

/**
 * Created by Nick@NewStand.org on 2017/3/10 15:50
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class AbortSignal extends Observable {
    public void abort() {
        setChanged();
        notifyObservers("Abort...");
    }
}
