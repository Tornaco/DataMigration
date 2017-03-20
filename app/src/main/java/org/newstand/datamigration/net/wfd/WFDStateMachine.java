package org.newstand.datamigration.net.wfd;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Looper;

import com.orhanobut.logger.Logger;

import org.newstand.lib.State;
import org.newstand.lib.StateMachine;

import lombok.Getter;

import static android.content.Context.WIFI_P2P_SERVICE;

/**
 * Created by Nick@NewStand.org on 2017/3/15 14:48
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class WFDStateMachine extends StateMachine {

    private IDLEState mIdleState = new IDLEState();
    private InitState mInitState = new InitState();
    private DiscoveringState mDiscoveringState = new DiscoveringState();
    private ConnectingState mConnectingState = new ConnectingState();
    private RequestingConnectionInfoState mRequestingConnectionInfoState = new RequestingConnectionInfoState();
    private TearingDownState mTearingDownState = new TearingDownState();

    @Getter
    private Context context;

    private WifiP2pManager mWifiP2pManager;
    private WifiP2pManager.Channel mChannel;

    protected WFDStateMachine(String name) {
        super(name);

        addState(mIdleState);
        addState(mInitState);
        addState(mDiscoveringState);
        addState(mConnectingState);
        addState(mRequestingConnectionInfoState);
        addState(mTearingDownState);

        setInitialState(mIdleState);
    }

    public static WFDStateMachine makeWFDStateMachine() {
        WFDStateMachine machine = new WFDStateMachine("WFDStateMachine");
        machine.setDbg(false);
        machine.start();
        return machine;
    }

    class LoggableState extends State {
        @Override
        public void enter() {
            super.enter();
            Logger.d("enter: %s", getName());
        }

        @Override
        public void exit() {
            super.exit();
            Logger.d("exit: %s", getName());
        }
    }

    class IDLEState extends LoggableState {
    }

    class InitState extends LoggableState {

        @Override
        public void enter() {
            super.enter();
            mWifiP2pManager = (WifiP2pManager) context.getSystemService(WIFI_P2P_SERVICE);
            mChannel = mWifiP2pManager.initialize(context, Looper.getMainLooper(),
                    new WifiP2pManager.ChannelListener() {
                        @Override
                        public void onChannelDisconnected() {
                            Logger.d("onChannelDisconnected");
                        }
                    });
        }

        @Override
        public void exit() {
            super.exit();
        }
    }

    class DiscoveringState extends LoggableState {
        @Override
        public void enter() {
            super.enter();
            mWifiP2pManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onFailure(int reason) {

                }
            });
        }
    }

    class ConnectingState extends LoggableState {

    }

    class RequestingConnectionInfoState extends LoggableState {

    }

    class TearingDownState extends LoggableState {

    }
}
