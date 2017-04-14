package org.newstand.datamigration.common;

import org.newstand.lib.iface.Adapter;

/**
 * Created by Nick@NewStand.org on 2017/4/14 9:58
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
@Adapter
public interface IFace {
    int getInt();

    float getFloat();

    boolean getBoolean();

    short getShort();

    double getDouble();

    String getString();

    Object getObject();

    void noReturn();
}
