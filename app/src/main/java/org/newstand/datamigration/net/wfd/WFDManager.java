package org.newstand.datamigration.net.wfd;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;

/**
 * Created by Nick@NewStand.org on 2017/3/14 10:18
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public abstract class WFDManager {

    public abstract void start();

    public abstract void discovery();

    public abstract void stopDiscovery();

    public abstract void connect(WifiP2pDevice device, WifiP2pManager.ActionListener listener);

    public abstract void requestConnectionInfo();

    public abstract void tearDown();

    public abstract void setDeviceName(String name);

    public static WFDService.WFDServiceImplBuilder builder(Context context) {
        return new WFDService.WFDServiceImplBuilder(context);
    }

    enum State {
        RUNNING, TEARING_DOWN, TEARDOWN, IDLE
    }
}
