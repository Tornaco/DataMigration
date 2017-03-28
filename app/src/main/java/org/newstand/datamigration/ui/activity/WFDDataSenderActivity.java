package org.newstand.datamigration.ui.activity;

import android.content.Intent;
import android.net.wifi.p2p.WifiP2pInfo;

/**
 * Created by Nick@NewStand.org on 2017/3/14 15:12
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class WFDDataSenderActivity extends WFDSetupActivity {
    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {
        super.onConnectionInfoAvailable(info);

        String host = info.groupOwnerAddress.getHostAddress();

        Intent intent = new Intent(this, SenderCategoryViewerActivity.class);
        intent.putExtra("host", host);
        transitionTo(intent);
    }
}
