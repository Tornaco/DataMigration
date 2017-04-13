package org.newstand.datamigration.ui.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.tbruyelle.rxpermissions2.RxPermissions;
import com.vlonjatg.progressactivity.ProgressRelativeLayout;

import org.newstand.datamigration.R;
import org.newstand.datamigration.common.Consumer;
import org.newstand.datamigration.data.model.Peer;
import org.newstand.datamigration.net.wfd.ConnectionListener;
import org.newstand.datamigration.net.wfd.DiscoveryListener;
import org.newstand.datamigration.net.wfd.WFDManager;
import org.newstand.datamigration.provider.SettingsProvider;
import org.newstand.datamigration.ui.adapter.P2PListViewAdapter;
import org.newstand.datamigration.ui.adapter.P2PListViewHolder;
import org.newstand.datamigration.ui.widget.AlertDialogCompat;
import org.newstand.datamigration.ui.widget.InputDialogCompat;
import org.newstand.datamigration.ui.widget.ProgressDialogCompat;
import org.newstand.datamigration.utils.Collections;
import org.newstand.logger.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import cn.iwgang.simplifyspan.SimplifySpanBuild;
import cn.iwgang.simplifyspan.other.OnClickableSpanListener;
import cn.iwgang.simplifyspan.unit.SpecialClickableUnit;
import cn.iwgang.simplifyspan.unit.SpecialTextUnit;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Nick@NewStand.org on 2017/3/13 15:31
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class WFDSetupActivity extends TransitionSafeActivity implements DiscoveryListener,
        WifiP2pManager.ConnectionInfoListener,
        ConnectionListener {

    @Getter
    private RecyclerView recyclerView;
    @Getter
    private P2PListViewAdapter adapter;

    @Getter
    private WFDManager wfdManager;

    @Getter
    private ProgressRelativeLayout progressRelativeLayout;

    private ProgressDialog mProgressDialog;

    @Setter
    @Getter
    private WifiP2pInfo availableWifiP2pInfo;

    @Getter
    private Handler handler;

    private Runnable mDiscoveryTimeoutRunnable = new Runnable() {
        @Override
        public void run() {
            progressRelativeLayout.showError(R.drawable.ic_phone_android,
                    null,
                    getString(R.string.title_err_wfd_device_not_found),
                    getString(R.string.title_err_wfd_device_not_found_action),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Logger.d("Timeout triggered.");
                            restartWFDService();
                        }
                    });
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getTitle());
        setContentView(R.layout.activity_wfd_setup);
        showHomeAsUp();
        setupView();
    }

    private void requestPermission() {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.WAKE_LOCK,
                Manifest.permission.CHANGE_NETWORK_STATE,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        )
                .subscribe(new io.reactivex.functions.Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean granted) throws Exception {
                        if (granted) {
                            startAndDiscovery();
                        } else {
                            onPermissionNotGrant();
                        }
                    }
                });
    }

    private void onPermissionNotGrant() {
        AlertDialog alertDialog = new AlertDialog.Builder(WFDSetupActivity.this)
                .setTitle("No permission")
                .setMessage("WTF????")
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishWithAfterTransition();
                    }
                }).create();
        alertDialog.show();
    }

    @Override
    protected void onSmoothHook() {
        super.onSmoothHook();

        handler = new Handler();
        wfdManager = WFDManager.builder(this)
                .connectionInfoListener(this)
                .connectionListener(this)
                .discoveryListener(this)
                .peerListListener(new WifiP2pManager.PeerListListener() {
                    @Override
                    public void onPeersAvailable(final WifiP2pDeviceList peers) {
                        removeDiscoveryTimeoutTracer();
                        Collection<WifiP2pDevice> devices = peers.getDeviceList();
                        final List<Peer> peerList = new ArrayList<>();
                        Collections.consumeRemaining(devices, new Consumer<WifiP2pDevice>() {
                            @Override
                            public void accept(@NonNull WifiP2pDevice wifiP2pDevice) {
                                if (isDMDevice(wifiP2pDevice)) {
                                    Peer peer = new Peer();
                                    peer.setDevice(wifiP2pDevice);
                                    peer.setIcon(getIconForDevice(wifiP2pDevice));
                                    peerList.add(peer);
                                } else {
                                    Logger.d("Ignore device:%s", wifiP2pDevice.deviceName);
                                }
                            }
                        });
                        onPeersListUpdate(peerList);
                    }
                }).build();

        requestPermission();
    }

    @Override
    protected boolean needSmoothHook() {
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        wfdManager.stopDiscovery();
    }

    protected void onPeersListUpdate(List<Peer> peerList) {
        getAdapter().update(peerList);
    }

    private void onDiscovering() {
        List<Integer> skipIds = new ArrayList<>();
        skipIds.add(R.id.recycler_view);
        skipIds.add(R.id.card_1);
        progressRelativeLayout.showLoading(skipIds);
    }

    private void setupView() {
        recyclerView = findView(R.id.recycler_view);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = onCreateAdapter();
        recyclerView.setAdapter(adapter);

        progressRelativeLayout = findView(R.id.progressLayout);

        setupNotes();
    }

    private void setupNotes() {
        TextView noteSummaryView = findView(android.R.id.text1);

        SimplifySpanBuild simplifySpanBuild = new SimplifySpanBuild();
        simplifySpanBuild.append(getString(R.string.summary_setup_wfd_note_name));
        simplifySpanBuild.append(new SpecialTextUnit(SettingsProvider.getDeviceName())
                .setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.accent))
                .showUnderline()
                .useTextBold()
                .setClickableUnit(new SpecialClickableUnit(noteSummaryView, new OnClickableSpanListener() {
                    @Override
                    public void onClick(TextView tv, String clickText) {
                        showNameSettingsDialog(SettingsProvider.getDeviceName());
                    }
                })));
        simplifySpanBuild.append("\n");
        simplifySpanBuild.append(getString(R.string.summary_setup_wfd_note_item));
        simplifySpanBuild.append("\n");
        simplifySpanBuild.append(getString(R.string.summary_setup_wfd_note_click));
        simplifySpanBuild.append("\n");
        simplifySpanBuild.append(getString(R.string.summary_setup_wfd_note_restart));
        simplifySpanBuild.append(new SpecialTextUnit(getString(R.string.title_restart_connector))
                .setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.accent))
                .showUnderline()
                .useTextBold()
                .setClickableUnit(new SpecialClickableUnit(noteSummaryView, new OnClickableSpanListener() {
                    @Override
                    public void onClick(TextView tv, String clickText) {
                        restartWFDService();
                    }
                })));

        noteSummaryView.setText(simplifySpanBuild.build());
    }

    private P2PListViewAdapter onCreateAdapter() {
        return new P2PListViewAdapter(this) {
            @Override
            protected void onItemClick(P2PListViewHolder holder) {
                Peer peer = getAdapter().getPeerList().get(holder.getAdapterPosition());
                requestConnect(peer);
            }
        };
    }

    protected boolean isDMDevice(WifiP2pDevice wifiP2pDevice) {
        return wifiP2pDevice != null;
    }

    protected void showNameSettingsDialog(final String currentName) {
        new InputDialogCompat.Builder(WFDSetupActivity.this)
                .setTitle(getString(R.string.title_set_device_id))
                .setInputDefaultText(currentName)
                .setInputMaxWords(32)
                .setPositiveButton(getString(android.R.string.ok), new InputDialogCompat.ButtonActionListener() {
                    @Override
                    public void onClick(CharSequence inputText) {
                        SettingsProvider.setDeviceName(inputText.toString());
                        setupNotes();
                        restartWFDService();
                    }
                })
                .setNegativeButton(getString(android.R.string.cancel), new InputDialogCompat.ButtonActionListener() {
                    @Override
                    public void onClick(CharSequence inputText) {
                        // Nothing.
                    }
                })
                .show();
    }

    protected boolean groupOwner() {
        return false;
    }

    protected void requestConnect(Peer peer) {
        progressRelativeLayout.showContent();
        ProgressDialogCompat.dismiss(mProgressDialog);
        mProgressDialog = ProgressDialogCompat.createUnCancelableIndeterminateShow(WFDSetupActivity.this, getString(R.string.summary_wfd_conn));
        wfdManager.connect(groupOwner(), peer.getDevice(), new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                onConnectSuccess();
            }

            @Override
            public void onFailure(int reason) {
                Logger.d("connect, onFailure %d", reason);
                AlertDialogCompat.createShow(WFDSetupActivity.this,
                        getString(R.string.title_wfd_conn_fail),
                        getString(R.string.summary_wfd_conn_fail));
                ProgressDialogCompat.dismiss(mProgressDialog);
            }
        });
    }

    protected void onConnectSuccess() {
        Logger.d("connect, onSuccess");
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialogCompat.createUnCancelableIndeterminateShow(WFDSetupActivity.this,
                    getString(R.string.summary_wfd_requesting_conn_res));
            return;
        }
        if (mProgressDialog.isShowing()) {
            mProgressDialog.setMessage(getString(R.string.summary_wfd_requesting_conn_res));
        }
    }

    protected Drawable getIconForDevice(WifiP2pDevice wifiP2pDevice) {
        return ContextCompat.getDrawable(this, R.drawable.ic_phone_android);
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {
        Logger.d("onConnectionInfoAvailable: %s", info);
        progressRelativeLayout.showContent();
        ProgressDialogCompat.dismiss(mProgressDialog);
        wfdManager.stopDiscovery();
        setAvailableWifiP2pInfo(info);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        wfdManager.tearDown();
    }

    @Override
    public void onP2PDiscoveryStart() {
        Logger.d("onP2PDiscoveryStart");
        traceDiscoveryTimeout();
    }

    private void traceDiscoveryTimeout() {
        removeDiscoveryTimeoutTracer();
        handler.postDelayed(mDiscoveryTimeoutRunnable, SettingsProvider.getDiscoveryTimeout());
    }

    private void removeDiscoveryTimeoutTracer() {
        handler.removeCallbacks(mDiscoveryTimeoutRunnable);
    }

    @Override
    public void onP2PDiscoveryStop() {
        Logger.d("onP2PDiscoveryStop");
    }

    @Override
    public void onP2PDiscoveryStartSuccess() {
        Logger.d("onP2PDiscoveryStartSuccess");
        onDiscovering();
    }

    @Override
    public void onP2PDiscoveryStopSuccess() {
        Logger.d("onP2PDiscoveryStopSuccess");
    }

    @Override
    public void onP2PDiscoveryStartFail(int reason) {
        Logger.d("discoverPeers, fail:%d", reason);
        progressRelativeLayout.showError(R.drawable.ic_wifi_tethering,
                null,
                getString(R.string.title_err_wfd_device_not_support),
                getString(R.string.title_err_wfd_device_not_support_action),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        restartWFDService();
                    }
                });
    }

    @Override
    public void onP2PDiscoveryStopFail(int reason) {
        Logger.d("onP2PDiscoveryStopFail, fail:%d", reason);
    }

    @Override
    public void onWifiP2PConnectionChanged(NetworkInfo networkInfo) {
        Logger.d("onWifiP2PConnectionChanged %s", networkInfo);
        if (networkInfo.isConnected()) {
            wfdManager.requestConnectionInfo();
        } else if (networkInfo.getState() == NetworkInfo.State.DISCONNECTED
                || networkInfo.getState() == NetworkInfo.State.DISCONNECTING) {
            onDisconnectedOrDisconnecting();
        }
    }

    protected void onDisconnectedOrDisconnecting() {
        Logger.w("onDisconnectedOrDisconnecting");
    }

    @Override
    public void onRequestConnectionInfo() {
        Logger.d("onRequestConnectionInfo");
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialogCompat.createUnCancelableIndeterminateShow(WFDSetupActivity.this,
                    getString(R.string.summary_wfd_requesting_conn_info));
            return;
        }
        if (mProgressDialog.isShowing()) {
            mProgressDialog.setMessage(getString(R.string.summary_wfd_requesting_conn_info));
        }
    }

    protected void startAndDiscovery() {
        wfdManager.setDeviceName(SettingsProvider.getDeviceName());
        wfdManager.start();
        wfdManager.discovery();
    }

    protected void restartWFDService() {
        Logger.d("restartWFDService...");
        getAdapter().update(java.util.Collections.<Peer>emptyList());
        progressRelativeLayout.showContent();
        wfdManager.tearDown();
        startAndDiscovery();
    }
}
