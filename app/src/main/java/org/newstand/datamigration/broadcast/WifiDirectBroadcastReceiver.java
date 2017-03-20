package org.newstand.datamigration.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.os.Bundle;

import org.newstand.datamigration.net.wfd.WFDEvents;

import dev.nick.eventbus.Event;
import dev.nick.eventbus.EventBus;

public class WifiDirectBroadcastReceiver extends BroadcastReceiver {

    public void register(Context context) {
        context.registerReceiver(this, getIntentFilter());
    }

    public void unRegister(Context context) {
        context.unregisterReceiver(this);
    }

    private IntentFilter getIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            intentFilter.addAction(WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION);
        }
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        return intentFilter;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();

        /*check if the wifi is enable*/
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            Event event = Event.builder().eventType(WFDEvents.WIFI_P2P_STATE_CHANGED_ACTION)
                    .arg1(state).build();
            EventBus.from(context).publish(event);
        }

        /*get the list*/
        else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            EventBus.from(context).publishEmptyEvent(WFDEvents.WIFI_P2P_PEERS_CHANGED_ACTION);
        } else if (WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION.equals(action)) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                int State = intent.getIntExtra(WifiP2pManager.EXTRA_DISCOVERY_STATE, -1);
                if (State == WifiP2pManager.WIFI_P2P_DISCOVERY_STARTED) {
                    EventBus.from(context).publishEmptyEvent(WFDEvents.WIFI_P2P_DISCOVERY_STARTED);
                } else if (State == WifiP2pManager.WIFI_P2P_DISCOVERY_STOPPED) {
                    EventBus.from(context).publishEmptyEvent(WFDEvents.WIFI_P2P_DISCOVERY_STOPED);
                }
            }
        }
        /*Respond to new connection or disconnections*/
        else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            Bundle bundle = new Bundle();
            bundle.putParcelable(WFDEvents.KEY_NETWORK_INFO, networkInfo);
            Event event = Event.builder().eventType(WFDEvents.WIFI_P2P_CONNECTION_CHANGED_ACTION)
                    .data(bundle).build();
            EventBus.from(context).publish(event);
        }

        /*Respond to this device's wifi state changing*/
        else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            EventBus.from(context).publishEmptyEvent(WFDEvents.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        }
    }
}
