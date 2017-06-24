package org.newstand.datamigration.ui.tiles;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Toast;

import org.newstand.datamigration.R;
import org.newstand.logger.Logger;

import dev.nick.tiles.tile.EmptyActionTileView;
import dev.tornaco.ftpserver.FTPServerProxy;

/**
 * Created by Nick@NewStand.org on 2017/4/6 18:26
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class FTPTestTile extends ThemedTile {

    public FTPTestTile(@NonNull Context context) {
        super(context, null);
    }

    @Override
    void onInitView(final Context context) {

        this.iconRes = R.drawable.ic_ftp;
        this.titleRes = R.string.title_transport_ftp;
        this.tileView = new EmptyActionTileView(getContext()) {
            @Override
            public void onClick(View v) {
                super.onClick(v);

                FTPServerProxy proxy = new FTPServerProxy();
                boolean ok = proxy.startServer(context);
                Logger.d("Start ftp server:%s", ok);

                if (ok) {
                    String res = String.format("Started@%s", proxy.getLocalIpAddress());
                    Toast.makeText(context, res, Toast.LENGTH_LONG).show();
                }
            }
        };
    }
}
