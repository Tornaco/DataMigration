package org.newstand.datamigration.ui.tiles;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.widget.RelativeLayout;

import org.newstand.datamigration.R;
import org.newstand.logger.Logger;

import java.net.InetAddress;

import be.ppareit.swiftp.FsService;
import be.ppareit.swiftp.FsSettings;
import dev.nick.tiles.tile.SwitchTileView;

/**
 * Created by Nick@NewStand.org on 2017/4/6 18:26
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class FTPControlTile extends ThemedTile {

    public FTPControlTile(@NonNull Context context) {
        super(context, null);
    }

    @Override
    void onInitView(final Context context) {

        this.iconRes = R.drawable.ic_play_arrow;
        this.titleRes = R.string.title_transport_ftp_control;

        if (FsService.isRunning()) {
            InetAddress address = FsService.getLocalInetAddress();
            if (address == null) {
                Logger.e("Unable to retrieve wifi ip address");
                this.summary = context.getString(R.string.running_summary_failed_to_get_ip_address);
            } else {
                String iptext = "ftp://" + address.getHostAddress() + ":"
                        + FsSettings.getPortNumber() + "/";
                this.summary = context.getString(R.string.running_summary_started, iptext);
            }
        } else {
            this.summary = context.getString(R.string.running_summary_stopped);
        }

        this.tileView = new SwitchTileView(context) {
            @Override
            protected void onBindActionView(RelativeLayout container) {
                super.onBindActionView(container);
                setChecked(FsService.isRunning());
            }

            @Override
            protected void onCheckChanged(boolean checked) {
                super.onCheckChanged(checked);
                if (checked) {
                    startServer(context);
                } else {
                    stopServer(context);
                }
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        updateSummary();
                    }
                }, 1000);
            }

            private void updateSummary() {
                if (FsService.isRunning()) {
                    InetAddress address = FsService.getLocalInetAddress();
                    if (address == null) {
                        Logger.e("Unable to retrieve wifi ip address");
                        getSummaryTextView().setText(R.string.running_summary_failed_to_get_ip_address);
                        return;
                    }
                    String iptext = "ftp://" + address.getHostAddress() + ":"
                            + FsSettings.getPortNumber() + "/";
                    String summary = context.getString(R.string.running_summary_started, iptext);
                    getSummaryTextView().setText(summary);
                } else {
                    getSummaryTextView().setText(R.string.running_summary_stopped);
                }
            }
        };
    }


    private void startServer(Context context) {
        context.sendBroadcast(new Intent(FsService.ACTION_START_FTPSERVER));
    }

    private void stopServer(Context context) {
        context.sendBroadcast(new Intent(FsService.ACTION_STOP_FTPSERVER));
    }
}
