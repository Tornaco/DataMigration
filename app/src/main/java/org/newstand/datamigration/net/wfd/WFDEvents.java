package org.newstand.datamigration.net.wfd;

/**
 * Created by Nick@NewStand.org on 2017/3/14 10:22
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public interface WFDEvents {
    int WIFI_P2P_DISCOVERY_STARTED = 0x200;
    int WIFI_P2P_DISCOVERY_STOPED = 0x201;
    int WIFI_P2P_STATE_CHANGED_ACTION = 0x202;
    int WIFI_P2P_PEERS_CHANGED_ACTION = 0x203;
    int WIFI_P2P_CONNECTION_CHANGED_ACTION = 0x204;
    int WIFI_P2P_THIS_DEVICE_CHANGED_ACTION = 0x205;

    String KEY_NETWORK_INFO = "networkInfo";
}
