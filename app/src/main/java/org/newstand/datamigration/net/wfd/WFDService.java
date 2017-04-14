package org.newstand.datamigration.net.wfd;

import android.content.Context;
import android.net.NetworkInfo;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;

import com.google.common.base.Preconditions;

import org.newstand.datamigration.broadcast.WifiDirectBroadcastReceiver;
import org.newstand.datamigration.utils.WakeLockWrapper;
import org.newstand.lib.wfdhook.WFDManagerHook;
import org.newstand.logger.Logger;

import dev.nick.eventbus.Event;
import dev.nick.eventbus.EventBus;
import dev.nick.eventbus.annotation.Events;
import dev.nick.eventbus.annotation.ReceiverMethod;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import static android.content.Context.WIFI_P2P_SERVICE;

/**
 * Created by Nick@NewStand.org on 2017/3/14 10:43
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
class WFDService extends WFDManager {

    private Context context;

    private WifiDirectBroadcastReceiver mReceiver;

    private WifiP2pManager mWifiP2pManager;
    private WifiP2pManager.Channel mChannel;

    private WifiP2pManager.PeerListListener peerListListener;

    private WifiP2pManager.ConnectionInfoListener connectionInfoListener;

    private DiscoveryListener discoveryListener;

    private ConnectionListener connectionListener;

    private PowerManager.WakeLock mWakeLock;

    private Handler mHandler;

    @Setter
    @Getter
    private State state;

    private WifiP2pManager.PeerListListener mInternalPeerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peersList) {
            if (peerListListener != null) {
                peerListListener.onPeersAvailable(peersList);
            }
        }
    };

    private WifiP2pManager.ConnectionInfoListener mInternalInfoListener = new WifiP2pManager.ConnectionInfoListener() {

        @Override
        public void onConnectionInfoAvailable(final WifiP2pInfo info) {
            if (connectionInfoListener != null) {
                connectionInfoListener.onConnectionInfoAvailable(info);
            }
        }
    };

    private void init() {
        this.mWifiP2pManager = (WifiP2pManager) context.getSystemService(WIFI_P2P_SERVICE);
        mHandler = new Handler(Looper.getMainLooper());
        this.mChannel = mWifiP2pManager.initialize(context, Looper.getMainLooper(),
                new WifiP2pManager.ChannelListener() {
                    @Override
                    public void onChannelDisconnected() {
                        Logger.d("onChannelDisconnected");
                    }
                });
        setState(State.IDLE);
    }

    private WFDService(Context context, WifiP2pManager.PeerListListener peerListListener,
                       WifiP2pManager.ConnectionInfoListener connectionInfoListener,
                       DiscoveryListener discoveryListener, ConnectionListener connectionListener) {
        this.context = context;
        this.peerListListener = peerListListener;
        this.connectionInfoListener = connectionInfoListener;
        this.discoveryListener = discoveryListener;
        this.connectionListener = connectionListener;
        init();
    }

    @Override
    public void discovery() {
        discoverPeers();
    }

    @Override
    public void stopDiscovery() {
        stopDiscoverPeers();
    }

    @Override
    public void start() {
        Preconditions.checkState(getState() != State.RUNNING);
        EventBus.from(context).subscribe(this);
        mReceiver = new WifiDirectBroadcastReceiver();
        mReceiver.register(context);
        setState(State.RUNNING);
        mWakeLock = WakeLockWrapper.getWakeLockInstance(context, "WFDService");
        removePersistentGroups();
    }

    private void createGroup() {
        if (getState() != State.RUNNING) return;
        mWifiP2pManager.createGroup(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Logger.d("beGroupOwner, success");
            }

            @Override
            public void onFailure(int reason) {
                Logger.d("beGroupOwner, fail:%d", reason);
            }
        });
    }

    private void discoverPeers() {
        if (getState() != State.RUNNING) return;
        mWifiP2pManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                if (discoveryListener != null) {
                    discoveryListener.onP2PDiscoveryStartSuccess();
                }
            }

            @Override
            public void onFailure(int reason) {
                if (discoveryListener != null) {
                    discoveryListener.onP2PDiscoveryStartFail(reason);
                }
            }
        });
    }

    private void removeGroup() {
        mWifiP2pManager.removeGroup(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Logger.d("removeGroup, success");
            }

            @Override
            public void onFailure(int reason) {
                Logger.d("removeGroup, fail:%d", reason);
            }
        });
    }

    private void cancelConnect() {
        mWifiP2pManager.cancelConnect(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Logger.d("cancelConnect, success");
            }

            @Override
            public void onFailure(int reason) {
                Logger.d("cancelConnect, fail:%d", reason);
            }
        });
    }

    @Override
    public void connect(boolean owner, WifiP2pDevice device, @NonNull final WifiP2pManager.ActionListener listener) {
        if (getState() != State.RUNNING) return;

        Logger.d("connect device %s owner? %s", device, String.valueOf(owner));

        cancelConnect();

        WifiP2pConfig config = new WifiP2pConfig();

        config.deviceAddress = device.deviceAddress;
        config.wps.setup = WpsInfo.PBC;
        config.groupOwnerIntent = owner ? 15 : 0;

        config.deviceAddress = device.deviceAddress;

        config.wps.setup = WpsInfo.PBC;

        mWifiP2pManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                listener.onSuccess();
            }

            @Override
            public void onFailure(int reason) {
                listener.onFailure(reason);
            }
        });
    }

    private void removePersistentGroups() {
        new WFDManagerHook(mWifiP2pManager, mChannel).deletePersistentGroups();
    }

    @Override
    public void requestConnectionInfo() {
        Logger.d("requestConnectionInfo");
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                connectionListener.onRequestConnectionInfo();
            }
        });
        mWifiP2pManager.requestConnectionInfo(mChannel, mInternalInfoListener);
    }

    private void stopDiscoverPeers() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mWifiP2pManager.stopPeerDiscovery(mChannel, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    if (discoveryListener != null) {
                        discoveryListener.onP2PDiscoveryStopSuccess();
                    }
                }

                @Override
                public void onFailure(int reason) {
                    if (discoveryListener != null) {
                        discoveryListener.onP2PDiscoveryStopFail(reason);
                    }
                }
            });
        }
    }

    @Override
    public void tearDown() {
        setState(State.TEARING_DOWN);
        EventBus.from(context).unSubscribe(this);
        if (mReceiver != null) mReceiver.unRegister(context);
        cancelConnect();
        removeGroup();
        stopDiscoverPeers();
        setState(State.TEARDOWN);
        if (mWakeLock != null && mWakeLock.isHeld()) mWakeLock.release();// FIX NPE
    }

    @SuppressWarnings("TryWithIdenticalCatches")
    @Override
    public void setDeviceName(final String name) {
        Logger.d("Trying set name to %s", name);
        new WFDManagerHook(mWifiP2pManager, mChannel)
                .setDeviceName(mChannel, name, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Logger.d("setDeviceName success to %s", name);
                    }

                    @Override
                    public void onFailure(int reason) {
                        Logger.d("setDeviceName fail %d", reason);
                    }
                });
    }

    @ReceiverMethod
    @Events(WFDEvents.WIFI_P2P_DISCOVERY_STARTED)
    public void onP2PDiscoveryStart() {
        if (discoveryListener != null) discoveryListener.onP2PDiscoveryStart();
        else Logger.d("onP2PDiscoveryStart");
    }

    @ReceiverMethod
    @Events(WFDEvents.WIFI_P2P_DISCOVERY_STOPED)
    public void onP2PDiscoveryStop() {
        if (discoveryListener != null) discoveryListener.onP2PDiscoveryStop();
        else Logger.d("onP2PDiscoveryStop");
    }

    @ReceiverMethod
    @Events(WFDEvents.WIFI_P2P_STATE_CHANGED_ACTION)
    public void onWifiP2PStateChanged(Event event) {
        int state = event.getArg1();
        Logger.d("onWifiP2PStateChanged %d", state);
    }

    @ReceiverMethod
    @Events(WFDEvents.WIFI_P2P_PEERS_CHANGED_ACTION)
    public void onWifiP2PPeersChanged() {
        Logger.d("onWifiP2PPeersChanged");
        mWifiP2pManager.requestPeers(mChannel, mInternalPeerListListener);
    }

    @ReceiverMethod
    @Events(WFDEvents.WIFI_P2P_CONNECTION_CHANGED_ACTION)
    public void onWifiP2PConnectionChanged(Event event) {
        NetworkInfo networkInfo = event.getData().getParcelable(WFDEvents.KEY_NETWORK_INFO);
        if (networkInfo == null) {
            Logger.e("Null info!!!");
            return;
        }
        if (connectionListener != null) {
            connectionListener.onWifiP2PConnectionChanged(networkInfo);
        } else {
            Logger.d("onWifiP2PConnectionChanged %s", networkInfo);
        }
    }

    @ReceiverMethod
    @Events(WFDEvents.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
    public void onWifiP2PThisDeviceChanged() {
        Logger.d("onWifiP2PThisDeviceChanged");
    }

    public static class WFDServiceImplBuilder {

        private Context context;
        private WifiP2pManager.PeerListListener peerListListener;
        private WifiP2pManager.ConnectionInfoListener connectionInfoListener;
        private DiscoveryListener discoveryListener;
        private ConnectionListener connectionListener;

        WFDServiceImplBuilder(Context context) {
            this.context = context;
        }

        public WFDService.WFDServiceImplBuilder peerListListener(WifiP2pManager.PeerListListener peerListListener) {
            this.peerListListener = peerListListener;
            return this;
        }

        public WFDService.WFDServiceImplBuilder connectionInfoListener(WifiP2pManager.ConnectionInfoListener connectionInfoListener) {
            this.connectionInfoListener = connectionInfoListener;
            return this;
        }

        public WFDService.WFDServiceImplBuilder discoveryListener(DiscoveryListener discoveryListener) {
            this.discoveryListener = discoveryListener;
            return this;
        }

        public WFDService.WFDServiceImplBuilder connectionListener(ConnectionListener connectionListener) {
            this.connectionListener = connectionListener;
            return this;
        }

        public WFDService build() {
            return new WFDService(context, peerListListener, connectionInfoListener, discoveryListener, connectionListener);
        }
    }
}
