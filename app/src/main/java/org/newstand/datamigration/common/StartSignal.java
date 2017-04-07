package org.newstand.datamigration.common;

import java.util.Observable;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Nick@NewStand.org on 2017/3/29 12:50
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
@Getter
@Setter
public class StartSignal extends Observable {

    private Object tag;

    public void start() {
        setChanged();
        notifyObservers("Start...");
    }
}
