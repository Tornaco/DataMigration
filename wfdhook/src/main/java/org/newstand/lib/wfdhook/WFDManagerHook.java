package org.newstand.lib.wfdhook;

import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pGroupList;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

/**
 * Created by Nick@NewStand.org on 2017/3/15 18:52
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
public class WFDManagerHook {

    private WifiP2pManager wifiP2pManager;
    private WifiP2pManager.Channel channel;

    public WFDManagerHook(WifiP2pManager wifiP2pManager, WifiP2pManager.Channel channel) {
        this.wifiP2pManager = wifiP2pManager;
        this.channel = channel;
    }

    public void setDeviceName(WifiP2pManager.Channel c, String devName, WifiP2pManager.ActionListener listener) {
        wifiP2pManager.setDeviceName(c, devName, listener);
    }

    public void deletePersistentGroup(WifiP2pManager.Channel c, int netId, WifiP2pManager.ActionListener listener) {
        wifiP2pManager.deletePersistentGroup(c, netId, listener);
    }

    public void deletePersistentGroups() {
        wifiP2pManager.requestPersistentGroupInfo(channel, new WifiP2pManager.PersistentGroupInfoListener() {
            @Override
            public void onPersistentGroupInfoAvailable(WifiP2pGroupList groups) {
                if (groups == null) return;
                for (WifiP2pGroup group : groups.getGroupList()) {
                    Log.d("WFDManagerHook", "deletePersistentGroup:" + group);
                    wifiP2pManager.deletePersistentGroup(channel, group.getNetworkId(),
                            new WifiP2pManager.ActionListener() {
                                @Override
                                public void onFailure(int reason) {
                                    Log.d("WFDManagerHook", "deletePersistentGroup onFailure:" + reason);
                                }

                                @Override
                                public void onSuccess() {
                                    Log.d("WFDManagerHook", "deletePersistentGroup success");
                                }
                            });
                }
            }
        });
    }
}
