package org.newstand.datamigration.data.model;

import android.graphics.drawable.Drawable;
import android.net.wifi.p2p.WifiP2pDevice;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by Nick@NewStand.org on 2017/3/14 13:49
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
@Setter
@Getter
@NoArgsConstructor
@ToString(callSuper = true)
public class Peer {
    WifiP2pDevice device;
    Drawable icon;
}
