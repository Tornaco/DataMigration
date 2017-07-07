package org.newstand.datamigration.worker.transport;

import java.util.Comparator;

/**
 * Created by guohao4 on 2017/7/7.
 */

public class SessionComparator implements Comparator<Session> {
    @Override
    public int compare(Session o1, Session o2) {
        return o1.getDate() < o2.getDate() ? 1 : 0;
    }
}
