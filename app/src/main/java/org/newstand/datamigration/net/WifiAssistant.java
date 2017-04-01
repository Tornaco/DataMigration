package org.newstand.datamigration.net;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.google.common.base.Preconditions;

import org.newstand.datamigration.sync.Sleeper;
import org.newstand.datamigration.utils.WifiExtManager;
import org.newstand.logger.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lombok.Getter;

/**
 * Created by Nick@NewStand.org on 2017/3/13 16:01
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
public class WifiAssistant {

    protected static final String AP_NAME_PREFIX = "DM_";
    protected static final String AP_PRE_SHARE_KEY = "DMS-0001";

    protected WifiManager mWifiManager;
    protected WifiExtManager mWifiExtManager;

    private WifiApCallback mWifiApCallback;
    private WifiCallback mWifiCallback;

    private WifiReceiver mWifiReceiver;

    private boolean mWifiConnected;
    private WifiInfo mConnectedWifi;

    private WifiConfiguration mLastConnectedWifiConfig;

    protected HashMap<String, ScanResult> mApMap;

    @Getter
    private Context context;

    public WifiAssistant(Context context) {
        this.context = context;
        setup();
    }

    public interface WifiApCallback {
        void onEnabled(boolean success);
    }

    public interface WifiCallback {
        void onConnected();

        void onDisconnected();
    }

    private void setup() {
        mApMap = new HashMap<>();
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        mWifiExtManager = new WifiExtManager(mWifiManager);
        mWifiReceiver = new WifiReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        intentFilter.addAction(WifiExtManager.WIFI_AP_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        context.registerReceiver(mWifiReceiver, intentFilter);
        mWifiConnected = mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED;
        mConnectedWifi = mWifiManager.getConnectionInfo();
    }

    public void reset() {
        context.unregisterReceiver(mWifiReceiver);
        mWifiConnected = false;
        mConnectedWifi = null;
    }

    public void startAP(String ssid, WifiApCallback wifiApCallback) {
        Preconditions.checkNotNull(ssid);
        Preconditions.checkNotNull(wifiApCallback);
        String apSSID = AP_NAME_PREFIX + ssid;
        int apState = mWifiExtManager.getWifiApState();
        Logger.d("wifi ap state is " + apState);
        if (apState == WifiExtManager.WIFI_AP_STATE_ENABLED
                || apState == WifiExtManager.WIFI_AP_STATE_ENABLING) {
            WifiConfiguration apConfig = mWifiExtManager.getWifiApConfiguration();
            if (apConfig != null && apConfig.SSID.equals(apSSID)) {
                wifiApCallback.onEnabled(true);
                return;
            } else {
                mWifiExtManager.setWifiApEnabled(null, false);
            }
        }

        mWifiApCallback = wifiApCallback;
        WifiConfiguration apConfig = generateApConfig(apSSID, AP_PRE_SHARE_KEY);
        if (!mWifiExtManager.setWifiApEnabled(apConfig, true)) wifiApCallback.onEnabled(false);
    }

    public void waitForAP(String ssid) {
        List<String> now = getAPSSIDList();
        while (!now.contains(ssid)) {
            Logger.d("Retrying...%s", now);
            now = getAPSSIDList();
            Sleeper.sleepQuietly(2 * 1000);
        }
        Logger.d("SSID %s detected", ssid);
    }

    private List<String> getAPSSIDList() {
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        }
        mWifiManager.startScan();
        List<ScanResult> wifiList = mWifiReceiver.getWifiList();
        List<String> apList = new ArrayList<>();
        mApMap.clear();
        for (ScanResult scanResult : wifiList) {
            String apSSID = scanResult.SSID;
            apList.add(apSSID);
            mApMap.put(apSSID, scanResult);
        }
        return apList;
    }

    public int connectToAP(String ssid, WifiCallback wifiCallback) {
        Logger.d("connectToAP(" + ssid + ")");
        String apSSID = AP_NAME_PREFIX + ssid;
        ScanResult scanResult = mApMap.get(ssid);
        if (scanResult == null) {
            throw new IllegalArgumentException("error ssid " + ssid);
        }
        Logger.d("apSSID is {" + apSSID + "}");

        int netId = -1;
        WifiConfiguration wifiConfig = null;
        List<WifiConfiguration> wifiConfigurations = mWifiManager.getConfiguredNetworks();
        for (WifiConfiguration config : wifiConfigurations) {
            if (apSSID.equals(config.SSID) && scanResult.BSSID.equals(config.BSSID)) {
                wifiConfig = config;
                netId = config.networkId;
                break;
            }
        }
        Logger.d("found wifi config, netId is " + netId + ", {" + wifiConfig + "}");
        boolean saveConfig = false;
        if (wifiConfig == null) {
            wifiConfig = generateWifiConfig(apSSID, scanResult, AP_PRE_SHARE_KEY);
            netId = mWifiManager.addNetwork(wifiConfig);
            saveConfig = true;
        }
        if (netId == -1) {
            Logger.d("invalid netId");
            return -1;
        }

        if (wifiConfigConnected(wifiConfig)) {
            return 1;
        }

        if (mWifiCallback != null) {
            Logger.e("can not call connectToAP before connect to ap over!", new Exception());
            if (mWifiManager.getWifiState() != WifiManager.WIFI_STATE_ENABLED
                    && mWifiManager.getWifiState() != WifiManager.WIFI_STATE_ENABLING) {
                mWifiManager.reconnect();
            }
            return -1;
        }

        mLastConnectedWifiConfig = wifiConfig;
        mWifiCallback = wifiCallback;

        boolean connectRes = false;
        Logger.d("wifi config, netId is " + netId + ", {" + wifiConfig + "}");
        connectRes = mWifiManager.enableNetwork(netId, true);
        Logger.d("after enableNetwork(" + netId + ") return " + connectRes);
        if (connectRes) {
            if (saveConfig) {
                Logger.d("call saveConfiguration()");
                mWifiManager.saveConfiguration();
                Logger.d("after saveConfiguration()");
            }
            connectRes = mWifiManager.reconnect();
            Logger.d("after reconnect() return " + connectRes);
        }

        if (!connectRes) {
            return -1;
        }

        return 0;
    }

    private boolean wifiConfigConnected(WifiConfiguration wifiConfig) {
        if (mWifiManager.getWifiState() != WifiManager.WIFI_STATE_ENABLED) {
            return false;
        }
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        if (wifiInfo.getSSID().equals(wifiConfig.SSID)
                && wifiInfo.getBSSID().equals(wifiConfig.BSSID)) {
            return true;
        }
        return false;
    }

    class WifiReceiver extends BroadcastReceiver {

        private List<ScanResult> scanList;

        private final Object lock = new Object();

        public List<ScanResult> getWifiList() {
            synchronized (lock) {
                try {
                    lock.wait();
                } catch (Exception ignored) {
                }
                return scanList;
            }
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Logger.d("WifiReceiver.onReceive " + intent);
            if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)) {
                synchronized (lock) {
                    scanList = mWifiManager.getScanResults();
                    lock.notify();
                }
            } else if (WifiExtManager.WIFI_AP_STATE_CHANGED_ACTION.equals(action)) {
                Logger.d("mWifiApCallback is " + mWifiApCallback);
                int wifiApState = intent.getIntExtra(WifiExtManager.EXTRA_WIFI_AP_STATE,
                        WifiExtManager.WIFI_AP_STATE_FAILED);
                Logger.d("wifiApState is " + wifiApState);
                if (wifiApState == WifiExtManager.WIFI_AP_STATE_FAILED) {
                    Logger.d("wifiApState changed to WIFI_AP_STATE_FAILED");
                    mWifiExtManager.setWifiApEnabled(null, false);
                    if (mWifiApCallback != null) {
                        mWifiApCallback.onEnabled(false);
                        mWifiApCallback = null;
                    }
                } else if (wifiApState == WifiExtManager.WIFI_AP_STATE_ENABLED) {
                    Logger.d("wifiApState changed to WIFI_AP_STATE_ENABLED");
                    if (mWifiApCallback != null) {
                        mWifiApCallback.onEnabled(true);
                        mWifiApCallback = null;
                    }
                }
            } else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
                Logger.d("mWifiCallback is " + mWifiCallback);
                NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                NetworkInfo.DetailedState detailedState = networkInfo.getDetailedState();
                Logger.d("networkInfo is " + networkInfo + ", detailedState is " + detailedState);
                if (detailedState == NetworkInfo.DetailedState.CONNECTED) {
                    mWifiConnected = true;
                    mConnectedWifi = (WifiInfo) intent.getParcelableExtra(WifiManager.EXTRA_WIFI_INFO);
                    Logger.d("wifi connected to wifiInfo " + mConnectedWifi);
                    if (mWifiCallback != null) {
                        if (mLastConnectedWifiConfig != null) {
                            if (mConnectedWifi.getSSID().equals(mLastConnectedWifiConfig.SSID)
                                    && mConnectedWifi.getBSSID().equals(mLastConnectedWifiConfig.BSSID)) {
                                Logger.d("mWifiCallback.onConnected()");
                                mWifiCallback.onConnected();
                                mWifiCallback = null;
                            }
                        }
                    }
                } else {
                    if (detailedState != NetworkInfo.DetailedState.CONNECTING) {
                        mWifiConnected = false;
                    }
                    if (mWifiCallback != null) {
                        if (detailedState == NetworkInfo.DetailedState.FAILED) {
                            Logger.d("connect failed, mWifiCallback.onDisconnected()");
                            mWifiCallback.onDisconnected();
                            mWifiCallback = null;
                        }
                    }
                }
            }
        }
    }

    private static WifiConfiguration generateApConfig(String apSSID, String setPassword) {
        WifiConfiguration apConfig = new WifiConfiguration();
        apConfig.SSID = apSSID;
        apConfig.preSharedKey = setPassword;
        apConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
        apConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        apConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
        apConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        apConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        apConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        apConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        apConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        return apConfig;
    }

    private static WifiConfiguration generateWifiConfig(String apSSID, ScanResult scanResult, String setPassword) {
        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = "\"" + apSSID + "\"";
        wifiConfig.BSSID = scanResult.BSSID;

        boolean wapi_psk = scanResult.capabilities.contains("WAPI-PSK");
        boolean wapi_cert = scanResult.capabilities.contains("WAPI-CERT");
        boolean wep = scanResult.capabilities.contains("WEP");
        boolean psk = scanResult.capabilities.contains("PSK");
        boolean eap = scanResult.capabilities.contains("EAP");
        boolean open = false;

        Logger.d("scan ap type{eap:" + eap + ",psk:" + psk + ",wep:" + wep + ",wapi_cert:" + wapi_cert + ",wapi_psk:" + wapi_psk + "}");

        if (!eap && !psk && !wep && !wapi_cert && !wapi_psk) {
            open = true;
        }

        if (open) {
            wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        } else {
            if (psk) {
                wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);

                if (setPassword.matches("[0-9A-Fa-f]{64}")) {
                    wifiConfig.preSharedKey = setPassword;
                } else {
                    wifiConfig.preSharedKey = '"' + setPassword + '"';
                }
            } else if (wep) {
                wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                wifiConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                wifiConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);

                int length = setPassword.length();
                if ((length == 10 || length == 26 || length == 58) && setPassword.matches("[0-9A-Fa-f]*")) {
                    wifiConfig.wepKeys[0] = setPassword;
                } else {
                    wifiConfig.wepKeys[0] = '"' + setPassword + '"';
                }
            }
        }
        return wifiConfig;
    }
}
