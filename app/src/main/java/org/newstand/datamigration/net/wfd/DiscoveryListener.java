package org.newstand.datamigration.net.wfd;

/**
 * Created by Nick@NewStand.org on 2017/3/14 15:04
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public interface DiscoveryListener {
    void onP2PDiscoveryStart();

    void onP2PDiscoveryStop();

    void onP2PDiscoveryStartSuccess();

    void onP2PDiscoveryStopSuccess();

    void onP2PDiscoveryStartFail(int reason);

    void onP2PDiscoveryStopFail(int reason);
}
