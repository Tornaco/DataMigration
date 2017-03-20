package org.newstand.datamigration.ui.activity;

import android.net.wifi.p2p.WifiP2pInfo;

import org.newstand.datamigration.data.model.Peer;
import org.newstand.datamigration.provider.SettingsProvider;
import org.newstand.datamigration.utils.Collections;

import java.util.List;

/**
 * Created by Nick@NewStand.org on 2017/3/14 15:12
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class WFDDataSenderActivity extends WFDSetupActivity {
    @Override
    protected void onPeersListUpdate(List<Peer> peerList) {
        super.onPeersListUpdate(peerList);

        if (!Collections.isEmpty(peerList) && SettingsProvider.autoConnectEnabled()) {
            Peer peer = peerList.get(0);
            requestConnect(peer);
        }
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {
        super.onConnectionInfoAvailable(info);

    }
}
